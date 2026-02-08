package com.pico.vrbg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pico.vrbg.config.DeviceConfig;
import com.pico.vrbg.manager.ControlSystemManager;
import com.pico.vrbg.manager.DeviceControlManager;
import com.pico.vrbg.manager.PicoBatteryManager;
import com.pico.vrbg.service.VRBackgroundService;
import com.pico.vrbg.util.NetworkManager;

public class MainActivity extends AppCompatActivity {

    private Button serviceToggleBtn;
    private Button openFileBtn;
    private Button restartBtn;
    private Button shutdownBtn;
    private Button batteryInfoBtn;
    private TextView statusTextView;
    private TextView batteryTextView;
    private TextView deviceIdView;
    private TextView lastUpdateView;

    // 设备配置相关
    private EditText etDeviceId;
    private EditText etDeviceName;
    private EditText etServerUrl;
    private EditText etServerPort;
    private EditText etReportInterval;
    private CheckBox cbAutoReport;
    private Button btnSaveConfig;
    private Button btnTestConnection;

    private SharedPreferences sharedPreferences;
    private DeviceConfig deviceConfig;
    private DeviceControlManager deviceControlManager;
    private PicoBatteryManager batteryManager;
    private ControlSystemManager controlSystemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initManagers();
        startBackgroundService();
        updateUI();
    }

    private void initViews() {
        statusTextView = findViewById(R.id.tv_status);
        batteryTextView = findViewById(R.id.tv_battery_info);
        deviceIdView = findViewById(R.id.tv_device_id);
        lastUpdateView = findViewById(R.id.tv_last_update);
        serviceToggleBtn = findViewById(R.id.btn_toggle_service);
        openFileBtn = findViewById(R.id.btn_open_file);
        restartBtn = findViewById(R.id.btn_restart);
        shutdownBtn = findViewById(R.id.btn_shutdown);
        batteryInfoBtn = findViewById(R.id.btn_battery_info);

        // 设备配置控件初始化
        etDeviceId = findViewById(R.id.et_device_id);
        etDeviceName = findViewById(R.id.et_device_name);
        etServerUrl = findViewById(R.id.et_server_url);
        etServerPort = findViewById(R.id.et_server_port);
        etReportInterval = findViewById(R.id.et_report_interval);
        cbAutoReport = findViewById(R.id.cb_auto_report);
        btnSaveConfig = findViewById(R.id.btn_save_config);
        btnTestConnection = findViewById(R.id.btn_test_connection);

        // 设置按钮监听
        serviceToggleBtn.setOnClickListener(v -> toggleService());
        openFileBtn.setOnClickListener(v -> openFile());
        restartBtn.setOnClickListener(v -> restartDevice());
        shutdownBtn.setOnClickListener(v -> shutdownDevice());
        batteryInfoBtn.setOnClickListener(v -> getBatteryInfo());
        btnSaveConfig.setOnClickListener(v -> saveConfig());
        btnTestConnection.setOnClickListener(v -> testConnection());

        // 加载保存的配置显示到UI
        loadConfigToUI();
    }

    private void initManagers() {
        sharedPreferences = getSharedPreferences("vr_bg_prefs", Context.MODE_PRIVATE);
        deviceConfig = new DeviceConfig(this);
        deviceControlManager = new DeviceControlManager(this);
        batteryManager = new PicoBatteryManager(this);
        controlSystemManager = new ControlSystemManager(this);
    }

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(this, VRBackgroundService.class);
        startService(serviceIntent);
        Toast.makeText(this, R.string.service_enabled, Toast.LENGTH_SHORT).show();
    }

    private void toggleService() {
        boolean isEnabled = sharedPreferences.getBoolean("service_enabled", true);
        if (isEnabled) {
            stopService(new Intent(this, VRBackgroundService.class));
            statusTextView.setText(R.string.service_disabled);
        } else {
            startBackgroundService();
            statusTextView.setText(R.string.service_enabled);
        }
        sharedPreferences.edit().putBoolean("service_enabled", !isEnabled).apply();
    }

    private void openFile() {
        String filePath = "/sdcard/Movies/sample.mp4";
        deviceControlManager.openFile(filePath);
        Toast.makeText(this, "Opening file: " + filePath, Toast.LENGTH_SHORT).show();
    }

    private void restartDevice() {
        deviceControlManager.restartDevice();
        Toast.makeText(this, R.string.restart_device, Toast.LENGTH_SHORT).show();
    }

    private void shutdownDevice() {
        deviceControlManager.shutdownDevice();
        Toast.makeText(this, R.string.shutdown_device, Toast.LENGTH_SHORT).show();
    }

    private void getBatteryInfo() {
        new Thread(() -> {
            int headBattery = batteryManager.getHeadBattery();
            int leftControllerBattery = batteryManager.getLeftControllerBattery();
            int rightControllerBattery = batteryManager.getRightControllerBattery();

            runOnUiThread(() -> {
                String batteryInfo = String.format(
                    "%s: %d%%\n%s: %d%%\n%s: %d%%",
                    getString(R.string.head_battery), headBattery,
                    getString(R.string.left_controller), leftControllerBattery,
                    getString(R.string.right_controller), rightControllerBattery
                );
                batteryTextView.setText(batteryInfo);
            });
        }).start();
    }

    /**
     * 从配置加载数据到UI
     */
    private void loadConfigToUI() {
        etDeviceId.setText(deviceConfig.getDeviceId());
        etDeviceName.setText(deviceConfig.getDeviceName());
        etServerUrl.setText(deviceConfig.getControlServerUrl());
        etServerPort.setText(String.valueOf(deviceConfig.getControlServerPort()));
        etReportInterval.setText(String.valueOf(deviceConfig.getDataReportInterval() / 1000));
        cbAutoReport.setChecked(deviceConfig.isAutoReportEnabled());

        // 更新设备ID显示
        deviceIdView.setText(getString(R.string.device_id) + ": " + deviceConfig.getDeviceId());
        lastUpdateView.setText(getString(R.string.last_update) + " " + getCurrentTime());
    }

    /**
     * 保存配置
     */
    private void saveConfig() {
        try {
            String deviceId = etDeviceId.getText().toString().trim();
            String deviceName = etDeviceName.getText().toString().trim();
            String serverUrl = etServerUrl.getText().toString().trim();
            String portStr = etServerPort.getText().toString().trim();
            String intervalStr = etReportInterval.getText().toString().trim();

            // 验证输入
            if (deviceId.isEmpty() || serverUrl.isEmpty() || portStr.isEmpty() || intervalStr.isEmpty()) {
                Toast.makeText(this, R.string.config_invalid, Toast.LENGTH_SHORT).show();
                return;
            }

            int port = Integer.parseInt(portStr);
            long interval = Long.parseLong(intervalStr) * 1000; // 转换为毫秒

            // 保存配置
            deviceConfig.setDeviceId(deviceId);
            deviceConfig.setDeviceName(deviceName);
            deviceConfig.setControlServerUrl(serverUrl);
            deviceConfig.setControlServerPort(port);
            deviceConfig.setDataReportInterval(interval);
            deviceConfig.setAutoReportEnabled(cbAutoReport.isChecked());

            Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show();
            deviceIdView.setText(getString(R.string.device_id) + ": " + deviceId);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "端口和间隔必须是数字", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 测试连接到播控系统
     */
    private void testConnection() {
        saveConfig();  // 先保存配置
        
        Toast.makeText(this, "正在测试连接...", Toast.LENGTH_SHORT).show();

        controlSystemManager.verifyConnection(new NetworkManager.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, R.string.connection_success, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, R.string.connection_failed + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * 获取当前时间字符串
     */
    private String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.CHINA);
        return sdf.format(new java.util.Date());
    }

    private void updateUI() {
        boolean isServiceEnabled = sharedPreferences.getBoolean("service_enabled", true);
        statusTextView.setText(isServiceEnabled ? R.string.service_enabled : R.string.service_disabled);
    }
}
