package com.tzq.maintenance.core;

import android.os.Looper;

import com.tzq.common.utils.IOUtil;
import com.tzq.common.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zqtang on 16/8/30.
 */
public class HttpTask {
    private String mUrl;
    private OkHttpClient mOkHttpClient;
    private List<CompleteCallBack> mCompleteCallBacks = new ArrayList<>();
    private List<ProgressCallBack> mProgressCallBacks = new ArrayList<>();

    public HttpTask(String url) {
        mUrl = url;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();

    }

    public HttpTask addCompleteCallBack(CompleteCallBack completeCallBack) {
        mCompleteCallBacks.add(completeCallBack);
        return this;
    }

    public HttpTask addProgressCallBack(ProgressCallBack progressCallBack) {
        mProgressCallBacks.add(progressCallBack);
        return this;
    }

    public void start(RequestBody body) {
        LogUtil.i("start " + mUrl);
        Request r;
        if (body == null) {
            r = new Request.Builder().url(mUrl).get().build();
        } else {
            r = new Request.Builder().url(mUrl).post(body).build();
        }
        Call call = mOkHttpClient.newCall(r);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.i("onResponse  " + response.toString());
                if (response.body() != null) {
                    onComplete(response.body().string());
                }
            }
        });
    }

    private void onComplete(Object data) {
        Looper.prepare();
        for (CompleteCallBack completeCallBack : mCompleteCallBacks) {
            completeCallBack.onComplete(true, data, "");
        }
        Looper.loop();
    }

    private void onError(String msg) {
        Looper.prepare();
        for (CompleteCallBack completeCallBack : mCompleteCallBacks) {
            completeCallBack.onComplete(false, null, msg);
        }
        Looper.loop();
    }

    public void start() {
        start(null);
    }

    public void download() {
        Request request = new Request.Builder().url(mUrl).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File("/sdcard/wangshu.jpg"));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.closeQuietly(fileOutputStream);
                }
            }
        });
    }

    public interface CompleteCallBack {
        void onComplete(boolean isSuccess, Object data, String msg);
    }

    public interface ProgressCallBack {
        void onProgress(long total, long current);
    }

}
