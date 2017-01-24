package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shkjs.patient.R;
import com.shkjs.patient.adapter.MyViewPagerAdapter;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.fragment.OrderFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p>
 * 我的订单
 */
public class MineOrderActivity extends BaseActivity {

    @Bind(R.id.mine_order_tablayout)
    TabLayout tabLayout;
    @Bind(R.id.mine_order_viewpager)
    ViewPager viewPager;

    private Toolbar toolbar;

    private List<String> titles;
    private List<OrderFragment> datalist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mine_order);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_order_text);

        initData();
        initListener();

    }

    private void initData() {

        titles = new ArrayList<>();

        //tablayout 和 viewpager关联后回移除先前设置的view，这里保存title
        titles.add(getString(R.string.all_order));
        titles.add(getString(R.string.wait_pay_order));
        titles.add(getString(R.string.no_complete_order));
        titles.add(getString(R.string.complete_order));
        titles.add(getString(R.string.refund_order));

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        datalist = new ArrayList<>();

        OrderFragment fragment = OrderFragment.getInstance(OrderStatus.ALL);
        datalist.add(fragment);
        fragment = OrderFragment.getInstance(OrderStatus.INITIAL);
        datalist.add(fragment);
        fragment = OrderFragment.getInstance(OrderStatus.PAID);
        datalist.add(fragment);
        fragment = OrderFragment.getInstance(OrderStatus.COMPLETE);
        datalist.add(fragment);
        fragment = OrderFragment.getInstance(OrderStatus.CANCEL);
        datalist.add(fragment);

    }

    private void initListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), datalist));
        tabLayout.setupWithViewPager(viewPager);//有bug，回清除tablayout的view（title）
        viewPager.setOffscreenPageLimit(5);//五种状态均保存
        viewPager.setCurrentItem(1, false);//默认选中未付款

    }

    protected class TabAdapter extends MyViewPagerAdapter {

        public TabAdapter(FragmentManager fm, List list) {
            super(fm, list);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
