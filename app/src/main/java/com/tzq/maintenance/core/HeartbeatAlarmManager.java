package com.tzq.maintenance.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.tzq.common.utils.LogUtil;

/**
 * Created by Administrator on 2016/11/22.
 */

public final class HeartbeatAlarmManager {
    private static final String TAG = "HeartbeatAlarmManager";
    private static final long HEART_DELAY = 20 * 1000;
    private static HeartbeatAlarmManager sHeartbeatAlarmManager;

    private Context mContext;
    private PendingIntent mAlarmIntent;

    private HeartbeatAlarmManager(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        sHeartbeatAlarmManager = new HeartbeatAlarmManager(context);
    }

    public static HeartbeatAlarmManager getInstance() {
        return sHeartbeatAlarmManager;
    }

    public void scheduleAlarm() {
        cancelAlarm();
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, CoreService.class);
        mAlarmIntent = PendingIntent.getService(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + HEART_DELAY, mAlarmIntent);

        LogUtil.i("schedule alarm ");
    }

    public void cancelAlarm() {
        if (mAlarmIntent == null) {
            return;
        }

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(mAlarmIntent);
    }
}
