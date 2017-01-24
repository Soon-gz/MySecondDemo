package com.shkjs.doctor.receiver;

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
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(context.getPackageName() + NimIntent.ACTION_RECEIVE_MSG)) {
            SoundPlayUtils.getInstance().play(SoundPlayUtils.SoundSource.MSG.getId());
        }
    }
}
