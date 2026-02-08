# Pico SDK 集成 - 快速开始指南

## 📋 当前项目状态

| 项目 | 状态 | 详情 |
|------|------|------|
| 🏗️ 构建系统 | ✅ 就绪 | Gradle 8.10, Android SDK 34, Java 21 |
| 📦 项目包 | ✅ 就绪 | Debug APK: 6.6 MB (无 SDK) |
| 🔗 Git 仓库 | ✅ 就绪 | GitHub: https://github.com/asd2364651-tech/vrbackground |
| Pico SDK | ⏳ 需要 | 本地 AAR 方案配置完成，等待 SDK 文件 |

---

## 🚀 快速集成步骤 (5 分钟)

### 步骤 1️⃣ 下载 Pico SDK
访问: https://developer.pico-interactive.com/downloads

**获取文件:**
- ✅ PICO Android SDK 最新版本 (推荐 2.5.0+)
- ⬇️ 解压后找到 `frame-xxx.aar` 和 `service-xxx.aar`

### 步骤 2️⃣ 放置 AAR 文件
```bash
# 进入项目目录
cd /Volumes/lexar-2T/cursorworkspace/vrbackground

# 复制 AAR 文件到本地库目录
cp ~/Downloads/pico-sdk-2.5.0/frame-2.5.0.aar app/libs/
cp ~/Downloads/pico-sdk-2.5.0/service-2.5.0.aar app/libs/  # (可选)

# 验证
ls -lh app/libs/
```

**预期输出:**
```
-rw-r--r--  frame-2.5.0.aar (1.2 MB)
-rw-r--r--  service-2.5.0.aar (0.8 MB)  # 可选
-rw-r--r--  README.md
```

### 步骤 3️⃣ 构建应用
```bash
# 清除旧构建
rm -rf .gradle build app/build

# 构建新 APK (含 Pico SDK)
./gradlew clean assembleDebug
```

**成功标志:** ✓ BUILD SUCCESSFUL

### 步骤 4️⃣ 验证集成
```bash
# 检查 APK 中的 Pico 类
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep picovr | head -5
```

**预期输出示例:**
```
    50  2025-02-08 12:00   com/picovr/app/framework/v2/api/ElectricityService.class
    45  2025-02-08 12:00   com/picovr/app/framework/v2/api/SystemService.class
    42  2025-02-08 12:00   com/picovr/app/framework/v2/api/EnterpriseService.class
```

---

## 📚 完整文档

| 文档 | 用途 |
|------|------|
| [app/libs/README.md](app/libs/README.md) | 📖 详细 AAR 集成指南 |
| [PICO_SDK_INTEGRATION_PLAN.md](PICO_SDK_INTEGRATION_PLAN.md) | 🎯 集成方案说明 |
| [PICO_SDK_LATEST.md](PICO_SDK_LATEST.md) | 📚 综合 API 参考 |
| [manage-pico-sdk.sh](manage-pico-sdk.sh) | 🛠️ 版本管理脚本 |

---

## 💻 使用 Pico API

一旦 AAR 文件被正确放置并构建成功，您可以在代码中使用 Pico API:

### 示例: 获取设备电池电量
```java
// 在 PicoBatteryManager.java 中
import com.picovr.app.framework.v2.api.ElectricityService;

public class PicoBatteryManager {
    public float getHeadsetBattery() {
        try {
            ElectricityService service = (ElectricityService) 
                ServiceManager.getInstance()
                    .getService(ElectricityService.class.getName());
            
            ElectricityServiceAPI api = service.getAPI();
            return api.getDeviceBattery();  // 0.0f - 1.0f
        } catch (Exception e) {
            Log.e("Pico", "电池查询失败: " + e.getMessage());
            return -1.0f;
        }
    }
}
```

### 示例: 查询系统信息
```java
import com.picovr.app.framework.v2.api.SystemService;

// 重启设备
SystemService systemService = (SystemService) 
    ServiceManager.getInstance()
        .getService(SystemService.class.getName());
systemService.getAPI().reboot();

// 获取设备序列号
String serial = systemService.getAPI().getDeviceSerial();
```

---

## 🆘 常见问题

### Q: 放置 AAR 后仍然无法构建？
**A:** 检查清单:
1. ✓ AAR 文件位于 `app/libs/` 目录
2. ✓ 文件名以 `.aar` 结尾
3. ✓ 执行了 `./gradlew clean`
4. ✓ `app/build.gradle` 包含 `implementation fileTree(dir: 'libs', ...)`

### Q: 是否需要更新 app/build.gradle？
**A:** 不需要。配置已预先设置。添加 AAR 文件后会自动识别。

### Q: 可以使用 Maven 方案吗？
**A:** 不推荐。Pico 的 Maven 仓库存在 TLS 握手问题。本地 AAR 方案更可靠。

### Q: 多个 AAR 文件应该如何处理？
**A:** 全部放入 `app/libs/` 目录即可。`fileTree` 会自动包含所有 AAR 文件。

### Q: 支持离线构建吗?
**A:** 是的。基于本地 AAR 文件，您可以完全离线构建。

---

## 🔍 项目已配置完成的内容

✅ **构建系统**
- Gradle 8.10 with Android Gradle Plugin 8.7.0
- Java 21 兼容性配置
- 正确的 Lint 配置

✅ **应用代码**
- MainActivity: UI 主界面
- PicoBatteryManager: 电池管理 (已准备 Pico API 调用)
- DeviceControlManager: 设备控制 (已准备 Pico API 调用)
- VRBackgroundService: 后台服务
- BootReceiver: 自启动配置

✅ **依赖库**
- AndroidX 框架完整
- Pico SDK 本地集成支持
- OkHttp3 + Gson 网络库
- 所有必需的 Google API

✅ **文档与工具**
- SDK 集成指南
- API 参考文档
- 版本管理脚本
- Gradle 配置优化

---

## 📞 相关资源

| 资源 | 链接 |
|------|------|
| 🌐 Pico 官方网站 | https://www.pico.com |
| 👨‍💻 开发者中心 | https://developer.pico-interactive.com |
| 📥 SDK 下载 | https://developer.pico-interactive.com/downloads |
| 📖 API 文档 | https://developer.pico-interactive.com/docs |
| 🐙 GitHub 仓库 | https://github.com/asd2364651-tech/vrbackground |

---

## ✨ 下一步

1. ✅ **获取 Pico SDK**: 从官方下载 AAR 文件
2. ✅ **放置文件**: 复制到 `app/libs/`
3. ✅ **构建应用**: `./gradlew clean assembleDebug`
4. ✅ **验证集成**: 检查 APK 中的 Pico 类
5. ✅ **实现 API**: 在代码中调用 Pico 功能

**预计完成时间**: 10-15 分钟

---

**准备好了吗? 现在就开始集成 Pico SDK！** 🚀

---
最后更新: 2025-02-08  
项目: VR Background Service  
版本: 1.0.0  
