package com.raspberry.library.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.raspberry.library.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by xiaohu on 2016/11/18.
 * <p>
 * 通知栏管理类
 */

public class NotificationManager {

    private android.app.NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private Builder mBuilder;
    private Context context;

    private NotificationManager(Context context, Builder mBuilder) {
        this.context = context;
        this.mBuilder = mBuilder;
        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(mBuilder.title)//设置通知栏标题
                .setContentText(mBuilder.content) //设置通知栏显示内容
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), mBuilder.iconId))//通知图标
                .setContentIntent(mBuilder.pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,
                // 主动网络连接)
                .setDefaults(mBuilder.flags)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setVibrate(new long[]{0, 300, 500, 700})//震动时间
                .setSmallIcon(mBuilder.iconId);//设置通知小ICON

        notification = builder.build();
        notification.ledARGB = mBuilder.ledARGB;
        notification.ledOnMS = mBuilder.ledOnMs;
        notification.ledOffMS = mBuilder.ledOffMs;
        notificationManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void sendNotify() {
        notificationManager.notify(mBuilder.notifyId, notification);
    }

    public void dismissNotify() {
        notificationManager.cancel(mBuilder.notifyId);
    }

    public void dismissNotify(int id) {
        notificationManager.cancel(id);
    }

    public void dismissAllNotify() {
        notificationManager.cancelAll();
    }

    public static class Builder {

        private Context context;

        private int notifyId = 1;
        //默认图标
        private int iconId = R.drawable.ic_launcher;
        //默认标题
        private CharSequence title = "";
        //默认内容
        private CharSequence content = "";
        //默认flag
        private int flags = Notification.FLAG_SHOW_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND
                | Notification.FLAG_ONLY_ALERT_ONCE;
        // 呼吸灯配置
        private int ledARGB = Color.GREEN;
        private int ledOnMs = 1000;
        private int ledOffMs = 1500;
        //是否震动
        private boolean isVibrate = true;
        //是否响铃
        private boolean isSound = true;
        //点击事件
        private PendingIntent pendingIntent;
        private Intent intent;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setContent(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder setIconId(int iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setNotifyId(int notifyId) {
            this.notifyId = notifyId;
            return this;
        }

        public Builder setVibrate(boolean vibrate) {
            isVibrate = vibrate;
            return this;
        }

        public Builder setSound(boolean sound) {
            isSound = sound;
            return this;
        }

        public Builder setPendingIntent(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.intent = intent;
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent
                    .FLAG_CANCEL_CURRENT);
            return this;
        }

        public NotificationManager create() {
            return new NotificationManager(context, this);
        }
    }
}
