#!/bin/bash

# 快速 SDK 安装检查清单

echo "========================================="
echo "Android SDK 安装检查清单"
echo "========================================="
echo ""

# 检查 Android Studio
echo "检查 1: Android Studio 安装"
if [ -d "/Applications/Android Studio.app" ]; then
    echo "✓ Android Studio 已安装"
else
    echo "✗ Android Studio 未找到"
    exit 1
fi

# 检查 SDK 目录
echo ""
echo "检查 2: SDK 目录"
SDK_PATH="$HOME/Library/Android/sdk"
if [ -d "$SDK_PATH" ]; then
    echo "✓ SDK 目录存在: $SDK_PATH"
else
    echo "✗ SDK 目录不存在"
    exit 1
fi

# 检查关键 SDK 组件
echo ""
echo "检查 3: SDK 组件"
echo ""
echo "需要安装:"

# Android 34
if [ -d "$SDK_PATH/platforms/android-34" ]; then
    echo "✓ Android 34 (API 34) Platform SDK"
else
    echo "☐ Android 34 (API 34) Platform SDK - 需要安装"
fi

# Build Tools
if [ -d "$SDK_PATH/build-tools" ]; then
    LATEST_BT=$(ls -t "$SDK_PATH/build-tools/" 2>/dev/null | head -1)
    if [ -n "$LATEST_BT" ]; then
        echo "✓ Build Tools ($LATEST_BT)"
    else
        echo "☐ Build Tools - 需要安装"
    fi
else
    echo "☐ Build Tools - 需要安装"
fi

# Platform Tools
if [ -d "$SDK_PATH/platform-tools" ]; then
    echo "✓ Platform Tools"
else
    echo "☐ Platform Tools - 需要安装"
fi

# local.properties
echo ""
echo "检查 4: 项目配置"
if [ -f "/Volumes/lexar-2T/cursorworkspace/vrbackground/local.properties" ]; then
    echo "✓ local.properties 已配置"
    echo "   内容:"
    cat /Volumes/lexar-2T/cursorworkspace/vrbackground/local.properties | sed 's/^/   /'
else
    echo "✗ local.properties 未找到"
fi

# Gradle
echo ""
echo "检查 5: Gradle 配置"
if [ -f "/Volumes/lexar-2T/cursorworkspace/vrbackground/gradlew" ]; then
    echo "✓ Gradle wrapper 已配置"
    GRADLE_VERSION=$(grep "distributionUrl" /Volumes/lexar-2T/cursorworkspace/vrbackground/gradle/wrapper/gradle-wrapper.properties | grep -o 'gradle-[^-]*' | cut -d- -f2)
    echo "   版本: Gradle $GRADLE_VERSION"
else
    echo "✗ Gradle wrapper 未找到"
fi

echo ""
echo "========================================="
echo ""
echo "⚠️  如果有组件标记为 '☐ 需要安装'，请:"
echo ""
echo "1. 打开 Android Studio: open -a 'Android Studio'"
echo "2. 进入菜单: Tools → SDK Manager"
echo "3. 安装所有标记的组件"
echo "4. 等待完成后运行: ./gradlew build"
echo ""
echo "========================================="
