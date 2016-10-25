package com.tzq.maintenance.utis;

import android.widget.Toast;

import com.tzq.maintenance.App;

/**
 * Created by Administrator on 2016/10/23.
 */

public class MyUtil {
    public static void toast(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int msgId) {
        Toast.makeText(App.getInstance(), msgId, Toast.LENGTH_SHORT).show();
    }
}
