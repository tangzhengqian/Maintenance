package com.tzq.maintenance;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

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
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("my.db").setDatabaseVersion(1).create();
        ActiveAndroid.initialize(dbConfiguration);
    }
}
