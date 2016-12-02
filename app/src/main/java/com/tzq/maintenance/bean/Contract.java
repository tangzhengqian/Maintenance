package com.tzq.maintenance.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/3.
 */

public class Contract implements Serializable {
    public int id;
    public String clause;
    public String content;
    public String withholding;
    public String date;
    public String created_at;
    public String edit_at;
    public String notes;
    public String management_name;
    public int company_id;
    public int user_id;
    public int maintenance_id;
    public int management_id;
}
