package com.tzq.maintenance;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.google.gson.Gson;
import com.tzq.common.core.PrefsManager;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CrashHandler;

/**
 * Created by Administrator on 2016/8/29.
 */

/**
 * database:103.214.195.78  pangxie520
 */
public class App extends Application {
    private static App mInstance;
    private User user;


    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PrefsManager.init(mInstance);
        CrashHandler.getInstance().init(mInstance);
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("getInstance.db").setDatabaseVersion(1).create();
        ActiveAndroid.initialize(dbConfiguration);

    }

    synchronized public User getUser() {
        if (user == null) {
            String json = PrefsManager.getInstance().getString(Config.prefs_key_login_user);
            if (!Util.isEmpty(json)) {
                user = new Gson().fromJson(json, User.class);
            } else {
                user = new User();
            }
        }
        return user;
    }

    synchronized public void setUser(User user) {
        PrefsManager.getInstance().save(Config.prefs_key_login_user, new Gson().toJson(user));
        this.user = user;
    }
}
