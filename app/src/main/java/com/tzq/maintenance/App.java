package com.tzq.maintenance;

import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.tzq.common.core.PrefsManager;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CoreService;
import com.tzq.maintenance.core.CrashHandler;
import com.tzq.maintenance.core.HeartbeatAlarmManager;
import com.tzq.maintenance.core.NotifyManager;

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

        new Select().from(Company.class).execute();
    }

//    public void start() {
//        V8 v8 = V8.createV8Runtime();
//        Console console = new Console();
//        V8Object v8Console = new V8Object(v8);
//        v8.add("console", v8Console);
//        v8Console.registerJavaMethod(console, "log", "log", new Class<?>[] { String.class });
//        v8Console.release();
//        v8.executeScript("console.log('hello, world');");
//    }
//
//    class Console {
//        public void log(final String message) {
//            LogUtil.i(message);
//        }
//    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
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
        this.user = user;
        if(user==null){
            PrefsManager.getInstance().save(Config.prefs_key_login_user, "");
        }else {
            PrefsManager.getInstance().save(Config.prefs_key_login_user, new Gson().toJson(user));
        }
    }
}
