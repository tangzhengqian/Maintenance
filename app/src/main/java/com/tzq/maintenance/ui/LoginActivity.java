package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CompleteListener;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.SyncUtil;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/9/5.
 */

/**
 * 超级管理员 admin  admin
 * 养护科科长：18780104253     pangxie
 * 养护科管理人员：18780104203 pangxie
 * 养护科巡查人员：18780104204 pangxie
 * 施工单位管理人员：18780104205 pangxie
 * 施工单位巡查人员： 18780104206 pangxie
 */
public class LoginActivity extends BaseActivity {
    EditText phoneNumberEt, passwordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);

        phoneNumberEt = (EditText) findViewById(R.id.phone_number_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        phoneNumberEt.setText("18780104206");
        passwordEt.setText("pangxie");
    }

    public void onViewClick(int id) {
        switch (id) {
            case R.id.login_bt:
                RequestBody formBody = new FormBody.Builder()
                        .add("username", phoneNumberEt.getText().toString())
                        .add("password", passwordEt.getText().toString())
                        .build();
                new HttpTask(Config.url_login).setActivity(mAct).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(boolean isSuccess, Object data, String msg) {
                        if (isSuccess) {
                            User user = new Gson().fromJson(data.toString(), User.class);
                            App.getInstance().setUser(user);
                            if(SyncUtil.isDataSyncCompleted()){
                                startActivity(new Intent(mAct, MainActivity.class));
                                finish();
                            }else {
                                SyncUtil.sCompleteListeners.add(new CompleteListener() {
                                    @Override
                                    public void onComplete(Object data) {
                                        startActivity(new Intent(mAct, MainActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });
                                SyncUtil.startSync();
                            }
                        }
                    }
                }).start(formBody);
                break;
        }
    }
}
