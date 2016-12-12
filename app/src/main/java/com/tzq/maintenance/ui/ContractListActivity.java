package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Contract;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.core.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ContractListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;
    private Adapter mListAdapter;
    private TextView footerView;
    private int mPage = 1;
    private final int page_size = 10;
    private List<Contract> mListData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_list_activity);
        setTitle("违约金列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mListView = (ListView) findViewById(R.id.list);
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
                Contract item = mListAdapter.getItem(position);
                startActivity(new Intent(mAct, ContractActivity.class).putExtra("contract", item));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contract_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            startActivity(new Intent(mAct, ContractActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        httpGetList(1);
    }

    private void httpGetList(final int p) {
        footerView.setText("正在加载更多数据...");
        new HttpTask(Config.url_contract_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
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
                                    List<Contract> list = Config.gson.fromJson(o.optString("list"), new TypeToken<List<Contract>>() {
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
                .build());
    }

    class Adapter extends CBaseAdapter<Contract> {

        public Adapter(Context c) {
            super(c);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VH vh = null;
            if (convertView == null) {
                convertView = View.inflate(mAct, R.layout.contract_list_item, null);
                vh = new VH();
                convertView.setTag(vh);
                vh.nameTv = (TextView) convertView.findViewById(R.id.clause_tv);
                vh.managementTv = (TextView) convertView.findViewById(R.id.management_tv);
                vh.withholdingTv = (TextView) convertView.findViewById(R.id.withholding_tv);
                vh.dateTv = (TextView) convertView.findViewById(R.id.date_tv);
            } else {
                vh = (VH) convertView.getTag();
            }

            Contract item = getItem(position);
            vh.nameTv.setText("违约条款:" + item.clause);
            vh.managementTv.setText("管理处:" + item.management_name);
            vh.withholdingTv.setText("扣款金额:" + item.withholding);
            vh.dateTv.setText("时间:" + item.date);
            return convertView;
        }

        class VH {
            public TextView nameTv;
            public TextView managementTv;
            public TextView withholdingTv;
            public TextView dateTv;
        }
    }
}
