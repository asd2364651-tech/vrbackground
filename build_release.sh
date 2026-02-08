#!/bin/bash

# VR Background Service APK 构建和安装脚本
# 用途：快速构建Release APK并打包

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRADLE="${PROJECT_DIR}/gradlew"

echo "================================"
echo "VR Background Service APK构建"
echo "================================"
echo ""

# 检查gradlew文件
if [ ! -f "$GRADLE" ]; then
    echo "❌ 错误: 未找到gradlew文件"
    echo "请确保在项目根目录运行此脚本"
    exit 1
fi

# 使脚本可执行
chmod +x "$GRADLE"

echo "📋 开始构建Release APK..."
$GRADLE clean assembleRelease --warning-mode all

APK_DIR="${PROJECT_DIR}/app/build/outputs/apk/release"
APK_FILE="${APK_DIR}/app-release.apk"

if [ -f "$APK_FILE" ]; then
    echo ""
    echo "✓ APK构建成功！"
    echo "📍 文件位置: $APK_FILE"
    echo "📊 文件大小: $(du -h "$APK_FILE" | cut -f1)"
    echo ""
    echo "接下来可以："
    echo "1. 将APK上传到Pico应用商店"
    echo "2. 使用 adb install $APK_FILE 安装到设备"
    echo "3. 分享给用户以供下载安装"
else
    echo ""
    echo "❌ APK构建失败"
    exit 1
fi
