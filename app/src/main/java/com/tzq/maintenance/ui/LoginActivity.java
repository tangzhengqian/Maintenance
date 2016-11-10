package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.R;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.bean.User;
import com.tzq.maintenance.core.CompleteListener;
import com.tzq.maintenance.core.HttpTask;
import com.tzq.maintenance.utis.MyUtil;
import com.tzq.maintenance.utis.ProgressDialogUtil;
import com.tzq.maintenance.utis.SyncUtil;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
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
        setContentView(R.layout.login_activity);
        setTitle(R.string.login_title);

        phoneNumberEt = (EditText) findViewById(R.id.phone_number_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        phoneNumberEt.setText("18780104204");
        passwordEt.setText("pangxie");

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), new File(""));

    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.login_bt:
                ProgressDialogUtil.show(mAct);
                new HttpTask(Config.url_login).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(ResponseData responseData) {
                        if (responseData.isSuccess()) {
                            User user = new Gson().fromJson(responseData.data, User.class);
                            App.getInstance().setUser(user);
                            SyncUtil.sCompleteListeners.add(new CompleteListener() {
                                @Override
                                public void onComplete(Object data) {
                                    MyUtil.toast("登录成功");
                                    ProgressDialogUtil.hide(mAct);
                                    startActivity(new Intent(mAct, MainActivity.class));
                                    finish();
                                }
                            });
                            SyncUtil.startSync();
                        }
                    }
                }).enqueue(new FormBody.Builder()
                        .add("username", phoneNumberEt.getText().toString())
                        .add("password", passwordEt.getText().toString())
                        .build());
                break;
        }
    }
}
