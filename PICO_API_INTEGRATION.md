# Pico VR 后台控制服务 - API集成指南

## 🔗 集成Pico SDK

本项目框架已经准备好，现在需要集成Pico的真实SDK接口。

---

## 1. 获取Pico SDK

### 官方SDK下载
- 访问 [Pico开发者中心](https://developer.pico-interactive.com/)
- 下载最新的 Android SDK
- 或添加依赖: `com.picovr.app:frame:2.4.18`

### 依赖配置

在 `app/build.gradle` 中：

```gradle
dependencies {
    // Pico VR SDK
    implementation 'com.picovr.app:frame:2.4.18'
    // 或使用本地AAR
    // implementation files('libs/pico-sdk.aar')
}
```

---

## 2. 电池信息API集成

### 修改文件: `PicoBatteryManager.java`

**当前代码 (模拟):**
```java
public int getHeadBattery() {
    try {
        // 模拟返回值
        return 85;
    } catch (Exception e) {
        Log.e(TAG, "Failed to get head battery", e);
        return -1;
    }
}
```

**集成Pico API后:**
```java
public int getHeadBattery() {
    try {
        // 调用Pico SDK获取头显电量
        int battery = PicoDeviceManager.getHeadsetBattery();
        // 或
        // int battery = com.picovr.app.PicoDeviceManager.getHeadsetBattery();
        return battery;
    } catch (Exception e) {
        Log.e(TAG, "Failed to get head battery", e);
        return -1;
    }
}
```

### Pico电池API参考

```java
// 头显电量 (返回0-100百分比)
int headBattery = PicoDeviceManager.getHeadsetBattery();

// 左手柄电量
int leftBattery = PicoDeviceManager.getLeftControllerBattery();

// 右手柄电量
int rightBattery = PicoDeviceManager.getRightControllerBattery();

// 充电状态 (true=充电中)
boolean isCharging = PicoDeviceManager.isHeadsetCharging();

// 获取设备温度
int temperature = PicoDeviceManager.getDeviceTemperature();

// 获取设备信息
String deviceModel = PicoDeviceManager.getDeviceModel();
String serialNumber = PicoDeviceManager.getSerialNumber();
```

---

## 3. 设备控制API集成

### 修改文件: `DeviceControlManager.java`

**重启设备:**
```java
public void restartDevice() {
    try {
        // 方式1: 使用Pico SDK (推荐)
        PicoDeviceManager.reboot();
        
        // 或方式2: 使用Runtime命令
        Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
    } catch (Exception e) {
        Log.e(TAG, "Failed to restart device", e);
    }
}
```

**关闭设备:**
```java
public void shutdownDevice() {
    try {
        // 方式1: 使用Pico SDK (推荐)
        PicoDeviceManager.shutdown();
        
        // 或方式2: 使用Runtime命令
        Runtime.getRuntime().exec(new String[]{"su", "-c", "shutdown -p now"});
    } catch (Exception e) {
        Log.e(TAG, "Failed to shutdown device", e);
    }
}
```

**打开文件:**
```java
public void openFile(String filePath) {
    try {
        // 使用Pico播控系统打开文件 (推荐)
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(new File(filePath)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // 或使用Pico SDK的媒体播放器
        // PicoMediaManager.play(filePath);
        
        context.startActivity(intent);
    } catch (Exception e) {
        Log.e(TAG, "Failed to open file", e);
    }
}
```

### Pico设备控制API参考

```java
// 重启设备
PicoDeviceManager.reboot();

// 关闭设备
PicoDeviceManager.shutdown();

// 唤醒设备
PicoDeviceManager.wakeUp();

// 进入睡眠模式
PicoDeviceManager.sleep();

// 获取屏幕亮度 (0-255)
int brightness = PicoDeviceManager.getScreenBrightness();

// 设置屏幕亮度
PicoDeviceManager.setScreenBrightness(128);

// 获取音量 (0-15)
int volume = PicoDeviceManager.getVolume();

// 设置音量
PicoDeviceManager.setVolume(10);

// 启用/禁用WiFi
PicoDeviceManager.setWiFiEnabled(true);

// 获取WiFi状态
boolean wifiEnabled = PicoDeviceManager.isWiFiEnabled();

// 启用/禁用蓝牙
PicoDeviceManager.setBluetoothEnabled(true);

// 获取蓝牙状态
boolean btEnabled = PicoDeviceManager.isBluetoothEnabled();
```

---

## 4. 文件播放API集成

### 使用Pico媒体播放器

**修改文件: `FileManagerHelper.java`**

```java
// 使用Pico SDK播放媒体文件
public void playMediaFile(String filePath) {
    try {
        // 方式1: 使用Intent (标准方式)
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(new File(filePath)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        
        // 方式2: 使用Pico媒体管理器
        // PicoMediaManager.playVideo(filePath);
        // PicoMediaManager.playAudio(filePath);
        // PicoMediaManager.displayImage(filePath);
        
    } catch (Exception e) {
        Log.e(TAG, "Failed to play media", e);
    }
}
```

### 支持的文件格式

```java
// 视频格式
supportedFormats = {
    "mp4", "avi", "mov", "mkv", "flv", "wmv", 
    "webm", "m4v", "3gp", "ts"
};

// 音频格式
supportedFormats = {
    "mp3", "aac", "ogg", "flac", "wav", "m4a", 
    "wma", "aiff"
};

// 图片格式
supportedFormats = {
    "jpg", "jpeg", "png", "gif", "bmp", "webp", 
    "tiff", "svg"
};

// 应用格式
supportedFormats = {
    "apk"
};
```

---

## 5. 远程命令通信

### 使用Pico通信协议

**修改文件: `NetworkManager.java`**

```java
// 与Pico云服务通信
public class PicoCloudManager {
    
    // 上报设备状态
    public void reportDeviceStatus() {
        PicoBatteryManager.BatteryInfo battery = batteryManager.getAllBatteryInfo();
        
        String json = "{" +
            "\"device_id\": \"" + getDeviceId() + "\"," +
            "\"head_battery\": " + battery.headBattery + "," +
            "\"left_battery\": " + battery.leftControllerBattery + "," +
            "\"right_battery\": " + battery.rightControllerBattery + "," +
            "\"timestamp\": " + System.currentTimeMillis() +
            "}";
        
        networkManager.postJson("https://api.pico.com/device/status", json, callback);
    }
    
    // 接收远程命令
    public void receiveRemoteCommand() {
        // 通过WebSocket或MQTT接收命令
        // PicoCloudService.subscribe("device/commands", (command) -> {
        //     executeCommand(command);
        // });
    }
    
    // 执行远程命令
    private void executeCommand(String command) {
        switch(command) {
            case "restart":
                deviceManager.restartDevice();
                break;
            case "open_file":
                deviceManager.openFile(/* file path */);
                break;
            // ... 其他命令
        }
    }
}
```

---

## 6. 权限和清单配置

### 特殊权限说明

```xml
<!-- Pico系统权限 (需要Pico系统支持) -->
<uses-permission android:name="com.picovr.permission.PICO_SYSTEM" />

<!-- 重启设备需要 -->
<uses-permission android:name="android.permission.REBOOT" />

<!-- 设备管理权限 (可选) -->
<uses-permission android:name="android.permission.DEVICE_POWER" />
```

### 声明DeviceAdmin (可选, 用于高权限控制)

```xml
<receiver
    android:name=".receiver.PicoDeviceAdminReceiver"
    android:permission="android.permission.BIND_DEVICE_ADMIN">
    <intent-filter>
        <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
    </intent-filter>
    <meta-data
        android:name="android.app.device_admin"
        android:resource="@xml/device_admin_receiver" />
</receiver>
```

---

## 7. 测试和调试

### 获取Pico SDK版本

```java
String sdkVersion = PicoDeviceManager.getSdkVersion();
Log.d(TAG, "Pico SDK Version: " + sdkVersion);
```

### 测试API可用性

```java
// 检查API是否可用
if (PicoDeviceManager.isAvailable()) {
    Log.d(TAG, "Pico API is available");
    int battery = PicoDeviceManager.getHeadsetBattery();
} else {
    Log.e(TAG, "Pico API is not available");
}
```

### 测试广播接收

```java
// 监听Pico系统广播
IntentFilter filter = new IntentFilter();
filter.addAction("com.picovr.action.BATTERY_CHANGED");
filter.addAction("com.picovr.action.DEVICE_READY");
context.registerReceiver(new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.picovr.action.BATTERY_CHANGED".equals(action)) {
            int battery = intent.getIntExtra("battery", -1);
            Log.d(TAG, "Battery: " + battery);
        }
    }
}, filter);
```

---

## 8. 常见集成问题

### 问题1: 如何获取Pico设备ID?

```java
String deviceId = PicoDeviceManager.getDeviceId();
String serialNumber = PicoDeviceManager.getSerialNumber();
```

### 问题2: 如何检查Pico设备是否在线?

```java
boolean online = PicoDeviceManager.isDeviceOnline();
```

### 问题3: 如何处理API版本兼容性?

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    // 使用新的API
    int battery = PicoDeviceManager.getHeadsetBattery();
} else {
    // 使用旧的API或备选方案
    int battery = fallbackGetBattery();
}
```

### 问题4: 如何处理权限不足的错误?

```java
try {
    PicoDeviceManager.reboot();
} catch (SecurityException e) {
    Log.e(TAG, "Permission denied", e);
    // 显示权限申请UI
    requestPermissions(new String[]{
        Manifest.permission.REBOOT
    }, REQUEST_CODE);
}
```

---

## 9. 完整集成示例

### 创建文件: `PicoIntegration.java`

```java
public class PicoIntegration {
    private static final String TAG = "PicoIntegration";
    
    // 获取完整设备信息
    public static DeviceInfo getDeviceInfo() {
        DeviceInfo info = new DeviceInfo();
        
        try {
            // 设备基本信息
            info.deviceId = PicoDeviceManager.getDeviceId();
            info.model = PicoDeviceManager.getDeviceModel();
            info.serialNumber = PicoDeviceManager.getSerialNumber();
            
            // 电池信息
            info.headBattery = PicoDeviceManager.getHeadsetBattery();
            info.leftBattery = PicoDeviceManager.getLeftControllerBattery();
            info.rightBattery = PicoDeviceManager.getRightControllerBattery();
            info.isCharging = PicoDeviceManager.isHeadsetCharging();
            
            // 系统信息
            info.brightness = PicoDeviceManager.getScreenBrightness();
            info.volume = PicoDeviceManager.getVolume();
            info.temperature = PicoDeviceManager.getDeviceTemperature();
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to get device info", e);
        }
        
        return info;
    }
    
    // 数据类
    public static class DeviceInfo {
        public String deviceId;
        public String model;
        public String serialNumber;
        public int headBattery;
        public int leftBattery;
        public int rightBattery;
        public boolean isCharging;
        public int brightness;
        public int volume;
        public int temperature;
    }
}
```

---

## 📚 参考文档

- [Pico官方开发文档](https://developer.pico-interactive.com/)
- [Pico SDK API参考](https://developer.pico-interactive.com/docs/)
- [Android官方文档](https://developer.android.com/)

---

**需要更多帮助？请查阅Pico官方文档或联系Pico技术支持。** 📞

