package com.pico.vrbg.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络管理器
 * 用于与远程服务器通信
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private OkHttpClient httpClient;
    private Context context;

    public NetworkManager(Context context) {
        this.context = context;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 同步GET请求
     */
    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    /**
     * 异步GET请求
     */
    public void getAsync(String url, final NetworkCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }
                callback.onSuccess(response.body().string());
            }
        });
    }

    /**
     * 发送JSON POST请求
     */
    public void postJson(String url, String json, final NetworkCallback callback) {
        okhttp3.MediaType jsonType = okhttp3.MediaType.get("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, jsonType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }
                callback.onSuccess(response.body().string());
            }
        });
    }

    /**
     * 网络回调接口
     */
    public interface NetworkCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}
