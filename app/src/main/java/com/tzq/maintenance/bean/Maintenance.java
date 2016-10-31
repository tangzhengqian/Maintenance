package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/10/26.
 */
@Table(name = "Maintenance",id = "_id")
public class Maintenance extends Model {
    @Column
    public int id;
    @Column
    public String company_id;
    @Column
    public String management_id;
    @Column
    public String name;
    @Column
    public String create_at;
    @Column
    public String desc;

}
