package com.tzq.maintenance;

import android.app.Application;

/**
 * Created by Administrator on 2016/8/29.
 */

public class App extends Application {
    private static App mInstance;

    public static Application my() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
