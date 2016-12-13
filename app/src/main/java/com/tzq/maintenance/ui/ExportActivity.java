package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

/**
 * Created by Administrator on 2016/11/28.
 */

public class ExportActivity extends BaseActivity {

    Spinner mTypeSp;
    TextView mStartDateTv, mEndDateTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_activity);
        setTitle("报表导出");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTypeSp = (Spinner) findViewById(R.id.type_sp);
        mStartDateTv = (TextView) findViewById(R.id.start_date_tv);
        mEndDateTv = (TextView) findViewById(R.id.end_date_tv);

        MyUtil.setUpSp(mAct, mTypeSp, Config.CATES);
    }

    private void download(String url, String fileName) {
        String cate = ((NormalBean) mTypeSp.getSelectedItem()).id;
        String startDate = mStartDateTv.getText().toString();
        String endDate = mEndDateTv.getText().toString();
        String surl = url + "?start_time=" + startDate + "&end_time=" + endDate + "&cate=" + cate;
        new HttpTask(surl).setActivity(mAct).setProgressMsg("正在下载...").addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("下载成功，路径：" + responseData.data);
                } else {
                    MyUtil.toast("下载失败");
                }
            }
        }).download(Config.exportDirPath,fileName);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view) {
            startActivity(new Intent(mAct, ExportListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewClick(View view) {
        String startDate=mStartDateTv.getText().toString();
        String endDate=mEndDateTv.getText().toString();
        switch (view.getId()) {
            case R.id.start_date_tv:
                MyUtil.showDateTimeDialog(mAct, mStartDateTv);
                break;
            case R.id.end_date_tv:
                MyUtil.showDateTimeDialog(mAct, mEndDateTv);
                break;
            case R.id.export1_bt:
                download(Config.url_export1, String.format("计量支付审核表_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export2_bt:
                download(Config.url_export2,  String.format("计量支付报表封面_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export3_bt:
                download(Config.url_export3,  String.format("编制说明_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export4_bt:
                download(Config.url_export4,  String.format("财务支付报表_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export5_bt:
                download(Config.url_export5,  String.format("支付报表_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export6_bt:
                download(Config.url_export6,  String.format("支付汇总表_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export7_bt:
                download(Config.url_export7,  String.format("道路保洁、巡查费用计算表_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export8_bt:
                download(Config.url_export8,  String.format("违约金_%s_%s.xls",startDate,endDate));
                break;
            case R.id.export9_bt:
                download(Config.url_export9,  String.format("巡查日志_%s_%s.xls",startDate,endDate));
                break;
        }
    }
}
