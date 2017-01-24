package com.shkjs.doctor.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.fragment.HistoryOrderFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryOrderActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;           //标题
    @Bind(R.id.history_order_finished_tv)
    TextView history_order_finished_tv; //已完成历史订单
    @Bind(R.id.history_order_unfinished_tv)
    TextView history_order_unfinished_tv; //未完成历史订单
    @Bind(R.id.history_order_viewpager)
    ViewPager history_order_viewpager;
    @Bind(R.id.history_order_right_line)
    View history_order_right_line;
    @Bind(R.id.history_order_left_line)
    View history_order_left_line;


    private List<HistoryOrderFragment>fragments;
    private HistoryOrderVPAdapter historyOrderVPAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);
        //注解
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("历史订单");
        //设置监听事件
        initListener();
    }

    private void initListener() {
        final Drawable drawable = getResources().getDrawable(R.drawable.scan_line);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        fragments = new ArrayList<>();
        fragments.add(createFragment("0"));
        fragments.add(createFragment("1"));
        historyOrderVPAdapter = new HistoryOrderVPAdapter(getSupportFragmentManager());
        history_order_viewpager.setAdapter(historyOrderVPAdapter);
        history_order_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        history_order_unfinished_tv.setTextColor(getResources().getColor(R.color.red));
                        history_order_finished_tv.setTextColor(getResources().getColor(R.color.black));
                        history_order_right_line.setVisibility(View.GONE);
                        history_order_left_line.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        history_order_unfinished_tv.setTextColor(getResources().getColor(R.color.black));
                        history_order_finished_tv.setTextColor(getResources().getColor(R.color.red));
                        history_order_right_line.setVisibility(View.VISIBLE);
                        history_order_left_line.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private HistoryOrderFragment createFragment(String s) {
        HistoryOrderFragment fragment = new HistoryOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("finish",s);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.back_iv,R.id.history_order_finished_tv,R.id.history_order_unfinished_tv})
    public void historyOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.history_order_finished_tv:
                history_order_viewpager.setCurrentItem(1);
                break;
            case R.id.history_order_unfinished_tv:
                history_order_viewpager.setCurrentItem(0);
                break;
        }
    }

    private class HistoryOrderVPAdapter extends FragmentStatePagerAdapter{

        public HistoryOrderVPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
