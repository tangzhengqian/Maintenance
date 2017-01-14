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
import android.widget.TextView;

import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.AutoTime;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class AutoTimeActivity extends BaseActivity {
    AutoTime mBean;
    TextView startTimeTv, endTimeTv;
    EditText noteEt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_time_activity);
        setTitle("自动签发");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startTimeTv = (TextView) findViewById(R.id.startTimeTv);
        endTimeTv = (TextView) findViewById(R.id.endTimeTv);
        noteEt = (EditText) findViewById(R.id.noteEt);

        mBean = (AutoTime) getIntent().getSerializableExtra("autoTime");

        if (mBean == null) {
            mBean = new AutoTime();
        } else {
            startTimeTv.setText(mBean.start_time);
            endTimeTv.setText(mBean.end_time);
            noteEt.setText(mBean.mark);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.auto_time_activity, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        if (mBean.id > 0) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
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
                        if (mBean.id > 0) {
                            new HttpTask(Config.url_autotime_delete + "?id=" + mBean.id).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                                @Override
                                public void onComplete(ResponseData responseData) {
                                    if (responseData.isSuccess()) {
                                        MyUtil.toast("删除成功");
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            }).enqueue();
                        } else {
                            MyUtil.toast("删除成功");
                            setResult(RESULT_OK);
                            finish();
                        }

                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void save() {
        mBean.mark = noteEt.getText().toString();
        mBean.start_time = startTimeTv.getText().toString();
        mBean.end_time = endTimeTv.getText().toString();
        if (TextUtils.isEmpty(mBean.mark)) {
            MyUtil.toast("请输入备注");
            return;
        }
        if (TextUtils.isEmpty(mBean.start_time) || TextUtils.isEmpty(mBean.end_time)) {
            MyUtil.toast("请输入时间段");
            return;
        }
        if (mBean.end_time.compareTo(mBean.start_time) <= 0) {
            MyUtil.toast("结束时间应该大于开始时间");
            return;
        }

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", mBean.id + "")
                .add("mark", mBean.mark)
                .add("start_time", mBean.start_time + "")
                .add("end_time", mBean.end_time + "");

        new HttpTask(Config.url_autotime_save).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
            case R.id.startTimeTv:
                MyUtil.showDateTimeDialog(mAct, startTimeTv);
                break;
            case R.id.endTimeTv:
                MyUtil.showDateTimeDialog(mAct, endTimeTv);
                break;
        }
    }

}
