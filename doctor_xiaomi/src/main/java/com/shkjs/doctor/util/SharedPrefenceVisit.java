package com.shkjs.doctor.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.bean.VisitTimeDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuwen on 2016/10/11.
 */

public class SharedPrefenceVisit extends SharedPreferencesUtils {

    public static String[]days = {Preference.VISIT_TIME_8,Preference.VISIT_TIME_9,Preference.VISIT_TIME_10,Preference.VISIT_TIME_11
            ,Preference.VISIT_TIME_12,Preference.VISIT_TIME_13,Preference.VISIT_TIME_14,Preference.VISIT_TIME_15,Preference.VISIT_TIME_16
            ,Preference.VISIT_TIME_17,Preference.VISIT_TIME_18,Preference.VISIT_TIME_19,Preference.VISIT_TIME_20,Preference.VISIT_TIME_21
            ,Preference.VISIT_TIME_22,Preference.VISIT_TIME_23};

    public static boolean putVisitTime(ArrayList<VisitTimeDataBean>list){
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            VisitTimeDataBean bean = list.get(i);
            editor.putString(days[i],bean.getSegmentType()+","+bean.getStartTime()+","+bean.getDiagnoseNum());
        }
        return editor.commit();
    }

    public static ArrayList<VisitTimeDataBean> getVisitTimeList(){
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences();
        ArrayList<VisitTimeDataBean>list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            VisitTimeDataBean bean = new VisitTimeDataBean();
            String visittimebean = sharedPreferences.getString(days[i],"0,0,0");
            String[] times = visittimebean.split(",");
            bean.setSegmentType(Integer.parseInt(times[0]));
            bean.setStartTime(Integer.parseInt(times[1]));
            bean.setDiagnoseNum(Integer.parseInt(times[2]));
            list.add(bean);
        }
        return list;
    }
}
