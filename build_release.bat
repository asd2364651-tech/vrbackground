@echo off
REM VR Background Service APK 构建脚本 (Windows)

setlocal enabledelayedexpansion

echo ================================
echo VR Background Service APK构建
echo ================================
echo.

REM 检查gradlew文件
if not exist "gradlew.bat" (
    echo 错误: 未找到gradlew.bat文件
    echo 请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo 开始构建Release APK...
call gradlew.bat clean assembleRelease

set APK_FILE=app\build\outputs\apk\release\app-release.apk

if exist "%APK_FILE%" (
    echo.
    echo ✓ APK构建成功！
    echo 文件位置: %APK_FILE%
    echo.
    echo 接下来可以：
    echo 1. 将APK上传到Pico应用商店
    echo 2. 使用 adb install %APK_FILE% 安装到设备
    echo 3. 分享给用户以供下载安装
) else (
    echo.
    echo ✗ APK构建失败
    exit /b 1
)

pause
