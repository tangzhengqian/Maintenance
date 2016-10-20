package com.tzq.maintenance.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.tzq.maintenance.R;

/**
 * Created by Administrator on 2016/10/20.
 */

public class TongZDListActivity extends BaseActivity {
    ListView tongzdListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongzd_list);
        setTitle("通知单列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tongzdListView= (ListView) findViewById(R.id.tongzd_list);
    }

    @Override
    public void onViewClick(int id) {

    }
}
