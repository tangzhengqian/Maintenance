package com.tzq.maintenance.core;

import android.content.Context;

import com.tzq.common.utils.LogUtil;

/**
 * Created by Administrator on 2016/10/23.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtil.e(ex.getMessage(), ex);
        mDefaultHandler.uncaughtException(thread, ex);
    }
}
