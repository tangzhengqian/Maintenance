package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Check;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/20.
 */

public class CheckListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;
    private Adapter mListAdapter;
    private TextView footerView;
    private int mPage = 1;
    private final int page_size = 10;
    private List<Check> mListData = new ArrayList<>();
    private int mSelectPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_list_activity);
        setTitle("验收单列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mListView = (ListView) findViewById(R.id.list_view);
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
                Check item = mListAdapter.getItem(position);
                startActivity(new Intent(mAct, CheckActivity.class).putExtra("check", item));
            }
        });
        httpGetList(1);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        mSelectPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        menu.add(0, 1, 0, "导出验收单");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Check check = mListAdapter.getItem(mSelectPosition);
                download(check);
                break;
        }
        return true;
    }

    private void download(Check check) {
        String surl = Config.url_export_check + "?id=" + check.id;
        new HttpTask(surl).setActivity(mAct).setProgressMsg("正在下载...").addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    MyUtil.toast("下载成功，路径：" + responseData.data);
                } else {
                    MyUtil.toast("下载失败");
                }
            }
        }).download("验收单_" + check.id + "_" + check.project_name + ".xls");
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

    @Override
    public void onRefresh() {
        httpGetList(1);
    }

    private void httpGetList(final int p) {
        footerView.setText("正在加载更多数据...");
        new HttpTask(Config.url_check_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
                                    List<Check> list = new Gson().fromJson(o.optString("list"), new TypeToken<List<Check>>() {
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
                            mListAdapter.setDataList(mListData);
                        }
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
//                .add("page_size", 10 + "")
                .build());
    }

    class Adapter extends CBaseAdapter<Check> {

        public Adapter(Context c) {
            super(c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
                vh.statusTv = (TextView) convertView.findViewById(R.id.notice_status_tv);
            } else {
                vh = (VH) convertView.getTag();
            }

            Check item = getItem(position);
            vh.nameTv.setText("名称：" + item.project_name);
            vh.noTv.setText("编号：" + item.id);
            vh.typeTv.setText("分类：" + MyUtil.getNoticeCateStr(item.cate));
            vh.costTv.setText("造价：" + item.project_cost);
            vh.statusTv.setText("状态：" + MyUtil.getStepStr(Integer.valueOf(item.step)));
            vh.dateTv.setText("" + item.created_at);
            return convertView;
        }

        class VH {
            public TextView nameTv;
            public TextView noTv;
            public TextView typeTv;
            public TextView costTv;
            public TextView dateTv;
            public TextView statusTv;
        }
    }
}
