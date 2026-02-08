package com.pico.vrbg.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 设备配置管理器
 * 负责管理设备编号、服务器地址等配置信息
 * 这些配置用于与播控系统通信
 */
public class DeviceConfig {

    private static final String TAG = "DeviceConfig";
    private static final String PREFS_NAME = "device_config";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_DEVICE_NAME = "device_name";
    private static final String KEY_CONTROL_SERVER_URL = "control_server_url";
    private static final String KEY_CONTROL_SERVER_PORT = "control_server_port";
    private static final String KEY_DATA_REPORT_INTERVAL = "data_report_interval";
    private static final String KEY_AUTO_REPORT_ENABLED = "auto_report_enabled";

    private SharedPreferences preferences;
    private Context context;

    // 默认值
    private static final String DEFAULT_DEVICE_ID = "PICO_VR_001";
    private static final String DEFAULT_DEVICE_NAME = "VR Set 1";
    private static final String DEFAULT_SERVER_URL = "192.168.1.100";
    private static final int DEFAULT_SERVER_PORT = 8080;
    private static final long DEFAULT_REPORT_INTERVAL = 60000; // 60秒
    private static final boolean DEFAULT_AUTO_REPORT = true;

    public DeviceConfig(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取设备编号
     * 用于播控系统识别和控制具体的设备
     * @return 设备编号 (例如: PICO_VR_001)
     */
    public String getDeviceId() {
        return preferences.getString(KEY_DEVICE_ID, DEFAULT_DEVICE_ID);
    }

    /**
     * 设置设备编号
     * @param deviceId 设备编号
     */
    public void setDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            Log.w(TAG, "Device ID cannot be empty");
            return;
        }
        preferences.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        Log.d(TAG, "Device ID set to: " + deviceId);
    }

    /**
     * 获取设备名称
     * 用于用户界面显示
     * @return 设备名称
     */
    public String getDeviceName() {
        return preferences.getString(KEY_DEVICE_NAME, DEFAULT_DEVICE_NAME);
    }

    /**
     * 设置设备名称
     * @param deviceName 设备名称
     */
    public void setDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            Log.w(TAG, "Device name cannot be empty");
            return;
        }
        preferences.edit().putString(KEY_DEVICE_NAME, deviceName).apply();
        Log.d(TAG, "Device name set to: " + deviceName);
    }

    /**
     * 获取播控系统服务器地址
     * @return 服务器IP地址或域名
     */
    public String getControlServerUrl() {
        return preferences.getString(KEY_CONTROL_SERVER_URL, DEFAULT_SERVER_URL);
    }

    /**
     * 设置播控系统服务器地址
     * @param serverUrl 服务器IP地址或域名
     */
    public void setControlServerUrl(String serverUrl) {
        if (serverUrl == null || serverUrl.isEmpty()) {
            Log.w(TAG, "Server URL cannot be empty");
            return;
        }
        preferences.edit().putString(KEY_CONTROL_SERVER_URL, serverUrl).apply();
        Log.d(TAG, "Control server URL set to: " + serverUrl);
    }

    /**
     * 获取播控系统服务器端口
     * @return 服务器端口号
     */
    public int getControlServerPort() {
        return preferences.getInt(KEY_CONTROL_SERVER_PORT, DEFAULT_SERVER_PORT);
    }

    /**
     * 设置播控系统服务器端口
     * @param port 服务器端口号
     */
    public void setControlServerPort(int port) {
        if (port <= 0 || port > 65535) {
            Log.w(TAG, "Invalid port number: " + port);
            return;
        }
        preferences.edit().putInt(KEY_CONTROL_SERVER_PORT, port).apply();
        Log.d(TAG, "Control server port set to: " + port);
    }

    /**
     * 获取完整的服务器URL
     * @return 完整的服务器地址 (格式: http://IP:PORT)
     */
    public String getControlServerFullUrl() {
        String url = getControlServerUrl();
        int port = getControlServerPort();
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        if (!url.contains(":")) {
            url = url + ":" + port;
        }
        
        return url;
    }

    /**
     * 获取数据上报间隔 (毫秒)
     * @return 间隔时间，单位毫秒
     */
    public long getDataReportInterval() {
        return preferences.getLong(KEY_DATA_REPORT_INTERVAL, DEFAULT_REPORT_INTERVAL);
    }

    /**
     * 设置数据上报间隔 (毫秒)
     * @param interval 间隔时间，单位毫秒，最小10000ms
     */
    public void setDataReportInterval(long interval) {
        if (interval < 10000) {
            Log.w(TAG, "Report interval too small, minimum is 10000ms");
            return;
        }
        preferences.edit().putLong(KEY_DATA_REPORT_INTERVAL, interval).apply();
        Log.d(TAG, "Data report interval set to: " + interval + "ms");
    }

    /**
     * 检查是否启用自动数据上报
     * @return true-启用自动上报，false-禁用
     */
    public boolean isAutoReportEnabled() {
        return preferences.getBoolean(KEY_AUTO_REPORT_ENABLED, DEFAULT_AUTO_REPORT);
    }

    /**
     * 设置自动数据上报开关
     * @param enabled true-启用，false-禁用
     */
    public void setAutoReportEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_REPORT_ENABLED, enabled).apply();
        Log.d(TAG, "Auto report " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * 获取完整的设备信息
     * @return 设备配置信息对象
     */
    public DeviceInfo getDeviceInfo() {
        DeviceInfo info = new DeviceInfo();
        info.deviceId = getDeviceId();
        info.deviceName = getDeviceName();
        info.controlServerUrl = getControlServerUrl();
        info.controlServerPort = getControlServerPort();
        info.dataReportInterval = getDataReportInterval();
        info.autoReportEnabled = isAutoReportEnabled();
        return info;
    }

    /**
     * 设备信息数据类
     */
    public static class DeviceInfo {
        public String deviceId;           // 设备编号
        public String deviceName;         // 设备名称
        public String controlServerUrl;   // 播控系统地址
        public int controlServerPort;     // 播控系统端口
        public long dataReportInterval;   // 数据上报间隔
        public boolean autoReportEnabled; // 是否启用自动上报

        @Override
        public String toString() {
            return "DeviceInfo{" +
                    "deviceId='" + deviceId + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", controlServerUrl='" + controlServerUrl + '\'' +
                    ", controlServerPort=" + controlServerPort +
                    ", dataReportInterval=" + dataReportInterval +
                    ", autoReportEnabled=" + autoReportEnabled +
                    '}';
        }
    }

    /**
     * 重置为默认配置
     */
    public void resetToDefaults() {
        preferences.edit().clear().apply();
        Log.d(TAG, "Device config reset to defaults");
    }

    /**
     * 验证配置是否有效
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        String deviceId = getDeviceId();
        String serverUrl = getControlServerUrl();
        int port = getControlServerPort();

        if (deviceId == null || deviceId.isEmpty()) {
            Log.e(TAG, "Invalid: Device ID is empty");
            return false;
        }

        if (serverUrl == null || serverUrl.isEmpty()) {
            Log.e(TAG, "Invalid: Server URL is empty");
            return false;
        }

        if (port <= 0 || port > 65535) {
            Log.e(TAG, "Invalid: Server port is out of range");
            return false;
        }

        return true;
    }
}
