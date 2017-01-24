package com.shkjs.nim.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.session.activity.P2PMessageActivity;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.activity.AVChatActivity;

/**
 * 音视频聊天通知栏
 * Created by huangjun on 2015/5/14.
 */
public class AVChatNotification {

    private Context context;

    private NotificationManager notificationManager;
    private Notification callingNotification;
    private Notification missCallNotification;
    private String account;
    private String displayName;
    private static final int CALLING_NOTIFY_ID = 111;
    private static final int MISS_CALL_NOTIFY_ID = 112;

    public AVChatNotification(Context context) {
        this.context = context;
    }

    public void init(String account) {
        this.account = account;
        this.displayName = NimUserInfoCache.getInstance().getUserDisplayName(account);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void buildCallingNotification() {
        if (callingNotification == null) {
            Intent localIntent = new Intent();
            localIntent.setClass(context, AVChatActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            String tickerText = String.format(context.getString(R.string.avchat_notification), displayName);
            //TODO lxh 换成自己家的图标
            int iconId = R.mipmap.ic_launcher;

            PendingIntent pendingIntent = PendingIntent.getActivity(context, CALLING_NOTIFY_ID, localIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            callingNotification = makeNotification(pendingIntent, context.getString(R.string.avchat_call),
                    tickerText, tickerText, iconId, false, false);
        }
    }

    private void buildMissCallNotification() {
        //TODO lxh 需更具情况修改
        if (missCallNotification == null) {
            Intent data = new Intent(context, P2PMessageActivity.class);
            data.putExtra(Extras.EXTRA_ACCOUNT, account);
            data.putExtra(Extras.EXTRA_FROM, Extras.EXTRA_FROM_NOTIFICATION);
            //            data.putExtra(Extras.EXTRA_DATA, data);
            data.putExtra(Extras.EXTRA_JUMP_P2P, true);
            data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            data.setAction(Intent.ACTION_VIEW);
            data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, CALLING_NOTIFY_ID, data, PendingIntent
                    .FLAG_UPDATE_CURRENT);

            String title = context.getString(R.string.avchat_no_pickup_call);
            String tickerText = NimUserInfoCache.getInstance().getUserDisplayName(account) + ": 【网络通话】";
            int iconId = R.drawable.avchat_no_pickup;

            missCallNotification = makeNotification(pendingIntent, title, tickerText, tickerText, iconId, true, true);
        }
    }

    private Notification makeNotification(PendingIntent pendingIntent, String title, String content, String
            tickerText, int iconId, boolean ring, boolean vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title).setContentText(content).setAutoCancel(true).setContentIntent(pendingIntent)
                .setTicker(tickerText).setSmallIcon(iconId);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (ring) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        builder.setDefaults(defaults);

        return builder.build();
    }

    public void activeCallingNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildCallingNotification();
                notificationManager.notify(CALLING_NOTIFY_ID, callingNotification);
            } else {
                notificationManager.cancel(CALLING_NOTIFY_ID);
            }
        }
    }

    public void activeMissCallNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildMissCallNotification();
                notificationManager.notify(MISS_CALL_NOTIFY_ID, missCallNotification);
            } else {
                notificationManager.cancel(MISS_CALL_NOTIFY_ID);
            }
        }
    }
}
