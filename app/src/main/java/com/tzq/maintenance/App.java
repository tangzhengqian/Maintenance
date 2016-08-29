package com.tzq.maintenance;

import android.app.Application;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/8/29.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RequestBody d=new FormBody.Builder().add("ff","fg").build();
        Request r=new Request.Builder()
                .post(d)
                .build();
    }
}
