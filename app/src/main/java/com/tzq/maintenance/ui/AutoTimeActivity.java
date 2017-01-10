package com.tzq.maintenance.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.AutoTime;
import com.tzq.maintenance.utis.MyUtil;

/**
 * Created by Administrator on 2016/10/31.
 */

public class AutoTimeActivity extends BaseActivity {
    final int REQUEST_DETAIL = 101;
    final int REQUEST_PHOTO = 102;
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
        return true;
    }


    private void save() {
//        mBean.clause = mClauseEt.getText().toString();
//        mBean.content = mContentEt.getText().toString();
//        mBean.withholding = mWithholdEt.getText().toString();
//        mBean.notes = mNoteEt.getText().toString();
//        mBean.date = mDateTv.getText().toString();

//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("id", mBean.id + "")
//                .add("clause", mBean.clause)
//                .add("content", mBean.content + "")
//                .add("withholding", mBean.withholding + "")
//                .add("date", mBean.date + "")
//                .add("notes", mBean.notes + "");
//
//        new HttpTask(Config.url_contract_save).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
//            @Override
//            public void onComplete(ResponseData responseData) {
//                if (responseData.isSuccess()) {
//                    MyUtil.toast("保存成功");
//        setResult(RESULT_OK);
//                    finish();
//                }
//            }
//        }).enqueue(builder.build());
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
