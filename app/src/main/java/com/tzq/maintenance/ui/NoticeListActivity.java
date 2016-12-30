package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Notice;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/20.
 */

public class NoticeListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final int REQUEST_UPDATE_NOTICE = 22;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;
    private Adapter mListAdapter;
    private TextView footerView;
    private int mPage = 1;
    private final int page_size = 10;
    private List<Notice> mListData = new ArrayList<>();
    private int mSelectPosition;
    private boolean isMutiSelectMode = false;
    private List<Integer> selectPositions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list_activity);
        setTitle("通知单列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mListView = (ListView) findViewById(R.id.tongzd_list);
        footerView = (TextView) getLayoutInflater().inflate(R.layout.list_footer_view, null);
        registerForContextMenu(mListView);

        swipeRefreshLayout.setOnRefreshListener(this);
        mListView.addFooterView(footerView);
        footerView.setOnClickListener(this);
        footerView.setText("点击这里加载更多");
        mListAdapter = new Adapter(mAct);
        mListAdapter.setDataList(mListData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice item = mListAdapter.getItem(position);
                startActivityForResult(new Intent(mAct, NoticeActivity.class).putExtra("notice", item), REQUEST_UPDATE_NOTICE);
            }
        });
        httpGetList(1);
    }


    @Override
    public void onViewClick(View view) {
        if (view == footerView) {
            httpGetList(mPage++);
        }
    }

    private void setRefresh(final boolean b) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(b);
            }
        }, 500);
    }

    boolean isFisrtCreate = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMutiSelectMode) {
            getMenuInflater().inflate(R.menu.notice_list_muti_select, menu);
            String s = "";
            if (!selectPositions.isEmpty()) {
                s = "(" + selectPositions.size() + ")";

            }
            setTitle("多选" + s);
            final CheckBox checkBox = (CheckBox) menu.findItem(R.id.action_select_all).getActionView();
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPositions.clear();
                    if (checkBox.isChecked()) {
                        int size = mListData.size();
                        for (int i = 0; i < size; i++) {
                            if (!selectPositions.contains(i)) {
                                selectPositions.add(i);
                            }
                        }
                    }
                    refreshMutiSelect();
                }
            });
        } else {
            getMenuInflater().inflate(R.menu.notice_list, menu);
            setTitle("通知单列表");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isMutiSelectMode) {
                    isMutiSelectMode = false;
                } else {
                    finish();
                }
                refreshMutiSelect();
                break;
            case R.id.action_create:
                startActivityForResult(new Intent(mAct, NoticeActivity.class), REQUEST_UPDATE_NOTICE);
                break;
            case R.id.action_muti_select:
                isMutiSelectMode = true;
                refreshMutiSelect();
                break;
            case R.id.action_export:
                new AlertDialog.Builder(mAct).setMessage("导出所选通知单？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialogUtil.show(mAct, "正在批量导出通知单...");
                        for (int pos : selectPositions) {
                            Notice notice = mListAdapter.getItem(selectPositions.get(pos));
                            download(notice);
                        }
                        isMutiSelectMode = false;
                        refreshMutiSelect();
                    }
                }).show();

                break;
//            case R.id.action_commit:
//                ProgressDialogUtil.show(mAct, "正在批量提交...");
//                new Thread() {
//                    @Override
//                    public void run() {
//                        for (int pos : selectPositions) {
//                            Notice notice = mListAdapter.getItem(selectPositions.get(pos));
//                            NoticeActivity.httpSave(notice);
//                            Looper.prepare();
//                            ProgressDialogUtil.hide(mAct);
//                            MyUtil.toast("提交完成");
//                            Looper.loop();
//                        }
//                    }
//                }.start();
//
//                isMutiSelectMode = false;
//                refreshMutiSelect();
//                break;
            case R.id.action_delete:
                new AlertDialog.Builder(mAct).setMessage("删除所选通知单？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialogUtil.show(mAct, "正在批量删除通知单...");
                        new Thread() {
                            @Override
                            public void run() {
                                for (int pos : selectPositions) {
                                    Notice notice = mListAdapter.getItem(pos);
                                    if (!Util.isEmpty(notice.offlineId)) {
                                        MyUtil.deleteOfflineNotice(notice);
                                    }
                                    if (notice.step != 31 && notice.id > 0) {
                                        new HttpTask(Config.url_notice_delete + "?id=" + notice.id).setActivity(mAct).execute(null);
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressDialogUtil.hide(mAct);
                                        MyUtil.toast("删除完成");
                                        isMutiSelectMode = false;
                                        refreshMutiSelect();
                                        httpGetList(1);
                                    }
                                });
                            }
                        }.start();
                    }
                }).show();

                break;

        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        mSelectPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        menu.add(0, 1, 0, "导出通知单");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Notice notice = mListAdapter.getItem(mSelectPosition);
                download(notice);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isMutiSelectMode) {
            isMutiSelectMode = false;
            invalidateOptionsMenu();
            mListAdapter.notifyDataSetChanged();
        } else {
            finish();
        }
    }

    private void download(Notice notice) {
        String surl = Config.url_export_notice + "?id=" + notice.id;
        new HttpTask(surl).setActivity(mAct).setProgressMsg("正在下载...").addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("下载成功，路径：" + responseData.data);
                } else {
                    MyUtil.toast("下载失败");
                }
            }
        }).download(Config.exportDirPath, "通知单_" + notice.id + "_" + notice.project_name + ".xls");
    }


    @Override
    public void onRefresh() {
        httpGetList(1);
    }

    private void httpGetList(final int p) {
        if (p == 1) {
            setRefresh(true);
        }
        footerView.setText("正在加载更多数据...");
        new HttpTask(Config.url_notice_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(final ResponseData responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        if (responseData.isSuccess()) {
                            try {
                                if (Util.isEmpty(responseData.data)) {
                                    mListData.clear();
                                } else {
                                    JSONObject o = new JSONObject(responseData.data);
                                    count = o.optInt("count");
                                    List<Notice> list = Config.gson.fromJson(o.optString("list"), new TypeToken<List<Notice>>() {
                                    }.getType());
                                    mPage = p;
                                    if (p == 1) {
                                        mListData.clear();
                                    }
                                    mListData.addAll(list);
                                }
                            } catch (JSONException e) {
                                LogUtil.e(e.getMessage(), e);
                            }

                            refreshMutiSelect();
                        }
                        mergeOffline();
                        mListAdapter.setDataList(mListData);
                        if (mListData.size() >= count) {
                            footerView.setText("没有更多的数据了");
                            footerView.setEnabled(false);
                        } else {
                            footerView.setText("点击这里加载更多");
                            footerView.setEnabled(true);
                        }

                        setRefresh(false);
                    }
                });

            }
        }).enqueue(new FormBody.Builder()
                .add("now_page", p + "")
                .build());
    }

    private void mergeOffline() {
        List<Notice> noticeList = MyUtil.getOfflineNotices();
        for (Notice n1 : noticeList) {
            for (Notice n2 : mListData) {
                if (n1.id <= 0) {
                    if (n1.offlineId != null && n1.offlineId.equals(n2.offlineId)) {
                        mListData.remove(n2);
                        break;
                    }
                } else {
                    if (n1.id == n2.id) {
                        mListData.remove(n2);
                        break;
                    }
                }
            }
            mListData.add(0, n1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPDATE_NOTICE) {
            if (resultCode == RESULT_OK) {
                httpGetList(1);
            }
        }
    }

    private void refreshMutiSelect() {
        invalidateOptionsMenu();
        mListAdapter.notifyDataSetChanged();
    }

    class Adapter extends CBaseAdapter<Notice> {

        public Adapter(Context c) {
            super(c);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            VH vh = null;
            if (convertView == null) {
                convertView = View.inflate(mAct, R.layout.notice_list_item, null);
                vh = new VH();
                convertView.setTag(vh);
                vh.nameTv = (TextView) convertView.findViewById(R.id.notice_name_tv);
                vh.noTv = (TextView) convertView.findViewById(R.id.notice_no_tv);
                vh.typeTv = (TextView) convertView.findViewById(R.id.notice_type_tv);
                vh.costTv = (TextView) convertView.findViewById(R.id.notice_cost_tv);
                vh.dateTv = (TextView) convertView.findViewById(R.id.notice_date_tv);
                vh.stepTv = (TextView) convertView.findViewById(R.id.notice_step_tv);
                vh.checkBox = (CheckBox) convertView.findViewById(R.id.check);
                vh.statusTv = (TextView) convertView.findViewById(R.id.statusTv);
            } else {
                vh = (VH) convertView.getTag();
            }

            final Notice item = getItem(position);
            vh.nameTv.setText("名称：" + item.project_name);
            vh.noTv.setText("编号：" + item.id);
            vh.typeTv.setText("分类：" + MyUtil.getNoticeCateStr(item.cate));
            vh.costTv.setText("造价：" + item.project_cost);
            vh.stepTv.setText("状态：" + MyUtil.getStepStrForNotice(Integer.valueOf(item.step)));
            vh.dateTv.setText("" + item.created_at);
            if (Util.isEmpty(item.offlineId)) {
                vh.statusTv.setText("" + MyUtil.getStatusStrForNotice(Integer.valueOf(item.step)));
                vh.statusTv.setTextColor(MyUtil.getStatusColorForNotice(Integer.valueOf(item.step)));
            } else {
                vh.statusTv.setText("离线缓存中");
                vh.statusTv.setTextColor(MyUtil.getStatusColorForNotice(-1));
            }

            vh.checkBox.setOnCheckedChangeListener(null);
            if (isMutiSelectMode) {
                vh.checkBox.setVisibility(View.VISIBLE);
                vh.checkBox.setChecked(selectPositions.contains(position));
                vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!selectPositions.contains(position)) {
                                selectPositions.add(position);
                            }
                        } else {
                            if (selectPositions.contains(Integer.valueOf(position))) {
                                selectPositions.remove(Integer.valueOf(position));
                            }
                        }
                        refreshMutiSelect();
                    }
                });
            } else {
                vh.checkBox.setVisibility(View.GONE);
            }

            return convertView;
        }

        class VH {
            public TextView nameTv;
            public TextView noTv;
            public TextView typeTv;
            public TextView costTv;
            public TextView dateTv;
            public TextView stepTv;
            public CheckBox checkBox;
            public TextView statusTv;
        }
    }
}
