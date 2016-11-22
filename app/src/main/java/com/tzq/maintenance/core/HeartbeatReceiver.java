package com.tzq.maintenance.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tzq.common.utils.LogUtil;

/**
 * Created by Administrator on 2016/11/23.
 */

public class HeartbeatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("onReceive");
        HeartbeatAlarmManager.getInstance().scheduleAlarm();
    }
}
