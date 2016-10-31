package com.tzq.maintenance.bean;

/**
 * Created by Administrator on 2016/10/31.
 */

public class NoticeType {
    public String id;
    public String name;

    public NoticeType() {
    }

    public NoticeType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
