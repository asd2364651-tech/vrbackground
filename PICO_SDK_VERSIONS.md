# Pico SDK 版本查询工具

## 🔍 如何查找最新的 Pico SDK 版本

### 方法 1: 查看官方 Maven 仓库 (推荐)

访问 Pico SDK Maven 仓库:
```
https://xrapi.picovr.com/maven/com/picovr/app/frame/
```

### 方法 2: 使用 Gradle 尝试构建并查看错误信息

在 `build.gradle` 中添加版本，然后运行 `./gradlew build` 会显示可用版本

### 已知的 Pico SDK 版本:

- **2.4.18** - 旧版本 (原项目使用)
- **2.5.0** 及以上 - 较新版本
- **3.x** - 最新系列版本

## 📝 快速添加 SDK 的步骤

1. 确认您希望使用的版本 (例如: 2.5.0 或更新)
2. 在 `app/build.gradle` 中启用依赖
3. 运行 `./gradlew build` 测试
4. 若版本不存在，Maven 仓库会返回 404，然后查询新版本

---

**注意**: Pico SDK 版本可能需要根据您的 Pico VR 设备系统版本而定。
