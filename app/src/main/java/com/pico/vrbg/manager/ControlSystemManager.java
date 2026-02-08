package com.pico.vrbg.manager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pico.vrbg.config.DeviceConfig;
import com.pico.vrbg.util.NetworkManager;

/**
 * 播控系统通信管理器
 * 负责与播控系统的通信，包括：
 * 1. 上报设备状态数据 (电量、温度等)
 * 2. 接收播控系统的远程命令
 * 3. 定期心跳检测
 */
public class ControlSystemManager {

    private static final String TAG = "ControlSystemManager";
    
    // API端点
    private static final String ENDPOINT_REPORT_STATUS = "/api/device/status/report";
    private static final String ENDPOINT_GET_COMMANDS = "/api/device/commands/pending";
    private static final String ENDPOINT_ACK_COMMAND = "/api/device/commands/ack";
    private static final String ENDPOINT_HEARTBEAT = "/api/device/heartbeat";

    private NetworkManager networkManager;
    private DeviceConfig deviceConfig;
    private Context context;
    private Gson gson;

    public ControlSystemManager(Context context) {
        this.context = context;
        this.networkManager = new NetworkManager(context);
        this.deviceConfig = new DeviceConfig(context);
        this.gson = new Gson();
    }

    /**
     * 上报设备状态到播控系统
     * 包含：电量、温度、运行状态等
     * @param batteryInfo 电池信息
     * @param deviceStatus 设备状态
     * @param callback 回调函数
     */
    public void reportDeviceStatus(PicoBatteryManager.BatteryInfo batteryInfo, 
                                   DeviceStatus deviceStatus,
                                   NetworkManager.NetworkCallback callback) {
        try {
            String deviceId = deviceConfig.getDeviceId();
            long timestamp = System.currentTimeMillis();

            // 构建上报数据
            StatusReportData data = new StatusReportData();
            data.deviceId = deviceId;
            data.timestamp = timestamp;
            data.battery = new BatteryData();
            data.battery.headBattery = batteryInfo.headBattery;
            data.battery.leftBattery = batteryInfo.leftControllerBattery;
            data.battery.rightBattery = batteryInfo.rightControllerBattery;
            data.battery.isCharging = batteryInfo.isCharging;
            data.status = deviceStatus;

            String json = gson.toJson(data);
            String url = deviceConfig.getControlServerFullUrl() + ENDPOINT_REPORT_STATUS;

            Log.d(TAG, "Reporting status to: " + url + " Device: " + deviceId);

            networkManager.postJson(url, json, new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "Status reported successfully: " + deviceId);
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to report status for device: " + deviceId, e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error reporting device status", e);
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    /**
     * 获取播控系统下发的待处理命令
     * @param callback 回调函数
     */
    public void getPendingCommands(final NetworkManager.NetworkCallback callback) {
        try {
            String deviceId = deviceConfig.getDeviceId();
            String url = deviceConfig.getControlServerFullUrl() + ENDPOINT_GET_COMMANDS + 
                    "?deviceId=" + deviceId;

            Log.d(TAG, "Getting pending commands for device: " + deviceId);

            networkManager.getAsync(url, new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "Received commands: " + response);
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get pending commands", e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending commands", e);
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    /**
     * 确认命令已执行
     * @param commandId 命令ID
     * @param success 是否成功执行
     * @param result 执行结果描述
     * @param callback 回调函数
     */
    public void acknowledgeCommand(String commandId, boolean success, String result,
                                   NetworkManager.NetworkCallback callback) {
        try {
            String deviceId = deviceConfig.getDeviceId();

            CommandAckData ackData = new CommandAckData();
            ackData.deviceId = deviceId;
            ackData.commandId = commandId;
            ackData.success = success;
            ackData.result = result;
            ackData.timestamp = System.currentTimeMillis();

            String json = gson.toJson(ackData);
            String url = deviceConfig.getControlServerFullUrl() + ENDPOINT_ACK_COMMAND;

            Log.d(TAG, "Acknowledging command: " + commandId);

            networkManager.postJson(url, json, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error acknowledging command", e);
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    /**
     * 发送心跳信号到播控系统
     * 用于检测设备是否在线
     * @param callback 回调函数
     */
    public void sendHeartbeat(NetworkManager.NetworkCallback callback) {
        try {
            String deviceId = deviceConfig.getDeviceId();

            HeartbeatData heartbeat = new HeartbeatData();
            heartbeat.deviceId = deviceId;
            heartbeat.timestamp = System.currentTimeMillis();

            String json = gson.toJson(heartbeat);
            String url = deviceConfig.getControlServerFullUrl() + ENDPOINT_HEARTBEAT;

            Log.d(TAG, "Sending heartbeat for device: " + deviceId);

            networkManager.postJson(url, json, new NetworkManager.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "Heartbeat sent successfully");
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.w(TAG, "Heartbeat failed", e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending heartbeat", e);
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    /**
     * 获取当前设备编号
     */
    public String getDeviceId() {
        return deviceConfig.getDeviceId();
    }

    /**
     * 获取播控系统地址
     */
    public String getControlServerUrl() {
        return deviceConfig.getControlServerFullUrl();
    }

    /**
     * 验证连接
     * @param callback 回调函数
     */
    public void verifyConnection(NetworkManager.NetworkCallback callback) {
        sendHeartbeat(callback);
    }

    // ==================== 数据类定义 ====================

    /**
     * 状态上报数据
     */
    public static class StatusReportData {
        public String deviceId;        // 设备编号
        public long timestamp;         // 时间戳
        public BatteryData battery;    // 电池信息
        public DeviceStatus status;    // 设备状态
    }

    /**
     * 电池数据
     */
    public static class BatteryData {
        public int headBattery;        // 头显电量
        public int leftBattery;        // 左手柄电量
        public int rightBattery;       // 右手柄电量
        public boolean isCharging;     // 是否充电
    }

    /**
     * 设备状态信息
     */
    public static class DeviceStatus {
        public String ipAddress;       // IP地址
        public String androidVersion;  // Android版本
        public int screenBrightness;   // 屏幕亮度
        public int volume;             // 音量
        public long uptime;            // 运行时长
        public String lastUpdateTime;  // 最后更新时间
    }

    /**
     * 命令确认数据
     */
    public static class CommandAckData {
        public String deviceId;        // 设备编号
        public String commandId;       // 命令ID
        public boolean success;        // 是否执行成功
        public String result;          // 执行结果
        public long timestamp;         // 时间戳
    }

    /**
     * 心跳数据
     */
    public static class HeartbeatData {
        public String deviceId;        // 设备编号
        public long timestamp;         // 时间戳
    }
}
