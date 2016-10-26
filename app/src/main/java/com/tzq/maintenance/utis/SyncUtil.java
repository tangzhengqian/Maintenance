package com.tzq.maintenance.utis;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.Maintenance;
import com.tzq.maintenance.bean.Management;
import com.tzq.maintenance.bean.Role;
import com.tzq.maintenance.core.CompleteListener;
import com.tzq.maintenance.core.HttpTask;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/25.
 */

public class SyncUtil {
    private static final int MAX_RETRY_COUNT = 5;
    private static int sRetryCount = 0;

    public static List<CompleteListener> sCompleteListeners = new ArrayList<>();

    public static void startSync() {
        sRetryCount = 0;
        getCompanyList();
    }

    private static void notifyComplete() {
        for (CompleteListener l : sCompleteListeners) {
            l.onComplete(null);
        }
    }

    private static void notifyFail() {
        for (CompleteListener l : sCompleteListeners) {
            l.onFail();
        }
    }


    private static void getCompanyList() {
        new HttpTask(Config.url_company_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, String data, String msg) {
                if (isSuccess) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Company.class);
                    List<Company> list = new Gson().fromJson(data, new TypeToken<List<Company>>() {
                    }.getType());
                    for (Company item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getManagementList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getCompanyList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .build());
    }

    private static void getManagementList() {
        new HttpTask(Config.url_management_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, String data, String msg) {
                if (isSuccess) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Management.class);
                    List<Management> list = new Gson().fromJson(data, new TypeToken<List<Management>>() {
                    }.getType());
                    for (Management item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getMaintenanceList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getManagementList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .add("company_id", App.getInstance().getUser().company_id + "")
                .build());
    }

    private static void getMaintenanceList() {
        new HttpTask(Config.url_maintenance_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, String data, String msg) {
                if (isSuccess) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Maintenance.class);
                    List<Maintenance> list = new Gson().fromJson(data, new TypeToken<List<Maintenance>>() {
                    }.getType());
                    for (Maintenance item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getRoleList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getMaintenanceList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .add("company_id", App.getInstance().getUser().company_id + "")
                .build());
    }

    private static void getRoleList() {
        new HttpTask(Config.url_role_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, String data, String msg) {
                if (isSuccess) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Role.class);
                    List<Role> list = new Gson().fromJson(data, new TypeToken<List<Role>>() {
                    }.getType());
                    for (Role item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    notifyComplete();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getRoleList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .build());
    }
}
