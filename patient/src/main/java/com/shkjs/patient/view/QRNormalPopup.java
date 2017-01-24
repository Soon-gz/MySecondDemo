package com.shkjs.patient.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.util.BasePopupWindow;
import com.shkjs.patient.R;


public class QRNormalPopup extends BasePopupWindow implements View.OnClickListener {

    private View popupView;

    public QRNormalPopup(Activity context) {
        super(context);
        bindEvent();
    }


    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }


    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.main_content_click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_normal, null);
        return popupView;
    }

    public ImageView getImageView() {
        ImageView img = (ImageView) popupView.findViewById(R.id.qrcode);
        return img;
    }

    public TextView getTextView() {
        return (TextView) popupView.findViewById(R.id.qrcode_hint);
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
    }

    @Override
    public void onClick(View v) {

    }
}
