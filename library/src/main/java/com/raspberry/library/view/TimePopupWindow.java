package com.raspberry.library.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import com.raspberry.library.R;
import com.raspberry.library.util.BasePopupWindow;
import com.raspberry.library.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohu on 2016/10/18.
 */

public class TimePopupWindow extends BasePopupWindow {

    private View popupView;
    private CycleWheelView year;
    private CycleWheelView month;
    private CycleWheelView day;

    private List<String> years;
    private List<String> months;
    private List<String> days;

    private boolean isLeapYear = false;

    public TimePopupWindow(Activity context) {
        super(context);
        init();
        setCancelListener();
    }

    public TimePopupWindow(Activity context, int w, int h) {
        super(context, w, h);
        init();
        setCancelListener();
    }

    @Override
    protected Animation getShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.popup_time_click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_time, null);
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return null;
    }

    /**
     * 初始化时间选择界面
     */
    private void init() {
        year = (CycleWheelView) popupView.findViewById(R.id.year);
        month = (CycleWheelView) popupView.findViewById(R.id.month);
        day = (CycleWheelView) popupView.findViewById(R.id.day);

        years = new ArrayList<>();
        months = new ArrayList<>();
        days = new ArrayList<>();

        try {
            initYears(1910);
            initMonths();

            year.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, String label) {
                    isLeapYear = isLeapYear(Integer.parseInt(label));
                    initDaySize(month.getSelection() + 1);
                }
            });

            month.setOnWheelItemSelectedListener(new CycleWheelView.WheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, String label) {
                    int number = Integer.parseInt(label);
                    initDaySize(number);
                }
            });

        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化年份
     *
     * @param minYear
     * @throws CycleWheelView.CycleWheelViewException
     */
    private void initYears(int minYear) throws CycleWheelView.CycleWheelViewException {
        years.clear();
        int maxYear = Integer.parseInt(TimeFormatUtils.getLocalTime("yyyy", System.currentTimeMillis()));
        for (int i = minYear; i <= maxYear; i++) {
            years.add("" + i);
        }

        year.setLabels(years);
        year.setWheelSize(3);
        //是否循环
        year.setCycleEnable(true);
        //分割线
        year.setDivider(Color.parseColor("#2BBBE6"), 2);
        //选中文字颜色
        year.setLabelSelectColor(Color.parseColor("#000000"));
        //选中块颜色
        year.setSolid(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        year.setSelection(years.size() - 1);
    }

    /**
     * 初始化月份
     *
     * @throws CycleWheelView.CycleWheelViewException
     */
    private void initMonths() throws CycleWheelView.CycleWheelViewException {
        months.clear();
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                months.add("0" + i);
            } else {
                months.add("" + i);
            }
        }
        month.setLabels(months);
        month.setWheelSize(3);
        //是否循环
        month.setCycleEnable(true);
        //分割线
        month.setDivider(Color.parseColor("#2BBBE6"), 2);
        //选中文字颜色
        month.setLabelSelectColor(Color.parseColor("#000000"));
        //选中块颜色
        month.setSolid(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        month.setSelection(0);
    }

    /**
     * 初始化天数
     *
     * @param maxDay
     */
    private void initDays(int maxDay) throws CycleWheelView.CycleWheelViewException {
        days.clear();
        for (int i = 1; i <= maxDay; i++) {
            if (i < 10) {
                days.add("0" + i);
            } else {
                days.add("" + i);
            }
        }
        day.setLabels(days);
        day.setWheelSize(3);
        //是否循环
        day.setCycleEnable(true);
        //分割线
        day.setDivider(Color.parseColor("#2BBBE6"), 2);
        //选中文字颜色
        day.setLabelSelectColor(Color.parseColor("#000000"));
        //选中块颜色
        day.setSolid(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        day.setSelection(0);
    }

    /**
     * 初始化天数
     *
     * @param number
     */
    private void initDaySize(int number) {
        try {
            switch (number) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    initDays(31);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    initDays(30);
                    break;
                case 2:
                    if (isLeapYear) {
                        initDays(29);
                    } else {
                        initDays(28);
                    }
                    break;
                default:
                    break;
            }
        } catch (CycleWheelView.CycleWheelViewException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否为闰年
     *
     * @param year
     * @return
     */
    private boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            return true;
        }
        return false;
    }

    /**
     * 确定按钮点击时间
     *
     * @param listener
     */
    public void setSureListener(View.OnClickListener listener) {
        popupView.findViewById(R.id.sure_btn).setOnClickListener(listener);
    }

    /**
     * 取消按钮点击事件
     */
    public void setCancelListener() {
        popupView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public String getTime() {
        return year.getSelectLabel() + "-" + month.getSelectLabel() + "-" + day.getSelectLabel();
    }
}
