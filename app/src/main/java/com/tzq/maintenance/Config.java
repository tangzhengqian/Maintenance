package com.tzq.maintenance;

import com.tzq.maintenance.bean.NoticeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/23.
 */

public class Config {
    public static final String url_host = "http://project.wuss.cn";
    public static final String url_login = url_host + "/Home/Login/login";
    public static final String url_management_list = url_host + "/Home/Api/getManagementList";
    public static final String url_maintenance_list = url_host + "/Home/Api/getMaintenanceList";
    public static final String url_role_list = url_host + "/Home/Api/getRoleList";
    public static final String url_company_list = url_host + "/Home/Api/getCompanyList";
    public static final String url_notice_list = url_host + "/Home/Api/getNoticeList";
    public static final String url_notice_detail = url_host + "/Home/Api/getNoticeDetail";
    public static final String url_structure_list = url_host + "/Home/Api/getStructure";
    public static final String url_detail_type_list = url_host + "/Home/Api/getDetailCate";
    public static final String url_detail_list = url_host + "/Home/Api/getDetail";
    public static final String url_contract_list = url_host + "/Home/Api/getContract";

    public static final String prefs_key_login_user = "user";
    public static final String prefs_key_sync_time = "sync_time";

    public static final long sync_delay = 1000 * 60 * 10;


    public static final List<NoticeType> CATES = new ArrayList<>();

    static {
        CATES.add(new NoticeType("1", "日常养护"));
        CATES.add(new NoticeType("2", "路产赔付"));
        CATES.add(new NoticeType("3", "灾害抢险"));
    }

}
