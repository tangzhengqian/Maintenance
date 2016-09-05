package com.tzq.maintenance.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.R;
import com.tzq.maintenance.core.HttpTask;

public class MainActivity extends BaseActivity {


    @Override
    int getlayoutId() {
        return R.layout.activity_main;
    }

    @Override
    void onCreated(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
        new HttpTask("http://php.weather.sina.com.cn/xml.php?city=%B1%B1%BE%A9&password=DJOYnieT8234jlsK&day=0").addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, Object data, String msg) {
                LogUtil.i(isSuccess + "  " + data.toString());
                Toast.makeText(mAct, "grgtdh", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
