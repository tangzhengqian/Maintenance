package com.tzq.maintenance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.tzq.common.utils.AppUtil;

/**
 * Created by zqtang on 16/8/30.
 */
public abstract class BaseActivity extends AppCompatActivity{
    protected Activity mAct;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct=this;
    }

    abstract public void onClick(View view);

    @Override
    protected void onPause() {
        AppUtil.hideImm(mAct);
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        AppUtil.hideImm(mAct);
        return super.onTouchEvent(event);
    }
}
