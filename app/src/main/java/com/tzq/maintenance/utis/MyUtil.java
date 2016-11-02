package com.tzq.maintenance.utis;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;
import com.tzq.common.utils.LogUtil;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.DetailType;
import com.tzq.maintenance.bean.NoticeType;
import com.tzq.maintenance.bean.Structure;

import java.util.List;

/**
 * Created by Administrator on 2016/10/23.
 */

public class MyUtil {
    public static void toast(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int msgId) {
        Toast.makeText(App.getInstance(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static String getNoticeStatusStr(int status) {
        switch (status) {
            case 0:
                return "新创建";
            case 10://为提交到施工管理
                return "施工审核";
            case 20://提交到养护科管理
                return "养护科审核";
            case 30://提交到科长
                return "科长审核";
            case 11://施工管理通过
                return "通过";
            case 21://养护科管理通过
                return "通过";
            case 31://科长通过
                return "通过";
            case 12://施工管理不通过
                return "不通过";
            case 22://养护科管理不通过
                return "不通过";
            case 32://科长不通过
                return "不通过";
        }
        return "未知";
    }

    public static String getNoticeCateStr(String id) {
        for (NoticeType n : Config.CATES) {
            if (n.id.equals(id)) {
                return n.name;
            }
        }
        return "";
    }

    public static int getNoticeCateIndex(String id) {
        int i = 0;
        for (NoticeType n : Config.CATES) {
            if (n.id.equals(id)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static int getStructureIndex(int id) {
        int i = 0;
        List<Structure> list = new Select().from(Structure.class).execute();
        for (Structure n : list) {
            if (n.id == id) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static int getDetailTypeIndex(List<DetailType> list, int id) {
        int i = 0;
        for (DetailType n : list) {
            if (n.id == id) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static int getDetailIndex(List<Detail> list, int id) {
        int i = 0;
        for (Detail n : list) {
            if (n.id == id) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static void displayPic(ImageView iv, String url) {
        LogUtil.i("displayPic  " + url);
        if (!url.startsWith("http://")) {
            url = Config.url_host + url;
        }
        Picasso.with(App.getInstance()).load(url).resize(60, 60).centerCrop().into(iv);
    }

    public static void displayLargePic(ImageView iv, String url) {
        LogUtil.i("displayPic  " + url);
        if (!url.startsWith("http://")) {
            url = Config.url_host + url;
        }
        Picasso.with(App.getInstance()).load(url).into(iv);
    }

    public static DetailType getDetailType(int id) {
        DetailType detailType = new Select().from(DetailType.class).where("id = " + id).executeSingle();
        return detailType == null ? new DetailType() : detailType;
    }

    public static Detail getDetail(int id) {
        Detail detail = new Select().from(Detail.class).where("id = " + id).executeSingle();
        return detail == null ? new Detail() : detail;
    }

    public static <T> void setUpSp(Context context, Spinner sp, List<T> list) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    public static <T> void setUpSp(Context context, Spinner sp, T[] array) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

}
