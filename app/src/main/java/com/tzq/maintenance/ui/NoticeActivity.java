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
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.Notice;
import com.tzq.maintenance.bean.NoticeDealBean;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    List<String> mBeforePicUris = new ArrayList<>();
    ArrayList<String> mNewBeforePicUris = new ArrayList<>();
    NoticeDealBean mNoticeDealBean;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_act, menu);
        MenuItem dealNextMenu = menu.findItem(R.id.action_deal_next);
        MenuItem dealCancelMenu = menu.findItem(R.id.action_deal_cancel);
        MenuItem deleteMenu = menu.findItem(R.id.action_delete);
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
                        //del pic
                        for (String uri : mBeforePicUris) {
                            if (!mNewBeforePicUris.contains(uri)) {
                                new HttpTask(Config.url_del_pic).execute(new FormBody.Builder().add("picUrl", uri).build());
                                mBeforePicUris.remove(uri);
                            }
                        }
                        //upload pic
                        for (String uri : mNewBeforePicUris) {
                            if (uri.startsWith("file://")) {
                                String path = uri.substring("file://".length());
                                File file = new File(path);
                                RequestBody fileBody = RequestBody.create(MediaType.parse("file"), file);
                                ResponseData responseData = new HttpTask(Config.url_add_pic).execute(new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("image", file.getName(), fileBody)
                                        .build());
                                if (responseData.isSuccess()) {
                                    mBeforePicUris.add(responseData.data);
                                }
                            }
                        }
                        saveNotice();
                    }
                }.start();

                break;
//            case R.id.action_edit:
//                break;
            case R.id.action_delete:
                new AlertDialog.Builder(mAct).setMessage("删除该通知单？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new HttpTask(Config.url_notice_delete + "?id=" + mNotice.id).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                            @Override
                            public void onComplete(ResponseData responseData) {
                                if (responseData.isSuccess()) {
                                    MyUtil.toast("删除成功");
                                    finish();
                                }
                            }
                        }).enqueue();
                    }
                }).show();
                break;
            case R.id.action_deal_next:
                new HttpTask(Config.url_notice_deal + "?id=" + mNotice.id + "&act=" + mNoticeDealBean.nextAct).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(ResponseData responseData) {
                        if (responseData.isSuccess()) {
                            MyUtil.toast("操作成功");
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
                            finish();
                        }
                    }
                }).enqueue();
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


    private void init() {
        if (mNotice == null) {
            mNotice = new Notice();
            setTitle("新建通知单");
            mDetailListLay.removeAllViews();
            setEditable(true);

            findViewById(R.id.add_detail_iv).setVisibility(View.VISIBLE);
        } else {
            setTitle("通知单详情");
            setEditable(true);

            mTypeSp.setSelection(MyUtil.getNoticeCateIndex(mNotice.cate));
            mStakeSp.setSelection(MyUtil.getNoticeStakeIndex(mNotice.stake_ud));
            structureSp.setSelection(MyUtil.getStructureIndex(mNotice.structure_id));
            mStakeNum1Et.setText(mNotice.stake_num1);
            mStakeNum2Et.setText(mNotice.stake_num2);
            mProjectNameEt.setText(mNotice.project_name);
            mCostEt.setText(mNotice.project_cost);
            mDateEt.setText(mNotice.created_at);
            mDaysEt.setText(mNotice.days);
            if (!Util.isEmpty(mNotice.before_pic)) {
                String[] beforePicUrls = mNotice.before_pic.split(",");
                mBeforePicUris = Arrays.asList(beforePicUrls);
                mNewBeforePicUris.addAll(mBeforePicUris);
                if (mBeforePicUris.size() >= 1) {
                    MyUtil.displayPic(mBeforeIv1, mBeforePicUris.get(0));
                }
                if (mBeforePicUris.size() >= 2) {
                    MyUtil.displayPic(mBeforeIv2, mBeforePicUris.get(1));
                }
            } else {
                MyUtil.displayPic(mBeforeIv1, "");
                MyUtil.displayPic(mBeforeIv1, "");
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
                startActivityForResult(new Intent(mAct, DetailActivity.class).putExtra("detail", detail), REQUEST_DETAIL);
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

    private void saveNotice() {
        mNotice.cate = ((NormalBean) mTypeSp.getSelectedItem()).id;
        mNotice.stake_ud = ((NormalBean) mStakeSp.getSelectedItem()).id;
        mNotice.stake_num1 = mStakeNum1Et.getText().toString();
        mNotice.stake_num2 = mStakeNum2Et.getText().toString();
        mNotice.project_name = mProjectNameEt.getText().toString();
        mNotice.start_time = mDateEt.getText().toString();
        mNotice.project_cost = mCostEt.getText().toString();
        mNotice.days = mDaysEt.getText().toString();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("cate", mNotice.cate)
                .add("id", mNotice.id + "")
                .add("stake_ud", mNotice.stake_ud + "")
                .add("stake_num1", mNotice.stake_num1 + "")
                .add("stake_num2", mNotice.stake_num2 + "")
                .add("project_name", mNotice.project_name + "")
                .add("start_time", "" + mNotice.start_time)
                .add("project_cost", "" + mNotice.project_cost)
                .add("structure_id", "" + mNotice.structure_id)
                .add("days", "" + mNotice.days);
        int count = mDetailListLay.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mDetailListLay.getChildAt(i);
            Detail childDetail = (Detail) child.getTag();
            builder.add("detail[" + i + "][detail_name_cate]", "" + childDetail.cate_id);
            builder.add("detail[" + i + "][detail_id]", "" + childDetail.id);
            builder.add("detail[" + i + "][detail_name]", "" + childDetail.detail_name);
            builder.add("detail[" + i + "][detail_price]", "" + childDetail.detail_price);
            builder.add("detail[" + i + "][detail_unit]", "" + childDetail.detail_unit);
            builder.add("detail[" + i + "][detail_quantities1]", "" + childDetail.detail_quantities1);
            builder.add("detail[" + i + "][detail_quantities2]", "" + childDetail.detail_quantities2);
            builder.add("detail[" + i + "][detail_quantities3]", "" + childDetail.detail_quantities3);
            builder.add("detail[" + i + "][detail_all_price]", "" + childDetail.detail_all_price);
        }

        if (mBeforePicUris != null) {
            StringBuffer sb = new StringBuffer();

            for (String url : mBeforePicUris) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(url);
            }
            builder.add("before_pic", sb.toString());
        }


        ResponseData responseData = new HttpTask(Config.url_notice_save).execute(builder.build());
        Looper.prepare();
        if (responseData.isSuccess()) {
            MyUtil.toast("保存成功");
            finish();
        }
        ProgressDialogUtil.hide(mAct);
        Looper.loop();
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.add_detail_iv:
                startActivityForResult(new Intent(mAct, DetailActivity.class), REQUEST_DETAIL);
                break;
            case R.id.brfore_pic_lay:
                startActivityForResult(new Intent(mAct, PhotoGridShowActivity.class).putStringArrayListExtra("uris", mNewBeforePicUris), REQUEST_PHOTO);
                break;
            case R.id.date_tv:
                MyUtil.showDateTimeDialog(mAct, mDateEt);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL) {
            if (resultCode == RESULT_OK) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                addDetailView(detail);
            } else if (resultCode == Config.RESULT_DELETE) {
                Detail detail = (Detail) data.getSerializableExtra("detail");
                deleteDetailView(detail);
            }

        } else if (requestCode == REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                mNewBeforePicUris = new ArrayList<>();
                ArrayList<String> uris = data.getStringArrayListExtra("uris");
                mNewBeforePicUris.addAll(uris);

                MyUtil.displayPic(mBeforeIv1, "");
                MyUtil.displayPic(mBeforeIv2, "");
                if (mNewBeforePicUris.size() >= 1) {
                    MyUtil.displayPic(mBeforeIv1, mNewBeforePicUris.get(0));
                }
                if (mNewBeforePicUris.size() >= 2) {
                    MyUtil.displayPic(mBeforeIv2, mNewBeforePicUris.get(1));
                }
            }
        }
    }
}
