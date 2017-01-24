package com.shkjs.patient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nimlib.sdk.NimIntent;
import com.shkjs.nim.utils.SoundPlayUtils;

/**
 * Created by xiaohu on 2016/12/8.
 * <p>
 * 网易消息广播
 */

public class NimMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(context.getPackageName() + NimIntent.ACTION_RECEIVE_MSG)) {
            //        if (action.equals(context.getPackageName() + NimIntent.ACTION_RECEIVE_CUSTOM_NOTIFICATION)) {
            //            CustomNotification notification = (CustomNotification) intent.getSerializableExtra(NimIntent
            //                    .EXTRA_BROADCAST_MSG);
            //            if (null != notification) {
            //                Logger.d(notification.getApnsText() + "\n" + notification.getContent() + "\n" +
            // notification
            //                        .getFromAccount() + "\n" + notification.getSessionId() + "\n" + notification
            // .getConfig()
            //                        .enablePushNick +
            //                        "\n" + notification.getPushPayload() + "\n" + notification.getSessionType()
            // .getValue() + "\n" +
            //                        notification.getTime());
            //            }
            //            SoundPlayUtils.getInstance().play(SoundPlayUtils.SoundSource.MSG.getId());
            //        } else if (action.equals(context.getPackageName() + NimIntent.ACTION_RECEIVE_MSG)) {
            //            Bundle bundle = intent.getExtras();
            //            for (String key : bundle.keySet()) {
            //                Logger.d("key : " + key + "\nvalue : " + bundle.get(key));
            //            }

            SoundPlayUtils.getInstance().play(SoundPlayUtils.SoundSource.MSG.getId());
        }
    }
}
