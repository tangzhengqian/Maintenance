package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/10/26.
 */
@Table(name = "Management",id = "_id")
public class Management extends Model {
    @Column
    public int id;
    @Column
    public String company_id;
    @Column
    public String management_name;
    @Column
    public String management_desc;
    @Column
    public String created_at;

    public Management(){}
}
