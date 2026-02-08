# VR Background Service 项目说明

## 📦 项目文件清单

### 根目录文件
- ✅ `README.md` - 完整项目文档
- ✅ `QUICKSTART.md` - 快速开始指南
- ✅ `settings.gradle` - Gradle项目配置
- ✅ `build.gradle` - 根构建文件
- ✅ `gradle.properties` - Gradle属性配置
- ✅ `build_helper.py` - Python构建助手脚本
- ✅ `build_release.sh` - macOS/Linux构建脚本
- ✅ `build_release.bat` - Windows构建脚本

### App模块 (`app/`)
- ✅ `build.gradle` - 应用构建配置
- ✅ `proguard-rules.pro` - ProGuard混淆规则

### 源代码 (`app/src/main/java/com/pico/vrbg/`)

#### 核心组件
- ✅ `MainActivity.java` - 应用主活动，提供UI控制界面
- ✅ `service/VRBackgroundService.java` - 后台服务，处理电量监控
- ✅ `service/BootReceiver.java` - 开机启动接收器

#### 管理器
- ✅ `manager/DeviceControlManager.java` - 设备控制（重启、关机、打开文件等）
- ✅ `manager/PicoBatteryManager.java` - Pico电池信息获取

#### 服务层
- ✅ `provider/VRControlProvider.java` - ContentProvider，供其他应用查询数据

#### 工具类
- ✅ `util/FileManagerHelper.java` - 文件管理辅助工具
- ✅ `util/NetworkManager.java` - 网络通信工具

#### 场景示例
- ✅ `scenario/SceneUsageExamples.java` - 6个典型应用场景示例

### 资源文件 (`app/src/main/res/`)

#### 布局
- ✅ `layout/activity_main.xml` - 主活动界面布局

#### 值资源
- ✅ `values/strings.xml` - 字符串资源（中英双语）
- ✅ `values/colors.xml` - 颜色资源定义
- ✅ `values/themes.xml` - 应用主题定义

#### XML配置
- ✅ `xml/backup_rules.xml` - 备份规则
- ✅ `xml/data_extraction_rules.xml` - 数据提取规则

### 配置清单
- ✅ `AndroidManifest.xml` - 应用清单，包含：
  - 权限声明
  - 活动、服务、广播接收器、内容提供者定义

---

## 🚀 快速开始

### 方式1: 使用构建脚本（推荐）

**macOS/Linux:**
```bash
cd /Volumes/lexar-2T/cursorworkspace/vrbackground
chmod +x build_release.sh
./build_release.sh
```

**Windows:**
```batch
cd C:\path\to\vrbackground
build_release.bat
```

**Python:**
```bash
python3 build_helper.py release
```

### 方式2: 使用Gradle命令

```bash
cd /Volumes/lexar-2T/cursorworkspace/vrbackground

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease

# 清理构建
./gradlew clean
```

### 方式3: 使用Android Studio

1. 打开 Android Studio
2. 选择 File → Open
3. 选择项目目录
4. 点击 Build → Build Bundle(s)/APK(s) → Build APK(s)

---

## 📱 安装到设备

```bash
# 连接Pico头显
adb devices

# 安装Release版本
adb install -r app/build/outputs/apk/release/app-release.apk

# 启动应用
adb shell am start -n com.pico.vrbg/.MainActivity

# 查看日志
adb logcat | grep VRBackground
```

---

## 🔑 主要功能

### 1. 设备电量监控
- ✅ 头显电量查询
- ✅ 左右手柄电量查询
- ✅ 充电状态检查
- ✅ 低电量自动警告

### 2. 设备控制
- ✅ 打开文件（支持多种格式）
- ✅ 重启设备
- ✅ 关闭设备
- ✅ 唤醒设备
- ✅ 锁屏
- ✅ 亮度调节

### 3. 后台服务
- ✅ 自动启动
- ✅ 定期监控
- ✅ 进程间通信
- ✅ ContentProvider接口

### 4. 文件管理
- ✅ 列表目录
- ✅ 格式过滤
- ✅ 文件操作
- ✅ 目录创建

---

## 🎯 6大使用场景

1. **虚拟演讲厅** - 自动播放演讲，监控电量
2. **虚拟博物馆** - 浏览多媒体馆藏
3. **工业VR培训** - 课程管理，电量检查
4. **VR远程协作** - 接收远程命令，设备状态上报
5. **VR健身应用** - 监控运动时长，手柄电量警告
6. **VR游戏娱乐** - 管理游戏启动，性能监控

详见 `SceneUsageExamples.java`

---

## ✨ 技术亮点

- ✅ 完整的Android应用框架
- ✅ 后台服务和定时任务
- ✅ ContentProvider进程通信
- ✅ 权限管理和动态权限申请
- ✅ Pico SDK集成
- ✅ 多线程处理
- ✅ 异常处理和日志记录
- ✅ ProGuard代码混淆
- ✅ 模块化和可扩展设计

---

## 📋 所需权限

| 权限 | 用途 |
|------|------|
| INTERNET | 网络通信 |
| ACCESS_NETWORK_STATE | 网络检查 |
| WAKE_LOCK | 唤醒设备 |
| REBOOT | 重启/关闭 |
| READ_EXTERNAL_STORAGE | 读文件 |
| WRITE_EXTERNAL_STORAGE | 写文件 |
| MANAGE_EXTERNAL_STORAGE | 管理文件 |
| com.picovr.permission.PICO_SYSTEM | Pico系统权限 |
| RECEIVE_BOOT_COMPLETED | 开机自启 |

---

## 🔧 集成Pico SDK

在 `app/build.gradle` 中已配置：
```gradle
implementation 'com.picovr.app:frame:2.4.18'
```

需要在以下类中集成真实API调用：
- `PicoBatteryManager.java` - 电池信息获取
- `DeviceControlManager.java` - 设备控制

---

## 📊 构建输出

**Debug版本:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Release版本:**
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🐛 常见问题

**Q: 如何修改包名？**
A: 修改 AndroidManifest.xml 中的 package 属性和 build.gradle 中的 applicationId

**Q: 如何更改应用名称？**
A: 修改 `res/values/strings.xml` 中的 `app_name` 字符串

**Q: 如何集成Pico的真实电池API？**
A: 修改 `PicoBatteryManager.java` 中的方法，调用Pico SDK的实际接口

**Q: 如何测试后台服务？**
A: 使用命令 `adb logcat | grep VRBackgroundService` 查看日志

---

## 📚 相关文档

- [完整项目文档 - README.md](./README.md)
- [快速开始指南 - QUICKSTART.md](./QUICKSTART.md)
- [Pico官方SDK文档](https://developer.pico-interactive.com/)
- [Android官方文档](https://developer.android.com/)

---

## 📝 版本信息

- **版本**: 1.0.0
- **最后更新**: 2024年
- **SDK版本**: Android 34
- **最低API**: API 24 (Android 7.0)
- **Gradle**: 8.1.0

---

**祝你开发愉快！** 🎉

