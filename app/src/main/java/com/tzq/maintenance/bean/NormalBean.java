package com.tzq.maintenance.bean;

/**
 * Created by Administrator on 2016/10/31.
 */

public class NormalBean {
    public String id;
    public String name;

    public NormalBean() {
    }

    public NormalBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
