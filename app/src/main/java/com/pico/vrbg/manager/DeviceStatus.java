package com.pico.vrbg.manager;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

/**
 * 设备状态收集器
 * 用于收集设备的各种状态信息，用于上报到播控系统
 */
public class DeviceStatus {

    private static final String TAG = "DeviceStatus";
    private Context context;
    private long startTime;

    public DeviceStatus(Context context) {
        this.context = context;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 获取设备IP地址
     */
    public String getIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().contains(".")) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting IP address", e);
        }
        return "0.0.0.0";
    }

    /**
     * 获取Android版本
     */
    public String getAndroidVersion() {
        return "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
    }

    /**
     * 获取设备型号
     */
    public String getDeviceModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    /**
     * 获取设备运行时长 (毫秒)
     */
    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 获取当前时间字符串
     */
    public String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 构建完整的设备状态对象
     */
    public ControlSystemManager.DeviceStatus buildStatus() {
        ControlSystemManager.DeviceStatus status = new ControlSystemManager.DeviceStatus();
        status.ipAddress = getIPAddress();
        status.androidVersion = getAndroidVersion();
        status.screenBrightness = 128;  // 默认，实际应从系统获取
        status.volume = 10;              // 默认，实际应从系统获取
        status.uptime = getUptime();
        status.lastUpdateTime = getCurrentTimeString();
        return status;
    }

    /**
     * 获取设备信息摘要
     */
    public String getDeviceInfoSummary() {
        return "Model: " + getDeviceModel() + "\n" +
               "IP: " + getIPAddress() + "\n" +
               "Android: " + getAndroidVersion() + "\n" +
               "Uptime: " + formatUptime(getUptime());
    }

    /**
     * 格式化运行时长
     */
    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "天 " + (hours % 24) + "小时";
        } else if (hours > 0) {
            return hours + "小时 " + (minutes % 60) + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟 " + (seconds % 60) + "秒";
        } else {
            return seconds + "秒";
        }
    }
}
