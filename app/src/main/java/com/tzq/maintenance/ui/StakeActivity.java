package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.Stake;
import com.tzq.maintenance.utis.MyUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/5.
 */

public class StakeActivity extends BaseActivity {
    final int REQUEST_PHOTO_BEFORE = 103;
    final int REQUEST_PHOTO_CONSTRUCTION = 104;
    final int REQUEST_PHOTO_AFTER = 105;

    Spinner mStakeSp;
    EditText mStakeNum1Et, mStakeNum2Et;
    ImageView mBeforeIv1, mBeforeIv2;
    ImageView mConsIv1, mConsIv2;
    ImageView mAfterIv1, mAfterIv2;
    Stake mBean;
    boolean mEditable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stake_activity);
        setTitle("子桩号");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStakeSp = (Spinner) findViewById(R.id.stake_sp);
        mStakeNum1Et = (EditText) findViewById(R.id.stake_num1_et);
        mStakeNum2Et = (EditText) findViewById(R.id.stake_num2_et);
        mBeforeIv1 = (ImageView) findViewById(R.id.before_iv1);
        mBeforeIv2 = (ImageView) findViewById(R.id.before_iv2);
//        mBeforeMoreIv = (ImageView) findViewById(R.id.before_more_iv);
        mConsIv1 = (ImageView) findViewById(R.id.construction_iv1);
        mConsIv2 = (ImageView) findViewById(R.id.construction_iv2);
//        mConsMoreIv = (ImageView) findViewById(R.id.construction_more_iv);
        mAfterIv1 = (ImageView) findViewById(R.id.after_iv1);
        mAfterIv2 = (ImageView) findViewById(R.id.after_iv2);
//        mAfterMoreIv = (ImageView) findViewById(R.id.after_more_iv);

        mBean = (Stake) getIntent().getSerializableExtra("stake");
        mEditable = getIntent().getBooleanExtra("editable", true);
        if (mBean == null) {
            mBean = new Stake();
        }

        MyUtil.setUpSp(mAct, mStakeSp, Config.STAKES);
        mStakeSp.setSelection(MyUtil.getNoticeStakeIndex(mBean.stake_ud));

        MyUtil.showImage(mBean.getBeforeNewPics(), mBeforeIv1, mBeforeIv2);
        MyUtil.showImage(mBean.getConstructionNewPics(), mConsIv1, mConsIv2);
        MyUtil.showImage(mBean.getAfterNewPics(), mAfterIv1, mAfterIv2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEditable) {
            getMenuInflater().inflate(R.menu.stake_act, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                if (fullData()) {
                    setResult(RESULT_OK, new Intent().putExtra("stake", mBean));
                    finish();
                }

                break;
            case R.id.action_delete:
                setResult(Config.RESULT_DELETE, new Intent().putExtra("stake", mBean));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.brfore_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mBean.getBeforeNewPics()).putExtra("editable", mEditable), REQUEST_PHOTO_BEFORE);
                break;
            case R.id.construction_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mBean.getConstructionNewPics()).putExtra("editable", mEditable), REQUEST_PHOTO_CONSTRUCTION);
                break;
            case R.id.after_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mBean.getAfterNewPics()).putExtra("editable", mEditable), REQUEST_PHOTO_AFTER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO_BEFORE) {
            if (resultCode == RESULT_OK) {
                mBean.getBeforeNewPics().clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mBean.getBeforeNewPics().addAll(uris);
                MyUtil.showImage(mBean.getBeforeNewPics(), mBeforeIv1, mBeforeIv2);
            }
        } else if (requestCode == REQUEST_PHOTO_CONSTRUCTION) {
            if (resultCode == RESULT_OK) {
                mBean.getConstructionNewPics().clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mBean.getConstructionNewPics().addAll(uris);
                MyUtil.showImage(mBean.getConstructionNewPics(), mConsIv1, mConsIv2);
            }
        } else if (requestCode == REQUEST_PHOTO_AFTER) {
            if (resultCode == RESULT_OK) {
                mBean.getAfterNewPics().clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mBean.getAfterNewPics().addAll(uris);
                MyUtil.showImage(mBean.getAfterNewPics(), mAfterIv1, mAfterIv2);
            }
        }
    }


    private boolean fullData() {
        mBean.stake_ud = ((NormalBean) mStakeSp.getSelectedItem()).name;
        mBean.stake_num1 = mStakeNum1Et.getText().toString();
        mBean.stake_num2 = mStakeNum2Et.getText().toString();
        if (Util.isEmpty(mBean.stake_num1)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n1 = Float.valueOf(mBean.stake_num1);
            if (String.valueOf((int) n1).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }

        if (Util.isEmpty(mBean.stake_num2)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n2 = Float.valueOf(mBean.stake_num2);
            if (String.valueOf((int) n2).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }
        return true;
    }

}
