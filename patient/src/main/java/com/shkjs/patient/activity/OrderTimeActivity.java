package com.shkjs.patient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.AccordionTransformer;
import com.shkjs.patient.R;
import com.shkjs.patient.adapter.MyViewPagerAdapter;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.fragment.VisitTimeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/19.
 * <p>
 * 预约时间界面
 */

public class OrderTimeActivity extends BaseActivity {

    @Bind(R.id.visit_time_viewpager_vp)
    ViewPager visit_time_viewpager_vp;      //viewpager切换
    @Bind(R.id.visit_time_date_ll)
    LinearLayout visit_time_date_ll;        //日期选择

    private Toolbar toolbar;

    private List<VisitTimeFragment> fragments;
    private MyViewPagerAdapter<VisitTimeFragment> adapter;
    private int lastPosition = -1;
    private String[] dayTags;

    private Doctor doctor;

    public static void start(Context context, Doctor doctor) {
        Intent intent = new Intent(context, OrderTimeActivity.class);
        intent.putExtra(OrderTimeActivity.class.getSimpleName(), doctor);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_time);

        doctor = (Doctor) getIntent().getSerializableExtra(OrderTimeActivity.class.getSimpleName());

        if (null == doctor) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.order_time);

        //初始化
        initListener();
        //初始化数据
        initData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }


    private void initData() {
        //        EventBus.getDefault().register(this);

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

        for (int i = 0; i < 29; i++) {
            fragments.add(VisitTimeFragment.newInstance(doctor, allTime));
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
            String afterTime = DateUtil.getStringFromTime(DateUtil.getTimeFromString(afterAllTime, "yyyy-MM-dd " +
                    "HH:MM"), "MM-dd");
            //后一天星期几   星期五
            String WeekDay = DateUtil.getWeekDay(afterAllTime, "yyyy-MM-dd HH:MM");
            //后一天滚动条显示数据   09-19\n星期五
            time = afterTime + "\n" + WeekDay;

            //保存时间，做fragment数据回传的标签
            dayTags[i + 1] = afterAllTime.substring(0, 10);

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
        visit_time_viewpager_vp.setCurrentItem(0);
        setSelectdDate(0);
        adapter.notifyDataSetChanged();
    }

    //更改选中日期的状态
    public void setSelectdDate(int position) {
        for (int i = 0; i < visit_time_date_ll.getChildCount(); i++) {
            if (i != position) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                TextView tv = (TextView) ll.getChildAt(0);
                tv.setTextColor(ContextCompat.getColor(this, R.color.gray_888888));
                tv.setBackgroundColor(ContextCompat.getColor(this, R.color.lightblue));
                tv.setFocusable(false);
                tv.setFocusableInTouchMode(false);
                //右滑设置选中项
            } else if (i > lastPosition && i < visit_time_date_ll.getChildCount() - 2) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                LinearLayout ll2 = (LinearLayout) visit_time_date_ll.getChildAt(i + 2);
                TextView tv = (TextView) ll.getChildAt(0);
                TextView tv2 = (TextView) ll2.getChildAt(0);
                tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv.setBackgroundColor(ContextCompat.getColor(this, R.color.deepskyblue));
                tv2.setFocusable(true);
                tv2.setFocusableInTouchMode(true);
                tv2.requestFocus();
                //左滑设置选中项
            } else if (i < lastPosition && i > 2) {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                LinearLayout ll2 = (LinearLayout) visit_time_date_ll.getChildAt(i - 2);
                TextView tv = (TextView) ll.getChildAt(0);
                TextView tv2 = (TextView) ll2.getChildAt(0);
                tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv.setBackgroundColor(ContextCompat.getColor(this, R.color.deepskyblue));
                tv2.setFocusable(true);
                tv2.setFocusableInTouchMode(true);
                tv2.requestFocus();
            } else {
                LinearLayout ll = (LinearLayout) visit_time_date_ll.getChildAt(i);
                TextView tv = (TextView) ll.getChildAt(0);
                tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv.setBackgroundColor(ContextCompat.getColor(this, R.color.deepskyblue));
                tv.setFocusable(true);
                tv.setFocusableInTouchMode(true);
                tv.requestFocus();
            }
        }
        //保存上一个位置
        lastPosition = position;
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order:
                        startActivity(new Intent(OrderTimeActivity.this, OrderNoticeActivity.class));
                        return true;
                }
                return false;
            }
        });

        dayTags = new String[30];
        fragments = new ArrayList<>();
        adapter = new MyViewPagerAdapter<>(getSupportFragmentManager(), fragments);
        visit_time_viewpager_vp.setAdapter(adapter);
        visit_time_viewpager_vp.setPageTransformer(false, new AccordionTransformer());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        EventBus.getDefault().unregister(this);
    }

}
