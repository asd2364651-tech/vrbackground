#!/usr/bin/env python3
"""
VR Background Service APK构建和部署助手脚本
支持快速构建、签名和安装APK

使用方法:
    python3 build_helper.py build      # 构建debug APK
    python3 build_helper.py release    # 构建release APK
    python3 build_helper.py install    # 构建并安装debug版本
    python3 build_helper.py clean      # 清建上一次构建
"""

import os
import sys
import subprocess
import argparse
from pathlib import Path

class APKBuilder:
    def __init__(self, project_root="."):
        self.project_root = Path(project_root)
        self.gradle_cmd = "./gradlew" if os.name != 'nt' else "gradlew.bat"
        
    def run_command(self, cmd, description=""):
        """运行shell命令"""
        if description:
            print(f"\n{'='*50}")
            print(f"📋 {description}")
            print(f"{'='*50}\n")
        
        try:
            result = subprocess.run(cmd, shell=True)
            return result.returncode == 0
        except Exception as e:
            print(f"❌ 命令执行失败: {e}")
            return False
    
    def clean(self):
        """清理上一次构建"""
        return self.run_command(
            f"{self.gradle_cmd} clean",
            "清理项目构建文件"
        )
    
    def build_debug(self):
        """构建Debug APK"""
        return self.run_command(
            f"{self.gradle_cmd} assembleDebug",
            "构建Debug APK"
        )
    
    def build_release(self):
        """构建Release APK"""
        return self.run_command(
            f"{self.gradle_cmd} assembleRelease",
            "构建Release APK"
        )
    
    def install_debug(self):
        """安装Debug APK到设备"""
        success = self.build_debug()
        if not success:
            return False
        
        apk_path = self.project_root / "app/build/outputs/apk/debug/app-debug.apk"
        if not apk_path.exists():
            print(f"❌ APK文件不存在: {apk_path}")
            return False
        
        return self.run_command(
            f"adb install -r \"{apk_path}\"",
            "安装Debug APK到设备"
        )
    
    def start_app(self):
        """启动应用"""
        return self.run_command(
            "adb shell am start -n com.pico.vrbg/.MainActivity",
            "启动应用"
        )
    
    def view_logs(self):
        """查看应用日志"""
        print("\n📋 获取应用日志 (按 Ctrl+C 停止)\n")
        self.run_command(
            "adb logcat | grep -E '(VRBackground|PicoBattery|DeviceControl)'",
            ""
        )
    
    def list_devices(self):
        """列出连接的设备"""
        print("\n📱 已连接的设备：\n")
        self.run_command("adb devices", "")

def main():
    parser = argparse.ArgumentParser(
        description="VR Background Service APK构建和部署助手"
    )
    parser.add_argument(
        'action',
        choices=['clean', 'build', 'release', 'install', 'run', 'logs', 'devices'],
        help='执行的操作'
    )
    
    args = parser.parse_args()
    
    builder = APKBuilder()
    
    if args.action == 'clean':
        builder.clean()
        print("\n✓ 清理完成")
    
    elif args.action == 'build':
        success = builder.build_debug()
        if success:
            print("\n✓ Debug APK构建成功")
            print("📍 位置: app/build/outputs/apk/debug/app-debug.apk")
    
    elif args.action == 'release':
        success = builder.build_release()
        if success:
            print("\n✓ Release APK构建成功")
            print("📍 位置: app/build/outputs/apk/release/app-release.apk")
    
    elif args.action == 'install':
        success = builder.install_debug()
        if success:
            print("\n✓ APK安装成功")
    
    elif args.action == 'run':
        success = builder.install_debug()
        if success:
            builder.start_app()
            print("\n✓ 应用已启动")
    
    elif args.action == 'logs':
        builder.view_logs()
    
    elif args.action == 'devices':
        builder.list_devices()

if __name__ == '__main__':
    main()
