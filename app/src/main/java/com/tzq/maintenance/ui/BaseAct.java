package com.tzq.maintenance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by zqtang on 16/8/30.
 */
public abstract class BaseAct extends AppCompatActivity{
    protected Activity mAct;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getlayoutId());
        mAct=this;
        ButterKnife.bind(this);
        onCreate();
    }
    abstract int getlayoutId();
    abstract void onCreate();

}
