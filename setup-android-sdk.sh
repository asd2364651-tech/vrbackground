#!/bin/bash

# Android SDK 自动配置脚本

echo "========================================="
echo "Android Studio & SDK 自动配置"
echo "========================================="

# 1. 创建 SDK 目录结构
echo "1. 创建 SDK 目录结构..."
mkdir -p ~/Library/Android/sdk
mkdir -p ~/Library/Android/sdk/cmdline-tools/latest

# 2. 等待用户在 Android Studio 中完成初始化
echo ""
echo "2. 请在 Android Studio 中完成以下步骤："
echo "   - Android Studio 应该已经启动"
echo "   - 等待首次初始化完成"
echo "   - 按照向导安装所需的 SDK 和工具"
echo ""
echo "按 ENTER 继续..."
read

# 3. 确认 SDK 路径
echo ""
echo "3. 检查 SDK 路径..."
SDK_PATH="$HOME/Library/Android/sdk"

if [ -d "$SDK_PATH" ]; then
    echo "✓ SDK 目录已创建: $SDK_PATH"
    
    # 检查必要的组件
    if [ -d "$SDK_PATH/platforms" ]; then
        echo "✓ 已检测到 Platform SDK"
    else
        echo "⚠ Platform SDK 未找到 - 您可能需要在 Android Studio 中安装"
    fi
    
    if [ -d "$SDK_PATH/build-tools" ]; then
        echo "✓ 已检测到 Build Tools"
    else
        echo "⚠ Build Tools 未找到 - 您可能需要在 Android Studio 中安装"
    fi
    
    if [ -f "$SDK_PATH/platforms/android-34/android.jar" ]; then
        echo "✓ 已检测到 Android 34 SDK"
    else
        echo "⚠ Android 34 SDK 未找到"
    fi
else
    echo "✗ 无法找到 SDK 目录"
    exit 1
fi

# 4. 创建/更新 local.properties
echo ""
echo "4. 更新 local.properties..."
cd /Volumes/lexar-2T/cursorworkspace/vrbackground
cat > local.properties << EOF
sdk.dir=$SDK_PATH
EOF

if [ -f local.properties ]; then
    echo "✓ local.properties 已创建:"
    cat local.properties
else
    echo "✗ 创建 local.properties 失败"
    exit 1
fi

# 5. 测试 Gradle 构建
echo ""
echo "5. 测试 Gradle 构建..."
./gradlew clean build 2>&1 | tail -20

echo ""
echo "========================================="
echo "配置完成！"
echo "========================================="
