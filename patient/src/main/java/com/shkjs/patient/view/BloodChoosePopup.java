package com.shkjs.patient.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.raspberry.library.util.BasePopupWindow;
import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.R;

/**
 * Created by xiaohu on 2016/10/18.
 */

public class BloodChoosePopup extends BasePopupWindow {

    private View popupView;

    public BloodChoosePopup(Activity context) {
        super(context);
        setCancelListener();
    }

    public BloodChoosePopup(Activity context, int w, int h) {
        super(context, w, h);
        setCancelListener();
    }

    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.popup_blood_click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_blood, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    /**
     * A型
     *
     * @param listener
     */
    public void setChooseAListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.blood_group_a).setOnClickListener(listener);
    }

    /**
     * B型
     *
     * @param listener
     */
    public void setChooseBListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.blood_group_b).setOnClickListener(listener);
    }

    /**
     * O型
     *
     * @param listener
     */
    public void setChooseOListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.blood_group_o).setOnClickListener(listener);
    }

    /**
     * AB型
     *
     * @param listener
     */
    public void setChooseABListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.blood_group_ab).setOnClickListener(listener);
    }

    /**
     * 取消按钮点击事件
     */
    public void setCancelListener() {
        popupView.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public String getText(TextView view) {
        return TextUtils.getText(view);
    }
}
