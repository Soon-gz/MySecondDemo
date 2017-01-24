package com.shkjs.patient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohu on 2016/9/18.
 * <p/>
 * 引导页
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private Button startBtn;
    private LinearLayout layout;
    private MyViewPageAdapter adapter;
    private MyPageChangeListener listener;
    private List<Integer> datalist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_guide);

        //清除缓存数据
        SharedPreferencesUtils.remove(MyApplication.USER_NAME);
        SharedPreferencesUtils.remove(MyApplication.USER_PWD);
        //引导页只运行一次
        SharedPreferencesUtils.put(MyApplication.IS_FIRST_RUN, false);
        //默认开启新消息提醒
        SharedPreferencesUtils.put(MyApplication.VOICE_SETTING, true);

        queryUserInfo = false;

        findView();
        initData();
        initListener();
    }

    private void findView() {
        viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        layout = (LinearLayout) findViewById(R.id.guide_ll);
        startBtn = (Button) findViewById(R.id.guide_start_btn);
    }

    private void initData() {
        datalist = new ArrayList<>();
        datalist.add(R.drawable.guidepage_1);
        datalist.add(R.drawable.guidepage_2);
        datalist.add(R.drawable.guidepage_3);
        adapter = new MyViewPageAdapter(this, datalist);
        listener = new MyPageChangeListener();
    }

    private void initListener() {
        startBtn.setOnClickListener(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setCurrentItem(0);
        setBackground(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_start_btn:
                startLoginActivity();
                break;
            default:
                break;
        }
    }

    private class MyViewPageAdapter extends PagerAdapter {

        private Context context;
        private List<Integer> datalist;

        public MyViewPageAdapter(Context context, List<Integer> datalist) {
            this.context = context;
            this.datalist = datalist;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            RelativeLayout relativeLayout = (RelativeLayout) container.findViewWithTag(datalist.get(position));
            if (null == relativeLayout) {
                relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.guide_item, null);
                relativeLayout.setTag(datalist.get(position));
            }
            ImageView view = (ImageView) relativeLayout.findViewById(R.id.guide_item_iv);
            container.addView(relativeLayout, 0);

            Glide.with(context).load(datalist.get(position)).centerCrop().into(view);

            return relativeLayout;
        }

        @Override
        public int getCount() {
            return datalist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == datalist.size() - 1) {
                startBtn.setVisibility(View.VISIBLE);
            } else {
                startBtn.setVisibility(View.GONE);
            }
            setBackground(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void setBackground(int position) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (position == i) {
                layout.getChildAt(i).setSelected(true);
            } else {
                layout.getChildAt(i).setSelected(false);
            }
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
