package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/11/1.
 */
@Table(name = "DetailType",id = "_id")
public class DetailType extends Model {
    @Column
    public int id;
    @Column
    public String cate_name;
    @Column
    public int management_id;
    @Column
    public int company_id;

    public DetailType(){}

    @Override
    public String toString() {
        return cate_name;
    }
}
