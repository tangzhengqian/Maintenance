package com.tzq.maintenance.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.bean.NewTime;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.SyncUtil;

/**
 * Created by Administrator on 2016/10/26.
 */

public class CoreService extends Service {
    public final static String ACTION_HEART_BEAT = "com.tzq.action.heart";
    private final static int what_sync_data = 0x11;
    private final static int what_check_time = 0x12;
    private final static int delay_sync_data = 20 * 60 * 1000;
    private final static int delay_check_time = 10 * 1000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case what_sync_data:
                    SyncUtil.startSync();
                    syncData();
                    break;
                case what_check_time:
                    if (App.getInstance().getUser() != null && !Util.isEmpty(App.getInstance().getUser().token)) {
                        new HttpTask(Config.url_get_new_time).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                            @Override
                            public void onComplete(ResponseData responseData) {
                                if (responseData.isSuccess()) {
                                    NewTime newTime = new Gson().fromJson(responseData.data, NewTime.class);
                                    NewTime pNewTime = MyUtil.getNewTime();
                                    if (newTime != null) {
                                        if (pNewTime == null) {
                                            NotifyManager.getInstance().notifyNewNotice();
                                            NotifyManager.getInstance().notifyNewCheck();
                                        } else {
                                            if (newTime.noticeCreateTime.compareTo(pNewTime.noticeCreateTime) > 0) {
                                                NotifyManager.getInstance().notifyNewNotice();
                                            }
                                            if (newTime.checkCreateTime.compareTo(pNewTime.checkCreateTime) > 0) {
                                                NotifyManager.getInstance().notifyNewCheck();
                                            }
                                        }
                                        MyUtil.saveNewTime(newTime);
                                    }

                                }
                                checkTime();
                            }
                        }).enqueue(null);
                    }

                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("onCreate");
        syncData();
        checkTime();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        LogUtil.i("onStartCommand  action=" + action);
        if (ACTION_HEART_BEAT.equals(action)) {
            HeartbeatAlarmManager.getInstance().scheduleAlarm();
        }
        return START_NOT_STICKY;
    }

    private void syncData() {
        mHandler.removeMessages(what_sync_data);
        mHandler.sendEmptyMessageDelayed(what_sync_data, delay_sync_data);
    }

    private void checkTime() {
        mHandler.removeMessages(what_check_time);
        mHandler.sendEmptyMessageDelayed(what_check_time, delay_check_time);
    }
}
