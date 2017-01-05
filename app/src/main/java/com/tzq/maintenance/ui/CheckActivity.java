package com.tzq.maintenance.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.NumberUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Check;
import com.tzq.maintenance.bean.DealBean;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.bean.Stake;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class CheckActivity extends BaseActivity {
    final int REQUEST_DETAIL = 101;
    final int REQUEST_DETAIL_NEW = 102;
    final int REQUEST_STAKE = 106;
    final int REQUEST_TUZHI = 107;
    final int REQUEST_ATTACH = 108;
    Check mCheck;
    Spinner mTypeSp, mStakeSp, structureSp;
    EditText mStakeNum1Et, mStakeNum2Et, mProjectNameEt, mDaysEt, mCostEt;
    TextView mDateEt;
    ImageView mTuzhiIv1, mTuzhiIv2;
    ImageView mAttachIv1, mAttachIv2;
    LinearLayout mDetailListLay, mDetailNewListLay, mSubStakeListLay;
    DealBean mNoticeDealBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTypeSp = (Spinner) findViewById(R.id.type_sp);
        mStakeSp = (Spinner) findViewById(R.id.stake_sp);
        structureSp = (Spinner) findViewById(R.id.structure_sp);
        mStakeNum1Et = (EditText) findViewById(R.id.stake_num1_et);
        mStakeNum2Et = (EditText) findViewById(R.id.stake_num2_et);
        mProjectNameEt = (EditText) findViewById(R.id.name_et);
        mDaysEt = (EditText) findViewById(R.id.days_et);
        mDateEt = (TextView) findViewById(R.id.date_tv);
        mCostEt = (EditText) findViewById(R.id.cost_et);
        mTuzhiIv1 = (ImageView) findViewById(R.id.tuzhi_iv1);
        mTuzhiIv2 = (ImageView) findViewById(R.id.tuzhi_iv2);
        mAttachIv1 = (ImageView) findViewById(R.id.attach_iv1);
        mAttachIv2 = (ImageView) findViewById(R.id.attach_iv2);
        mDetailListLay = (LinearLayout) findViewById(R.id.detail_list_lay);
        mDetailNewListLay = (LinearLayout) findViewById(R.id.detail_new_list_lay);
        mSubStakeListLay = (LinearLayout) findViewById(R.id.substake_list_lay);

        mCheck = (Check) getIntent().getSerializableExtra("check");
        MyUtil.setUpSp(mAct, mTypeSp, Config.CATES);
        MyUtil.setUpSp(mAct, mStakeSp, Config.STAKES);
        MyUtil.setUpSp(mAct, structureSp, new Select().from(Structure.class).execute());
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!MyUtil.isCheckEditable(mCheck)) {
            return true;
        }
        getMenuInflater().inflate(R.menu.notice_act, menu);
        MenuItem dealNextMenu = menu.findItem(R.id.action_deal_next);
        MenuItem dealCancelMenu = menu.findItem(R.id.action_deal_cancel);
        MenuItem deleteMenu = menu.findItem(R.id.action_delete);
        MenuItem saveMenu = menu.findItem(R.id.action_save);

        if (App.getInstance().getUser().role_id == 1) {
            deleteMenu.setVisible(true);
        } else {
            deleteMenu.setVisible(false);
        }
        if (mCheck.id <= 0) {
            deleteMenu.setVisible(false);
        }
        if (mCheck.id <= 0) {
            dealNextMenu.setVisible(false);
            dealCancelMenu.setVisible(false);
        } else {
            mNoticeDealBean = MyUtil.getCheckDealStr(mCheck.step, App.getInstance().getUser().role_id);
            if (mNoticeDealBean != null) {
                LogUtil.i(mNoticeDealBean.toString());
            }
            if (mNoticeDealBean == null) {
                dealNextMenu.setVisible(false);
                dealCancelMenu.setVisible(false);
            } else {
                if (Util.isEmpty(mNoticeDealBean.nextStr)) {
                    dealNextMenu.setVisible(false);
                } else {
                    dealNextMenu.setVisible(true);
                    dealNextMenu.setTitle(mNoticeDealBean.nextStr);
                }
                if (Util.isEmpty(mNoticeDealBean.cancelStr)) {
                    dealCancelMenu.setVisible(false);
                } else {
                    dealCancelMenu.setVisible(true);
                    dealCancelMenu.setTitle(mNoticeDealBean.cancelStr);
                }
            }
        }
        saveMenu.setVisible(MyUtil.isCheckEditable(mCheck));
        deleteMenu.setVisible(MyUtil.isCheckEditable(mCheck));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                ProgressDialogUtil.show(mAct);
                new Thread() {
                    @Override
                    public void run() {
                        updatePic(mCheck.getTuzhiPicUris(), mCheck.getTuzhiNewPicUris());
                        updatePic(mCheck.getAttachPicUris(), mCheck.getAttachNewPicUris());

                        prepareCheck();
                        final boolean result = httpSave();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogUtil.hide(mAct);
                                if (result) {
                                    MyUtil.deleteOfflineCheck(mCheck);
                                    MyUtil.toast("保存成功");
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }
                        });
                    }
                }.start();

                break;
            case R.id.action_delete:
                new AlertDialog.Builder(mAct).setMessage("删除该验收单？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Util.isEmpty(mCheck.offlineId)) {
                            MyUtil.deleteOfflineCheck(mCheck);
                        }
                        if (mCheck.id > 0) {
                            new HttpTask(Config.url_check_delete + "?id=" + mCheck.id).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
            case R.id.action_deal_next:
                new HttpTask(Config.url_check_deal + "?id=" + mCheck.id + "&act=" + mNoticeDealBean.nextAct).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(ResponseData responseData) {
                        if (responseData.isSuccess()) {
                            MyUtil.toast("操作成功");
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }).enqueue();
                break;
            case R.id.action_deal_cancel:
                new HttpTask(Config.url_check_deal + "?id=" + mCheck.id + "&act=" + mNoticeDealBean.cancelAct).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(ResponseData responseData) {
                        if (responseData.isSuccess()) {
                            MyUtil.toast("操作成功");
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }).enqueue();
                break;
            case R.id.action_offline_save:
                prepareCheck();
                if (Util.isEmpty(mCheck.offlineId)) {
                    mCheck.offlineId = UUID.randomUUID().toString();
                }
                MyUtil.saveOfflineCheck(mCheck);
                MyUtil.toast("保存成功");
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updatePic(List<String> uris, ArrayList<String> newUris) {
        //del pic
        for (String uri : uris) {
            if (!newUris.contains(uri)) {
                new HttpTask(Config.url_del_pic).execute(new FormBody.Builder().add("picUrl", uri).build());
                uris.remove(uri);
            }
        }
        //upload pic
        for (String uri : newUris) {
            if (uri.startsWith("file://")) {
                String path = uri.substring("file://".length());
                File file = new File(path);
                RequestBody fileBody = RequestBody.create(MediaType.parse("file"), file);
                ResponseData responseData = new HttpTask(Config.url_add_pic).execute(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", file.getName(), fileBody)
                        .build());
                if (responseData.isSuccess()) {
                    uri = responseData.data;
                }
            }
        }
    }


    private void init() {
        setTitle("验收单详情");
        setEditable(MyUtil.isCheckEditable(mCheck));

        mTypeSp.setSelection(MyUtil.getNoticeCateIndex(mCheck.cate));
        mStakeSp.setSelection(MyUtil.getNoticeStakeIndex(mCheck.stake_ud));
        structureSp.setSelection(MyUtil.getStructureIndex(mCheck.structure_id));
        mStakeNum1Et.setText(mCheck.stake_num1);
        mStakeNum2Et.setText(mCheck.stake_num2);
        mProjectNameEt.setText(mCheck.project_name);
        mCostEt.setText(mCheck.project_cost);
        mDateEt.setText(mCheck.created_at);
//        mDaysEt.setText(mBean.days);

        List<Detail> detailList = mCheck.detail;
        mDetailListLay.removeAllViews();
        if (!Util.isEmpty(detailList)) {
            for (Detail detail : detailList) {
                addDetailView(detail);
            }
        }

        mDetailNewListLay.removeAllViews();
        if (!Util.isEmpty(mCheck.detail_new_edit)) {
            for (Detail detail : mCheck.detail_new_edit) {
                addDetailNewView(detail);
            }
        }

        mSubStakeListLay.removeAllViews();
        if (!Util.isEmpty(mCheck.sub_stakes)) {
            for (Stake stake : mCheck.sub_stakes) {
                addSubStakeView(stake);
            }
        }

        MyUtil.showImage(mCheck.getTuzhiNewPicUris(), mTuzhiIv1, mTuzhiIv2);
        MyUtil.showImage(mCheck.getAttachNewPicUris(), mAttachIv1, mAttachIv2);
    }


    private void setEditable(boolean b) {
        mTypeSp.setEnabled(b);
        mStakeSp.setEnabled(b);
        structureSp.setEnabled(b);
        mStakeNum1Et.setEnabled(b);
        mStakeNum2Et.setEnabled(b);
        mProjectNameEt.setEnabled(b);
        mDaysEt.setEnabled(b);
        mDateEt.setEnabled(b);
        mCostEt.setEnabled(b);
        if (b) {
            findViewById(R.id.add_detail_new_iv).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.add_detail_new_iv).setVisibility(View.INVISIBLE);
        }
    }

    private void addDetailView(final Detail detail) {
        if (detail == null) {
            return;
        }
        View view = null;
        int count = mDetailListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            if (childDetail.id == detail.id) {
                view = child;
            }
        }
        if (view == null) {
            view = View.inflate(mAct, R.layout.notice_detail_lay, null);
            mDetailListLay.addView(view);
        }
        view.setTag(detail);
        TextView typeTv = (TextView) view.findViewById(R.id.detail_type_tv);
        TextView nameTv = (TextView) view.findViewById(R.id.detail_name_tv);
        TextView costTv = (TextView) view.findViewById(R.id.detail_cost_tv);
        typeTv.setText(MyUtil.getDetailType(detail.cate_id).cate_name);
        nameTv.setText(MyUtil.getDetail(detail.id).detail_name);
        costTv.setText(detail.detail_all_price);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("detail", detail).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_DETAIL);
            }
        });
    }

    private void addDetailNewView(final Detail detail) {
        if (detail == null) {
            return;
        }
        View view = null;
        int count = mDetailNewListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailNewListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            if (childDetail.id == detail.id) {
                view = child;
            }
        }
        if (view == null) {
            view = View.inflate(mAct, R.layout.notice_detail_lay, null);
            mDetailNewListLay.addView(view);
        }
        view.setTag(detail);
        TextView typeTv = (TextView) view.findViewById(R.id.detail_type_tv);
        TextView nameTv = (TextView) view.findViewById(R.id.detail_name_tv);
        TextView costTv = (TextView) view.findViewById(R.id.detail_cost_tv);
        typeTv.setText(MyUtil.getDetailType(detail.cate_id).cate_name);
        nameTv.setText(MyUtil.getDetail(detail.id).detail_name);
        costTv.setText(detail.detail_all_price);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("detail", detail).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_DETAIL_NEW);
            }
        });
    }

    private void deleteDetailView(final Detail detail) {
        if (detail == null) {
            return;
        }
        int count = mDetailListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            if (childDetail.id == detail.id) {
                childDetail = detail;
                mDetailListLay.removeView(child);
                return;
            }
        }
    }

    private void deleteDetailNewView(final Detail detail) {
        if (detail == null) {
            return;
        }
        int count = mDetailNewListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailNewListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            if (childDetail.id == detail.id) {
                childDetail = detail;
                mDetailNewListLay.removeView(child);
                return;
            }
        }
    }

    private void addSubStakeView(final Stake stake) {
        if (stake == null) {
            return;
        }
        View view = null;
        int count = mSubStakeListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mSubStakeListLay.getChildAt(i);
            Stake childTag = (Stake) child.getTag();
            if (childTag.id == stake.id) {
                view = child;
            }
        }
        if (view == null) {
            view = View.inflate(mAct, R.layout.notice_sub_stake_lay, null);
            mSubStakeListLay.addView(view);
        }
        view.setTag(stake);
        TextView stakeTv = (TextView) view.findViewById(R.id.stake_name_tv);
        TextView stakeN1Tv = (TextView) view.findViewById(R.id.stake_num1_tv);
        TextView stakeN2Tv = (TextView) view.findViewById(R.id.stake_num2_tv);
        ImageView beforeIv = (ImageView) view.findViewById(R.id.before_iv);
        ImageView consIv = (ImageView) view.findViewById(R.id.construct_iv);
        ImageView afterIv = (ImageView) view.findViewById(R.id.after_iv);
        stakeTv.setText(stake.stake_ud);
        stakeN1Tv.setText(stake.stake_num1);
        stakeN2Tv.setText(stake.stake_num2);
        MyUtil.showImage(stake.getBeforeNewPics(), beforeIv, null);
        MyUtil.showImage(stake.getConstructionNewPics(), consIv, null);
        MyUtil.showImage(stake.getAfterNewPics(), afterIv, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mAct, StakeActivity.class).putExtra("stake", stake).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_STAKE);
            }
        });
    }

    private void deleteStakeView(final Stake stake) {
        if (stake == null) {
            return;
        }
        int count = mSubStakeListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mSubStakeListLay.getChildAt(i);
            Stake childTag = (Stake) child.getTag();
            if (childTag.id == stake.id) {
                childTag = stake;
                mSubStakeListLay.removeView(child);
                return;
            }
        }
    }

    private void prepareCheck() {
        mCheck.cate = ((NormalBean) mTypeSp.getSelectedItem()).id;
        mCheck.stake_ud = ((NormalBean) mStakeSp.getSelectedItem()).name;
        mCheck.stake_num1 = mStakeNum1Et.getText().toString();
        mCheck.stake_num2 = mStakeNum2Et.getText().toString();
        mCheck.project_name = mProjectNameEt.getText().toString();
        mCheck.start_time = mDateEt.getText().toString();
        mCheck.project_cost = mCostEt.getText().toString();
        mCheck.structure_id = ((Structure) structureSp.getSelectedItem()).id;

        int count = mDetailListLay.getChildCount();
        mCheck.detail = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            View child = mDetailListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            mCheck.detail.add(childDetail);
        }

        int detailNewCount = mDetailNewListLay.getChildCount();
        mCheck.detail_new_edit = new ArrayList<>();
        for (int i = 0; i < detailNewCount; i++) {
            View child = mDetailNewListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            mCheck.detail_new_edit.add(childDetail);
        }
    }

    private boolean httpSave() {
        if (Util.isEmpty(mCheck.stake_num1)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n1 = Float.valueOf(mCheck.stake_num1);
            if (String.valueOf((int) n1).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }

        if (Util.isEmpty(mCheck.stake_num2)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n2 = Float.valueOf(mCheck.stake_num2);
            if (String.valueOf((int) n2).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("cate", mCheck.cate)
                .add("id", mCheck.id + "")
                .add("stake_ud", mCheck.stake_ud + "")
                .add("stake_num1", mCheck.stake_num1 + "")
                .add("stake_num2", mCheck.stake_num2 + "")
                .add("project_name", mCheck.project_name + "")
                .add("start_time", "" + mCheck.start_time)
                .add("project_cost", "" + mCheck.project_cost)
                .add("structure_id", "" + mCheck.structure_id);
        int i = 0;
        for (Detail detail : mCheck.detail) {
            builder.add("detail[" + i + "][detail_name_cate]", "" + detail.cate_id);
            builder.add("detail[" + i + "][detail_id]", "" + detail.id);
            builder.add("detail[" + i + "][detail_name]", "" + detail.detail_name);
            builder.add("detail[" + i + "][detail_num]", "" + detail.detail_num);
            builder.add("detail[" + i + "][detail_price]", "" + detail.detail_price);
            builder.add("detail[" + i + "][detail_unit]", "" + detail.detail_unit);
            builder.add("detail[" + i + "][detail_quantities1]", "" + detail.detail_quantities1);
            builder.add("detail[" + i + "][detail_quantities2]", "" + detail.detail_quantities2);
            builder.add("detail[" + i + "][detail_quantities3]", "" + detail.detail_quantities3);
            builder.add("detail[" + i + "][detail_all_price]", "" + detail.detail_all_price);
            i++;
        }

        int j = 0;
        int detailNewCount = mDetailNewListLay.getChildCount();
        for (Detail detail : mCheck.detail_new_edit) {
            builder.add("detail_new_edit[" + j + "][detail_name_cate]", "" + detail.cate_id);
            builder.add("detail_new_edit[" + j + "][detail_id]", "" + detail.id);
            builder.add("detail_new_edit[" + j + "][detail_name]", "" + detail.detail_name);
            builder.add("detail_new_edit[" + i + "][detail_num]", "" + detail.detail_num);
            builder.add("detail_new_edit[" + j + "][detail_price]", "" + detail.detail_price);
            builder.add("detail_new_edit[" + j + "][detail_unit]", "" + detail.detail_unit);
            builder.add("detail_new_edit[" + j + "][detail_quantities1]", "" + detail.detail_quantities1);
            builder.add("detail_new_edit[" + j + "][detail_quantities2]", "" + detail.detail_quantities2);
            builder.add("detail_new_edit[" + j + "][detail_quantities3]", "" + detail.detail_quantities3);
            builder.add("detail_new_edit[" + j + "][detail_all_price]", "" + detail.detail_all_price);
            j++;
        }

//        builder.add("before_pic", getFormPic(mCheck.get));
        builder.add("tuzhi", getFormPic(mCheck.getTuzhiNewPicUris()));
        builder.add("attach", getFormPic(mCheck.getAttachNewPicUris()));

        ResponseData responseData = new HttpTask(Config.url_check_save).execute(builder.build());
        return responseData.isSuccess();
    }

    private String getFormPic(List<String> uris) {
        String s = "";
        if (uris != null) {
            StringBuffer sb = new StringBuffer();

            for (String url : uris) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(url);
            }
            s = sb.toString();
        }
        return s;
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.add_detail_iv:
                startActivityForResult(new Intent(mAct, DetailActivity.class), REQUEST_DETAIL);
                break;
            case R.id.add_detail_new_iv:
                startActivityForResult(new Intent(mAct, DetailActivity.class), REQUEST_DETAIL_NEW);
                break;
            case R.id.date_tv:
                MyUtil.showDateTimeDialog(mAct, mDateEt);
                break;
            case R.id.add_substake_iv:
                startActivityForResult(new Intent(mAct, StakeActivity.class).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_STAKE);
                break;
            case R.id.tuzhi_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mCheck.getTuzhiNewPicUris()).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_TUZHI);
                break;
            case R.id.attach_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mCheck.getAttachNewPicUris()).putExtra("editable", MyUtil.isCheckEditable(mCheck)), REQUEST_ATTACH);
                break;
        }
    }

    private void cal() {
        int count1 = mDetailListLay.getChildCount();
        double sum = 0;
        for (int i = 0; i < count1; i++) {
            View v = mDetailListLay.getChildAt(i);
            Detail detail = (Detail) v.getTag();
            double all = NumberUtil.strToDouble(detail.detail_all_price);
            sum += all;
        }

        int count2 = mDetailNewListLay.getChildCount();
        for (int i = 0; i < count2; i++) {
            View v = mDetailNewListLay.getChildAt(i);
            Detail detail = (Detail) v.getTag();
            double all = NumberUtil.strToDouble(detail.detail_all_price);
            sum += all;
        }
        mCostEt.setText(NumberUtil.doubleToStr(sum));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL) {
            if (resultCode == RESULT_OK) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                addDetailView(detail);
                cal();
            } else if (resultCode == Config.RESULT_DELETE) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                deleteDetailView(detail);
                cal();
            }

        } else if (requestCode == REQUEST_DETAIL_NEW) {
            if (resultCode == RESULT_OK) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                addDetailNewView(detail);
                cal();
            } else if (resultCode == Config.RESULT_DELETE) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                deleteDetailNewView(detail);
                cal();
            }

        } else if (requestCode == REQUEST_STAKE) {
            if (resultCode == RESULT_OK) {
                Stake stake = (Stake) data.getSerializableExtra("stake");
                addSubStakeView(stake);
            } else if (resultCode == Config.RESULT_DELETE) {
                Stake stake = (Stake) data.getSerializableExtra("stake");
                deleteStakeView(stake);
            }

        } else if (requestCode == REQUEST_TUZHI) {
            if (resultCode == RESULT_OK) {
                mCheck.getTuzhiNewPicUris().clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mCheck.getTuzhiNewPicUris().addAll(uris);
                MyUtil.showImage(mCheck.getTuzhiNewPicUris(), mTuzhiIv1, mTuzhiIv2);
            }
        } else if (requestCode == REQUEST_ATTACH) {
            if (resultCode == RESULT_OK) {
                mCheck.getAttachNewPicUris().clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mCheck.getAttachNewPicUris().addAll(uris);
                MyUtil.showImage(mCheck.getAttachNewPicUris(), mAttachIv1, mAttachIv2);
            }
        }
    }
}
