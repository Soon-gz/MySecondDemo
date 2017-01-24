package com.shkjs.doctor.util;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.raspberry.library.util.BasePopupWindow;
import com.shkjs.doctor.R;

/**
 * Created by xiaohu on 2016/10/13.
 */

public class HealthPopup extends BasePopupWindow {

    private View popupView;

    public HealthPopup(Activity context) {
        super(context);
    }

    public HealthPopup(Activity context, int w, int h) {
        super(context, w, h);
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
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_health, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    /**
     * 通过扫描二维码添加
     * @param listener 点击事件监听
     */
    public void addFriendsByScan(View.OnClickListener listener) {
        popupView.findViewById(R.id.qrscan_add).setOnClickListener(listener);
    }

    /**
     *通过手机号添加医生好友
     * @param listener 点击事件监听
     */
    public void addFriendsByPhone(View.OnClickListener listener) {
        popupView.findViewById(R.id.phone_add).setOnClickListener(listener);
    }

    @Override
    public void showPopupWindow(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPopupWindow.showAsDropDown(v, 0, 0, Gravity.TOP | Gravity.RIGHT);
        } else {
            super.showPopupWindow(v);
        }
    }


}
