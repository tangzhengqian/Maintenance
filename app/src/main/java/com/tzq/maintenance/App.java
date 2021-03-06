package com.tzq.maintenance;

import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.tzq.common.core.PrefsManager;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CoreService;
import com.tzq.maintenance.core.CrashHandler;
import com.tzq.maintenance.core.HeartbeatAlarmManager;
import com.tzq.maintenance.core.NotifyManager;

import java.io.File;

/**
 * Created by Administrator on 2016/8/29.
 */

/**
 * database:103.214.195.78  pangxie520
 */
public class App extends com.activeandroid.app.Application {
    private static App mInstance;
    private User user;


    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CrashHandler.getInstance().init(mInstance);
        HeartbeatAlarmManager.init(this);
        HeartbeatAlarmManager.getInstance().scheduleAlarm();
        PrefsManager.init(mInstance);
        NotifyManager.init(this);

        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("getInstance.db").setDatabaseVersion(1).create();
        ActiveAndroid.initialize(dbConfiguration);
        startService(new Intent(mInstance, CoreService.class));

        File crashFile = new File(Config.crash_info_file);
        if (crashFile.exists()) {
            long lastTime = crashFile.lastModified();
            if (System.currentTimeMillis() - lastTime > 1000 * 60 * 60 * 24 * 6) {//6 days
                crashFile.delete();
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }


    synchronized public User getUser() {
        if (user == null) {
            String json = PrefsManager.getInstance().getString(Config.prefs_key_login_user);
            if (!Util.isEmpty(json)) {
                user = Config.gson.fromJson(json, User.class);
            } else {
                user = new User();
            }
        }
        return user;
    }

    synchronized public void setUser(User user) {
        this.user = user;
        if (user == null) {
            PrefsManager.getInstance().save(Config.prefs_key_login_user, "");
        } else {
            PrefsManager.getInstance().save(Config.prefs_key_login_user, Config.gson.toJson(user));
        }
    }
}
