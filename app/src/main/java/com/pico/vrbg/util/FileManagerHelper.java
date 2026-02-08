package com.pico.vrbg.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理辅助类
 * 处理文件的读取、写入、删除等操作
 */
public class FileManagerHelper {

    private static final String TAG = "FileManagerHelper";
    private Context context;

    public FileManagerHelper(Context context) {
        this.context = context;
    }

    /**
     * 获取外部存储目录
     */
    public File getExternalStorageDir() {
        return context.getExternalFilesDir(null);
    }

    /**
     * 获取电影/视频目录
     */
    public File getMoviesDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
    }

    /**
     * 获取音乐目录
     */
    public File getMusicDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
    }

    /**
     * 获取下载目录
     */
    public File getDownloadDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * 列出指定目录的所有文件
     */
    public List<File> listFiles(String dirPath) {
        List<File> files = new ArrayList<>();
        try {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] fileArray = dir.listFiles();
                if (fileArray != null) {
                    for (File file : fileArray) {
                        if (file.isFile()) {
                            files.add(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to list files from " + dirPath, e);
        }
        return files;
    }

    /**
     * 根据文件扩展名过滤文件
     */
    public List<File> listFilesByExtension(String dirPath, String... extensions) {
        List<File> files = new ArrayList<>();
        try {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] fileArray = dir.listFiles((file) -> {
                    String name = file.getName().toLowerCase();
                    for (String ext : extensions) {
                        if (name.endsWith("." + ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                });
                if (fileArray != null) {
                    for (File file : fileArray) {
                        files.add(file);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to list files by extension", e);
        }
        return files;
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    Log.d(TAG, "File deleted: " + filePath);
                }
                return deleted;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete file: " + filePath, e);
        }
        return false;
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get file size", e);
        }
        return -1;
    }

    /**
     * 使用ContentProvider打开文件
     */
    public void openFileWithProvider(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Log.e(TAG, "File not found: " + filePath);
                return;
            }

            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(fileUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open file with provider", e);
        }
    }

    /**
     * 创建目录
     */
    public boolean createDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                return dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create directory: " + dirPath, e);
            return false;
        }
    }
}
