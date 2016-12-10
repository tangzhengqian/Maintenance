package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/1.
 */
@Table(name = "Detail", id = "_id")
public class Detail extends Model implements Serializable {
    @Column
    @SerializedName(value = "id",alternate = {"detail_id"})
    public int id;
    @Column
    public String detail_name;
    @Column
    public String detail_input_price;
    @Column
    public String detail_already_price;//default price
    @Column
    public String detail_quantities;
    @Column
    @SerializedName(value = "cate_id",alternate = {"detail_name_cate"})
    public int cate_id;
    @Column
    public String detail_num;
    @Column
    public String detail_unit;
    @Column
    public int management_id;
    @Column
    public int company_id;

    public String detail_price;//custom price
    public String detail_quantities1;
    public String detail_quantities2;
    public String detail_quantities3;
    public String detail_all_price;

    @Override
    public String toString() {
        return detail_name;
    }
}
