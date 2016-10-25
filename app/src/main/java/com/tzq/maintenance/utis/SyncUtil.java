package com.tzq.maintenance.utis;

import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.core.CompleteListener;
import com.tzq.maintenance.core.HttpTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;

/**
 * Created by Administrator on 2016/10/25.
 */

public class SyncUtil {
    private static final int MAX_RETRY_COUNT = 5;

    private static Map<String, Integer> sSyncFailMap = new HashMap<>();
    public static List<CompleteListener> sCompleteListeners = new ArrayList<>();

    public static void startSync() {
        notifyStatus();
        int status = checkStatus();
        if (status == 0 || status == 1) {
            return;
        }
        sSyncFailMap.clear();
        getCompanyList();
        getManagementList();
        getMaintenanceList();
        getRoleList();
    }

    public static boolean isDataSyncCompleted(){
        return true;
    }


    private static int checkStatus() {
        Iterator<String> iterator = sSyncFailMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            int count = sSyncFailMap.get(key);
            if (count != -1) {
                if (count > MAX_RETRY_COUNT) {
                    return 2;//fail
                } else {
                    return 1;//ding
                }
            }
        }
        return 0;//complete
    }

    private static void notifyStatus() {
        int status = checkStatus();
        if (status == 0) {
            for (CompleteListener l : sCompleteListeners) {
                l.onComplete(null);
            }
        } else if (status == 2) {
            for (CompleteListener l : sCompleteListeners) {
                l.onFail();
            }
        } else {
            //is sync ing
        }
    }


    private static void getCompanyList() {
        final String key = "getManagementList";
        new HttpTask(Config.url_company_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, Object data, String msg) {
                if (isSuccess) {
                    sSyncFailMap.put(key, -1);
                    notifyStatus();
                } else {
                    int count = sSyncFailMap.get(key) == null ? 0 : sSyncFailMap.get(key);
                    if (count < MAX_RETRY_COUNT) {
                        count++;
                        sSyncFailMap.put(key, count);
                        getCompanyList();
                    }else {
                        notifyStatus();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .add("company_id", App.getInstance().getUser().company_id + "")
                .build());
    }

    private static void getManagementList() {
        final String key = "getManagementList";
        new HttpTask(Config.url_management_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, Object data, String msg) {
                if (isSuccess) {
                    sSyncFailMap.put(key, -1);
                    notifyStatus();
                } else {
                    int count = sSyncFailMap.get(key) == null ? 0 : sSyncFailMap.get(key);
                    if (count < MAX_RETRY_COUNT) {
                        count++;
                        sSyncFailMap.put(key, count);
                        getManagementList();
                    }else {
                        notifyStatus();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .build());
    }

    private static void getMaintenanceList() {
        final String key = "getMaintenanceList";
        new HttpTask(Config.url_maintenance_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, Object data, String msg) {
                if (isSuccess) {
                    sSyncFailMap.put(key, -1);
                    notifyStatus();
                } else {
                    int count = sSyncFailMap.get(key) == null ? 0 : sSyncFailMap.get(key);
                    if (count < MAX_RETRY_COUNT) {
                        count++;
                        sSyncFailMap.put(key, count);
                        getMaintenanceList();
                    }else {
                        notifyStatus();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .build());
    }

    private static void getRoleList() {
        final String key = "getRoleList";
        new HttpTask(Config.url_role_list).addCompleteCallBack(new HttpTask.CompleteCallBack() {
            @Override
            public void onComplete(boolean isSuccess, Object data, String msg) {
                if (isSuccess) {
                    sSyncFailMap.put(key, -1);
                    notifyStatus();
                } else {
                    int count = sSyncFailMap.get(key) == null ? 0 : sSyncFailMap.get(key);
                    if (count < MAX_RETRY_COUNT) {
                        count++;
                        sSyncFailMap.put(key, count);
                        getRoleList();
                    }else {
                        notifyStatus();
                    }
                }
            }
        }).start(new FormBody.Builder()
                .build());
    }
}
