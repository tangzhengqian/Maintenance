package com.tzq.maintenance.bean;

/**
 * Created by Administrator on 2016/11/14.
 */

public class DealBean {
    public String nextStr;
    public String nextAct;
    public String cancelStr;
    public String cancelAct;

    public DealBean(String nextStr, String nextAct, String cancelStr, String cancelAct) {
        this.nextStr = nextStr;
        this.nextAct = nextAct;
        this.cancelStr = cancelStr;
        this.cancelAct = cancelAct;
    }

    @Override
    public String toString() {
        return "DealBean{" +
                "nextStr='" + nextStr + '\'' +
                ", nextAct='" + nextAct + '\'' +
                ", cancelStr='" + cancelStr + '\'' +
                ", cancelAct='" + cancelAct + '\'' +
                '}';
    }

    public DealBean(){}
}
