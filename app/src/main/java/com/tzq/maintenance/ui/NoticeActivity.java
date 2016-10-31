package com.tzq.maintenance.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Notice;
import com.tzq.maintenance.bean.NoticeType;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.utis.MyUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class NoticeActivity extends BaseActivity {
    private Notice mNotice;
    Spinner mTypeSp, mStakeSp, structureSp;
    EditText mStakeNum1Et, mStakeNum2Et, mProjectNameEt, mDateEt, mDaysEt, mCostEt;
    ImageView mBeforeIv1, mBeforeIv2, mBeforeMoreIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);
        setTitle("通知单");
        mTypeSp = (Spinner) findViewById(R.id.type_sp);
        mStakeSp = (Spinner) findViewById(R.id.stake_sp);
        structureSp = (Spinner) findViewById(R.id.structure_sp);
        mStakeNum1Et = (EditText) findViewById(R.id.stake_num1_et);
        mStakeNum2Et = (EditText) findViewById(R.id.stake_num2_et);
        mProjectNameEt = (EditText) findViewById(R.id.name_et);
        mDaysEt = (EditText) findViewById(R.id.days_et);
        mDateEt = (EditText) findViewById(R.id.date_et);
        mCostEt = (EditText) findViewById(R.id.cost_et);
        mBeforeIv1 = (ImageView) findViewById(R.id.before_iv1);
        mBeforeIv2 = (ImageView) findViewById(R.id.before_iv2);
        mBeforeMoreIv = (ImageView) findViewById(R.id.before_more_iv);


        mNotice = (Notice) getIntent().getSerializableExtra("notice");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<NoticeType> cateAdapter = new ArrayAdapter<NoticeType>(this, android.R.layout.simple_spinner_item, Config.CATES);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSp.setAdapter(cateAdapter);

        List<Structure> structureList = new Select().from(Structure.class).execute();
        ArrayAdapter<Structure> structureAdapter = new ArrayAdapter<Structure>(this, android.R.layout.simple_spinner_item, structureList);
        structureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        structureSp.setAdapter(structureAdapter);

        update();
    }



    private void update() {
        if (mNotice == null) {
            mNotice = new Notice();
            setTitle("新建通知单");
        } else {
            setTitle("通知单详情");
            mTypeSp.setEnabled(false);
            mStakeSp.setEnabled(false);
            structureSp.setEnabled(false);
            mStakeNum1Et.setEnabled(false);
            mStakeNum2Et.setEnabled(false);
            mProjectNameEt.setEnabled(false);
            mDaysEt.setEnabled(false);
            mDateEt.setEnabled(false);
            mCostEt.setEnabled(false);

            mTypeSp.setSelection(MyUtil.getNoticeCateIndex(mNotice.cate));
            mStakeSp.setSelection(getStakeIndex());
            structureSp.setSelection(MyUtil.getStructureIndex(mNotice.structure_id));
            mStakeNum1Et.setText(mNotice.stake_num1);
            mStakeNum2Et.setText(mNotice.stake_num2);
            mProjectNameEt.setText(mNotice.project_name);
            mCostEt.setText(mNotice.project_cost);
            mDateEt.setText(mNotice.created_at);
            mDaysEt.setText(mNotice.days);
            String[] beforePicUrls = mNotice.before_pic.split(",");
            if (beforePicUrls.length >= 1) {
                MyUtil.displayPic(mBeforeIv1, beforePicUrls[0]);
            }
            if (beforePicUrls.length >= 2) {
                MyUtil.displayPic(mBeforeIv2, beforePicUrls[2]);
            }
        }
    }

    private int getStakeIndex() {
        String[] a = getResources().getStringArray(R.array.notice_stake);
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(mNotice.stake_ud)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onViewClick(View view) {

    }
}
