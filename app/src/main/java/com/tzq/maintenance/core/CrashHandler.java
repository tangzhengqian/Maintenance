package com.tzq.maintenance.core;

import android.content.Context;
import android.content.pm.PackageManager;

import com.tzq.common.utils.FileUtil;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        try {
            LogUtil.e(ex.getMessage(), ex);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss");
            String time = sdf.format(Calendar.getInstance().getTime());
            StringBuilder sb = new StringBuilder()
                    .append("------------" + time + "------------")
                    .append("\nversion:"+ App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0).versionName)
                    .append("\n")
                    .append(ex.getMessage())
                    .append("\n");
            for (StackTraceElement element : ex.getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
            FileUtil.writeStringToFile(new File(Config.crash_info_file), sb.toString(), true);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mDefaultHandler.uncaughtException(thread, ex);
    }
}
