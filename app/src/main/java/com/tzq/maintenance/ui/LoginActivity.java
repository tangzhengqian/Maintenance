package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tzq.common.utils.LogUtil;
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

    public void onClick(View view){
        LogUtil.i("--onClick "+view.getId());
        switch (view.getId()){
            case R.id.login_bt:
                startActivity(new Intent(mAct,MainActivity.class));
                finish();
                break;
        }
    }
}
