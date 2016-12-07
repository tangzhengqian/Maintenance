package com.tzq.maintenance;

import com.tzq.maintenance.bean.NormalBean;

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
    public static final String url_notice_save = url_host + "/Home/Api/saveNotice";
    public static final String url_notice_delete = url_host + "/Home/Api/delNotice";
    public static final String url_notice_deal = url_host + "/Home/Api/dealNotice";
    public static final String url_structure_list = url_host + "/Home/Api/getStructure";
    public static final String url_detail_type_list = url_host + "/Home/Api/getDetailCate";
    public static final String url_detail_list = url_host + "/Home/Api/getDetail";
    public static final String url_add_pic = url_host + "/Home/Api/addPic";
    public static final String url_del_pic = url_host + "/Home/Api/delPic";
    public static final String url_check_list = url_host + "/Home/Api/getCheckList";
    public static final String url_check_save = url_host + "/Home/Api/saveCheck";
    public static final String url_check_delete = url_host + "/Home/Api/delCheck";
    public static final String url_check_deal = url_host + "/Home/Api/dealCheck";
    public static final String url_get_new_time = url_host + "/Home/Api/getNewTime";
    public static final String url_contract_list = url_host + "/Home/Api/getContractList";
    public static final String url_contract_save = url_host + "/Home/Api/saveContract";


    public static final String url_export1 = url_host + "/Home/ApiDown/exportPaymentCheck";
    public static final String url_export2 = url_host + "/Home/ApiDown/exportPaymentCover";
    public static final String url_export3 = url_host + "/Home/ApiDown/exportCompileState";
    public static final String url_export4 = url_host + "/Home/ApiDown/exportFinancialPayment";
    public static final String url_export5 = url_host + "/Home/ApiDown/exportCalculatePayment";
    public static final String url_export6 = url_host + "/Home/ApiDown/exportPaymentGather";
    public static final String url_export7 = url_host + "/Home/ApiDown/exportCleanLook";
    public static final String url_export8 = url_host + "/Home/ApiDown/exportAllContract";
    public static final String url_export9 = url_host + "/Home/ApiDown/exportPatrolLog";

    public static final String url_export_notice = url_host + "/Home/ApiDown/exportNotice";
    public static final String url_export_check = url_host + "/Home/ApiDown/exportCheck";


    public static final String prefs_key_login_user = "user";
    public static final String prefs_key_sync_time = "sync_time";
    public static final String prefs_key_new_time = "prefs_key_new_time";
    public static final String prefs_key_check_list = "prefs_key_check_list";

    public static final long sync_delay = 1000 * 60 * 10;

    public static final int RESULT_DELETE = 33;


    public static final List<NormalBean> CATES = new ArrayList<>();
    public static final List<NormalBean> STAKES = new ArrayList<>();

    static {
        CATES.add(new NormalBean("1", "日常养护"));
        CATES.add(new NormalBean("2", "路产赔付"));
        CATES.add(new NormalBean("3", "灾害抢险"));

        STAKES.add(new NormalBean("1", "上行"));
        STAKES.add(new NormalBean("2", "下行"));
    }

}
