package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/10/26.
 */
@Table(name = "Role",id = "_id")
public class Role  extends Model {
    @Column
    public int id;
    @Column
    public String auth_str;
    @Column
    public String auth_id;
    @Column
    public String desc;
    @Column
    public String name;
}
