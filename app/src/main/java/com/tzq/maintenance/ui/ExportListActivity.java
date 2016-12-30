package com.tzq.maintenance.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tzq.common.ui.CBaseAdapter;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ExportListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;
    private Adapter mListAdapter;
    private TextView footerView;
    private List<String> mListData = new ArrayList<>();
    private boolean isMutiSelectMode = false;
    private List<Integer> selectPositions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_list_activity);
        setTitle("文件列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mListView = (ListView) findViewById(R.id.list_view);
        footerView = (TextView) getLayoutInflater().inflate(R.layout.list_footer_view, null);
        registerForContextMenu(mListView);

        swipeRefreshLayout.setOnRefreshListener(this);
        mListView.addFooterView(footerView);
        footerView.setOnClickListener(this);
        footerView.setText("点击这里加载更多");
        footerView.setVisibility(View.INVISIBLE);
        mListAdapter = new Adapter(mAct);
        mListAdapter.setDataList(mListData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        refreshDir();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMutiSelectMode) {
            getMenuInflater().inflate(R.menu.export_list_muti, menu);
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
            getMenuInflater().inflate(R.menu.export_list, menu);
            setTitle("文件列表");
        }
        return true;
    }

    private void refreshMutiSelect() {
        invalidateOptionsMenu();
        mListAdapter.notifyDataSetChanged();
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
            case R.id.action_muti_select:
                isMutiSelectMode = true;
                refreshMutiSelect();
                break;
            case R.id.action_muti_delete:
                new AlertDialog.Builder(mAct).setMessage("删除所选文件？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialogUtil.show(mAct, "正在批量删除通知单...");
                        new Thread() {
                            @Override
                            public void run() {
                                for (int pos : selectPositions) {
                                    String name = mListAdapter.getItem(pos);
                                    String path = Config.exportDirPath + "/" + name;
                                    File file = new File(path);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressDialogUtil.hide(mAct);
                                        MyUtil.toast("删除完成");
                                        isMutiSelectMode = false;
                                        refreshMutiSelect();
                                        refreshDir();
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
    public void onViewClick(View view) {
//        if (view == footerView) {
//            refreshDir();
//        }
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
        refreshDir();
    }

    private void refreshDir() {
        mListData.clear();
        File dir = new File(Config.exportDirPath);
        if (dir.exists() && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    mListData.add(file.getName());
                }
            }
        } else {
            dir.mkdirs();
        }
        mListAdapter.setDataList(mListData);
        setRefresh(false);
    }

    class Adapter extends CBaseAdapter<String> {

        public Adapter(Context c) {
            super(c);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            VH vh = null;
            if (convertView == null) {
                convertView = View.inflate(mAct, R.layout.export_list_item, null);
                vh = new VH();
                convertView.setTag(vh);
                vh.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                vh.openBt = (Button) convertView.findViewById(R.id.openBt);
                vh.iconIv = (ImageView) convertView.findViewById(R.id.icon_iv);
                vh.checkBox = (CheckBox) convertView.findViewById(R.id.check);
            } else {
                vh = (VH) convertView.getTag();
            }

            final String item = getItem(position);
            vh.nameTv.setText("" + item);
            vh.openBt.setVisibility(isMutiSelectMode ? View.GONE : View.VISIBLE);
            vh.openBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("file://" + Config.exportDirPath + "/" + item));
                        startActivity(i);
                    } catch (Exception e) {
                        LogUtil.e(e.getMessage(), e);
                        MyUtil.toast("打开失败，请安装对应的app");
                    }
                }
            });

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
            public Button openBt;
            public ImageView iconIv;
            public CheckBox checkBox;
        }
    }
}
