package com.pico.vrbg.scenario;

import android.content.Context;
import android.util.Log;

import com.pico.vrbg.manager.DeviceControlManager;
import com.pico.vrbg.manager.PicoBatteryManager;
import com.pico.vrbg.util.FileManagerHelper;

import java.io.File;
import java.util.List;

/**
 * VR应用使用场景示例
 * 展示了本服务的各种典型应用场景
 */
public class SceneUsageExamples {

    private static final String TAG = "SceneUsageExamples";
    private Context context;
    private DeviceControlManager deviceManager;
    private PicoBatteryManager batteryManager;
    private FileManagerHelper fileManager;

    public SceneUsageExamples(Context context) {
        this.context = context;
        this.deviceManager = new DeviceControlManager(context);
        this.batteryManager = new PicoBatteryManager(context);
        this.fileManager = new FileManagerHelper(context);
    }

    /**
     * 场景1: 虚拟演讲厅
     * 功能：自动播放演讲视频，监控设备电量，低电量时提醒休息
     */
    public void scenario_VirtualLectureHall() {
        Log.d(TAG, "=== 虚拟演讲厅场景 ===");
        
        // 1. 打开演讲视频
        String videoPath = "/sdcard/Videos/lecture.mp4";
        if (fileManager.fileExists(videoPath)) {
            deviceManager.openFile(videoPath);
            Log.d(TAG, "开始播放演讲视频");
        }

        // 2. 监控设备电量
        int headBattery = batteryManager.getHeadBattery();
        Log.d(TAG, "头显电量: " + headBattery + "%");

        if (headBattery < 20) {
            Log.w(TAG, "警告: 头显电量过低，建议充电");
        }

        // 3. 如果电量不足，自动锁屏"以节省电量
        if (headBattery < 10) {
            deviceManager.lockDevice();
            Log.d(TAG, "电池过低，已锁屏");
        }
    }

    /**
     * 场景2: 虚拟资料库/博物馆
     * 功能：浏览不同的内容文件，支持多格式媒体
     */
    public void scenario_VirtualMuseum() {
        Log.d(TAG, "=== 虚拟博物馆场景 ===");

        String museumDir = "/sdcard/Museums";
        
        // 列出所有媒体文件
        List<File> mediaFiles = fileManager.listFilesByExtension(museumDir, "mp4", "jpg", "png");
        Log.d(TAG, "发现 " + mediaFiles.size() + " 个媒体文件");

        // 顺序播放馆藏内容
        for (File file : mediaFiles) {
            Log.d(TAG, "正在查看: " + file.getName());
            deviceManager.openFile(file.getAbsolutePath());
            
            try {
                Thread.sleep(3000); // 查看3秒后切换到下一个
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 场景3: 工业VR培训
     * 功能：课程进行中定期检查电量，完成课程后自动保存进度并提示
     */
    public void scenario_IndustrialTraining() {
        Log.d(TAG, "=== 工业VR培训场景 ===");

        String trainingApp = "/sdcard/Apps/IndustrialTraining.apk";

        // 1. 启动培训应用
        if (fileManager.fileExists(trainingApp)) {
            deviceManager.openFile(trainingApp);
            Log.d(TAG, "培训应用已启动");
        }

        // 2. 在培训过程中定期检查设备状态
        // 实际应用中应该在后台服务中定期调用
        PicoBatteryManager.BatteryInfo battery = batteryManager.getAllBatteryInfo();
        Log.d(TAG, "当前设备状态: " + battery.toString());

        // 3. 如果手柄电量过低，提醒用户更换电池
        if (battery.leftControllerBattery < 15 || battery.rightControllerBattery < 15) {
            Log.w(TAG, "检测到手柄电量过低，请更换电池");
        }

        // 4. 完成培训后自动重启设备以最新配置
        Log.d(TAG, "培训完成，准备重启设备...");
        // deviceManager.restartDevice(); // 实际应用中需要用户确认
    }

    /**
     * 场景4: VR远程协作
     * 功能：维持与远程主控的连接，接收与执行远程命令
     */
    public void scenario_RemoteCollaboration() {
        Log.d(TAG, "=== VR远程协作场景 ===");

        // 1. 监控本地设备电量并定期上报
        int headBattery = batteryManager.getHeadBattery();
        int leftBattery = batteryManager.getLeftControllerBattery();
        int rightBattery = batteryManager.getRightControllerBattery();

        Log.d(TAG, String.format("设备状态 - 头显: %d%%, 左手柄: %d%%, 右手柄: %d%%",
                headBattery, leftBattery, rightBattery));

        // 2. 接收远程命令示例
        executeRemoteCommand("open_file", "/sdcard/Collaboration/project.mp4");
        
        // 3. 如果接收到休息命令，可以自动进行设备休眠
        if (headBattery < 30) {
            Log.d(TAG, "电量中等，建议在本地休息区放置充电器");
        }
    }

    /**
     * 场景5: VR健身应用
     * 功能：监控运动时长，定期检查设备状态，保护设备
     */
    public void scenario_VRFitness() {
        Log.d(TAG, "=== VR健身应用场景 ===");

        String fitnessApp = "/sdcard/Apps/VRFitness.apk";

        // 1. 启动健身应用
        if (fileManager.fileExists(fitnessApp)) {
            deviceManager.openFile(fitnessApp);
            Log.d(TAG, "健身应用已启动");
        }

        // 2. 定期检查头显和手柄电量
        // 这应该在后台服务中实现定期检查
        PicoBatteryManager.BatteryInfo batteryInfo = batteryManager.getAllBatteryInfo();
        
        // 3. 如果运动中手柄电量过低，提醒用户可能影响体验
        if (batteryInfo.leftControllerBattery < 25 || batteryInfo.rightControllerBattery < 25) {
            Log.w(TAG, "运动中检测到手柄电量低，可能影响动作捕捉精度");
        }

        // 4. 长时间运动后建议休息和冷却
        Log.d(TAG, "30分钟健身后：建议休息5分钟，让设备冷却并补充水分");
    }

    /**
     * 场景6: VR游戏娱乐
     * 功能：自动管理游戏启动，监控游戏性能，优化设备资源
     */
    public void scenario_VRGaming() {
        Log.d(TAG, "=== VR游戏娱乐场景 ===");

        String gamesDir = "/sdcard/Games";
        
        // 1. 列出所有可用游戏
        List<File> games = fileManager.listFilesByExtension(gamesDir, "apk");
        Log.d(TAG, "找到 " + games.size() + " 个可用游戏");

        // 2. 启动第一个游戏
        if (!games.isEmpty()) {
            String gamePath = games.get(0).getAbsolutePath();
            deviceManager.openFile(gamePath);
            Log.d(TAG, "启动游戏: " + games.get(0).getName());
        }

        // 3. 游戏进行中监控设备温度（通过电量状态推测）
        PicoBatteryManager.BatteryInfo battery = batteryManager.getAllBatteryInfo();
        
        if (battery.isCharging) {
            Log.d(TAG, "设备已连接充电器，可以进行长时间游戏");
        } else {
            Log.d(TAG, "设备未充电，建议在 " + battery.headBattery + "% 电量时停止游戏以保护硬件");
        }

        // 4. 游戏结束后可以自动返回主菜单或关闭
        Log.d(TAG, "游戏结束，返回主菜单");
    }

    /**
     * 执行远程命令的辅助方法
     */
    private void executeRemoteCommand(String command, String param) {
        Log.d(TAG, "执行远程命令: " + command + " 参数: " + param);
        
        switch (command) {
            case "open_file":
                deviceManager.openFile(param);
                break;
            case "restart":
                deviceManager.restartDevice();
                break;
            case "shutdown":
                deviceManager.shutdownDevice();
                break;
            case "wake_up":
                deviceManager.wakeUpDevice();
                break;
            default:
                Log.w(TAG, "未知命令: " + command);
        }
    }
}
