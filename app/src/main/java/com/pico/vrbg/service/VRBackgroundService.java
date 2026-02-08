package com.pico.vrbg.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.content.Context;

import androidx.annotation.Nullable;

import com.pico.vrbg.config.DeviceConfig;
import com.pico.vrbg.manager.ControlSystemManager;
import com.pico.vrbg.manager.DeviceStatus;
import com.pico.vrbg.manager.PicoBatteryManager;
import com.pico.vrbg.manager.DeviceControlManager;
import com.pico.vrbg.util.NetworkManager;

public class VRBackgroundService extends Service {

    private static final String TAG = "VRBackgroundService";
    private static final int BATTERY_UPDATE_INTERVAL = 60000; // 每分钟更新一次电量信息

    private PicoBatteryManager batteryManager;
    private DeviceControlManager deviceControlManager;
    private ControlSystemManager controlSystemManager;
    private DeviceStatus deviceStatus;
    private DeviceConfig deviceConfig;
    private SharedPreferences sharedPreferences;
    private Thread batteryMonitorThread;
    private Thread dataReporterThread;
    private volatile boolean isRunning = true;

    public class LocalBinder extends Binder {
        public VRBackgroundService getService() {
            return VRBackgroundService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        batteryManager = new PicoBatteryManager(this);
        deviceControlManager = new DeviceControlManager(this);
        deviceConfig = new DeviceConfig(this);
        controlSystemManager = new ControlSystemManager(this);
        deviceStatus = new DeviceStatus(this);
        sharedPreferences = getSharedPreferences("vr_bg_prefs", Context.MODE_PRIVATE);

        startBatteryMonitoring();
        
        // 如果启用了自动上报，则启动数据上报线程
        if (deviceConfig.isAutoReportEnabled()) {
            startDataReporter();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 启动电量监控线程
     */
    private void startBatteryMonitoring() {
        batteryMonitorThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // 获取电池信息
                    int headBattery = batteryManager.getHeadBattery();
                    int leftBattery = batteryManager.getLeftControllerBattery();
                    int rightBattery = batteryManager.getRightControllerBattery();

                    // 记录日志
                    Log.d(TAG, String.format("Head: %d%%, Left: %d%%, Right: %d%%",
                            headBattery, leftBattery, rightBattery));

                    // 检查低电量警告
                    if (headBattery < 20) {
                        Log.w(TAG, "Head battery low: " + headBattery + "%");
                    }

                    // 保存到SharedPreferences用于其他应用查询
                    sharedPreferences.edit()
                            .putInt("head_battery", headBattery)
                            .putInt("left_battery", leftBattery)
                            .putInt("right_battery", rightBattery)
                            .putLong("last_update", System.currentTimeMillis())
                            .apply();

                    Thread.sleep(BATTERY_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Battery monitoring thread interrupted", e);
                    break;
                }
            }
        });
        batteryMonitorThread.setName("BatteryMonitor");
        batteryMonitorThread.start();
    }

    /**
     * 启动数据上报线程
     * 定期将设备数据上报到播控系统
     */
    private void startDataReporter() {
        dataReporterThread = new Thread(() -> {
            while (isRunning && deviceConfig.isAutoReportEnabled()) {
                try {
                    // 获取当前的数据
                    PicoBatteryManager.BatteryInfo batteryInfo = batteryManager.getAllBatteryInfo();
                    ControlSystemManager.DeviceStatus status = deviceStatus.buildStatus();

                    // 上报到播控系统
                    controlSystemManager.reportDeviceStatus(batteryInfo, status, 
                        new NetworkManager.NetworkCallback() {
                            @Override
                            public void onSuccess(String response) {
                                Log.d(TAG, "Data reported successfully to control system");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.w(TAG, "Failed to report data to control system: " + e.getMessage());
                            }
                        });

                    // 也可以定期获取待处理的命令
                    controlSystemManager.getPendingCommands(new NetworkManager.NetworkCallback() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d(TAG, "Pending commands: " + response);
                            processRemoteCommands(response);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.w(TAG, "Failed to get pending commands: " + e.getMessage());
                        }
                    });

                    // 等待直到下一次上报时间
                    long reportInterval = deviceConfig.getDataReportInterval();
                    Thread.sleep(reportInterval);

                } catch (InterruptedException e) {
                    Log.e(TAG, "Data reporter thread interrupted", e);
                    break;
                }
            }
        });
        dataReporterThread.setName("DataReporter");
        dataReporterThread.start();
        Log.d(TAG, "Data reporter thread started");
    }

    /**
     * 处理播控系统下发的远程命令
     */
    private void processRemoteCommands(String commandJson) {
        // 这里可以解析JSON格式的命令并执行
        // 格式示例: {"commandId": "cmd001", "command": "open_file", "params": "/sdcard/video.mp4"}
        Log.d(TAG, "Processing remote commands: " + commandJson);
        // 后续可以集成JSON解析库处理命令
    }

    /**
     * 处理远程命令
     */
    public void handleRemoteCommand(String command, String params) {
        Log.d(TAG, "Handling command: " + command);

        switch (command) {
            case "open_file":
                deviceControlManager.openFile(params);
                break;
            case "restart":
                deviceControlManager.restartDevice();
                break;
            case "shutdown":
                deviceControlManager.shutdownDevice();
                break;
            case "wake_up":
                deviceControlManager.wakeUpDevice();
                break;
            case "get_battery":
                // 电量信息已在startBatteryMonitoring保存
                break;
            default:
                Log.w(TAG, "Unknown command: " + command);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        isRunning = false;
        if (batteryMonitorThread != null) {
            batteryMonitorThread.interrupt();
        }
        if (dataReporterThread != null) {
            dataReporterThread.interrupt();
        }
    }
}
