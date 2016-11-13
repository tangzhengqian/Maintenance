package com.tzq.maintenance.core;

import android.app.Activity;
import android.os.Looper;

import com.tzq.common.utils.IOUtil;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import org.json.JSONObject;

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
    private boolean mIsShowMessage = true;
    private boolean mIsGet;

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

    public HttpTask setShowMessage(boolean b) {
        mIsShowMessage = b;
        return this;
    }

    public HttpTask setGet(boolean b) {
        mIsGet = b;
        return this;
    }

    private Call getCall(RequestBody body) {
        final Request r;
        String sessionId = Util.avoidNull(App.getInstance().getUser().token);
        Request.Builder builder = new Request.Builder();
        builder.url(mUrl).addHeader("client", "Android").addHeader("token", sessionId);
        if (mIsGet) {
            r = builder.get().build();
        } else {
            r = builder.post(body).build();
        }

        return mOkHttpClient.newCall(r);
    }

    public void enqueue(RequestBody body) {
        LogUtil.i("enqueue " + mUrl);

        showProgressDialog();
        getCall(body).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                LogUtil.i("onFailure  " + e.getMessage());
                hideProgressDialog();
                onComplete(new ResponseData(-1, e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                hideProgressDialog();
                ResponseData responseData = getResponseData(response);
                if (responseData.code == 0) {
                    onComplete(responseData);
                } else {
                    showMessage(responseData.msg);
                    onComplete(responseData);
                }

            }
        });
    }

    public ResponseData execute(RequestBody body) {
        LogUtil.i("execute " + mUrl);
        try {
            Response response = getCall(body).execute();
            return getResponseData(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseData(-1, e.getMessage());
        }
    }

    private ResponseData getResponseData(Response response) {
        ResponseData responseData = new ResponseData();
        try {
            String body = response.body().string();
            LogUtil.i("onResponse  " + body);
            JSONObject o = new JSONObject(body);
            responseData.code = o.getInt("code");
            responseData.msg = o.optString("msg");
            responseData.data = o.getString("data");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.code = -1;
            responseData.msg = "返回数据格式出错";
            responseData.data = "";
        }
        return responseData;
    }

    private void showMessage(String msg) {
        Looper.prepare();
        if (mIsShowMessage) {
            MyUtil.toast(msg);
        }
        Looper.loop();
    }

    private void onComplete(final ResponseData responseData) {
        Looper.prepare();
        for (CompleteCallBack completeCallBack : mCompleteCallBacks) {
            completeCallBack.onComplete(responseData);
        }
        Looper.loop();

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
        void onComplete(ResponseData responseData);
    }

    public interface ProgressCallBack {
        void onProgress(long total, long current);
    }

}
