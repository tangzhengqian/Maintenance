package com.tzq.maintenance.bean;

/**
 * Created by Administrator on 2016/11/14.
 */

public class NoticeDealBean {
    public String nextStr;
    public String nextAct;
    public String cancelStr;
    public String cancelAct;

    public NoticeDealBean(String nextStr, String nextAct, String cancelStr, String cancelAct) {
        this.nextStr = nextStr;
        this.nextAct = nextAct;
        this.cancelStr = cancelStr;
        this.cancelAct = cancelAct;
    }

    @Override
    public String toString() {
        return "NoticeDealBean{" +
                "nextStr='" + nextStr + '\'' +
                ", nextAct='" + nextAct + '\'' +
                ", cancelStr='" + cancelStr + '\'' +
                ", cancelAct='" + cancelAct + '\'' +
                '}';
    }
}
