package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.tzq.common.utils.NumberUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.DetailType;
import com.tzq.maintenance.utis.MyUtil;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/11/1.
 */

public class DetailActivity extends BaseActivity {
    Spinner mTypeSp, mNameSp;
    EditText mPriceEt, mUnitEt, mQuantity1Et, mQuantity2Et, mQuantity3Et, mCostEt;

    List<DetailType> mDetailTypeList = new Select().from(DetailType.class).execute();
    List<Detail> mDetailList;
    Detail mDetail = new Detail();
    boolean mEditable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        setTitle("细目");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTypeSp = (Spinner) findViewById(R.id.detail_type_sp);
        mNameSp = (Spinner) findViewById(R.id.detail_sp);
        mPriceEt = (EditText) findViewById(R.id.detail_price_et);
        mUnitEt = (EditText) findViewById(R.id.detail_unit_et);
        mQuantity1Et = (EditText) findViewById(R.id.detail_q1_et);
        mQuantity2Et = (EditText) findViewById(R.id.detail_q2_et);
        mQuantity3Et = (EditText) findViewById(R.id.detail_q3_et);
        mCostEt = (EditText) findViewById(R.id.detail_cost_et);

        reset();
        mDetail = (Detail) getIntent().getSerializableExtra("detail");
        mEditable = getIntent().getBooleanExtra("editable", true);
        MyUtil.setUpSp(mAct, mTypeSp, mDetailTypeList);
        mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(0).id).execute();
        MyUtil.setUpSp(mAct, mNameSp, mDetailList);
        if (mDetail != null) {
            mTypeSp.setSelection(MyUtil.getDetailTypeIndex(mDetailTypeList, mDetail.cate_id));
            mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(mTypeSp.getSelectedItemPosition()).id).execute();
            MyUtil.setUpSp(mAct, mNameSp, mDetailList);
            mNameSp.setSelection(MyUtil.getDetailIndex(mDetailList, mDetail.id));
            mPriceEt.setText(mDetail.detail_price);
            mUnitEt.setText(mDetail.detail_unit);
            mQuantity1Et.setText(mDetail.detail_quantities1);
            mQuantity2Et.setText(mDetail.detail_quantities2);
            mQuantity3Et.setText(mDetail.detail_quantities3);
            mCostEt.setText(mDetail.detail_all_price);
        } else {
            mDetail = new Detail();
            mDetail.id = -1;
            mTypeSp.setSelection(0);
            mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(mTypeSp.getSelectedItemPosition()).id).execute();
            MyUtil.setUpSp(mAct, mNameSp, mDetailList);
//            mNameSp.setSelection(0);
            mPriceEt.setText(((Detail) mNameSp.getSelectedItem()).detail_already_price);
            mUnitEt.setText(((Detail) mNameSp.getSelectedItem()).detail_unit);
        }
        cal();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSelectListener();
            }
        }, 2000);

    }

    private void setSelectListener() {
        mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(mTypeSp.getSelectedItemPosition()).id).execute();
                MyUtil.setUpSp(mAct, mNameSp, mDetailList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mDetailList != null && mDetailList.size() > position) {
                    mDetail = mDetailList.get(position);
                    if (mDetail != null) {
                        mUnitEt.setText(mDetail.detail_unit);
                        mPriceEt.setText(mDetail.detail_already_price);
                        cal();
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEditable) {
            getMenuInflater().inflate(R.menu.menu_detail_act, menu);
        }
        return true;
    }

    private void cal(){
        double p= NumberUtil.strToDouble(mPriceEt.getText().toString());
        double n1=1;
        if(!Util.isEmpty(mQuantity1Et.getText().toString())){
            n1=NumberUtil.strToDouble(mQuantity1Et.getText().toString());
        }
        double n2=1;
        if(!Util.isEmpty(mQuantity2Et.getText().toString())){
            n2=NumberUtil.strToDouble(mQuantity2Et.getText().toString());
        }
        double n3=1;
        if(!Util.isEmpty(mQuantity3Et.getText().toString())){
            n3=NumberUtil.strToDouble(mQuantity3Et.getText().toString());
        }
        double cost=p*n1*n2*n3;
        mCostEt.setText(""+NumberUtil.doubleToStr(cost,2));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            mDetail.id = ((Detail) mNameSp.getSelectedItem()).id;
            mDetail.cate_id = ((DetailType) mTypeSp.getSelectedItem()).id;
            mDetail.detail_price = mPriceEt.getText().toString();
            mDetail.detail_unit = mUnitEt.getText().toString();
            mDetail.detail_quantities1 = mQuantity1Et.getText().toString();
            mDetail.detail_quantities2 = mQuantity2Et.getText().toString();
            mDetail.detail_quantities3 = mQuantity3Et.getText().toString();
            mDetail.detail_all_price = mCostEt.getText().toString();
            setResult(RESULT_OK, new Intent().putExtra("detail", mDetail));
            finish();
            return true;
        } else if (id == R.id.action_delete) {
            setResult(Config.RESULT_DELETE, new Intent().putExtra("detail", mDetail));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        mDetail = null;
        mPriceEt.setText("");
        mUnitEt.setText("");
        mQuantity1Et.setText("");
        mQuantity2Et.setText("");
        mQuantity3Et.setText("");
        mCostEt.setText("");
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.refreshBt:
                cal();
                break;
        }

    }
}
