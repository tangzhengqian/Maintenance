package com.tzq.maintenance.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Look;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class LookActivity extends BaseActivity {
    Look mBean;
    EditText titleEt,contentEt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_activity);
        setTitle("巡查记录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEt = (EditText) findViewById(R.id.titleEt);
        contentEt = (EditText) findViewById(R.id.contentEt);

        mBean = (Look) getIntent().getSerializableExtra("look");

        if (mBean == null) {
            mBean = new Look();
        } else {
            titleEt.setText(mBean.title);
            contentEt.setText(mBean.content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auto_time_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(mAct).setMessage("要删除？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK);
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void save() {
        mBean.title = titleEt.getText().toString();
        mBean.content = contentEt.getText().toString();
        if (TextUtils.isEmpty(mBean.title)) {
            MyUtil.toast("请输入标题");
            return;
        }
        if (TextUtils.isEmpty(mBean.content)) {
            MyUtil.toast("请输入内容");
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", mBean.id + "")
                .add("title", mBean.title)
                .add("content", mBean.content + "");

        new HttpTask(Config.url_look_save).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("保存成功");
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }).enqueue(builder.build());
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
        }
    }

}
