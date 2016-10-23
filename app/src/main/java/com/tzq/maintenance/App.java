package com.tzq.maintenance;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.tzq.maintenance.core.CrashHandler;

/**
 * Created by Administrator on 2016/8/29.
 */

/**
 * database:103.214.195.78  pangxie520
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
        CrashHandler.getInstance().init(mInstance);
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("my.db").setDatabaseVersion(1).create();
        ActiveAndroid.initialize(dbConfiguration);
    }
}
