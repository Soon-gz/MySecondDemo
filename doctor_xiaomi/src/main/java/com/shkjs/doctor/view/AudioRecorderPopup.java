package com.shkjs.doctor.view;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.BasePopupWindow;
import com.shkjs.doctor.R;

/**
 * Created by xiaohu on 2016/10/14.
 */

public class AudioRecorderPopup extends BasePopupWindow {

    public static final int INITIAL = 121;//初始状态
    public static final int RECORDING = 122;//录音中
    public static final int COMPLETE = 123;//录音完成（结束）
    public static final int CANCEL = 124;//录音取消
    public static final int SURE = 125;//确定使用录音

    private int status;//录音状态

    private View popupView;
    private Context context;

    public AudioRecorderPopup(Activity context) {
        super(context);
        this.context = context;
        init();
    }

    public AudioRecorderPopup(Activity context, int w, int h) {
        super(context, w, h);
        this.context = context;
        init();
    }

    private void init() {
        if (null != mDismissView) {
            mDismissView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    setStatus(CANCEL);
                }
            });
        }
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
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_audio_recorder, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        setStatus(INITIAL);
    }

    @Override
    public void showPopupWindow(View v) {
        super.showPopupWindow(v);
        setStatus(INITIAL);
    }

    @Override
    public void showPopupWindow(int res) {
        super.showPopupWindow(res);
        setStatus(INITIAL);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        getTime().setBase(SystemClock.elapsedRealtime());
    }

    /**
     * 得到事件显示控件
     *
     * @return
     */
    public Chronometer getTime() {
        return (Chronometer) popupView.findViewById(R.id.time_tv);
    }

    /**
     * 设置确定按钮点击事件
     *
     * @param listener
     */
    public void setSurelListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.sure_btn).setOnClickListener(listener);
    }

    /**
     * 设置取消按钮点击事件
     *
     * @param listener
     */
    public void setCancelListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.cancel_btn).setOnClickListener(listener);
    }

    /**
     * 设置录音点击事件
     *
     * @param listener
     */
    public void setRecordListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.image_iv).setOnClickListener(listener);
    }

    /**
     * 得到录音状态
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置录音状态，根据不同状态展示不同界面
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
        if (null != popupView) {
            switch (status) {
                case INITIAL:
                    Glide.with(context).load(R.drawable.main_healthreport_upload_btn_startrecording).into((ImageView)
                            popupView.findViewById(R.id.image_iv));
                    popupView.findViewById(R.id.button_ll).setVisibility(View.INVISIBLE);
                    popupView.findViewById(R.id.title_tv).setVisibility(View.VISIBLE);
                    popupView.findViewById(R.id.time_tv).setVisibility(View.GONE);
                    break;
                case RECORDING:
                    Glide.with(context).load(R.drawable.main_healthreport_upload_btn_inrecording).into((ImageView)
                            popupView.findViewById(R.id.image_iv));
                    popupView.findViewById(R.id.button_ll).setVisibility(View.INVISIBLE);
                    popupView.findViewById(R.id.title_tv).setVisibility(View.GONE);
                    popupView.findViewById(R.id.time_tv).setVisibility(View.VISIBLE);
                    break;
                case COMPLETE:
                    Glide.with(context).load(R.drawable.main_healthreport_upload_btn_endrecording).into((ImageView)
                            popupView.findViewById(R.id.image_iv));
                    popupView.findViewById(R.id.button_ll).setVisibility(View.VISIBLE);
                    popupView.findViewById(R.id.title_tv).setVisibility(View.GONE);
                    popupView.findViewById(R.id.time_tv).setVisibility(View.VISIBLE);
                    break;
                case CANCEL:
                    dismiss();
                    break;
                case SURE:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
