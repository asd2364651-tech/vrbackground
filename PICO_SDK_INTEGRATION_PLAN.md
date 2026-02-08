# Pico SDK 集成方案

## 当前状态
✓ 项目构建成功 (Debug APK: 6.6 MB)  
✓ 所有依赖配置完毕  
⏳ Pico SDK 待集成 - **AAR 本地方案已就绪**

## 当前推荐方案：使用本地 AAR 文件

### 为什么选择本地 AAR？
- ✓ **避免 Maven 仓库 TLS 握手问题** (Pico Maven 无法访问)
- ✓ **更新速度快** (不依赖网络 Maven 仓库)
- ✓ **支持最新版本** (2.5.0+, 3.x 版本只通过官方渠道)
- ✓ **本地完全控制** (离线构建也可以)

### 前置准备

#### 1. 获取 Pico SDK AAR 文件
从 Pico 官方开发者中心下载：
```
https://developer.pico-interactive.com/downloads
```

下载 **PICO Android SDK** 最新版本，解压后获取 AAR 文件：
- `frame-x.x.x.aar` (主要 Framework)
- `service-x.x.x.aar` (可选, 系统服务)

#### 2. 放置 AAR 文件
将 AAR 文件复制到项目位置：
```bash
# 简单方式：拖放到 VS Code 的 app/libs 目录

# 或使用命令：
cp <your-sdk-path>/frame-2.5.0.aar app/libs/
cp <your-sdk-path>/service-2.5.0.aar app/libs/
```

完整指南见：[app/libs/README.md](app/libs/README.md)

### 实现步骤

#### ✓ 步骤 1: build.gradle 配置 (已完成)
```gradle
// app/build.gradle 已配置
dependencies {
    // 本地 Pico SDK AAR 文件支持
    implementation fileTree(dir: 'libs', include: ['*.aar', '*.jar'])
}
```

#### 步骤 2: 放置 AAR 文件到 app/libs/
```bash
# 方式 A: 自动脚本
./manage-pico-sdk.sh  # 选择选项 3

# 方式 B: 手动复制
cp ~/Downloads/pico-sdk-2.5.0/frame-2.5.0.aar app/libs/
```

#### 步骤 3: 构建验证
```bash
rm -rf .gradle build app/build
./gradlew clean assembleDebug
```

**预期输出：**
```
✓ Build successful
APK 位置: app/build/outputs/apk/debug/app-debug.apk
```

#### 步骤 4: 验证 Pico 类已包含
```bash
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep picovr | head -10
```

### 目前已配置完成
- ✓ `app/build.gradle` 配置本地 libs 支持
- ✓ `app/libs/` 目录已创建
- ✓ [app/libs/README.md](app/libs/README.md) 提供详细指导
- ✓ 项目可以成功构建 (无需 SDK 时)

### 下一步操作
1. **获取 Pico SDK**: 从 https://developer.pico-interactive.com/downloads 下载
2. **解压并提取 AAR**: 获取 frame-xxx.aar 文件
3. **放入 app/libs 目录**: 复制 AAR 文件到 `app/libs/`
4. **构建**: 运行 `./gradlew clean assembleDebug`
5. **验证**: 检查 APK 中是否包含 Pico 类

### 如遇到 Maven 方案问题的原因

之前尝试 Maven 集成时失败原因：
```
错误: TLS 握手失败
位置: https://xrapi.picovr.com/maven/
原因: Pico Maven 仓库不支持 Java 21 的某些 TLS 配置
```

**本地 AAR 方案完全绕过了这个问题。**

---

## 参考资源

| 资源 | 链接 |
|------|------|
| Pico 开发者中心 | https://developer.pico-interactive.com |
| SDK 下载页面 | https://developer.pico-interactive.com/downloads |
| 本地 AAR 指南 | [app/libs/README.md](app/libs/README.md) |
| 综合集成文档 | [PICO_SDK_LATEST.md](PICO_SDK_LATEST.md) |
| 版本管理脚本 | [manage-pico-sdk.sh](manage-pico-sdk.sh) |

---
**最后更新:** 2025-02-08
**选择方案:** 推荐 AAR 本地方案

