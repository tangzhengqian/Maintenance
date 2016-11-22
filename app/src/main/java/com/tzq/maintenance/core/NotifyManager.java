package com.tzq.maintenance.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.tzq.maintenance.R;
import com.tzq.maintenance.ui.CheckListActivity;
import com.tzq.maintenance.ui.NoticeListActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2016/11/22.
 */

public class NotifyManager {
    private static final int NOTIFY_NOTICE_ID = 100;
    private static final int NOTIFY_CHECK_ID = 200;
    private Context mContext;
    private NotificationManager mNotifyManger;
    private static NotifyManager sInstance;

    private NotifyManager(Context context) {
        mContext = context;
        mNotifyManger = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
    }

    public static void init(Context context) {
        sInstance = new NotifyManager(context);
    }

    public static NotifyManager getInstance() {
        return sInstance;
    }

    public void notifyNewNotice() {
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //Ticker是状态栏显示的提示
        builder.setTicker("有新的通知单");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle("通知单");
        //第二行内容 通常是通知正文
        builder.setContentText("有新的通知单需要您来处理");
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        builder.setSubText("点击查看");
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
//        builder.setNumber(2);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //下拉显示的大图标
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.push));
        Intent intent = new Intent(mContext, NoticeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        mNotifyManger.notify(NOTIFY_NOTICE_ID, notification);
    }

    public void notifyNewCheck() {
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        //Ticker是状态栏显示的提示
        builder.setTicker("有新的验收单");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle("验收单");
        //第二行内容 通常是通知正文
        builder.setContentText("有新的验收单需要您来处理");
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        builder.setSubText("点击查看");
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
//        builder.setNumber(2);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //下拉显示的大图标
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.push));
        Intent intent = new Intent(mContext, CheckListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        mNotifyManger.notify(NOTIFY_CHECK_ID, notification);
    }
}
