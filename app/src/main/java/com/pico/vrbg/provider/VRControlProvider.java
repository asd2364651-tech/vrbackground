package com.pico.vrbg.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * VR控制服务提供者
 * 允许其他应用通过ContentProvider查询电池信息和控制设备
 */
public class VRControlProvider extends ContentProvider {

    private static final String TAG = "VRControlProvider";
    private static final String AUTHORITY = "com.pico.vrbg.provider";

    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreate() {
        try {
            Context context = getContext();
            if (context != null) {
                sharedPreferences = context.getSharedPreferences("vr_bg_prefs", Context.MODE_PRIVATE);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize provider", e);
            return false;
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        
        MatrixCursor cursor = new MatrixCursor(new String[]{"key", "value"});

        try {
            String path = uri.getPath();
            
            if (path != null && path.contains("battery")) {
                // 返回电池信息
                cursor.addRow(new Object[]{"head_battery", 
                    sharedPreferences.getInt("head_battery", -1)});
                cursor.addRow(new Object[]{"left_battery", 
                    sharedPreferences.getInt("left_battery", -1)});
                cursor.addRow(new Object[]{"right_battery", 
                    sharedPreferences.getInt("right_battery", -1)});
                cursor.addRow(new Object[]{"last_update", 
                    sharedPreferences.getLong("last_update", -1)});
            }
        } catch (Exception e) {
            Log.e(TAG, "Query failed", e);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.dir/vnd.com.pico.vrbg.item";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Insert operation not supported");
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "Delete operation not supported");
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "Update operation not supported");
        return 0;
    }
}
