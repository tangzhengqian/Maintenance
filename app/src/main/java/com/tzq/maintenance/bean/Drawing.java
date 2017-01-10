package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by zqtang on 2017/1/10.
 */
@Table(name = "Drawing", id = "_id")
public class Drawing extends Model implements Serializable {
    @Column
    public int id;
    @Column
    public String image_url;
    @Column
    public String image_name;
    @Column
    public int management_id;
    @Column
    public String created_at;
    @Column
    public String created_by;
}
