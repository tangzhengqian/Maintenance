package com.tzq.maintenance.utis;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzq.common.core.PrefsManager;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.bean.Company;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.DetailType;
import com.tzq.maintenance.bean.Maintenance;
import com.tzq.maintenance.bean.Management;
import com.tzq.maintenance.bean.ResponseData;
import com.tzq.maintenance.bean.Role;
import com.tzq.maintenance.bean.Structure;
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
        PrefsManager.getInstance().save(Config.prefs_key_sync_time, System.currentTimeMillis());
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
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Company.class).execute();
                    List<Company> list = new Gson().fromJson(responseData.data, new TypeToken<List<Company>>() {
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
        }).enqueue(new FormBody.Builder()
                .build());
    }

    private static void getManagementList() {
        new HttpTask(Config.url_management_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Management.class).execute();
                    List<Management> list = new Gson().fromJson(responseData.data, new TypeToken<List<Management>>() {
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
        }).enqueue(new FormBody.Builder()
                .add("company_id", App.getInstance().getUser().company_id + "")
                .build());
    }

    private static void getMaintenanceList() {
        new HttpTask(Config.url_maintenance_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Maintenance.class).execute();
                    List<Maintenance> list = new Gson().fromJson(responseData.data, new TypeToken<List<Maintenance>>() {
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
        }).enqueue(new FormBody.Builder()
                .add("company_id", App.getInstance().getUser().company_id + "")
                .build());
    }

    private static void getRoleList() {
        new HttpTask(Config.url_role_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Role.class).execute();
                    List<Role> list = new Gson().fromJson(responseData.data, new TypeToken<List<Role>>() {
                    }.getType());
                    for (Role item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getStructureList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getRoleList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).enqueue(new FormBody.Builder()
                .build());
    }

    private static void getStructureList() {
        new HttpTask(Config.url_structure_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Structure.class).execute();
                    List<Structure> list = new Gson().fromJson(responseData.data, new TypeToken<List<Structure>>() {
                    }.getType());
                    for (Structure item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getDetailTypeList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getStructureList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).enqueue(new FormBody.Builder()
                .build());
    }

    private static void getDetailTypeList() {
        new HttpTask(Config.url_detail_type_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(DetailType.class).execute();
                    List<DetailType> list = new Gson().fromJson(responseData.data, new TypeToken<List<DetailType>>() {
                    }.getType());
                    for (DetailType item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    sRetryCount = 0;
                    getDetailList();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getDetailTypeList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).enqueue(new FormBody.Builder()
                .build());
    }

    private static void getDetailList() {
        new HttpTask(Config.url_detail_list).setShowMessage(false).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(ResponseData responseData) {
                if (responseData.isSuccess()) {
                    ActiveAndroid.beginTransaction();
                    new Delete().from(Detail.class).execute();
                    List<Detail> list = new Gson().fromJson(responseData.data, new TypeToken<List<Detail>>() {
                    }.getType());
                    for (Detail item : list) {
                        item.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    notifyComplete();
                } else {
                    sRetryCount++;
                    if (sRetryCount <= MAX_RETRY_COUNT) {
                        getDetailList();
                    } else {
                        notifyFail();
                    }
                }
            }
        }).enqueue(new FormBody.Builder()
                .build());
    }
}
