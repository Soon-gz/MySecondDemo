package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.adapter.MyViewPagerAdapter;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.EventBusVisitTime;
import com.shkjs.doctor.bean.VisitTimeDataBean;
import com.shkjs.doctor.bean.VistTimeChang;
import com.shkjs.doctor.fragment.VisitTimeFragment;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AccordionTransformer;
import com.shkjs.doctor.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class VisitTimeActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                   //标题
    @Bind(R.id.text_ringht)
    TextView text_ringht;                   //标题栏右上角文字
    @Bind(R.id.visit_time_viewpager_vp)
    ViewPager visit_time_viewpager_vp;      //viewpager切换
    @Bind(R.id.visit_time_date_ll)
    LinearLayout visit_time_date_ll;        //日期选择

    private List<VisitTimeFragment> fragments;
    private MyViewPagerAdapter<VisitTimeFragment> adapter;
    private int lastPosition = -1;
    private String[] dayTags;

    //在Activity中对Fragment的每天数据做储存
    private List<HashMap<String, ArrayList<VisitTimeDataBean>>> listAllData;
    //用于判断Activity中存储的数据是否发生改变
    private boolean dataIsChanged = false;

    private RaspberryCallback<ObjectResponse<String>> callback;
    private List<HashMap<String, ArrayList<VistTimeChang>>> listChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_time);
        //注解
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText(getIntent().getStringExtra("doctorName"));
        text_ringht.setText("保存");
        //初始化
        initListener();
        //初始化数据
        initData();
    }


    //Activity和Fragment数据交互
    public void onEventMainThread(EventBusVisitTime eventBusVisitTime) {
        dataIsChanged = true;
    }


    private void initData() {

        listAllData = new ArrayList<>();
        listChanged = new ArrayList<>();
        EventBus.getDefault().register(this);

        //今天日期     2016-9-18  12:12
        String allTime = DateUtil.getCurrentTime("yyyy-MM-dd HH:MM");
        //今天几月几号  09-18
        String currentTime = DateUtil.getCurrentTime("MM-dd");
        //今天星期几  星期四
        String currentWeekDay = DateUtil.getCurrentWeekDay();
        //滚动条显示的日期加星期  09-18\n星期四
        String time = currentTime + "\n" + currentWeekDay;
        //保存时间，做标签
        dayTags[0] = allTime.substring(0, 10);

        //第一天的数据缓存
        HashMap<String, ArrayList<VisitTimeDataBean>> map = new HashMap<>();
        map.put(allTime.substring(0, 10), new ArrayList<VisitTimeDataBean>());
        listAllData.add(map);
        //对数据是否变化做标签
        HashMap<String, ArrayList<VistTimeChang>> mapChanged = new HashMap<>();
        mapChanged.put(allTime.substring(0, 10), new ArrayList<VistTimeChang>());
        listChanged.add(mapChanged);

        for (int i = 0; i < 29; i++) {
            fragments.add(VisitTimeFragment.newInstance(allTime.substring(0, 10), listAllData.get(i).get(allTime.substring(0, 10)), listChanged.get(i).get(allTime.substring(0, 10))));
            View view = LayoutInflater.from(this).inflate(R.layout.custom_tablayout_tv, null);
            TextView tv = (TextView) view.findViewById(R.id.textview_custom);
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    visit_time_viewpager_vp.setCurrentItem(finalI);
                }
            });
            //设置第一天的日期
            tv.setText(time);
            //后一天的所有时间   2016-9-19  12:12
            String afterAllTime = DateUtil.getSpecifiedDayAfter(allTime, "yyyy-MM-dd HH:MM");
            //后一天几月几号     9-19
            String afterTime = DateUtil.getStringFromTime(DateUtil.getTimeFromString(afterAllTime, "yyyy-MM-dd HH:MM"), "MM-dd");
            //后一天星期几   星期五
            String WeekDay = DateUtil.getWeekDay(afterAllTime, "yyyy-MM-dd HH:MM");
            //后一天滚动条显示数据   09-19\n星期五
            time = afterTime + "\n" + WeekDay;

            //保存时间，做fragment数据回传的标签
            dayTags[i + 1] = afterAllTime.substring(0, 10);
            //以time作为key，当天的数据作为value
            HashMap<String, ArrayList<VisitTimeDataBean>> map1 = new HashMap<>();
            map1.put(afterAllTime.substring(0, 10), new ArrayList<VisitTimeDataBean>());
            listAllData.add(map1);
            //对数据是否变化做标签
            HashMap<String, ArrayList<VistTimeChang>> mapChanged1 = new HashMap<>();
            mapChanged1.put(afterAllTime.substring(0, 10), new ArrayList<VistTimeChang>());
            listChanged.add(mapChanged1);

            //保存这天数据，以便计算下一天的数据
            allTime = afterAllTime;
            visit_time_date_ll.addView(view);

        }
        visit_time_viewpager_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectdDate(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setSelectdDate(0);
        adapter.notifyDataSetChanged();
    }

    //更改选中日期的状态
    public void setSelectdDate(int position) {
        for (int i = 0; i < visit_time_date_ll.getChildCount(); i++) {
            if (i != position) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                TextView tv = (TextView) ll.getChildAt(0);
                tv.setTextColor(getResources().getColor(R.color.gray_888888));
                tv.setBackgroundColor(getResources().getColor(R.color.lightblue));
                tv.setFocusable(false);
                tv.setFocusableInTouchMode(false);
                //右滑设置选中项
            } else if (i > lastPosition && i < visit_time_date_ll.getChildCount() - 2) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                LinearLayout ll2 = (LinearLayout) visit_time_date_ll.getChildAt(i + 2);
                TextView tv = (TextView) ll.getChildAt(0);
                TextView tv2 = (TextView) ll2.getChildAt(0);
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
                tv2.setFocusable(true);
                tv2.setFocusableInTouchMode(true);
                tv2.requestFocus();
                //左滑设置选中项
            } else if (i < lastPosition && i > 2) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                LinearLayout ll2 = (LinearLayout) visit_time_date_ll.getChildAt(i - 2);
                TextView tv = (TextView) ll.getChildAt(0);
                TextView tv2 = (TextView) ll2.getChildAt(0);
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
                tv2.setFocusable(true);
                tv2.setFocusableInTouchMode(true);
                tv2.requestFocus();
            } else {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                TextView tv = (TextView) ll.getChildAt(0);
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setBackgroundColor(getResources().getColor(R.color.deepskyblue));
                tv.setFocusable(true);
                tv.setFocusableInTouchMode(true);
                tv.requestFocus();
            }
        }
        //保存上一个位置
        lastPosition = position;
    }

    private void initListener() {
        dayTags = new String[30];
        fragments = new ArrayList<>();
        adapter = new MyViewPagerAdapter<>(getSupportFragmentManager(), fragments);
        visit_time_viewpager_vp.setAdapter(adapter);
        visit_time_viewpager_vp.setPageTransformer(false, new AccordionTransformer());
        callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)) {
                    dataIsChanged = false;
                    ToastUtils.showToast("保存成功。");
                }
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        initVisitChangeFalse();
                    }
                });
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
    }

    @OnClick({R.id.back_iv, R.id.text_ringht})
    public void back(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                if (dataIsChanged) {
                    CustomAlertDialog.dialogExSureCancel("您已对坐诊时间进行操作，是否在未保存的情况下离开界面？", this, new CustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            finish();
                        }
                    });
                } else {
                    finish();
                }
                break;
            case R.id.text_ringht:
                if (dataIsChanged) {
                    StringBuilder builder = new StringBuilder();
                    int num = 0;
                    for (int i = 0; i < listAllData.size(); i++) {
                        for (int j = 0; j < listAllData.get(i).get(dayTags[i]).size(); j++) {
                            if (listChanged.get(i).get(dayTags[i]).get(j).isChanged()) {
                                String bodyStr = getNetBody(i, j,num);
                                num++;
                                bodyStr += "&";
                                builder.append(bodyStr);
                                Log.i("tag00",dayTags[i]+","+(j+8)+":00--"+(j+9)+":00 时间段发生变化，上传中...");
                            }
                        }
                    }
                    if (!StringUtil.isEmpty(builder.toString())){
                        HttpProtocol.visitTimeSetting(callback, builder.toString().substring(0,builder.toString().length() - 1),this);
                        Log.i("tag00","====================================");
                        Log.i("tag00", builder.toString().substring(0,builder.toString().length() - 1));
                    }else {
                        ToastUtils.showToast("坐诊时间未进行任何操作，不需保存。");
                    }
                } else {
                    ToastUtils.showToast("坐诊时间未进行任何操作，不需保存。");
                }
                break;
        }
    }

    /**
     * 连续两次点击返回键，回到桌面
     */
    @Override
    public void onBackPressed() {
        if (dataIsChanged) {
            CustomAlertDialog.dialogExSureCancel("您已对坐诊时间进行操作，是否在未保存的情况下离开界面？", this, new CustomAlertDialog.OnDialogClickListener() {
                @Override
                public void doSomeThings() {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //拼接网络上传数据
    public String getNetBody(int i, int j,int num) {
        String bodyStr = "list[" + num + "]." + "diagnoseNum=" + listAllData.get(i).get(dayTags[i]).get(j).getDiagnoseNum() + "&list[" + num + "]." + "startTime=" + listAllData.get(i).get(dayTags[i]).get(j).getStartTime()
                + "&list[" + num + "]." + "endTime=" + listAllData.get(i).get(dayTags[i]).get(j).getEndTime()
                + "&list[" + num + "]." + "diagnoseDate=" + dayTags[i];
        return bodyStr;
    }

    //重新设置已变标签
    public void initVisitChangeFalse(){
        for (int i = 0; i < listChanged.size(); i++) {
            for (int j = 0; j < listChanged.get(i).get(dayTags[i]).size(); j++) {
                listChanged.get(i).get(dayTags[i]).get(j).setChanged(false);
            }
        }
    }
}
