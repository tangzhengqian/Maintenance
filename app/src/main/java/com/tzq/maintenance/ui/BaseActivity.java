package com.tzq.maintenance.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.tzq.common.utils.AppUtil;
import com.tzq.common.utils.LogUtil;

/**
 * Created by zqtang on 16/8/30.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected Activity mAct;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct=this;
    }

    abstract public void onViewClick(View view);

    public void onClick(View view) {
        LogUtil.i("--onClick "+view.getId());
        switch (view.getId()){

        }
        onViewClick(view);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
