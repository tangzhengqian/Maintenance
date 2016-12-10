package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tzq.common.core.PrefsManager;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CompleteListener;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.SyncUtil;


public class SplashActivity extends BaseActivity {
    private TextView showTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        showTv = (TextView) findViewById(R.id.showTv);
        try {
            showTv.setText("工程养护系统\t" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long syncTime = PrefsManager.getInstance().getLong(Config.prefs_key_sync_time);
        User user = App.getInstance().getUser();
        if (user.user_id > 0) {
            if (syncTime == 0 || System.currentTimeMillis() - syncTime > Config.sync_delay) {
                SyncUtil.sCompleteListeners.add(new CompleteListener() {
                    @Override
                    public void onComplete(Object data) {
                        gotoMain();
                    }

                    @Override
                    public void onFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyUtil.toast("同步数据资源出错!");
                                finish();
                            }
                        });
                    }
                });
                SyncUtil.startSync();
            }else {
                gotoMain();
            }
        }else {
            startActivity(new Intent(mAct, LoginActivity.class));
            finish();
        }
    }

    private void gotoMain() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mAct, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onViewClick(View view) {

    }

}
