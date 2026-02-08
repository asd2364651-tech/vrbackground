package com.pico.vrbg.manager;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;

/**
 * 设备控制管理器
 * 负责控制VR设备的各种操作：打开文件、重启、关机等
 */
public class DeviceControlManager {

    private static final String TAG = "DeviceControlManager";
    private Context context;
    private PowerManager powerManager;

    public DeviceControlManager(Context context) {
        this.context = context;
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    /**
     * 打开指定的内容文件
     * @param filePath 文件路径
     */
    public void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Log.e(TAG, "File not found: " + filePath);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(android.net.Uri.fromFile(file), getMimeType(filePath));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d(TAG, "Opening file: " + filePath);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open file: " + filePath, e);
        }
    }

    /**
     * 重启设备
     */
    public void restartDevice() {
        try {
            // 需要REBOOT权限
            Runtime.getRuntime().exec("su -c reboot");
            Log.d(TAG, "Device reboot command sent");
        } catch (Exception e) {
            Log.e(TAG, "Failed to restart device", e);
        }
    }

    /**
     * 关闭设备
     */
    public void shutdownDevice() {
        try {
            // 需要REBOOT权限
            Runtime.getRuntime().exec("su -c shutdown -p now");
            Log.d(TAG, "Device shutdown command sent");
        } catch (Exception e) {
            Log.e(TAG, "Failed to shutdown device", e);
        }
    }

    /**
     * 唤醒设备
     */
    public void wakeUpDevice() {
        try {
            if (powerManager != null) {
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                        PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "VRBackgroundService:WakeLock"
                );
                wakeLock.acquire(10000); // 10秒后自动释放
                Log.d(TAG, "Device wake up");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to wake up device", e);
        }
    }

    /**
     * 获取文件的MIME类型
     */
    private String getMimeType(String filePath) {
        if (filePath.endsWith(".mp4") || filePath.endsWith(".avi")) {
            return "video/*";
        } else if (filePath.endsWith(".mp3") || filePath.endsWith(".wav")) {
            return "audio/*";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".png")) {
            return "image/*";
        } else if (filePath.endsWith(".apk")) {
            return "application/vnd.android.package-archive";
        }
        return "*/*";
    }

    /**
     * 锁屏设备
     */
    public void lockDevice() {
        try {
            Runtime.getRuntime().exec("su -c input keyevent 26");
            Log.d(TAG, "Device locked");
        } catch (Exception e) {
            Log.e(TAG, "Failed to lock device", e);
        }
    }

    /**
     * 调整亮度
     * @param brightness 亮度值 (0-255)
     */
    public void setBrightness(int brightness) {
        try {
            brightness = Math.max(0, Math.min(255, brightness));
            Runtime.getRuntime().exec(new String[]{
                    "su", "-c", "settings put system screen_brightness " + brightness
            });
            Log.d(TAG, "Brightness set to: " + brightness);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set brightness", e);
        }
    }
}
