package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by zqtang on 16/8/30.
 */
@Table(name = "Users",id = "user_id")
public class Users extends Model{
    @Column
    public String user_name;



}
