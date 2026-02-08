#!/bin/bash

# Pico SDK 版本管理脚本
# 用途: 查询最新版本并更新项目中的 Pico SDK

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_GRADLE="$PROJECT_DIR/app/build.gradle"
MAVEN_REPO="https://xrapi.picovr.com/maven/com/picovr/app/frame"

echo "════════════════════════════════════════════════════════════"
echo "     Pico SDK 版本管理工具"
echo "════════════════════════════════════════════════════════════"
echo ""

# 显示菜单
show_menu() {
    echo "请选择操作:"
    echo ""
    echo "1. 查询所有可用 Pico SDK 版本"
    echo "2. 更新到最新版本"
    echo "3. 更新到指定版本"
    echo "4. 启用 Pico SDK (Maven)"
    echo "5. 启用 Pico SDK (本地 AAR)"
    echo "6. 禁用 Pico SDK"
    echo "7. 测试构建"
    echo "0. 退出"
    echo ""
}

# 查询可用版本
query_versions() {
    echo "🔍 正在查询 Pico Maven 仓库..."
    echo "   仓库: $MAVEN_REPO"
    echo ""
    
    # 尝试列出目录 (需要 curl)
    if command -v curl &> /dev/null; then
        echo "尝试获取可用版本列表..."
        # 注: 这可能需要特殊的访问权限
        curl -s "$MAVEN_REPO/" 2>/dev/null | grep -oP 'href="\d+\.\d+\.\d+' | sed 's|href="||' | sort -V || echo "无法直接列出版本"
    else
        echo "❌ 需要 curl 工具"
    fi
    
    echo ""
    echo "已知的 Pico SDK 版本:"
    echo "  • 2.4.18 (已测试可用)"
    echo "  • 2.4.17, 2.4.16 (可能可用)"
    echo "  • 其他版本 (请查询 Maven 仓库)"
}

# 获取当前版本
get_current_version() {
    grep -oP "com.picovr.app:frame:\K[\d.]*|//.*com.picovr.app:frame:\K[\d.]*" "$BUILD_GRADLE" | head -1
}

# 更新为最新版本
update_latest() {
    CURRENT=$(get_current_version)
    echo "当前版本: $CURRENT"
    echo ""
    echo "推荐使用版本: 2.4.18"
    update_version "2.4.18"
}

# 更新为指定版本
update_version() {
    local version=$1
    
    if [ -z "$version" ]; then
        read -p "请输入版本号 (例如: 2.4.18): " version
    fi
    
    echo ""
    echo "准备更新到版本: $version"
    
    # 备份原文件
    cp "$BUILD_GRADLE" "$BUILD_GRADLE.bak"
    
    # 更新版本号
    sed -i '' "s/com.picovr.app:frame:[^']*'/com.picovr.app:frame:$version'/g" "$BUILD_GRADLE"
    
    # 启用依赖
    sed -i '' "s|\/\/ *implementation 'com.picovr.app:frame|implementation 'com.picovr.app:frame|g" "$BUILD_GRADLE"
    
    echo "✓ 版本已更新为: $version"
    echo "✓ 备份文件: $BUILD_GRADLE.bak"
    echo ""
    echo "下一步: 运行 ./gradlew clean build 进行编译"
}

# 启用 Maven 依赖
enable_maven() {
    read -p "请输入版本号 (默认 2.4.18): " version
    version=${version:-2.4.18}
    
    cp "$BUILD_GRADLE" "$BUILD_GRADLE.bak"
    
    # 替换为 Maven 依赖
    sed -i '' '/\/\/ Pico VR SDK/,/\/\/ 后台任务/s|// *implementation.*frame.*|implementation '\''com.picovr.app:frame:'"$version"\'\'|' "$BUILD_GRADLE"
    
    echo "✓ 已启用 Maven 依赖: version=$version"
}

# 启用本地 AAR
enable_local_aar() {
    read -p "请输入 AAR 文件名 (默认 frame-release.aar): " aar_file
    aar_file=${aar_file:-frame-release.aar}
    
    # 检查文件是否存在
    if [ ! -f "$PROJECT_DIR/app/libs/$aar_file" ]; then
        echo "❌ 错误: 文件不存在 - $PROJECT_DIR/app/libs/$aar_file"
        echo ""
        echo "请先:"
        echo "1. 创建目录: mkdir -p app/libs"
        echo "2. 复制 AAR 文件: cp your-pico-sdk.aar app/libs/$aar_file"
        return 1
    fi
    
    cp "$BUILD_GRADLE" "$BUILD_GRADLE.bak"
    
    # 替换为本地 AAR
    sed -i '' '/\/\/ Pico VR SDK/,/\/\/ 后台任务/s|.*implementation.*frame.*|    implementation files('\''libs/'"$aar_file"'\'')|' "$BUILD_GRADLE"
    
    echo "✓ 已启用本地 AAR: $aar_file"
}

# 禁用 Pico SDK
disable_sdk() {
    cp "$BUILD_GRADLE" "$BUILD_GRADLE.bak"
    
    # 注释所有 Pico 依赖
    sed -i '' 's/^\([[:space:]]*\)implementation '\''com.picovr/\1\/\/ implementation '\''com.picovr/' "$BUILD_GRADLE"
    sed -i '' 's/^\([[:space:]]*\)implementation files('\''libs.*frame/\1\/\/ implementation files('\''libs/' "$BUILD_GRADLE"
    
    echo "✓ 已禁用 Pico SDK"
}

# 测试构建
test_build() {
    echo "正在运行构建测试..."
    cd "$PROJECT_DIR"
    
    if ./gradlew clean build -q 2>&1 | tail -5; then
        echo ""
        echo "✓ 构建成功!"
    else
        echo ""
        echo "❌ 构建失败，请检查错误信息"
    fi
}

# 主程序
while true; do
    show_menu
    read -p "请输入选项 (0-7): " choice
    
    case $choice in
        1) query_versions ;;
        2) update_latest; test_build ;;
        3) update_version; test_build ;;
        4) enable_maven ;;
        5) enable_local_aar ;;
        6) disable_sdk ;;
        7) test_build ;;
        0) echo "退出"; break ;;
        *) echo "❌ 无效选项" ;;
    esac
    
    echo ""
    read -p "按 Enter 继续..."
    clear
done
