package com.tzq.maintenance.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */

public class Check implements Serializable {
    public int id;
    public String stake_ud;//上行
    public String cate;//
    public String stake_num1;//
    public String stake_num2;//
    public String project_name;//
    public String start_time;//
//    public int is_checked;//
    public String end_time;//
    public List<Detail> detail;//
    public List<Detail> detail_new;//
//    public String days;//
    public String project_cost;//
    public String before_pic;//
    public int structure_id;//
    public String created_at;//
    public String edit_at;//
//    public int created_user_id;//
    public int step;//
//    public int company_id;//
//    public int maintenance_id;//
//    public int role_id;//

    //验收单特有的
    public int notice_id;
    public String check_time;//
    public List<Detail> detail_new_edit;//
    public int notice_pass;//
    public String construction_pic;//"./Public/file_material/20161028/_14776327328b76397a5a12a9d2.jpg,./Public/file_material/20161028/_1477632736f10c40ed8075f45c.jpg,
    public String after_pic;//

}
