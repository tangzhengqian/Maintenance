package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.Notice;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class NoticeActivity extends BaseActivity {
    final int REQUEST_DETAIL = 101;
    final int REQUEST_PHOTO = 102;
    Notice mNotice;
    Spinner mTypeSp, mStakeSp, structureSp;
    EditText mStakeNum1Et, mStakeNum2Et, mProjectNameEt, mDateEt, mDaysEt, mCostEt;
    ImageView mBeforeIv1, mBeforeIv2, mBeforeMoreIv;
    LinearLayout mDetailListLay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);
        setTitle("通知单");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mDetailListLay = (LinearLayout) findViewById(R.id.detail_list_lay);

        mNotice = (Notice) getIntent().getSerializableExtra("notice");
        MyUtil.setUpSp(mAct, mTypeSp, Config.CATES);
        MyUtil.setUpSp(mAct, structureSp, new Select().from(Structure.class).execute());
        update();
    }


    private void getNoticeDetail(int noticeId) {
        new HttpTask(Config.url_notice_detail).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, String data, String msg) {
                if (isSuccess) {

                }
            }
        }).start(new FormBody.Builder()
                .add("id", noticeId + "")
                .build());
    }


    private void update() {
        if (mNotice == null) {
            mNotice = new Notice();
            setTitle("新建通知单");
            mDetailListLay.removeAllViews();
            mTypeSp.setEnabled(true);
            mStakeSp.setEnabled(true);
            structureSp.setEnabled(true);
            mStakeNum1Et.setEnabled(true);
            mStakeNum2Et.setEnabled(true);
            mProjectNameEt.setEnabled(true);
            mDaysEt.setEnabled(true);
            mDateEt.setEnabled(true);
            mCostEt.setEnabled(true);
            findViewById(R.id.add_detail_iv).setVisibility(View.VISIBLE);
        } else {
            setTitle("通知单详情");
//            mTypeSp.setEnabled(false);
//            mStakeSp.setEnabled(false);
//            structureSp.setEnabled(false);
//            mStakeNum1Et.setEnabled(false);
//            mStakeNum2Et.setEnabled(false);
//            mProjectNameEt.setEnabled(false);
//            mDaysEt.setEnabled(false);
//            mDateEt.setEnabled(false);
//            mCostEt.setEnabled(false);
//            findViewById(R.id.add_detail_iv).setVisibility(View.INVISIBLE);

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
            List<Detail> detailList = mNotice.detail;
            mDetailListLay.removeAllViews();
            if (!Util.isEmpty(detailList)) {
                for (Detail detail : detailList) {
                    addDetailView(detail);
                }
            }
        }
    }

    private void addDetailView(final Detail detail) {
        if (detail == null) {
            return;
        }
        View view = View.inflate(mAct, R.layout.notice_detail_lay, null);
        view.setTag(detail);
        TextView typeTv = (TextView) view.findViewById(R.id.detail_type_tv);
        TextView nameTv = (TextView) view.findViewById(R.id.detail_name_tv);
        TextView costTv = (TextView) view.findViewById(R.id.detail_cost_tv);
        typeTv.setText(MyUtil.getDetailType(detail.detail_name_cate).cate_name);
        nameTv.setText(MyUtil.getDetail(detail.detail_id).detail_name);
        costTv.setText(detail.detail_all_price);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("type", DetailActivity.TYPE_NOTICE_DETAIL).putExtra("detail", detail), REQUEST_DETAIL);
            }
        });
        mDetailListLay.addView(view);
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
        switch (view.getId()) {
            case R.id.add_detail_iv:
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("type", DetailActivity.TYPE_NOTICE_DETAIL), REQUEST_DETAIL);
                break;
            case R.id.brfore_pic_lay:
                String[] beforePicUrls = mNotice.before_pic.split(",");
                ArrayList<String> list = new ArrayList<>();
                for (String s : beforePicUrls) {
                    list.add(s);
                }
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("urls", list), REQUEST_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DETAIL) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                addDetailView(detail);
            }

        }
    }
}
