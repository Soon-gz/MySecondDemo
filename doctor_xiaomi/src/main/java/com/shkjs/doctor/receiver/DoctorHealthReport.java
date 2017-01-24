package com.shkjs.doctor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.activity.VideoConsultHReport;

/**
 * Created by Administrator on 2016/11/1.
 */

public class DoctorHealthReport extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null != action && action.equals("com.shkjs.doctor.activity.VideoConsultHReport")){
            Intent intent1 = new Intent(context, VideoConsultHReport.class);
            UserInfoBean userInfoBean = (UserInfoBean) intent.getSerializableExtra(Preference.USER_INFO);
            String orderCode = intent.getStringExtra(Preference.ORDER_CODE);
            Log.i("tag00","用户信息："+userInfoBean.getId()+" orderCode:"+orderCode);
            intent1.putExtra(Preference.USER_INFO,userInfoBean);
            intent1.putExtra(Preference.ORDER_CODE,orderCode);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent1);
        }
    }
}
