#!/bin/bash

# Pico SDK 依赖问题快速修复脚本

echo "========================================="
echo "Pico SDK 依赖问题 - 快速修复"
echo "========================================="
echo ""
echo "请选择解决方案:"
echo ""
echo "1. 禁用 Pico SDK (用于测试其他依赖)"
echo "2. 更新 Pico SDK 版本"
echo "3. 配置 TLS 支持"
echo "4. 显示帮助信息"
echo "0. 退出"
echo ""
read -p "请输入选择 (0-4): " choice

case $choice in
    1)
        echo ""
        echo "禁用 Pico SDK..."
        sed -i '' "s/    implementation 'com.picovr.app:frame:2.4.18'/    \/\/ implementation 'com.picovr.app:frame:2.4.18'/g" app/build.gradle
        echo "✓ 已注释掉 Pico SDK 依赖"
        echo ""
        echo "现在尝试构建: ./gradlew build"
        ;;
    2)
        echo ""
        echo "请输入新的 Pico SDK 版本号 (例如: 2.5.0)"
        read -p "版本号: " version
        sed -i '' "s/com.picovr.app:frame:2.4.18/com.picovr.app:frame:$version/g" app/build.gradle
        echo "✓ 已更新为版本: $version"
        echo ""
        echo "现在尝试构建: ./gradlew build"
        ;;
    3)
        echo ""
        echo "配置 TLS 支持..."
        cat >> gradle.properties << 'EOF'

# TLS 协议配置
org.gradle.jvmargs=-Dcom.sun.jndi.ldap.connect.pool=false \
  -Dcom.sun.jndi.ldap.connect.timeout.response=10000 \
  -Dhttps.protocols=TLSv1.2,TLSv1.3
EOF
        echo "✓ 已添加 TLS 配置到 gradle.properties"
        echo ""
        echo "现在尝试构建: ./gradlew build"
        ;;
    4)
        echo ""
        echo "========================================="
        echo "帮助信息"
        echo "========================================="
        echo ""
        echo "问题: Pico VR SDK 下载失败"
        echo ""
        echo "可能原因:"
        echo "1. 版本 2.4.18 已不可用或被移除"
        echo "2. 网络连接问题或 TLS 协议不兼容"
        echo "3. Pico 仓库地址变更"
        echo ""
        echo "建议:"
        echo "• 查看 Pico 官方文档获取最新版本号"
        echo "• 检查网络连接和代理设置"
        echo "• 尝试使用镜像仓库 (Aliyun等)"
        echo ""
        ;;
    0)
        echo "退出"
        exit 0
        ;;
    *)
        echo "无效选择"
        exit 1
        ;;
esac

echo ""
echo "========================================="
