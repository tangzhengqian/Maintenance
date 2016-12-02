package com.tzq.maintenance.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Contract;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class ContractActivity extends BaseActivity {
    final int REQUEST_DETAIL = 101;
    final int REQUEST_PHOTO = 102;
    Contract mContract;
    EditText mClauseEt, mContentEt, mWithholdEt, mNoteEt;
    TextView mDateTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_activity);
        setTitle("违约金");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClauseEt = (EditText) findViewById(R.id.clause_et);
        mContentEt = (EditText) findViewById(R.id.content_et);
        mWithholdEt = (EditText) findViewById(R.id.withholding_et);
        mNoteEt = (EditText) findViewById(R.id.note_et);
        mDateTv = (TextView) findViewById(R.id.date_tv);

        mContract = (Contract) getIntent().getSerializableExtra("contract");

        if (mContract == null) {
            mContract = new Contract();
            mContract.id = -1;
        }

        mClauseEt.setText(mContract.clause);
        mContentEt.setText(mContract.content);
        mWithholdEt.setText(mContract.withholding);
        mNoteEt.setText(mContract.notes);
        mDateTv.setText(mContract.date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contract_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNoticeDetail(int noticeId) {
        new HttpTask(Config.url_notice_detail).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {

                }
            }
        }).enqueue(new FormBody.Builder()
                .add("id", noticeId + "")
                .build());
    }


    private void save() {
        mContract.clause = mClauseEt.getText().toString();
        mContract.content = mContentEt.getText().toString();
        mContract.withholding = mWithholdEt.getText().toString();
        mContract.notes = mNoteEt.getText().toString();
        mContract.date = mDateTv.getText().toString();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("clause", mContract.clause)
                .add("content", mContract.content + "")
                .add("withholding", mContract.withholding + "")
                .add("date", mContract.date + "")
                .add("notes", mContract.notes + "");

        new HttpTask(Config.url_contract_save).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("保存成功");
                    finish();
                }
            }
        }).enqueue(builder.build());
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.date_tv:
                MyUtil.showDateTimeDialog(mAct, mDateTv);
                break;
        }
    }

}
