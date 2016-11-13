package com.tzq.maintenance.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.tzq.maintenance.App;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.Maintenance;
import com.tzq.maintenance.bean.Role;

public class MainActivity extends BaseActivity {
    TextView userMsgTv;
    Button tzdBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle("工程管理系统");
        userMsgTv = (TextView) findViewById(R.id.user_msg_tv);
        tzdBtn = (Button) findViewById(R.id.tzd_bt);
        String name = App.getInstance().getUser().user_name;
        Company company = new Select().from(Company.class).where("id=" + App.getInstance().getUser().company_id).executeSingle();
        Maintenance maintenance = new Select().from(Maintenance.class).where("id=" + App.getInstance().getUser().maintenance_id).executeSingle();
//        Management management=new Select().from(Management.class).where("id="+App.getInstance().getUser().maintenance_id).executeSingle();
        Role role = new Select().from(Role.class).where("id=" + App.getInstance().getUser().role_id).executeSingle();
        userMsgTv.setText("用户：" + name +
                "\n所属公司：" + company.name +
                "\n所属养护科：" + maintenance.name +
                "\n职位：" + role.name);

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
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tzd_bt:
                startActivity(new Intent(mAct, NoticeListActivity.class));
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
        if (id == R.id.action_quit) {
            new AlertDialog.Builder(mAct).setMessage("退出当前帐号？").setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.getInstance().setUser(null);
                    startActivity(new Intent(mAct,LoginActivity.class));
                    finish();
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
