# Android Studio 与 SDK 配置指南

## ✅ 已完成的步骤

### 1. Android Studio 安装
- ✓ Android Studio 2025.2 已通过 Homebrew 安装
- ✓ 运行位置: `/Applications/Android Studio.app`
- ✓ 命令行工具: `studio` 命令可用

### 2. Java 版本优化
- ✓ Java 版本: 17 → **21** (与系统匹配)
- ✓ Gradle 版本: 8.7 → **8.10** (支持 Java 21)
- ✓ Android 插件: 8.1.0 → **8.7.0** (最新稳定版)

### 3. SDK 目录结构初始化
- ✓ SDK 路径: `/Users/yangwenbo/Library/Android/sdk`
- ✓ 创建了基本目录结构
- ✓ local.properties 已配置

### 4. Gradle 测试
- ✓ Gradle clean 命令执行成功
- ✓ 项目可被 Gradle 识别

---

## 📋 后续步骤（完整 SDK 安装）

完整的 Android SDK 安装可通过以下方式之一完成：

### 方式 1: 使用 Android Studio GUI（推荐）

1. **打开 Android Studio**
   ```bash
   open -a "Android Studio"
   ```

2. **进入 SDK Manager**
   - 菜单: Tools → SDK Manager
   - 或在欢迎屏幕上选择 "More Actions" → "SDK Manager"

3. **安装所需组件**
   - ☐ SDKs → Android 14 (API 34) - **必需**
   - ☐ SDK Tools → Android SDK Build-Tools 34.x
   - ☐ SDK Tools → Android Emulator（可选）
   - ☐ SDK Tools → Android SDK Platform-Tools

4. **完成安装**
   - 点击 "Apply" 按钮
   - 同意许可证条款
   - 等待下载和安装完成

### 方式 2: 使用命令行（高级用户）

如果您已有 `sdkmanager` 工具，可以运行：

```bash
sdkmanager "platforms;android-34" \
           "build-tools;34.0.0" \
           "system-images;android-34;google_apis;arm64-v8a"
```

---

## 🔧 验证配置

安装 SDK 组件后，运行以下命令验证完整构建：

```bash
cd /Volumes/lexar-2T/cursorworkspace/vrbackground
./gradlew build
```

如果看到 `BUILD SUCCESSFUL`，说明所有配置都完成了！

---

## 📁 重要文件

- **项目根目录**: `/Volumes/lexar-2T/cursorworkspace/vrbackground`
- **local.properties**: 已配置 SDK 路径
- **build.gradle**: 已更新至最新兼容版本
- **gradlew**: 已更新为 Gradle 8.10
- **设置脚本**: `setup-android-sdk.sh` (可选)

---

## ⚠️ 常见问题

### Q: 仍然看到编译错误？
A: 确保 Android Studio 已完成 SDK 安装向导。查看 SDK Manager 中 Android 34 (API 34) 是否已安装。

### Q: 如何重新启动 SDK 安装？
A: 运行:
```bash
rm -rf ~/Library/Android/sdk
open -a "Android Studio"
```
然后执行 SDK Manager 中的安装步骤。

### Q: 需要更改 Java 版本？
A: 修改 `build.gradle` 中的:
- `JavaLanguageVersion.of(21)` → 更改版本号
- `JavaVersion.VERSION_21` (两处位置)

---

## 📞 后续支持

任何问题请参考：
- Android 官方文档: https://developer.android.com/studio
- Gradle 文档: https://docs.gradle.org
- 项目文件: 查看项目根目录的 `README.md`
