package com.tzq.maintenance.core;

import android.app.Activity;
import android.os.Looper;

import com.google.gson.Gson;
import com.tzq.common.utils.IOUtil;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.bean.Rep;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

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
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by zqtang on 16/8/30.
 */
public class HttpTask {
    private String mUrl;
    private OkHttpClient mOkHttpClient;
    private List<CompleteCallBack> mCompleteCallBacks = new ArrayList<>();
    private List<ProgressCallBack> mProgressCallBacks = new ArrayList<>();
    private Activity mActivity;

    public HttpTask(String url) {
        mUrl = url;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//包含header、body数据
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);
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

    public HttpTask setActivity(Activity activity) {
        mActivity = activity;
        return this;
    }

    public void start(RequestBody body) {
        LogUtil.i("start " + mUrl);
        final Request r;
        if (body == null) {
            r = new Request.Builder().url(mUrl).addHeader("client", "Android").get().build();
        } else {
            r = new Request.Builder().url(mUrl).addHeader("client", "Android").post(body).build();
        }
        Call call = mOkHttpClient.newCall(r);
        showProgressDialog();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i("onFailure  " + e.getMessage());
                hideProgressDialog();
                onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                LogUtil.i("onResponse  " + body);
                Looper.prepare();
                hideProgressDialog();
                Rep res = new Gson().fromJson(body, Rep.class);
                if (res != null) {
                    if (res.code == Rep.CODE_SUCCESS) {
                        MyUtil.toast(res.msg);
                        onComplete(res.data);
                    } else {
                        MyUtil.toast(res.msg);
                        onError(res.msg);
                    }
                } else {
                    MyUtil.toast("返回数据格式出错");
                    onError("返回数据格式出错");
                }
                Looper.loop();
            }
        });
    }

    private void onComplete(Object data) {
        for (CompleteCallBack completeCallBack : mCompleteCallBacks) {
            completeCallBack.onComplete(true, data, "");
        }

    }

    private void onError(String msg) {
        for (CompleteCallBack completeCallBack : mCompleteCallBacks) {
            completeCallBack.onComplete(false, null, msg);
        }
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

    private void showProgressDialog() {
        if (mActivity != null && mActivity instanceof Activity) {
            ProgressDialogUtil.show(mActivity);
        }
    }

    private void hideProgressDialog() {
        if (mActivity != null && mActivity instanceof Activity) {
            ProgressDialogUtil.hide(mActivity);
        }
    }

    public interface CompleteCallBack {
        void onComplete(boolean isSuccess, Object data, String msg);
    }

    public interface ProgressCallBack {
        void onProgress(long total, long current);
    }

}
