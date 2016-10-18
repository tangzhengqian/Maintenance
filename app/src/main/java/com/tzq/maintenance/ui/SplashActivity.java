package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tzq.maintenance.R;


public class SplashActivity extends BaseActivity {
    private TextView showTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showTv = (TextView) findViewById(R.id.showTv);
        try {
            showTv.setText("施工养护系统\t" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
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

    @Override
    public void onClick(View view) {

    }
}
