package com.tzq.maintenance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

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

import okhttp3.FormBody;

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
//        phoneNumberEt.setText("18780104253");
//        passwordEt.setText("pangxie");

    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.login_bt:
                ProgressDialogUtil.show(mAct, "正在登录...");
                new HttpTask(Config.url_login).addCompleteCallBack(new HttpTask.CompleteCallBack() {
                    @Override
                    public void onComplete(final ResponseData responseData) {
                        if (responseData.isSuccess()) {
                            User user = Config.gson.fromJson(responseData.data, User.class);
                            App.getInstance().setUser(user);
                            SyncUtil.sCompleteListeners.add(new CompleteListener() {
                                @Override
                                public void onComplete(Object data) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyUtil.toast("登录成功");
                                            ProgressDialogUtil.hide(mAct);
                                            startActivity(new Intent(mAct, MainActivity.class));
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onFail() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyUtil.toast("获取资源失败");
                                            ProgressDialogUtil.hide(mAct);
                                            App.getInstance().setUser(null);
                                        }
                                    });
                                }

                            });
                            SyncUtil.startSync();
                        } else {

                            ProgressDialogUtil.hide(mAct);
                            MyUtil.toast("登录失败");
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
