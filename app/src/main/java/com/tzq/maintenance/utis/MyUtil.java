package com.tzq.maintenance.utis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;
import com.tzq.common.utils.LogUtil;
import com.tzq.common.utils.Util;
import com.tzq.maintenance.App;
import com.tzq.maintenance.Config;
import com.tzq.maintenance.bean.DealBean;
import com.tzq.maintenance.bean.Detail;
import com.tzq.maintenance.bean.DetailType;
import com.tzq.maintenance.bean.NormalBean;
import com.tzq.maintenance.bean.Structure;
import com.tzq.maintenance.core.CompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static String getStepStr(int step) {
        switch (step) {
            case 0:
                return "新创建";
            case 10:
                return "施工审核";
            case 20:
                return "养护科审核";
            case 30:
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

    public static DealBean getNoticeDealStr(int step, int createUserRoleId, int nowUseRoleId) {
        DealBean bean = null;
        if (step == 0) {
            if (createUserRoleId == nowUseRoleId) {
                if (createUserRoleId == 6) {//施工单位巡查人员
                    return new DealBean("提交到施工管理", "for_5", null, null);
                } else if (createUserRoleId == 5) {//施工单位管理
                    return new DealBean("提交到养护科管理", "for_3", "不通过", "return_6");
                } else if (createUserRoleId == 4) {//养护科巡查人员
                    return new DealBean("提交到养护科管理", "for_3", null, null);
                } else if (createUserRoleId == 3) {//养护科管理
                    return new DealBean("提交到科长", "for_2", "不通过", "return_5");
                } else if (createUserRoleId == 2) {//养护科管理
                    return new DealBean("通过", "for_end", "不通过", "return_3");
                }
            }
        } else if (step == 10) {
            if (nowUseRoleId == 5) {//施工单位管理
                return new DealBean("提交到养护科管理", "for_3", "不通过", "return_6");
            }
        } else if (step == 20) {
            if (nowUseRoleId == 3) {//养护科管理
                return new DealBean("提交到科长", "for_2", "不通过", "return_5");
            }
        } else if (step == 30) {
            if (nowUseRoleId == 2) {//科长
                return new DealBean("通过", "for_end", "不通过", "return_3");
            }
        }
        return bean;
    }

    public static DealBean getCheckDealStr(int step, int nowUseRoleId) {
        DealBean bean = null;
        if (step == 0) {
            if (nowUseRoleId == 5) {//施工单位管理
                return new DealBean("提交到养护科管理", "for_3", null, null);
            }
        } else if (step == 10) {
            if (nowUseRoleId == 3) {//养护科管理
                return new DealBean("提交到科长", "for_2", "不通过", "return_5");
            }
        } else if (step == 30) {
            if (nowUseRoleId == 2) {//科长
                return new DealBean("通过", "for_end", "不通过", "return_3");
            }
        }
        return bean;
    }

    public static String getNoticeCateStr(String id) {
        for (NormalBean n : Config.CATES) {
            if (n.id.equals(id)) {
                return n.name;
            }
        }
        return "";
    }

    public static int getNoticeStakeIndex(String id) {
        int i = 0;
        for (NormalBean n : Config.CATES) {
            if (n.id.equals(id)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static int getNoticeCateIndex(String id) {
        int i = 0;
        for (NormalBean n : Config.CATES) {
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
//        LogUtil.i("displayPic  " + url);
        if (!url.contains(":")) {
            url = Config.url_host + url;
        }
        Picasso.with(App.getInstance()).load(url).resize(100, 100).centerCrop().into(iv);
    }

    public static void displayLargePic(ImageView iv, String url) {
//        LogUtil.i("displayPic  " + url);
        if (!url.contains(":")) {
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

    public static void showDateTimeDialog(final Activity act, final View v) {
        String value = "";
        if (v instanceof TextView) {
            TextView t = (TextView) v;
            value = t.getText().toString();
        } else if (v instanceof EditText) {
            EditText t = (EditText) v;
            value = t.getText().toString();
        }
        final String dfVaule = value;
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        showDateDialog(act, dfVaule, df, new CompleteListener() {
            @Override
            public void onComplete(final Object date) {
                showTimeDialog(act, dfVaule, df, new CompleteListener() {

                    @Override
                    public void onComplete(Object time) {
                        String dateTime = date.toString() + " " + time.toString() + ":00";
                        LogUtil.i("--dateTime=" + dateTime);
                        if (v instanceof TextView) {
                            TextView t = (TextView) v;
                            t.setText(dateTime);
                        } else if (v instanceof EditText) {
                            EditText t = (EditText) v;
                            t.setText(dateTime);
                        }
                    }
                });
            }
        });
    }

    public static void showDateDialog(Activity act, final String dftValue, final SimpleDateFormat sdf, final CompleteListener l) {
        Date date = null;
        try {
            Calendar cal = Calendar.getInstance();
            if (Util.isEmpty(dftValue)) {
                cal.setTimeInMillis(System.currentTimeMillis());
            } else {
                date = sdf.parse(dftValue);
                cal.setTime(date);
            }
            DatePickerDialog dpd = new DatePickerDialog(act, new OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(year, monthOfYear, dayOfMonth);
                    String str = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                    if (l != null) {
                        l.onComplete(str);
                    }
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.setCanceledOnTouchOutside(false);
            dpd.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void showTimeDialog(Activity act, String dftValue, final SimpleDateFormat df, final CompleteListener l) {
        Date date = null;
        try {
            Calendar cal = Calendar.getInstance();
            if (Util.isEmpty(dftValue)) {
                cal.setTimeInMillis(System.currentTimeMillis());
            } else {
                date = df.parse(dftValue);

                cal.setTime(date);
            }
            TimePickerDialog tpd = new TimePickerDialog(act, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    String str = new SimpleDateFormat("HH:mm").format(c.getTime());
                    if (l != null) {
                        l.onComplete(str);
                    }
                }
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            tpd.setCanceledOnTouchOutside(false);
            tpd.show();
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage(), e);
        }
    }

}
