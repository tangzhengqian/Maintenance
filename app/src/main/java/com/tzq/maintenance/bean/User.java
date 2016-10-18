package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by zqtang on 16/8/30.
 */
@Table(name = "User",id = "_id")
public class User extends Model{
    @Column
    public String name;

}
