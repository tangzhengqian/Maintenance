package com.tzq.maintenance.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.utis.SyncUtil;

/**
 * Created by Administrator on 2016/10/26.
 */

public class CoreService extends Service {
    private final static int what_sync_data = 0x11;
    private final static int delay_sync_data = 5 * 60 * 1000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case what_sync_data:
                    SyncUtil.startSync();
                    sendEmptyMessageDelayed(what_sync_data, delay_sync_data);
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
        handler.sendEmptyMessage(what_sync_data);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        LogUtil.i("onStartCommand  action=" + action);
        return START_NOT_STICKY;
    }
}
