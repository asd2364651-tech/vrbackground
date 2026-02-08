# VR Background Service - 快速开始指南

## 📋 目录

1. [项目概述](#项目概述)
2. [快速构建](#快速构建)
3. [主要功能](#主要功能)
4. [文件说明](#文件说明)
5. [后续开发](#后续开发)

---

## 项目概述

这是一个为Pico VR头显设计的后台控制服务APK。主要功能包括：

✅ 通过VR播控系统打开内容文件  
✅ 获取头显和手柄电量信息  
✅ 控制设备重启/关机/唤醒  
✅ 后台运行和开机自启  
✅ 与其他应用通过IPC通信  

---

## 快速构建

### 前置条件
- Android Studio 2024.1+
- JDK 11+
- Gradle 8.1.0+

### 构建步骤

```bash
# 1. 进入项目目录
cd /Volumes/lexar-2T/cursorworkspace/vrbackground

# 2. 清理旧构建
./gradlew clean

# 3. 构建Release APK (推荐用于打包)
./gradlew assembleRelease

# 4. 或者构建Debug APK (用于开发测试)
./gradlew assembleDebug

# 5. APK位置
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

### 安装到设备

```bash
# 查看连接的设备
adb devices

# 安装APK
adb install -r app/build/outputs/apk/release/app-release.apk

# 启动应用
adb shell am start -n com.pico.vrbg/.MainActivity

# 查看日志
adb logcat | grep "VRBackground"
```

---

## 主要功能

### 1. 电量监控
- **头显电量**: 实时获取头显电池百分比
- **手柄电量**: 分别获取左右手柄电量
- **充电状态**: 监控是否正在充电
- **自动警告**: 低电量时自动报警

### 2. 设备控制
- **打开文件**: 支持视频、音频、图片等多种格式
- **重启设备**: 安全重启VR头显
- **关闭设备**: 完全关闭电源
- **唤醒设备**: 唤醒休眠设备
- **锁屏**: 保护隐私
- **亮度调节**: 自适应屏幕亮度

### 3. 后台服务
- **自动启动**: 设备开机自动启动服务
- **定期监控**: 每60秒更新一次电量信息
- **后台运行**: 不占用前台资源
- **进程通信**: 支持与其他应用通信

### 4. 文件管理
- **文件列表**: 列出目录下所有文件
- **格式过滤**: 按扩展名过滤文件
- **文件操作**: 删除、创建、检查文件
- **目录管理**: 创建和管理目录结构

---

## 文件说明

### 核心类

| 文件 | 说明 |
|------|------|
| `MainActivity.java` | 应用主界面，提供UI控制 |
| `VRBackgroundService.java` | 后台服务，处理电量监控 |
| `BootReceiver.java` | 开机自启接收器 |
| `DeviceControlManager.java` | 设备控制（重启、关机等） |
| `PicoBatteryManager.java` | 电池信息获取 |
| `VRControlProvider.java` | ContentProvider，供其他应用查询 |
| `FileManagerHelper.java` | 文件管理辅助工具 |
| `NetworkManager.java` | 网络通信工具 |
| `SceneUsageExamples.java` | 6个典型应用场景示例 |

### 配置文件

| 文件 | 说明 |
|------|------|
| `AndroidManifest.xml` | 应用清单，声明权限和组件 |
| `build.gradle` | 构建配置，定义依赖 |
| `activity_main.xml` | 主界面布局 |
| `strings.xml` | 字符串资源 |
| `colors.xml` | 颜色资源 |
| `themes.xml` | 主题定义 |

---

## 后续开发

### 常见修改

#### 1. 集成真实的Pico电池API

在 `PicoBatteryManager.java` 中修改：

```java
public int getHeadBattery() {
    try {
        // 替换为真实的Pico SDK调用
        return PicoDeviceManager.getHeadsetBattery();
    } catch (Exception e) {
        Log.e(TAG, "Failed to get battery", e);
        return -1;
    }
}
```

#### 2. 修改电量监控间隔

在 `VRBackgroundService.java` 中修改：

```java
// 改为30秒更新一次
private static final int BATTERY_UPDATE_INTERVAL = 30000;
```

#### 3. 添加自定义场景

参考 `SceneUsageExamples.java` 中的场景示例，添加你自己的应用场景。

#### 4. 修改UI界面

编辑 `activity_main.xml` 布局文件，自定义界面样式。

#### 5. 添加网络功能

使用 `NetworkManager.java` 与远程服务器通信：

```java
NetworkManager networkManager = new NetworkManager(context);
networkManager.getAsync("https://api.example.com/status", new NetworkManager.NetworkCallback() {
    @Override
    public void onSuccess(String response) {
        Log.d(TAG, "Response: " + response);
    }
    
    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, "Request failed", e);
    }
});
```

### 调试技巧

```bash
# 查看实时日志
adb logcat | grep "VRBackground"

# 查看特定类的日志
adb logcat -s "PicoBatteryManager"

# 保存日志到文件
adb logcat > logcat.txt

# 清除应用数据
adb shell pm clear com.pico.vrbg

# 查看应用进程
adb shell ps | grep com.pico.vrbg

# 强制停止应用
adb shell am force-stop com.pico.vrbg
```

### 性能优化建议

1. **减少后台资源消耗**
   - 增加电量监控间隔
   - 使用WorkManager替代定时任务
   - 缓存数据避免重复查询

2. **优化APK大小**
   - 启用ProGuard混淆和优化
   - 移除不使用的资源
   - 使用库的轻量级版本

3. **改善用户体验**
   - 使用Material Design组件
   - 实现流畅的动画过渡
   - 提供清晰的反馈信息

---

## 项目结构一览

```
vrbackground/
├── README.md                          ← 完整文档
├── QUICKSTART.md                      ← 本文件
├── settings.gradle                    ← Gradle设置
├── build.gradle                       ← 根构建配置
│
└── app/
    ├── build.gradle                   ← 应用构建配置
    ├── proguard-rules.pro             ← ProGuard混淆规则
    │
    └── src/main/
        ├── AndroidManifest.xml        ← 应用清单
        │
        ├── java/com/pico/vrbg/
        │   ├── MainActivity.java       ← 主活动
        │   │
        │   ├── service/
        │   │   ├── VRBackgroundService.java
        │   │   └── BootReceiver.java
        │   │
        │   ├── manager/
        │   │   ├── DeviceControlManager.java
        │   │   └── PicoBatteryManager.java
        │   │
        │   ├── provider/
        │   │   └── VRControlProvider.java
        │   │
        │   ├── util/
        │   │   ├── FileManagerHelper.java
        │   │   └── NetworkManager.java
        │   │
        │   └── scenario/
        │       └── SceneUsageExamples.java
        │
        └── res/
            ├── layout/
            │   └── activity_main.xml
            ├── values/
            │   ├── strings.xml
            │   ├── colors.xml
            │   └── themes.xml
            └── xml/
                ├── backup_rules.xml
                └── data_extraction_rules.xml
```

---

## 下一步

1. ✅ 根据需要修改代码
2. ✅ 测试功能
3. ✅ 构建Release APK
4. ✅ 上传到Pico应用商店或企业平台
5. ✅ 在真实设备上验证

---

**享受开发！** 🚀

