package com.raspberry.library.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ImageViewPagerAdapter extends PagerAdapter {

    private List<ImageView> imageViews;
    private int size;
    private Context context;

    public ImageViewPagerAdapter(Context context, List<ImageView> imageViews) {
        this.context = context;
        this.imageViews = imageViews;
        size = imageViews.size();
    }

    @Override
    public int getCount() {
        return imageViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //		((ViewPager) container).removeView((View) object);// 完全溢出view,避免数据多时出现重复现象
        container.removeView(imageViews.get(position));//删除页卡
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imageViews.get(position), 0);
        return imageViews.get(position);
    }

}
