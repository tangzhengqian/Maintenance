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
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.Maintenance;
import com.tzq.maintenance.bean.Role;

public class MainActivity extends BaseActivity {
    TextView userMsgTv;
    Button noticesBtn, checksBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle("工程养护系统");
        userMsgTv = (TextView) findViewById(R.id.user_msg_tv);
        noticesBtn = (Button) findViewById(R.id.notices_bt);
        checksBtn = (Button) findViewById(R.id.checks_bt);
        String name = App.getInstance().getUser().user_name;
        Company company = new Select().from(Company.class).where("id=" + App.getInstance().getUser().company_id).executeSingle();
        Maintenance maintenance = new Select().from(Maintenance.class).where("id=" + App.getInstance().getUser().maintenance_id).executeSingle();
        Role role = new Select().from(Role.class).where("id=" + App.getInstance().getUser().role_id).executeSingle();
        if (name == null) {
            name = "";
        }
        userMsgTv.setText("用户：" + name +
                "\n所属公司：" + company.name +
                "\n所属养护科：" + maintenance.name +
                "\n职位：" + role.name);


    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.notices_bt).setVisibility(View.VISIBLE);
        findViewById(R.id.checks_bt).setVisibility(View.VISIBLE);
        findViewById(R.id.contract_bt).setVisibility(View.VISIBLE);
        findViewById(R.id.export_bt).setVisibility(View.VISIBLE);
        findViewById(R.id.autotime_bt).setVisibility(View.VISIBLE);
        findViewById(R.id.lookBt).setVisibility(View.VISIBLE);
        switch (App.getInstance().getUser().role_id) {
            case Config.ROLE_KEZHANG:
                break;
            case Config.ROLE_MAINT_MANAGER:
                break;
            case Config.ROLE_MAINT_MEMBER:
                findViewById(R.id.checks_bt).setVisibility(View.GONE);
                findViewById(R.id.export_bt).setVisibility(View.GONE);
                findViewById(R.id.autotime_bt).setVisibility(View.GONE);
                break;
            case Config.ROLE_WORK_MANAGER:
                findViewById(R.id.contract_bt).setVisibility(View.GONE);
                break;
            case Config.ROLE_WORK_MEMBER:
                findViewById(R.id.contract_bt).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.notices_bt:
                startActivity(new Intent(mAct, NoticeListActivity.class));
                break;
            case R.id.checks_bt:
                startActivity(new Intent(mAct, CheckListActivity.class));
                break;
            case R.id.contract_bt:
                startActivity(new Intent(mAct, ContractListActivity.class));
                break;
            case R.id.export_bt:
                startActivity(new Intent(mAct, ExportActivity.class));
                break;
            case R.id.autotime_bt:
                startActivity(new Intent(mAct, AutoTimeListActivity.class));
                break;
            case R.id.lookBt:
                startActivity(new Intent(mAct, LookListActivity.class));
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
            new AlertDialog.Builder(mAct).setMessage("退出当前帐号？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.getInstance().setUser(null);
                    startActivity(new Intent(mAct, LoginActivity.class));
                    finish();
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
