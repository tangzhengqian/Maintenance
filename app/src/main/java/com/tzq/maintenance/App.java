package com.tzq.maintenance;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.tzq.common.core.PrefsManager;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CrashHandler;

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
        PrefsManager.init(mInstance);
        CrashHandler.getInstance().init(mInstance);
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("getInstance.db").setDatabaseVersion(1).create();
        ActiveAndroid.initialize(dbConfiguration);
//        startService(new Intent(mInstance, CoreService.class));

        new Select().from(Company.class).execute();
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
