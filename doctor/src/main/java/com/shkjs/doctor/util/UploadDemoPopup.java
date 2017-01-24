package com.shkjs.doctor.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.raspberry.library.util.BasePopupWindow;
import com.shkjs.doctor.R;


public class UploadDemoPopup extends BasePopupWindow implements View.OnClickListener {

    private View popupView;

    public UploadDemoPopup(Activity context) {
        super(context);
    }


    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }


    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.popup_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_upload_demo, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_uplaod_anima);
    }


    @Override
    public void onClick(View v) {
    }
}
