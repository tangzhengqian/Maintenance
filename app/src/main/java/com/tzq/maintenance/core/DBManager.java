package com.tzq.maintenance.core;

import android.content.Context;

import com.tzq.maintenance.bean.DaoMaster;
import com.tzq.maintenance.bean.DaoSession;

/**
 * Created by zqtang on 16/8/30.
 */
public class DBManager {
    private final static String dbName = "my_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    public static void init(Context context) {
        mInstance = new DBManager(context);
    }
    public static DBManager get() {
        return mInstance;
    }

    public DaoSession getDaoSession() {
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        return daoMaster.newSession();
    }

}
