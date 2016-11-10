package com.tzq.maintenance.bean;

/**
 * Created by Administrator on 2016/11/11.
 */

public class ResponseData {
    public int code;
    public String data;
    public String msg;
    public ResponseData() {
    }
    public ResponseData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess(){
        return code==0;
    }
}
