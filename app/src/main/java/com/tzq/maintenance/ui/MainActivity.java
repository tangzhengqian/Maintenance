package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.R;
import com.tzq.maintenance.core.HttpTask;

public class MainActivity extends BaseActivity {
    TextView userMsgTv;
    Button tzdBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("工程管理系统");
        userMsgTv = (TextView) findViewById(R.id.user_msg_tv);
        tzdBtn = (Button) findViewById(R.id.tzd_bt);

        userMsgTv.setText("用户：xxxx" +
                "\n所属公司：xxxxxxx" +
                "\n所属养护科：xxxxxx" +
                "\n职位：xxxx");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        }).show();
//            }
//        });

    }

    @Override
    public void onViewClick(int id) {
        switch (id) {
            case R.id.tzd_bt:
                startActivity(new Intent(mAct, TongZDListActivity.class));
                break;
        }
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

            new HttpTask("http://php.weather.sina.com.cn/xml.php?city=%B1%B1%BE%A9&password=DJOYnieT8234jlsK&day=0").setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                @Override
                public void onComplete(boolean isSuccess, Object data, String msg) {
                    LogUtil.i(isSuccess + "  " + data.toString());
                    Toast.makeText(mAct, data.toString(), Toast.LENGTH_SHORT).show();
                }
            }).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
