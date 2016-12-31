package com.tzq.maintenance.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
import com.tzq.common.utils.NumberUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.DealBean;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.Notice;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/10/31.
 */

public class NoticeActivity extends BaseActivity {
    final int REQUEST_DETAIL = 101;
    final int REQUEST_PHOTO = 102;
    Notice mNotice;
    Spinner mTypeSp, mStakeSp, structureSp;
    EditText mStakeNum1Et, mStakeNum2Et, mProjectNameEt, mDaysEt, mCostEt;
    TextView mDateEt;
    ImageView mBeforeIv1, mBeforeIv2, mBeforeMoreIv;
    LinearLayout mDetailListLay;
    DealBean mNoticeDealBean;


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
        mDateEt = (TextView) findViewById(R.id.date_tv);
        mCostEt = (EditText) findViewById(R.id.cost_et);
        mBeforeIv1 = (ImageView) findViewById(R.id.before_iv1);
        mBeforeIv2 = (ImageView) findViewById(R.id.before_iv2);
        mBeforeMoreIv = (ImageView) findViewById(R.id.before_more_iv);
        mDetailListLay = (LinearLayout) findViewById(R.id.detail_list_lay);

        mNotice = (Notice) getIntent().getSerializableExtra("notice");
        MyUtil.setUpSp(mAct, mTypeSp, Config.CATES);
        MyUtil.setUpSp(mAct, mStakeSp, Config.STAKES);
        MyUtil.setUpSp(mAct, structureSp, new Select().from(Structure.class).execute());
        init();
    }

    private boolean isEditable() {
        if (mNotice.step == 31) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isEditable()) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.notice_act, menu);
        MenuItem dealNextMenu = menu.findItem(R.id.action_deal_next);
        MenuItem dealCancelMenu = menu.findItem(R.id.action_deal_cancel);
        MenuItem deleteMenu = menu.findItem(R.id.action_delete);
        MenuItem saveMenu = menu.findItem(R.id.action_save);

        if (mNotice.created_user_id == App.getInstance().getUser().user_id || App.getInstance().getUser().role_id == 1) {
            deleteMenu.setVisible(true);
        } else {
            deleteMenu.setVisible(false);
        }
        if (mNotice.id <= 0) {
            dealNextMenu.setVisible(false);
            dealCancelMenu.setVisible(false);
        } else {
            mNoticeDealBean = MyUtil.getNoticeDealStr(mNotice.step, mNotice.role_id, App.getInstance().getUser().role_id);
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
        saveMenu.setVisible(isEditable());
        deleteMenu.setVisible(isEditable());

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
                        prepareNotice();
                        final boolean result = httpSave(mNotice);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogUtil.hide(mAct);
                                if (result) {
                                    MyUtil.deleteOfflineNotice(mNotice);
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
                new AlertDialog.Builder(mAct).setMessage("删除该通知单？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!Util.isEmpty(mNotice.offlineId)) {
                            MyUtil.deleteOfflineNotice(mNotice);
                        }
                        if (mNotice.id > 0) {
                            new HttpTask(Config.url_notice_delete + "?id=" + mNotice.id).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
                new HttpTask(Config.url_notice_deal + "?id=" + mNotice.id + "&act=" + mNoticeDealBean.nextAct).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
                new HttpTask(Config.url_notice_deal + "?id=" + mNotice.id + "&act=" + mNoticeDealBean.cancelAct).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
                prepareNotice();
                if (Util.isEmpty(mNotice.offlineId)) {
                    mNotice.offlineId = UUID.randomUUID().toString();
                }
                MyUtil.saveOfflineNotice(mNotice);
                MyUtil.toast("保存成功");
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareNotice() {
        mNotice.cate = ((NormalBean) mTypeSp.getSelectedItem()).id;
        mNotice.stake_ud = ((NormalBean) mStakeSp.getSelectedItem()).name;
        mNotice.stake_num1 = mStakeNum1Et.getText().toString();
        mNotice.stake_num2 = mStakeNum2Et.getText().toString();
        mNotice.project_name = mProjectNameEt.getText().toString();
        mNotice.start_time = mDateEt.getText().toString();
        mNotice.project_cost = mCostEt.getText().toString();
        mNotice.days = mDaysEt.getText().toString();
        mNotice.structure_id = ((Structure) structureSp.getSelectedItem()).id;
        if (mNotice.detail == null) {
            mNotice.detail = new ArrayList<>();
        }
        int count = mDetailListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            mNotice.detail.add(childDetail);
        }

    }

    private void init() {
        if (mNotice == null) {
            mNotice = new Notice();
            mNotice.newBeforePicUris = new ArrayList<>();
            setTitle("新建通知单");
            mDetailListLay.removeAllViews();
            setEditable(true);
            findViewById(R.id.add_detail_iv).setVisibility(View.VISIBLE);
        } else {
            setTitle("通知单详情");
            setEditable(isEditable());

            mTypeSp.setSelection(MyUtil.getNoticeCateIndex(mNotice.cate));
            mStakeSp.setSelection(MyUtil.getNoticeStakeIndex(mNotice.stake_ud));
            structureSp.setSelection(MyUtil.getStructureIndex(mNotice.structure_id));
            mStakeNum1Et.setText(mNotice.stake_num1);
            mStakeNum2Et.setText(mNotice.stake_num2);
            mProjectNameEt.setText(mNotice.project_name);
            mCostEt.setText(mNotice.project_cost);
            mDateEt.setText(mNotice.start_time);
            mDaysEt.setText(mNotice.days);

            if (!Util.isEmpty(mNotice.before_pic)) {
                String[] beforePicUrls = mNotice.before_pic.split(",");
                mNotice.beforePicUris = Arrays.asList(beforePicUrls);
                if (mNotice.beforePicUris != null) {
                    if (mNotice.newBeforePicUris == null) {
                        mNotice.newBeforePicUris = new ArrayList<>();
                        mNotice.newBeforePicUris.addAll(mNotice.beforePicUris);
                    }
                }
            }


            List<Detail> detailList = mNotice.detail;
            mDetailListLay.removeAllViews();
            if (!Util.isEmpty(detailList)) {
                for (Detail detail : detailList) {
                    addDetailView(detail);
                }
            }
        }

        if (mNotice.beforePicUris == null) {
            mNotice.beforePicUris = new ArrayList<>();
        }
        if (mNotice.newBeforePicUris == null) {
            mNotice.newBeforePicUris = new ArrayList<>();
        }
        if (mNotice.newBeforePicUris != null) {
            if (mNotice.newBeforePicUris.size() >= 1) {
                MyUtil.displayPic(mBeforeIv1, mNotice.newBeforePicUris.get(0));
            }
            if (mNotice.newBeforePicUris.size() >= 2) {
                MyUtil.displayPic(mBeforeIv2, mNotice.newBeforePicUris.get(1));
            }
        } else {
            MyUtil.displayPic(mBeforeIv1, "");
            MyUtil.displayPic(mBeforeIv1, "");
        }
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
            findViewById(R.id.add_detail_iv).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.add_detail_iv).setVisibility(View.INVISIBLE);
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
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("detail", detail).putExtra("editable", isEditable()), REQUEST_DETAIL);
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
                mDetailListLay.removeView(child);
                return;
            }
        }
    }

    //it will block thread
    public static boolean httpSave(Notice notice) {
        if (Util.isEmpty(notice.stake_num1)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n1 = Float.valueOf(notice.stake_num1);
            if (String.valueOf((int) n1).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }

        if (Util.isEmpty(notice.stake_num2)) {
            MyUtil.toast("请输入桩号");
            return false;
        } else {
            float n2 = Float.valueOf(notice.stake_num2);
            if (String.valueOf((int) n2).length() != 6) {
                MyUtil.toast("桩号必须有6位整数");
                return false;
            }
        }


        //del pic
        for (String uri : notice.beforePicUris) {
            if (!notice.newBeforePicUris.contains(uri)) {
                new HttpTask(Config.url_del_pic).execute(new FormBody.Builder().add("picUrl", uri).build());
                notice.beforePicUris.remove(uri);
            }
        }
        //upload pic
        for (String uri : notice.newBeforePicUris) {
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


        FormBody.Builder builder = new FormBody.Builder();
        builder.add("cate", notice.cate)
                .add("id", notice.id + "")
                .add("stake_ud", notice.stake_ud + "")
                .add("stake_num1", notice.stake_num1 + "")
                .add("stake_num2", notice.stake_num2 + "")
                .add("project_name", notice.project_name + "")
                .add("start_time", "" + notice.start_time)
                .add("project_cost", "" + notice.project_cost)
                .add("structure_id", "" + notice.structure_id)
                .add("days", "" + notice.days);
        int i = 0;
        for (Detail childDetail : notice.detail) {
            builder.add("detail[" + i + "][detail_name_cate]", "" + childDetail.cate_id);
            builder.add("detail[" + i + "][detail_id]", "" + childDetail.id);
            builder.add("detail[" + i + "][detail_name]", "" + childDetail.detail_name);
            builder.add("detail[" + i + "][detail_num]", "" + childDetail.detail_num);
            builder.add("detail[" + i + "][detail_price]", "" + childDetail.detail_price);
            builder.add("detail[" + i + "][detail_unit]", "" + childDetail.detail_unit);
            builder.add("detail[" + i + "][detail_quantities1]", "" + childDetail.detail_quantities1);
            builder.add("detail[" + i + "][detail_quantities2]", "" + childDetail.detail_quantities2);
            builder.add("detail[" + i + "][detail_quantities3]", "" + childDetail.detail_quantities3);
            builder.add("detail[" + i + "][detail_all_price]", "" + childDetail.detail_all_price);
            i++;
        }

        String before = "";
        if (notice.newBeforePicUris != null) {
            StringBuffer sb = new StringBuffer();
            for (String url : notice.newBeforePicUris) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(url);
            }
            before = sb.toString();
        }
        builder.add("before_pic", before);


        ResponseData responseData = new HttpTask(Config.url_notice_save).execute(builder.build());
        Looper.prepare();
        return responseData.isSuccess();
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.add_detail_iv:
                startActivityForResult(new Intent(mAct, DetailActivity.class), REQUEST_DETAIL);
                break;
            case R.id.brfore_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mNotice.newBeforePicUris).putExtra("editable", isEditable()), REQUEST_PHOTO);
                break;
            case R.id.date_tv:
                MyUtil.showDateTimeDialog(mAct, mDateEt);
                break;
        }
    }

    private void cal() {
        int count = mDetailListLay.getChildCount();
        double sum = 0;
        for (int i = 0; i < count; i++) {
            View v = mDetailListLay.getChildAt(i);
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

        } else if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                mNotice.newBeforePicUris.clear();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mNotice.newBeforePicUris.addAll(uris);

                MyUtil.displayPic(mBeforeIv1, "");
                MyUtil.displayPic(mBeforeIv2, "");
                if (mNotice.newBeforePicUris.size() >= 1) {
                    MyUtil.displayPic(mBeforeIv1, mNotice.newBeforePicUris.get(0));
                }
                if (mNotice.newBeforePicUris.size() >= 2) {
                    MyUtil.displayPic(mBeforeIv2, mNotice.newBeforePicUris.get(1));
                }
            }
        }
    }
}
