package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.tzq.common.utils.AppUtil;
import com.tzq.maintenance.R;

import butterknife.BindView;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.showTv)
    TextView mShowTv;

    @Override
    int getlayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    void onCreated(Bundle savedInstanceState) {
        try {
            mShowTv.setText("施工养护系统\t" + AppUtil.getPackageInfo(mAct, mAct.getPackageName()).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mAct, LoginActivity.class));
                finish();
            }
        }, 1500);
    }
}
