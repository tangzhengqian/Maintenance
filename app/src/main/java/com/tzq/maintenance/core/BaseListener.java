package com.tzq.maintenance.core;

/**
 * Created by Administrator on 2016/10/25.
 */

public interface BaseListener {
    void onComplete(Object data);
    void onFail();
}
