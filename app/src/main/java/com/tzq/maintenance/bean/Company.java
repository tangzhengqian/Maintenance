package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/10/26.
 */
@Table(name = "Company",id = "_id")
public class Company extends Model {
    @Column
    public int id;
    @Column
    public String name;
    @Column
    public String code;
    @Column
    public String desc;
    @Column
    public String createtime;

    public Company(){}

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", createtime='" + createtime + '\'' +
                '}';
    }
}
