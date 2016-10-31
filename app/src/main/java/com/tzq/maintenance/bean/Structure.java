package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/11/1.
 */
@Table(name = "Structure",id = "_id")
public class Structure extends Model {
    @Column
    public int id;
    @Column
    public String structure_name;
    @Column
    public String management_id;

    @Override
    public String toString() {
        return structure_name;
    }
}
