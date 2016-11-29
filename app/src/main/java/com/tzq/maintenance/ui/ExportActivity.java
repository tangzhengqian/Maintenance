package com.tzq.maintenance.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
        new HttpTask(surl).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("下载成功，路径：" + responseData.data);
                } else {
                    MyUtil.toast("下载失败");
                }
            }
        }).download(fileName);
    }


    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.start_date_tv:
                MyUtil.showDateTimeDialog(mAct, mStartDateTv);
                break;
            case R.id.end_date_tv:
                MyUtil.showDateTimeDialog(mAct, mEndDateTv);
                break;
            case R.id.export1_bt:
                download(Config.url_export1,"计量支付审核表.xls");
                break;
            case R.id.export2_bt:
                download(Config.url_export2,"计量支付报表封面.xls");
                break;
            case R.id.export3_bt:
                download(Config.url_export3,"编制说明.xls");
                break;
            case R.id.export4_bt:
                download(Config.url_export4,"财务支付报表.xls");
                break;
            case R.id.export5_bt:
                download(Config.url_export5,"支付报表.xls");
                break;
            case R.id.export6_bt:
                download(Config.url_export6,"支付汇总表.xls");
                break;
            case R.id.export7_bt:
                download(Config.url_export7,"道路保洁、巡查费用计算表.xls");
                break;
            case R.id.export8_bt:
                download(Config.url_export8,"违约金.xls");
                break;
            case R.id.export9_bt:
                download(Config.url_export9,"巡查日志.xls");
                break;
        }
    }
}
