package com.raspberry.library.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.raspberry.library.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaohu on 2016/11/25.
 */

public class ViewPagerUtils {
    // 图片集合
    private ArrayList<String> strList;
    // 图片view
    private ArrayList<View> listViews;
    // 点点数组
    private ImageView[] tips;
    // 上下文
    private Context context;
    // 盛放点点的容器
    private ViewGroup group;
    // 当前viewpager
    private ViewPager viewPage;
    // 适配器
    private MyAdapter adapter;
    // view点击回调
    private OnClickCallback onClickCallback;
    // view滑动回调
    private OnPageChangeCallback onPageChangCallback;
    // timer
    public Timer timer;
    // 当前下标
    private int currentIndex;// 初始下标
    // 异步类
    public SynTimerTask synTimerTask;

    /**
     * viewpager工具类构造
     *
     * @param context  上下文
     * @param viewPage 当前页面的viewpager
     * @param group    底部点点
     * @param falg     是否自动滚动
     */
    public ViewPagerUtils(Context context, ViewPager viewPage, ViewGroup group, boolean falg) {
        this.context = context;
        this.viewPage = viewPage;
        this.group = group;
        if (falg)
            timer = new Timer(); // 定义计时器
    }

    /**
     * 设置viewpager数据
     *
     * @param strList 数据集合
     */
    public void setData(ArrayList<String> strList) {
        this.strList = strList;
    }

    /**
     * 创建滚动视图
     *
     * @param falg 是否自动滚动
     */
    public void creatView(boolean falg) {
        listViews = new ArrayList<View>();
        for (int i = 0; i < strList.size(); i++) {
            ImageView view = new ImageView(context);
            view.setBackgroundColor(0xff000000);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT));
            listViews.add(view);
        }
        if (tips != null)
            tips = null;
        if (group != null)
            group.removeAllViews();
        tips = new ImageView[strList.size()];
        if (strList.size() > 1) {
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
                tips[i] = imageView;
                tips[i].setBackgroundResource(R.drawable.dot_selector);
                if (i == 0) {
                    tips[i].setSelected(true);
                } else {
                    tips[i].setSelected(false);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 5;
                layoutParams.rightMargin = 5;
                layoutParams.bottomMargin = 36;
                group.addView(tips[i], layoutParams);
            }
        }
        loadimage(falg);
    }

    /**
     * 渲染viewpager adapteer
     *
     * @param falg 是否自动滚动
     */
    private void loadimage(boolean falg) {
        adapter = new MyAdapter(listViews);
        viewPage.setAdapter(adapter);
        viewPage.setPageMargin(0);
        viewPage.setCurrentItem(currentIndex);
        viewPage.setOnPageChangeListener(new PageChangeListener());
        if (falg)
            timer();
    }

    /**
     * 适配器Adapter
     */
    private class MyAdapter extends PagerAdapter {
        private ArrayList<View> listViews;
        private int size;

        public MyAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            viewPage.removeView(listViews.get(position % size));
        }

        @Override
        public Object instantiateItem(ViewGroup viewPger, final int position) {
            ImageView view = (ImageView) listViews.get(position);
            Glide.with(context).load(strList.get(position)).centerCrop().into(view);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewPage.addView(view, 0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getOnClickCallback() != null)
                        getOnClickCallback().onClick(position);
                }
            });
            return view;
        }
    }

    /**
     * 滑动监听
     */
    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;
            setImageBackground(position);
            // 因为下标从1开始，所以要更新页面下标记得加1
            if (getOnPageChangCallback() != null)
                getOnPageChangCallback().onPageChang(currentIndex);
        }
    }

    /**
     * 当前视图下标
     *
     * @param selectItems index下标
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setSelected(true);
            } else {
                tips[i].setSelected(false);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (currentIndex < listViews.size()) {
                currentIndex++;
                viewPage.setCurrentItem(currentIndex);
            } else {
                currentIndex = 0;
                viewPage.setCurrentItem(0);
            }
        }
    };

    /**
     * 滚动计时器
     */
    private void timer() {
        // 定义计划任务，根据参数的不同可以完成以下种类的工作：在固定时间执行某任务，在固定时间开始重复执行某任务，重复时间间隔可控，在延迟多久后执行某任务，在延迟多久后重复执行某任务，重复时间间隔可控
        synTimerTask = new SynTimerTask();
        timer.schedule(synTimerTask, 3000, 5000);
    }

    public class SynTimerTask extends TimerTask {
        @Override
        public void run() {
            // Log.d("post","homeview:"+Thread.currentThread().getName());
            Message msg = new Message();
            handler.sendMessage(msg);
        }
    }

    ;

    /**
     * 视图点击事件回调
     */
    public interface OnClickCallback {
        // 返回当前下标
        public void onClick(int position);
    }

    public OnClickCallback getOnClickCallback() {
        return onClickCallback;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    /**
     * 滑动事件回调监听
     */
    public interface OnPageChangeCallback {
        // 返回当前下标
        public void onPageChang(int position);
    }

    public OnPageChangeCallback getOnPageChangCallback() {
        return onPageChangCallback;
    }

    public void setOnPageChangCallback(OnPageChangeCallback onPageChangCallback) {
        this.onPageChangCallback = onPageChangCallback;
    }
}
