# VR Background Service APK
## Pico VR头显后台控制服务

一个完整的Android VR应用，为Pico VR头显提供后台控制、电量监控、文件管理等功能。

---

## 📋 功能特性

### 1. **VR播控系统集成**
- 通过VR播控系统控制设备打开指定的内容文件
- 支持多种文件格式 (视频、音频、图片、应用)
- 自动识别文件类型并使用合适的应用打开

### 2. **Pico API 电量监控**
- 获取头显电量百分比
- 获取左、右手柄电量百分比
- 实时监控充电状态
- 自动报警低电量警告

### 3. **设备控制功能**
- 🔄 **重启设备** - 安全重启VR头显
- 🔌 **关闭设备** - 完全关闭设备电源
- ⏰ **唤醒设备** - 唤醒睡眠中的设备
- 🔒 **锁屏设备** - 保护隐私
- 🌞 **调节亮度** - 自适应屏幕亮度

### 4. **后台服务**
- 自动启动的后台服务
- 开机自启动支持
- 定期电量监控 (60秒更新一次)
- 与其他应用通过ContentProvider通信

---

## 🎯 使用场景

### 场景1: **虚拟演讲厅**
```
自动播放演讲视频 → 监控电量 → 低电量提醒休息 → 必要时自动锁屏
```
**应用**: 教育培训、学术演讲、线上课程

### 场景2: **虚拟资料库/博物馆**
```
浏览馆藏媒体文件 → 顺序播放 → 支持多格式展示
```
**应用**: 数字博物馆、文化遗产展示、艺术作品展览

### 场景3: **工业VR培训**
```
启动培训应用 → 定期检查设备状态 → 手柄电量警告 → 培训后自动重启更新配置
```
**应用**: 工业培训、医疗模拟、专业技能培养

### 场景4: **VR远程协作**
```
监控本地电量 → 与服务器保持通信 → 接收远程命令 → 执行控制操作
```
**应用**: 远程会议、团队协作、远程支持

### 场景5: **VR健身应用**
```
启动健身应用 → 定期检查手柄电量 → 运动中精度警告 → 长时间后建议休息
```
**应用**: 虚拟运动、健身游戏、健康管理

### 场景6: **VR游戏娱乐**
```
列出可用游戏 → 启动选中游戏 → 监控设备温度 → 游戏结束后返回主菜单
```
**应用**: 家庭娱乐、游戏竞技、休闲娱乐

---

## 📦 项目结构

```
vrbackground/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/pico/vrbg/
│   │       │   ├── MainActivity.java              # 主活动
│   │       │   ├── manager/
│   │       │   │   ├── DeviceControlManager.java  # 设备控制
│   │       │   │   └── PicoBatteryManager.java    # 电量管理
│   │       │   ├── service/
│   │       │   │   ├── VRBackgroundService.java   # 后台服务
│   │       │   │   └── BootReceiver.java          # 开机自启
│   │       │   ├── provider/
│   │       │   │   └── VRControlProvider.java     # ContentProvider
│   │       │   ├── util/
│   │       │   │   ├── FileManagerHelper.java     # 文件管理
│   │       │   │   └── NetworkManager.java        # 网络通信
│   │       │   └── scenario/
│   │       │       └── SceneUsageExamples.java    # 场景示例
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml
│   │       │   └── values/
│   │       │       ├── strings.xml
│   │       │       ├── colors.xml
│   │       │       └── themes.xml
│   │       └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
└── settings.gradle
```

---

## 🛠️ 开发环境要求

- **Android Studio**: 2024.1 或更新版本
- **Gradle**: 8.1.0+
- **Android SDK**: API 24+ (建议 API 34)
- **Java/Kotlin**: JDK 11+
- **Pico SDK**: 集成最新版本

---

## 🚀 构建和打包

### 方法1: 使用Android Studio

1. **打开项目**
   ```bash
   # 在Android Studio中打开项目目录
   File → Open → /path/to/vrbackground
   ```

2. **选择构建变体**
   - 选择 "release" 构建类型以获得最优性能
   - 确保签名密钥已配置

3. **构建APK**
   ```
   Build → Build Bundle(s)/APK(s) → Build APK(s)
   ```

4. **查找输出APK**
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

### 方法2: 使用命令行Gradle

```bash
# 进入项目目录
cd /path/to/vrbackground

# 构建debug APK
./gradlew assembleDebug

# 构建release APK (推荐)
./gradlew assembleRelease

# 同时构建APK并安装到设备
./gradlew installRelease
```

### 方法3: 使用GradleW包装器 (跨平台)

```bash
# macOS/Linux
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

---

## 📱 安装到设备

### 准备工作
1. 启用Pico头显的开发者模式
2. 使用USB数据线连接头显和电脑
3. 确保已安装ADB工具

### 安装步骤

```bash
# 查看连接的设备
adb devices

# 安装APK
adb install app/build/outputs/apk/release/app-release.apk

# 如果已安装旧版本，强制覆盖安装
adb install -r app/build/outputs/apk/release/app-release.apk

# 启动应用
adb shell am start -n com.pico.vrbg/.MainActivity

# 查看日志
adb logcat | grep "VRBackground"
```

---

## 🔑 配置和签名

### 创建签名密钥

```bash
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

### 配置签名信息

在 `app/build.gradle` 中添加：
```gradle
signingConfigs {
    release {
        storeFile file("path/to/my-release-key.jks")
        storePassword "your_password"
        keyAlias "my-key-alias"
        keyPassword "your_password"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
    }
}
```

---

## 📡 集成Pico SDK

### 添加Pico依赖

在 `app/build.gradle` 中：
```gradle
dependencies {
    // Pico VR SDK
    implementation 'com.picovr.app:frame:2.4.18'
}
```

### 获取电池信息 (Pico API示例)

```java
// 在PicoBatteryManager中实现实际的Pico API调用
public int getHeadBattery() {
    try {
        // 使用Pico SDK获取头显电量
        return PicoDeviceManager.getHeadsetBattery();
    } catch (Exception e) {
        Log.e(TAG, "Failed to get battery", e);
        return -1;
    }
}
```

### 设备控制 (Pico API示例)

```java
// 重启设备
public void restartDevice() {
    try {
        Runtime.getRuntime().exec("su -c reboot");
    } catch (Exception e) {
        Log.e(TAG, "Failed to restart", e);
    }
}
```

---

## 📋 权限说明

应用需要以下权限 (在 `AndroidManifest.xml` 中声明):

| 权限 | 用途 |
|------|------|
| `INTERNET` | 网络通信 |
| `ACCESS_NETWORK_STATE` | 检查网络连接 |
| `WAKE_LOCK` | 唤醒设备 |
| `REBOOT` | 重启/关闭设备 |
| `READ_EXTERNAL_STORAGE` | 读取文件 |
| `WRITE_EXTERNAL_STORAGE` | 写入文件 |
| `MANAGE_EXTERNAL_STORAGE` | 管理所有文件 |
| `com.picovr.permission.PICO_SYSTEM` | Pico系统权限 |
| `RECEIVE_BOOT_COMPLETED` | 开机自启 |

---

## 🔧 高级配置

### 后台服务监控间隔

在 `VRBackgroundService.java` 中修改：
```java
private static final int BATTERY_UPDATE_INTERVAL = 60000; // 毫秒
```

### 日志输出

查看实时日志：
```bash
adb logcat -s "VRBackground"
```

过滤特定日志：
```bash
adb logcat | grep "PicoBatteryManager"
```

---

## 🐛 调试和故障排除

### 常见问题

1. **APK无法安装**
   ```bash
   # 检查是否有同名应用
   adb uninstall com.pico.vrbg
   # 重新安装
   adb install app/build/outputs/apk/release/app-release.apk
   ```

2. **后台服务不启动**
   - 检查设备权限
   - 查看日志: `adb logcat | grep VRBackgroundService`

3. **电池信息获取失败**
   - 确保已正确集成Pico SDK
   - 验证Pico API调用方式

4. **设备控制命令无效**
   - 需要root权限执行重启/关闭命令
   - 检查设备是否已启用开发者模式

### 调试技巧

```bash
# 启用详细日志
adb shell setprop log.tag.VRBackgroundService DEBUG
adb shell setprop log.tag.DeviceControlManager DEBUG

# 实时查看日志输出
adb logcat -f /sdcard/logcat.txt &

# 检查应用是否运行
adb shell ps | grep com.pico.vrbg

# 清除应用数据
adb shell pm clear com.pico.vrbg
```

---

## 📊 性能优化

1. **减少电池消耗**
   - 增加电量监控间隔
   - 使用WorkManager替代定时任务
   - 优化后台服务唤醒频率

2. **改善用户体验**
   - 使用ContentProvider缓存数据
   - 实现文件预加载
   - 异步加载媒体文件

3. **代码混淆**
   - 已启用ProGuard混淆
   - 保护Pico SDK类: `-keep class com.pico.** { *; }`

---

## 🤝 与其他应用通信

### 通过ContentProvider查询电池信息

```java
// 其他应用可以这样查询电量
ContentResolver resolver = getContentResolver();
Cursor cursor = resolver.query(
    Uri.parse("content://com.pico.vrbg.provider/battery"),
    null, null, null, null
);

if (cursor != null && cursor.moveToFirst()) {
    int headBattery = cursor.getInt(cursor.getColumnIndex("head_battery"));
    cursor.close();
}
```

### 通过Intent发送命令

```java
// 控制服务执行命令
Intent intent = new Intent();
intent.setComponent(new ComponentName(
    "com.pico.vrbg",
    "com.pico.vrbg.service.VRBackgroundService"
));
intent.setAction("com.pico.vrbg.COMMAND");
intent.putExtra("command", "open_file");
intent.putExtra("param", "/sdcard/video.mp4");
startService(intent);
```

---

## 📚 API文档

### DeviceControlManager

```java
// 打开文件
void openFile(String filePath);

// 重启设备
void restartDevice();

// 关闭设备
void shutdownDevice();

// 唤醒设备
void wakeUpDevice();

// 锁屏
void lockDevice();

// 设置亮度 (0-255)
void setBrightness(int brightness);
```

### PicoBatteryManager

```java
// 获取头显电量 (0-100)
int getHeadBattery();

// 获取左手柄电量 (0-100)
int getLeftControllerBattery();

// 获取右手柄电量 (0-100)
int getRightControllerBattery();

// 检查是否在充电
boolean isHeadsetCharging();

// 一次获取所有信息
BatteryInfo getAllBatteryInfo();
```

### FileManagerHelper

```java
// 列出目录下的所有文件
List<File> listFiles(String dirPath);

// 按扩展名过滤文件
List<File> listFilesByExtension(String dirPath, String... extensions);

// 删除文件
boolean deleteFile(String filePath);

// 检查文件是否存在
boolean fileExists(String filePath);

// 获取文件大小
long getFileSize(String filePath);

// 创建目录
boolean createDirectory(String dirPath);
```

---

## 📦 发布和部署

### 发布到应用商店

1. 生成签名的release APK
2. 测试APK (至少在5台设备上)
3. 准备应用描述、截图、权限说明
4. 上传到Pico应用商店或Google Play Store

### 企业部署

1. 使用MDM解决方案进行设备管理
2. 通过企业门户批量分发APK
3. 配置设备策略和应用白名单
4. 监控设备状态和应用性能

---

## 📝 更新日志

- **v1.0.0** (2024年初)
  - 完整的后台服务框架
  - 电池监控功能
  - 设备控制功能
  - 文件管理功能
  - 六个典型场景示例
  - ContentProvider接口
  - 网络通信模块

---

## 📄 许可证

MIT License - 可自由使用、修改和分发

---

## 💬 支持和反馈

如有问题或建议，请：
1. 查看日志输出（adb logcat）
2. 检查权限和SDK版本
3. 测试在真实Pico设备上运行
4. 参考Pico官方文档和SDK示例

---

## 🔒 安全性考虑

1. **权限管理**
   - 仅请求必要的权限
   - 在运行时检查权限

2. **数据保护**
   - 敏感信息加密存储
   - 安全的进程间通信

3. **设备保护**
   - 验证远程命令来源
   - 设备控制操作需确认

---

**祝您使用愉快！** 🎮🥽

