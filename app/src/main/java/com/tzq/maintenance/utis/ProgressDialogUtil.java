package com.tzq.maintenance.utis;

import android.app.Activity;
import android.app.ProgressDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/20.
 */

public class ProgressDialogUtil {

    public static class PD {
        public ProgressDialog progressDialog;
        public int count;
    }

    private static Map<Activity, PD> sProgressDialogMap = new HashMap<>();

    public static void show(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        PD pd = sProgressDialogMap.get(activity);
        if (pd == null) {
            pd = new PD();
            pd.progressDialog = new ProgressDialog(activity);
            pd.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.progressDialog.setMessage("正在拼命加载数据....");
            pd.progressDialog.setCancelable(false);

        }
        pd.count++;
        sProgressDialogMap.put(activity, pd);
        if (pd.progressDialog.isShowing()) {
            return;
        }
        pd.progressDialog.show();

    }

    public static void hide(Activity activity) {
        PD pd = sProgressDialogMap.get(activity);
        if (pd == null) {
            return;
        }
        pd.count--;
        if (pd.count <= 0) {
            pd.progressDialog.dismiss();
        }
    }
}
