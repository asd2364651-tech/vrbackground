# 使用本地 Pico SDK AAR 文件

本目录用于存放 Pico Android SDK 的 AAR 和 JAR 文件。

## 从何处获取 Pico SDK

### 方案 1: 从官方开发者中心下载 (推荐)
1. 访问 https://developer.pico-interactive.com/downloads
2. 登录您的开发者账号
3. 下载 **PICO Android SDK** (最新版本推荐)
4. 解压后找到以下文件：
   - `frame-xxx.aar` - Pico VR Framework
   - `service-xxx.aar` - Pico Services (可选)
   - 其他支持库

### 方案 2: 从 Maven 中央仓库使用缓存
如果已经通过 Maven 下载过 Pico SDK，从本地缓存复制：
```bash
# macOS/Linux
cp ~/.m2/repository/com/picovr/app/frame/*/frame-*.aar ./

# Windows
copy %USERPROFILE%\.m2\repository\com\picovr\app\frame\*\frame-*.aar .
```

## 安装 Pico SDK AAR

### 步骤 1: 放置 AAR 文件
将下载的 AAR 文件复制到此目录 (`app/libs/`)：
```
app/libs/
├── frame-2.4.18.aar          # 主要 Pico VR Framework
├── service-2.4.18.aar         # (可选) Pico Services
└── README.md                   # 本文件
```

### 步骤 2: 验证 build.gradle 配置
`app/build.gradle` 已配置自动引入本地 libs:
```gradle
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar', '*.jar'])
}
```

### 步骤 3: 清除缓存并构建
```bash
rm -rf .gradle build app/build
./gradlew clean assembleDebug
```

### 步骤 4: 验证集成成功
```bash
# 检查 APK 中是否包含 Pico 类
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -i picovr | head -5
```

**预期输出示例：**
```
    50  02-08-2025 12:00   com/picovr/app/framework/v2/api/ElectricityService.class
    45  02-08-2025 12:00   com/picovr/app/framework/v2/api/EnterpriseService.class
    ...
```

## Pico SDK 文件大小参考

| 文件名 | 大小 | 说明 |
|--------|------|------|
| frame-2.4.18.aar | ~1.2 MB | 主要 VR Framework |
| service-2.4.18.aar | ~0.8 MB | 系统服务 API |
| 其他支持库 | 可变 | 依赖库 |

**最终 APK 大小 (含 Pico SDK):** ~8-10 MB

## 使用 Pico SDK API

将 AAR 文件放入此目录后，您可以在代码中使用 Pico API：

```java
import com.picovr.app.framework.v2.api.ElectricityService;
import com.picovr.app.framework.v2.api.SystemService;

// 示例: 获取电池电量
ElectricityService service = (ElectricityService) 
    ServiceManager.getInstance().getService(ElectricityService.class.getName());
ElectricityServiceAPI api = service.getAPI();
float batteryLevel = api.getDeviceBattery();
```

完整 API 文档见：[PICO_SDK_LATEST.md](../PICO_SDK_LATEST.md)

## 常见问题

### Q: 构建时仍然无法找到 AAR 文件？
**A:** 确保：
1. AAR 文件位置正确：`app/libs/` 目录下
2. 文件名包含 `.aar` 扩展名
3. 执行 `./gradlew clean` 清除构建缓存
4. 检查 `app/build.gradle` 中 libs 配置是否存在

### Q: 是否可以同时使用 Maven 和本地 AAR？
**A:** 可以。在 `app/build.gradle` 中同时配置两种方案，Gradle 会优先使用本地 AAR。

### Q: 下载的 SDK 包含多个 AAR，我该放入哪些？
**A:** 至少放入以下关键文件：
- `frame-xxx.aar` (必需) - 主 VR Framework
- `service-xxx.aar` (可选) - 如果需要使用设备服务 API

## 获取帮助

- Pico 开发者文档: https://developer.pico-interactive.com/docs
- 本项目集成指南: [PICO_SDK_INTEGRATION_PLAN.md](../PICO_SDK_INTEGRATION_PLAN.md)
- SDK 脚本工具: `manage-pico-sdk.sh`

---
**最后更新:** 2025-02-08
