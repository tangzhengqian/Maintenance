package com.tzq.maintenance.ui;

import android.os.Bundle;

import com.tzq.maintenance.R;

/**
 * Created by Administrator on 2016/9/5.
 */

public class LoginActivity extends BaseActivity {
    @Override
    int getlayoutId() {
        return R.layout.activity_login;
    }

    @Override
    void onCreated(Bundle savedInstanceState) {
        setTitle("用户登录");
    }
}
