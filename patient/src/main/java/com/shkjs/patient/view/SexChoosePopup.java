package com.shkjs.patient.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.raspberry.library.util.BasePopupWindow;
import com.shkjs.patient.R;

/**
 * Created by xiaohu on 2016/10/17.
 */

public class SexChoosePopup extends BasePopupWindow {

    private View popupView;

    public SexChoosePopup(Activity context) {
        super(context);
        //默认取消事件
        setCancelListener();
    }

    public SexChoosePopup(Activity context, int w, int h) {
        super(context, w, h);
        //默认取消事件
        setCancelListener();
    }

    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.popup_sex_choose_click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_sex_choose, null);
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
     * 事件监听
     *
     * @param listener 点击事件监听
     */
    public void chooseManListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.sex_man_tv).setOnClickListener(listener);
    }

    /**
     * 事件监听
     *
     * @param listener 点击事件监听
     */
    public void chooseWomanListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.sex_woman_tv).setOnClickListener(listener);
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
