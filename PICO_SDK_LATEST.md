# Pico Android SDK 集成指南 (最新版本)

## 📦 获取最新 Pico SDK

### 方法 1: 通过 Maven 仓库 (推荐)

#### 官方 Maven 仓库
```
名称: Pico Maven Repository
URL: https://xrapi.picovr.com/maven
```

#### 查询可用版本

访问: `https://xrapi.picovr.com/maven/com/picovr/app/frame/`

您会看到可用的所有版本，例如：
- 2.4.18 (已知可用)
- 2.4.x 系列
- 其他版本 (取决于当前发布)

#### 添加依赖到 app/build.gradle

```gradle
dependencies {
    // Pico VR SDK - 使用最新可用版本
    implementation 'com.picovr.app:frame:2.4.18'
    // 如果有更新版本，替换版本号
    // implementation 'com.picovr.app:frame:X.Y.Z'
}
```

### 方法 2: 从官方开发者中心下载

1. **访问 Pico 开发者中心**
   - https://developer.pico-interactive.com/

2. **登录账户**
   - 使用您的 Pico 开发者账号

3. **下载 SDK**
   - 导航到 Resources → SDK Download
   - 选择 Android SDK
   - 下载最新版本的 ZIP 文件

4. **提取 AAR 文件**
   ```bash
   unzip pico-android-sdk-*.zip
   ```

### 方法 3: 手动集成本地 AAR 文件

#### 步骤 1: 创建本地库目录

```bash
cd /Volumes/lexar-2T/cursorworkspace/vrbackground
mkdir -p app/libs
```

#### 步骤 2: 复制 AAR 文件

将从 Pico SDK 中获取的 `frame-release.aar` 复制到 `app/libs/`:

```bash
cp /path/to/pico-sdk/frame-release.aar app/libs/
```

#### 步骤 3: 配置 build.gradle

在 `app/build.gradle` 中配置本地库:

```gradle
dependencies {
    // 本地 AAR 库
    implementation files('libs/frame-release.aar')
    
    // 或使用通配符引用所有 AAR
    implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

#### 步骤 4: 构建项目

```bash
./gradlew clean build
```

---

## 🔧 启用 Pico SDK 依赖

### 当前项目状态

Pico SDK 依赖目前被注释掉（以便无 SDK 时正常编译）。

**app/build.gradle 中的当前配置:**
```gradle
// Pico VR SDK
// 最新版本可在 https://xrapi.picovr.com/maven 查看
// implementation 'com.picovr.app:frame:2.4.18'
```

### 启用 SDK

#### 选项 A: 使用 Maven 依赖

取消注释并更新版本号:

```gradle
// 取消下方注释，使用最新版本
implementation 'com.picovr.app:frame:2.4.18'
```

#### 选项 B: 使用本地 AAR

替换为:

```gradle
// 使用本地 AAR 文件
implementation files('libs/frame-release.aar')
```

### 测试构建

启用依赖后，运行构建测试:

```bash
cd /Volumes/lexar-2T/cursorworkspace/vrbackground
./gradlew clean build
```

预期输出:
```
BUILD SUCCESSFUL in Xs
```

---

## 📋 Pico SDK 主要类

启用 SDK 后，您可以使用以下主要类:

### 1. ElectricityService (电池管理)
```java
// 获取头显电量
int headBattery = ElectricityService.getHeadBattery(context);

// 获取手柄电量
int leftControllerBattery = ElectricityService.getLeftControllerBattery(context);
int rightControllerBattery = ElectricityService.getRightControllerBattery(context);
```

### 2. SystemService (系统控制)
```java
// 重启设备
SystemService.reboot(context);

// 关闭设备
SystemService.shutdown(context);

// 调节亮度
SystemService.setBrightness(context, brightness);
```

### 3. IntentService (应用启动)
```java
// 打开指定的内容文件
Intent intent = IntentService.createOpenFileIntent(filePath);
startActivity(intent);
```

---

## 🐛 常见问题

### Q1: Maven 仓库无法访问

**问题**: `Could not resolve com.picovr.app:frame:X.X.X`

**解决**:
1. 检查网络连接
2. 尝试手动下载 AAR 文件

```bash
# 验证仓库可访问性
curl -I https://xrapi.picovr.com/maven/
```

### Q2: TLS 握手失败

**问题**: `The server may not support the client's requested TLS protocol`

**解决**: 在 `gradle.properties` 中配置:
```properties
org.gradle.jvmargs=-Dhttps.protocols=TLSv1.2,TLSv1.3
```

### Q3: 编译错误

**问题**: `Multiple dex files define [Lpico/...`

**解决**: 清除Gradle缓存并重新构建
```bash
./gradlew clean build --no-cache
```

---

## 📚 官方资源

- **开发者中心**: https://developer.pico-interactive.com/
- **SDK 文档**: https://developer.pico-interactive.com/document
- **Maven 仓库**: https://xrapi.picovr.com/maven
- **API 参考**: 下载 SDK 包中的 javadoc

---

## ✅ 验证集成

集成完毕后，验证以下内容:

```bash
# 1. 检查编译成功
./gradlew clean build

# 2. 检查生成的 APK
ls -lh app/build/outputs/apk/debug/

# 3. 验证 classes.dex 中包含 Pico 类
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -i pico
```

---

**状态**: ✅ 项目已准备集成任何版本的 Pico SDK
