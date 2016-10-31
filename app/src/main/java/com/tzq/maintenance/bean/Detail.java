package com.tzq.maintenance.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2016/11/1.
 */
@Table(name = "Detail", id = "_id")
public class Detail extends Model {
    @Column
    public int id;
    @Column
    public String detail_name;
    @Column
    public String detail_input_price;
    @Column
    public String detail_already_price;
    @Column
    public String detail_quantities;
    @Column
    public int cate_id;
    @Column
    public String detail_num;
    @Column
    public String detail_unit;
    @Column
    public int management_id;
    @Column
    public int company_id;
}
