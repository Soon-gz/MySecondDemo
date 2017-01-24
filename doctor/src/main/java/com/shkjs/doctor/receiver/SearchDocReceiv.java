package com.shkjs.doctor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shkjs.doctor.activity.SearchDoctorActivity;

/**
 * Created by Administrator on 2016/11/1.
 */

public class SearchDocReceiv extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null != action && action.equals("com.shkjs.doctor.activity.SearchDoctorActivity")) {
            Intent intent1 = new Intent(context, SearchDoctorActivity.class);
            String account = intent.getStringExtra("Account");
            intent1.putExtra("account",account);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent1);
        }
    }
}
