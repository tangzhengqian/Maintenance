package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tzq.maintenance.R;

/**
 * Created by Administrator on 2016/9/5.
 */

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);
    }

    public void onViewClick(int id) {
        switch (id) {
            case R.id.login_bt:
                startActivity(new Intent(mAct, MainActivity.class));
                finish();
                break;
        }
    }
}
