package com.tzq.maintenance.bean;

import com.tzq.common.utils.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */

public class Check implements Serializable {
    public int id;
    public String stake_ud;//桩号
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
    public String offlineId;//

    public List<Stake> sub_stakes;
    public String tuzhi;
    public String tuzhi_sys;
    public String attach;
    private List<String> tuzhiPicUris;//just for cache
    private ArrayList<String> newTuzhiPicUris;//just offline save
    private List<String> tuzhiSysPicUris;//just for cache
    private ArrayList<String> newTuzhiSysPicUris;//just offline save
    private List<String> attachPicUris;//just for cache
    private ArrayList<String> newAttachPicUris;//just offline save

    public Check() {
    }

    public List<String> getTuzhiPicUris() {
        if (!Util.isEmpty(tuzhiPicUris)) {
            return tuzhiPicUris;
        }

        if (tuzhiPicUris == null) {
            tuzhiPicUris = new ArrayList<>();
        }
        if (!Util.isEmpty(tuzhi)) {
            String[] urls = tuzhi.split(",");
            for (String s : urls) {
                tuzhiPicUris.add(s);
            }
        }

        return tuzhiPicUris;
    }

    public ArrayList<String> getTuzhiNewPicUris() {
        if (newTuzhiPicUris != null) {
            return newTuzhiPicUris;
        }
        newTuzhiPicUris = new ArrayList<>();
        newTuzhiPicUris.addAll(getTuzhiPicUris());
        return newTuzhiPicUris;
    }

    public List<String> getTuzhiSysPicUris() {
        if (!Util.isEmpty(tuzhiSysPicUris)) {
            return tuzhiSysPicUris;
        }

        if (tuzhiSysPicUris == null) {
            tuzhiSysPicUris = new ArrayList<>();
        }
        if (!Util.isEmpty(tuzhi_sys)) {
            String[] urls = tuzhi_sys.split(",");
            for (String s : urls) {
                tuzhiSysPicUris.add(s);
            }
        }

        return tuzhiSysPicUris;
    }

    public ArrayList<String> getTuzhiSysNewPicUris() {
        if (newTuzhiSysPicUris != null) {
            return newTuzhiSysPicUris;
        }
        newTuzhiSysPicUris = new ArrayList<>();
        newTuzhiSysPicUris.addAll(getTuzhiSysPicUris());
        return newTuzhiSysPicUris;
    }

    public List<String> getAttachPicUris() {
        if (!Util.isEmpty(attachPicUris)) {
            return attachPicUris;
        }

        if (attachPicUris == null) {
            attachPicUris = new ArrayList<>();
        }
        if (!Util.isEmpty(attach)) {
            String[] urls = attach.split(",");
            for (String s : urls) {
                attachPicUris.add(s);
            }
        }

        return attachPicUris;
    }

    public ArrayList<String> getAttachNewPicUris() {
        if (newAttachPicUris != null) {
            return newAttachPicUris;
        }
        newAttachPicUris = new ArrayList<>();
        newAttachPicUris.addAll(getAttachPicUris());
        return newAttachPicUris;
    }
}
