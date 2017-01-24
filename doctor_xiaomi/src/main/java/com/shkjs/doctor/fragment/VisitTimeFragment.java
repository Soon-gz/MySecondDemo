package com.shkjs.doctor.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.bean.EventBusVisitTime;
import com.shkjs.doctor.bean.VisitTimeDataBean;
import com.shkjs.doctor.bean.VistTimeChang;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListPageResponse;
import com.shkjs.doctor.util.SharedPrefenceVisit;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class VisitTimeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    //判断选项是否点开
    private boolean morningIsOpened = false;
    private boolean afternoonIsOpened = false;
    private boolean eveningIsOpened = false;
    //区分一天的时间段
    private static final int MORNING = 100;
    private static final int NOON = 200;
    private static final int EVENING = 300;
    private static final int ADD = 0;
    private static final int SUB = 1;
    //用于判断获得bean是属于哪个时间段的
    private static final int TIME_8 = 8;
    private static final int TIME_9 = 9;
    private static final int TIME_10 = 10;
    private static final int TIME_11 = 11;
    private static final int TIME_12 = 12;
    private static final int TIME_13 = 13;
    private static final int TIME_14 = 14;
    private static final int TIME_15 = 15;
    private static final int TIME_16 = 16;
    private static final int TIME_17 = 17;
    private static final int TIME_18 = 18;
    private static final int TIME_19 = 19;
    private static final int TIME_20 = 20;
    private static final int TIME_21 = 21;
    private static final int TIME_22 = 22;
    private static final int TIME_23 = 23;
    //用于统计上午，下午，晚上可约已约的人数
    private int MORNING_DIAGNOSENUM = 0;
    private int MORNING_DIAGNOSE_SUBCRIBEDNUM = 0;
    private int NOON_DIAGNOSENUM = 0;
    private int NOON_DIAGNOSE_SUBCRIBEDNUM = 0;
    private int EVENING_DIAGNOSENUM = 0;
    private int EVENING_DIAGNOSE_SUBCRIBEDNUM = 0;

    //创建Fragment携带的参数
    private String mParam1;
    private ArrayList<VistTimeChang> mParam2;
    private ArrayList<VisitTimeDataBean> mParam3;

    //设置统计各个时间段可约人数
    @Bind(R.id.visit_morning_time_8_12)
    LinearLayout visit_morning_time_8_12;
    @Bind(R.id.visit_afternoon_time_12_18)
    LinearLayout visit_afternoon_time_12_18;
    @Bind(R.id.visit_evening_time_18_24)
    LinearLayout visit_evening_time_18_24;
    //可约已约数量显示
    @Bind(R.id.visit_timefg_an_cancel_tv)
    TextView morning_canVisitNumber;
    @Bind(R.id.visit_timefg_an_sure_tv)
    TextView morning_hasVisitedNumber;
    @Bind(R.id.visit_timefg_mn_cancel_tv)
    TextView aftternoon_canVisitNumber;
    @Bind(R.id.visit_timefg_mn_sure_tv)
    TextView afternoon_hasVisitedNumber;
    @Bind(R.id.visit_timefg_en_cancel_tv)
    TextView evening_canVisitNumber;
    @Bind(R.id.visit_timefg_en_sure_tv)
    TextView evening_hasVisitedNumber;

    //每个时间段显示的可约人数
    @Bind(R.id.visit_morning_time_numbertv_8)
    TextView visit_morning_time_numbertv_8;
    @Bind(R.id.visit_morning_time_numbertv_9)
    TextView visit_morning_time_numbertv_9;
    @Bind(R.id.visit_morning_time_numbertv_10)
    TextView visit_morning_time_numbertv_10;
    @Bind(R.id.visit_morning_time_numbertv_11)
    TextView visit_morning_time_numbertv_11;
    @Bind(R.id.visit_afternoon_time_numbertv_12)
    TextView visit_afternoon_time_numbertv_12;
    @Bind(R.id.visit_afternoon_time_numbertv_13)
    TextView visit_afternoon_time_numbertv_13;
    @Bind(R.id.visit_afternoon_time_numbertv_14)
    TextView visit_afternoon_time_numbertv_14;
    @Bind(R.id.visit_afternoon_time_numbertv_15)
    TextView visit_afternoon_time_numbertv_15;
    @Bind(R.id.visit_afternoon_time_numbertv_16)
    TextView visit_afternoon_time_numbertv_16;
    @Bind(R.id.visit_afternoon_time_numbertv_17)
    TextView visit_afternoon_time_numbertv_17;
    @Bind(R.id.visit_evening_time_numbertv_18)
    TextView visit_evening_time_numbertv_18;
    @Bind(R.id.visit_evening_time_numbertv_19)
    TextView visit_evening_time_numbertv_19;
    @Bind(R.id.visit_evening_time_numbertv_20)
    TextView visit_evening_time_numbertv_20;
    @Bind(R.id.visit_evening_time_numbertv_21)
    TextView visit_evening_time_numbertv_21;
    @Bind(R.id.visit_evening_time_numbertv_22)
    TextView visit_evening_time_numbertv_22;
    @Bind(R.id.visit_evening_time_numbertv_23)
    TextView visit_evening_time_numbertv_23;
    @Bind(R.id.message_fragment_srl)
    SwipeRefreshLayout message_fragment_srl;
    @Bind(R.id.visit_time_fg_daorumb_tv)
    TextView visit_time_fg_daorumb_tv;
    @Bind(R.id.visit_time_fg_scrollview)
    ScrollView visit_time_fg_scrollview;
    @Bind(R.id.visit_time_fg_mr_iv)
    ImageView visit_time_fg_mr_iv;
    @Bind(R.id.visit_time_fg_ar_iv)
    ImageView visit_time_fg_ar_iv;
    @Bind(R.id.visit_time_fg_er_iv)
    ImageView visit_time_fg_er_iv;


    //所有加号和减号和显示人数的id
    private int[] addIds = {R.id.visit_morning_time_addiv_8, R.id.visit_morning_time_addiv_9, R.id.visit_morning_time_addiv_10
            , R.id.visit_morning_time_addiv_11, R.id.visit_afternoon_time_addiv_12, R.id.visit_afternoon_time_addiv_13
            , R.id.visit_afternoon_time_addiv_14, R.id.visit_afternoon_time_addiv_15, R.id.visit_afternoon_time_addiv_16
            , R.id.visit_afternoon_time_addiv_17, R.id.visit_evening_time_addiv_18, R.id.visit_evening_time_addiv_19
            , R.id.visit_evening_time_addiv_20, R.id.visit_evening_time_addiv_21, R.id.visit_evening_time_addiv_22, R.id.visit_evening_time_addiv_23};
    private int[] subIds = {R.id.visit_morning_time_subtv_8, R.id.visit_morning_time_subtv_9, R.id.visit_morning_time_subtv_10, R.id.visit_morning_time_subtv_11
            , R.id.visit_afternoon_time_subtv_12, R.id.visit_afternoon_time_subtv_13, R.id.visit_afternoon_time_subtv_14, R.id.visit_afternoon_time_subtv_15
            , R.id.visit_afternoon_time_subtv_16, R.id.visit_afternoon_time_subtv_17, R.id.visit_evening_time_subtv_18, R.id.visit_evening_time_subtv_19
            , R.id.visit_evening_time_subtv_20, R.id.visit_evening_time_subtv_21, R.id.visit_evening_time_subtv_22, R.id.visit_evening_time_subtv_23};
    private int[] textIds = {R.id.visit_morning_time_numbertv_8, R.id.visit_morning_time_numbertv_9, R.id.visit_morning_time_numbertv_10, R.id.visit_morning_time_numbertv_11
            , R.id.visit_afternoon_time_numbertv_12, R.id.visit_afternoon_time_numbertv_13, R.id.visit_afternoon_time_numbertv_14, R.id.visit_afternoon_time_numbertv_15
            , R.id.visit_afternoon_time_numbertv_16, R.id.visit_afternoon_time_numbertv_17, R.id.visit_evening_time_numbertv_18, R.id.visit_evening_time_numbertv_19
            , R.id.visit_evening_time_numbertv_20, R.id.visit_evening_time_numbertv_21, R.id.visit_evening_time_numbertv_22, R.id.visit_evening_time_numbertv_23};

    private RaspberryCallback<ListPageResponse<VisitTimeDataBean>> callback;

    /**
     * 坐诊时间日期管理生成的对象过多，使用工厂模式创建Fragemnt,携带今天数据对象
     *
     * @param param1
     * @param param3
     * @param param2
     * @return
     */
    public static VisitTimeFragment newInstance(String param1, ArrayList<VisitTimeDataBean> param3, ArrayList<VistTimeChang> param2) {
        VisitTimeFragment fragment = new VisitTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        args.putParcelableArrayList(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = (ArrayList<VistTimeChang>) getArguments().getSerializable(ARG_PARAM2);
            mParam3 = (ArrayList<VisitTimeDataBean>) getArguments().getSerializable(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_time, container, false);
        //注解
        ButterKnife.bind(this, view);
        //设置数据
        initData(view);
        //加载网络数据
        loadNetData();
        initScrollView();
        return view;
    }

    private void initScrollView() {
        visit_time_fg_scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (message_fragment_srl != null) {
                    message_fragment_srl.setEnabled(visit_time_fg_scrollview.getScrollY() == 0);
                }
            }
        });
    }

    public void runMainThread() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new TimerTask() {
                @Override
                public void run() {
                    if (message_fragment_srl != null && message_fragment_srl.isRefreshing()) {
                        message_fragment_srl.setRefreshing(false);
                    }
                }
            });
        }
    }

    private void loadNetData() {
        if (callback == null) {
            callback = new RaspberryCallback<ListPageResponse<VisitTimeDataBean>>() {

                @Override
                public void onFailure(Request request, Response response, Exception e) {
                    super.onFailure(request, response, e);
                    runMainThread();
                }

                @Override
                public void onSuccess(ListPageResponse<VisitTimeDataBean> response, int code) {
                    super.onSuccess(response, code);
                    runMainThread();
                    if (HttpProtocol.checkStatus(response, code)) {
                        if (response.getData() == null) {
                            Log.i("tag00", mParam1 + "数据为空。");
                        } else {
                            if (response.getData().size() == 0) {
                                Log.i("tag00", mParam1 + "没有数据。");
                            } else {
                                Log.i("tag00", mParam1 + response.getData().size());
                                for (int i = 0; i < response.getData().size(); i++) {
                                    Log.i("tag00", mParam1 + response.getData().get(i).toString());
                                    mParam3.get(response.getData().get(i).getStartTime() - 8).setDiagnoseNum(response.getData().get(i).getDiagnoseNum());
                                    mParam3.get(response.getData().get(i).getStartTime() - 8).setDiagnoseNumSubscribed(response.getData().get(i).getDiagnoseNumSubscribed());
                                    mParam3.get(response.getData().get(i).getStartTime() - 8).setStartTime(response.getData().get(i).getStartTime());
                                    mParam3.get(response.getData().get(i).getStartTime() - 8).setEndTime(response.getData().get(i).getEndTime());
                                    mParam3.get(response.getData().get(i).getStartTime() - 8).setSegmentType(response.getData().get(i).getSegmentType());
                                }
                                Log.i("tag00", "====================================");
                            }
                            if (getContext() != null) {
                                getActivity().runOnUiThread(new TimerTask() {
                                    @Override
                                    public void run() {
                                        initDayData();
                                    }
                                });
                            }
                        }
                    }
                }
            };
            callback.setMainThread(false);
            callback.setContext(getActivity());
            callback.setCancelable(false);
            callback.setShowDialog(false);
        }
        if (mParam2.size() == 0) {
            Log.i("tag00", "mParam2+数据为空");
            for (int i = 0; i < 16; i++) {
                VistTimeChang vistTimeChang = new VistTimeChang(false);
                mParam2.add(vistTimeChang);
            }
        }
        if (mParam3.size() == 0) {
            Log.i("tag00", "mParam3+数据为空");
            for (int i = 0; i < 16; i++) {
                VisitTimeDataBean bean = new VisitTimeDataBean();
                bean.setStartTime(i + 8);
                bean.setEndTime(i + 9);
                bean.setDiagnoseNumSubscribed(0);
                bean.setDiagnoseNum(0);
                if (i < 5) {
                    bean.setSegmentType(1);
                } else if (i < 10) {
                    bean.setSegmentType(2);
                } else {
                    bean.setSegmentType(3);
                }
                mParam3.add(bean);
            }
            //加载网络数据
            HttpProtocol.getVisitTimeSettings(callback, DateUtil.String2Long(mParam1), Integer.MAX_VALUE);
        } else {
            initDayData();
        }
    }

    //初始化每天的数据
    public void initDayData() {
        MORNING_DIAGNOSENUM = 0;
        MORNING_DIAGNOSE_SUBCRIBEDNUM = 0;
        NOON_DIAGNOSENUM = 0;
        NOON_DIAGNOSE_SUBCRIBEDNUM = 0;
        EVENING_DIAGNOSENUM = 0;
        EVENING_DIAGNOSE_SUBCRIBEDNUM = 0;
        //一天的所有的时间段
        for (int i = 0; i < mParam3.size(); i++) {
            switch (mParam3.get(i).getSegmentType()) {
                //上午时间段
                case 1:
                    MORNING_DIAGNOSENUM += mParam3.get(i).getDiagnoseNum();
                    MORNING_DIAGNOSE_SUBCRIBEDNUM += mParam3.get(i).getDiagnoseNumSubscribed();
                    switch (mParam3.get(i).getStartTime()) {
                        //判断是哪个具体的时间段
                        case TIME_8:
                            visit_morning_time_numbertv_8.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_9:
                            visit_morning_time_numbertv_9.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_10:
                            visit_morning_time_numbertv_10.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_11:
                            visit_morning_time_numbertv_11.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                    }
                    break;
                //下午时间段
                case 2:
                    NOON_DIAGNOSENUM += mParam3.get(i).getDiagnoseNum();
                    NOON_DIAGNOSE_SUBCRIBEDNUM += mParam3.get(i).getDiagnoseNumSubscribed();
                    switch (mParam3.get(i).getStartTime()) {
                        //判断是哪个具体的时间段
                        case TIME_12:
                            visit_afternoon_time_numbertv_12.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_13:
                            visit_afternoon_time_numbertv_13.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_14:
                            visit_afternoon_time_numbertv_14.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_15:
                            visit_afternoon_time_numbertv_15.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_16:
                            visit_afternoon_time_numbertv_16.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_17:
                            visit_afternoon_time_numbertv_17.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                    }
                    break;
                //晚上时间段
                case 3:
                    EVENING_DIAGNOSENUM += mParam3.get(i).getDiagnoseNum();
                    EVENING_DIAGNOSE_SUBCRIBEDNUM += mParam3.get(i).getDiagnoseNumSubscribed();
                    switch (mParam3.get(i).getStartTime()) {
                        //判断是哪个具体的时间段
                        case TIME_18:
                            visit_evening_time_numbertv_18.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_19:
                            visit_evening_time_numbertv_19.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_20:
                            visit_evening_time_numbertv_20.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_21:
                            visit_evening_time_numbertv_21.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_22:
                            visit_evening_time_numbertv_22.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                        case TIME_23:
                            visit_evening_time_numbertv_23.setText(mParam3.get(i).getDiagnoseNum() + "");
                            break;
                    }
                    break;
            }
        }
        //这一天中，上午已约人数和未约人数
        morning_canVisitNumber.setText(MORNING_DIAGNOSENUM + "");
        morning_hasVisitedNumber.setText(MORNING_DIAGNOSE_SUBCRIBEDNUM + "");
        //这一天中，中午已约人数和未约人数
        aftternoon_canVisitNumber.setText(NOON_DIAGNOSENUM + "");
        afternoon_hasVisitedNumber.setText(NOON_DIAGNOSE_SUBCRIBEDNUM + "");
        //这一天中，晚上已约人数和未约人数
        evening_canVisitNumber.setText(EVENING_DIAGNOSENUM + "");
        evening_hasVisitedNumber.setText(EVENING_DIAGNOSE_SUBCRIBEDNUM + "");
    }

    //设置加号、减号的点击事件
    private void initData(View view) {
        for (int i = 0; i < addIds.length; i++) {
            if (i < 4) {
                findViewAndSetClick(view, addIds[i], textIds[i], ADD, MORNING, i);
            } else if (i < 10) {
                findViewAndSetClick(view, addIds[i], textIds[i], ADD, NOON, i);
            } else {
                findViewAndSetClick(view, addIds[i], textIds[i], ADD, EVENING, i);
            }
        }
        for (int i = 0; i < subIds.length; i++) {
            if (i < 4) {
                findViewAndSetClick(view, subIds[i], textIds[i], SUB, MORNING, i);
            } else if (i < 10) {
                findViewAndSetClick(view, subIds[i], textIds[i], SUB, NOON, i);
            } else {
                findViewAndSetClick(view, subIds[i], textIds[i], SUB, EVENING, i);
            }
        }

        message_fragment_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("tag00", "开始刷新");
                HttpProtocol.getVisitTimeSettings(callback, DateUtil.String2Long(mParam1), Integer.MAX_VALUE);
            }
        });

    }

    //界面所有的点击事件
    @OnClick({R.id.visit_time_fg_settingmb_tv, R.id.visit_time_fg_daorumb_tv, R.id.visit_time_fg_morning_rl
            , R.id.visit_time_fg_afternoon_rl, R.id.visit_time_fg_evening_rl})
    public void fragmentOnClick(View view) {
        switch (view.getId()) {
            case R.id.visit_time_fg_settingmb_tv:
                CustomAlertDialog.dialogExSureCancel("确定保存当前设置" + mParam1.substring(0, 10) + "的坐诊时间为本地模板?", getActivity(), new CustomAlertDialog.OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        SharedPrefenceVisit.put(Preference.VISITTIME_DATE, mParam1.substring(0, 10));
                        if (SharedPrefenceVisit.putVisitTime(mParam3)) {
                            ToastUtils.showToast("成功设置" + mParam1.substring(0, 10) + "的坐诊时间为本地模板。");
                            visit_time_fg_daorumb_tv.setBackgroundResource(R.color.green_0cc493);
                            visit_time_fg_daorumb_tv.setEnabled(true);
                        } else {
                            ToastUtils.showToast("设置模板失败。");
                            CustomAlertDialog.dialogVisitTimeWithSure(getActivity(), "设置模板失败。", R.drawable.main_common_failure);
                        }
                    }
                });
                break;
            case R.id.visit_time_fg_daorumb_tv:
                if (StringUtil.isEmpty(SharedPrefenceVisit.getString(Preference.VISITTIME_DATE))) {
                    CustomAlertDialog.dialogWithSure(getActivity(), "当前手机没有缓存模板，请设置模板之后再导入。", null);
                } else {
                    CustomAlertDialog.dialogExSureCancel("确定导入" + SharedPreferencesUtils.getString(Preference.VISITTIME_DATE) + "模板吗？", getActivity(), new CustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            ArrayList<VisitTimeDataBean> list = SharedPrefenceVisit.getVisitTimeList();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getDiagnoseNum() < mParam3.get(i).getDiagnoseNumSubscribed()) {
                                    CustomAlertDialog.dialogVisitTimeWithSure(getActivity(), "导入模板的 " + (i + 8) + ":00--" + (i + 9) + ":00 时间段可约人数为 " + list.get(i).getDiagnoseNum() + " 人,当天该时间段已约人数为" + mParam3.get(i).getDiagnoseNumSubscribed() + "人，模板导入失败。", R.drawable.main_common_failure);
                                    return;
                                }
                                mParam3.get(i).setStartTime(list.get(i).getStartTime());
                                mParam3.get(i).setSegmentType(list.get(i).getSegmentType());
                                mParam3.get(i).setDiagnoseNum(list.get(i).getDiagnoseNum());
                                mParam2.get(i).setChanged(true);
                            }
                            initDayData();
                            EventBus.getDefault().post(new EventBusVisitTime());
                        }
                    });
                }
                break;
            case R.id.visit_time_fg_morning_rl:
                if (morningIsOpened) {
                    morningIsOpened = false;
                    visit_time_fg_mr_iv.setImageResource(R.drawable.main_sittime_dropdown);
                    visit_morning_time_8_12.setVisibility(View.GONE);
                } else {
                    morningIsOpened = true;
                    visit_time_fg_mr_iv.setImageResource(R.drawable.main_sittime_pull);
                    visit_morning_time_8_12.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.visit_time_fg_afternoon_rl:
                if (afternoonIsOpened) {
                    afternoonIsOpened = false;
                    visit_time_fg_ar_iv.setImageResource(R.drawable.main_sittime_dropdown);
                    visit_afternoon_time_12_18.setVisibility(View.GONE);
                } else {
                    afternoonIsOpened = true;
                    visit_time_fg_ar_iv.setImageResource(R.drawable.main_sittime_pull);
                    visit_afternoon_time_12_18.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.visit_time_fg_evening_rl:
                if (eveningIsOpened) {
                    eveningIsOpened = false;
                    visit_time_fg_er_iv.setImageResource(R.drawable.main_sittime_dropdown);
                    visit_evening_time_18_24.setVisibility(View.GONE);
                } else {
                    eveningIsOpened = true;
                    visit_time_fg_er_iv.setImageResource(R.drawable.main_sittime_pull);
                    visit_evening_time_18_24.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    //点击加号，显示加1
    public void addNumber(TextView textView, int whenTime, int position) {
        //当前时间段加1
        textView.setText(Integer.parseInt(textView.getText().toString()) + 1 + "");
        //当前对应的实例+1，设置起始时间
        mParam3.get(position).setStartTime(position + 8);
        mParam3.get(position).setEndTime(position + 9);
        //设置当前时间段可预约人数
        mParam3.get(position).setDiagnoseNum(Integer.parseInt(textView.getText().toString()));
        switch (whenTime) {
            case MORNING:
                morning_canVisitNumber.setText(Integer.parseInt(morning_canVisitNumber.getText().toString()) + 1 + "");
                mParam3.get(position).setSegmentType(1);//设置当前时间段属于哪个大的时间范围  1=上午，2=下午，3=晚上
                break;
            case NOON:
                aftternoon_canVisitNumber.setText(Integer.parseInt(aftternoon_canVisitNumber.getText().toString()) + 1 + "");
                mParam3.get(position).setSegmentType(2);//设置当前时间段属于哪个大的时间范围
                break;
            case EVENING:
                evening_canVisitNumber.setText(Integer.parseInt(evening_canVisitNumber.getText().toString()) + 1 + "");
                mParam3.get(position).setSegmentType(3);//设置当前时间段属于哪个大的时间范围
                break;
        }
        EventBus.getDefault().post(new EventBusVisitTime());
        mParam2.get(position).setChanged(true);
    }

    //点击减号，显示减1
    public void subNumber(TextView textView, int whenTime, int position) {
        if (Integer.parseInt(textView.getText().toString()) == 0) {
            ToastUtils.showToast("当前设置为0，不能再减少。");
            return;
        }
        if (mParam3.get(position).getDiagnoseNum() <= mParam3.get(position).getDiagnoseNumSubscribed()) {
            CustomAlertDialog.dialogVisitTimeWithSure(getActivity(), "此小时段，已约人数为" + mParam3.get(position).getDiagnoseNumSubscribed() + "人，" + "设置的人数不能低于已约人数！", R.drawable.main_common_failure);
            return;
        }
        switch (whenTime) {
            case MORNING:
                if (Integer.parseInt(morning_canVisitNumber.getText().toString()) <= Integer.parseInt(morning_hasVisitedNumber.getText().toString())) {
                    ToastUtils.showToast("可约人数不能低于已约人数！");
                    return;
                }
                morning_canVisitNumber.setText(Integer.parseInt(morning_canVisitNumber.getText().toString()) - 1 + "");
                break;
            case NOON:
                if (Integer.parseInt(aftternoon_canVisitNumber.getText().toString()) <= Integer.parseInt(afternoon_hasVisitedNumber.getText().toString())) {
                    ToastUtils.showToast("可约人数不能低于已约人数！");
                    return;
                }
                aftternoon_canVisitNumber.setText(Integer.parseInt(aftternoon_canVisitNumber.getText().toString()) - 1 + "");
                break;
            case EVENING:
                if (Integer.parseInt(evening_canVisitNumber.getText().toString()) <= Integer.parseInt(evening_hasVisitedNumber.getText().toString())) {
                    ToastUtils.showToast("可约人数不能低于已约人数！");
                    return;
                }
                evening_canVisitNumber.setText(Integer.parseInt(evening_canVisitNumber.getText().toString()) - 1 + "");
                break;
        }
        textView.setText(Integer.parseInt(textView.getText().toString()) - 1 + "");
        mParam3.get(position).setDiagnoseNum(Integer.parseInt(textView.getText().toString()));
        mParam3.get(position).setStartTime(position + 8);
        mParam3.get(position).setEndTime(position + 9);
        EventBus.getDefault().post(new EventBusVisitTime());
        mParam2.get(position).setChanged(true);
    }

    //通过id找到控件，然后设置点击事件
    public void findViewAndSetClick(View view, int imgId, int textId, int type, final int whenTime, final int position) {
        final Button btn = (Button) view.findViewById(imgId);
        final TextView textView = (TextView) view.findViewById(textId);
        switch (type) {
            //这是加号
            case ADD:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addNumber(textView, whenTime, position);
                    }
                });
                break;
            //这是减号
            case SUB:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subNumber(textView, whenTime, position);
                    }
                });
                break;
        }
    }

}
