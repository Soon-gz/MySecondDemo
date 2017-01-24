package com.raspberry.library.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.raspberry.library.R;
import com.raspberry.library.util.BasePopupWindow;

/**
 * Created by xiaohu on 2016/9/28.
 */

public class PhotoPopupWindow extends BasePopupWindow {

    private View popupView;

    public PhotoPopupWindow(Activity context) {
        super(context);
        //默认取消事件
        setCancelListener();
    }

    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.popup_photo_click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_photo, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    /**
     * 给view设置点击事件监听
     *
     * @param id       view的id
     * @param listener 点击事件监听
     */
    public void setOnClickListener(int id, View.OnClickListener listener) {
        popupView.findViewById(id).setOnClickListener(listener);
    }

    /**
     * 选择图片事件监听
     *
     * @param listener 点击事件监听
     */
    public void setChooseListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.choose_tv).setOnClickListener(listener);
    }

    /**
     * 拍照事件监听
     *
     * @param listener 点击事件监听
     */
    public void setPhotographListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.photograph_tv).setOnClickListener(listener);
    }

    public void setCancelListener() {
        popupView.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
