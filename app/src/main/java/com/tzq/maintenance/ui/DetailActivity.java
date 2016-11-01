package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.DetailType;
import com.tzq.maintenance.utis.MyUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/1.
 */

public class DetailActivity extends BaseActivity {
    public static final int TYPE_DETAIL = 0;
    public static final int TYPE_NOTICE_DETAIL = 0;
    Spinner mTypeSp, mNameSp;
    EditText mPriceEt, mUnitEt, mQuantity1Et, mQuantity2Et, mQuantity3Et, mCostEt;

    List<DetailType> mDetailTypeList = new Select().from(DetailType.class).execute();
    List<Detail> mDetailList;
    int mType;
    Detail mDetail = new Detail();

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

        mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i("---onItemSelected  " + position);
                reset();
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
                LogUtil.i("---onItemSelected  " + position);
                reset();
                if (mDetailList != null && mDetailList.size() > position) {
                    mDetail = mDetailList.get(position);
                    if (mDetail != null) {
                        mPriceEt.setText(mDetail.detail_already_price);
                        mUnitEt.setText(mDetail.detail_unit);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mType = getIntent().getIntExtra("type", TYPE_DETAIL);
        mDetail = (Detail) getIntent().getSerializableExtra("detail");
        MyUtil.setUpSp(mAct, mTypeSp, mDetailTypeList);
        mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(0).id).execute();
        MyUtil.setUpSp(mAct, mNameSp, mDetailList);
        if (mDetail != null && mType == TYPE_NOTICE_DETAIL) {
            mTypeSp.setSelection(MyUtil.getDetailTypeIndex(mDetailTypeList, mDetail.cate_id));
            mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(mTypeSp.getSelectedItemPosition()).id).execute();
            MyUtil.setUpSp(mAct, mNameSp, mDetailList);
            mNameSp.setSelection(MyUtil.getDetailIndex(mDetailList, mDetail.id));
            mPriceEt.setText(mDetail.detail_already_price);
            mUnitEt.setText(mDetail.detail_unit);
            if (mType == TYPE_NOTICE_DETAIL) {
                mPriceEt.setText(mDetail.detail_price);
                mUnitEt.setText(mDetail.detail_unit);
                mQuantity1Et.setText(mDetail.detail_quantities1);
                mQuantity2Et.setText(mDetail.detail_quantities2);
                mQuantity3Et.setText(mDetail.detail_quantities3);
                mCostEt.setText(mDetail.detail_all_price);
            } else {

            }
        } else {
            mDetail = new Detail();
            mDetailList = new Select().from(Detail.class).where("cate_id = " + mDetailTypeList.get(mTypeSp.getSelectedItemPosition()).id).execute();
            MyUtil.setUpSp(mAct, mNameSp, mDetailList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ok) {
            if (mType == TYPE_NOTICE_DETAIL) {
                mDetail.detail_price = mPriceEt.getText().toString();
                mDetail.detail_unit = mUnitEt.getText().toString();
                mDetail.detail_quantities1 = mQuantity1Et.getText().toString();
                mDetail.detail_quantities2 = mQuantity2Et.getText().toString();
                mDetail.detail_quantities3 = mQuantity3Et.getText().toString();
                mDetail.detail_all_price = mCostEt.getText().toString();
            }
            setResult(RESULT_OK, new Intent().putExtra("detail", mDetail));
            finish();
            return true;
        } else if (id == R.id.action_delete) {
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

    }
}
