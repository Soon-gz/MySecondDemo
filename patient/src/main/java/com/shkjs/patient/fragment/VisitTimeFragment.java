package com.shkjs.patient.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RecycleViewDecoration;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.PayActivity;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.bean.SitDiagnose;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/19.
 */

public class VisitTimeFragment extends BaseFragment {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.no_message_layout_textview)
    TextView noMsgTV;

    private static final String DOCTOR = "Doctor";
    private static final String TIME = "Time";

    private boolean isPrepared;//预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据

    private Context context;
    private List<SitDiagnose> datalist;
    private BaseRecyclerAdapter<SitDiagnose> adapter;
    private Doctor doctor;
    private String time;
    private Page page;

    /**
     * 坐诊时间日期管理生成的对象过多，使用工厂模式创建Fragemnt,携带今天数据对象
     *
     * @param doctor
     * @param time
     * @return
     */
    public static VisitTimeFragment newInstance(Doctor doctor, String time) {
        VisitTimeFragment fragment = new VisitTimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(DOCTOR, doctor);
        args.putString(TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            doctor = (Doctor) getArguments().getSerializable(DOCTOR);
            time = getArguments().getString(TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_time, container, false);
        //注解
        ButterKnife.bind(this, view);

        //设置数据
        initData();
        //设置监听
        initListener();
        //加载数据
        isPrepared = true;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setlazyLoad();
    }

    private void initData() {
        context = getActivity();
        datalist = new ArrayList<>();
        noMsgTV.setText("暂无该医生预约时间信息");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMsgTV.setCompoundDrawables(null, drawable, null, null);
        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);

        adapter = new BaseRecyclerAdapter<SitDiagnose>(context, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.visit_time_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final SitDiagnose item) {

                holder.getTextView(R.id.time_tv).setText(SpliceUtils.getTime(item));

                holder.setClickListener(R.id.order_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                        orderVideo(item);
                        createSureView(item);
                    }
                });

            }
        };
    }

    private void initListener() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.addItemDecoration(new RecycleViewDecoration(context));//分割线
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载数据方法
     */
    protected void setlazyLoad() {
        super.setlazyLoad();
        if (!isPrepared || !isVisible) {
            return;
        }
        queryDoctorTime();
    }

    /**
     * 查询医生坐诊时间
     */
    private void queryDoctorTime() {

        if (null == doctor || null == time) {
            return;
        }

        RaspberryCallback<ListPageResponse<SitDiagnose>> callback = new
                RaspberryCallback<ListPageResponse<SitDiagnose>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                showView();
            }

            @Override
            public void onSuccess(ListPageResponse<SitDiagnose> response, int code) {
                super.onSuccess(response, code);

                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        datalist.clear();
                        for (SitDiagnose sitDiagnose : response.getData()) {
                            if (sitDiagnose.getDiagnoseNum() > sitDiagnose.getDiagnoseNumSubscribed()) {
                                datalist.add(sitDiagnose);
                            }
                        }
                        //                        datalist.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    }
                }
                showView();
            }
        };

        callback.setContext(context);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.queryDoctorTime(doctor.getId(), page, "" + DateUtil.String2Long(time), callback);
        showView();
    }

    /**
     * 预约视频就诊
     *
     * @param sitDiagnose 坐诊时间ID
     */
    private void orderVideo(final SitDiagnose sitDiagnose) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        getOrderInfo(response.getData(), null, sitDiagnose);
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL) && null != response.getData()) {
                        try {
                            String json = response.getData();
                            final JSONObject object = JSON.parseObject(json);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String orderCode = object.getString("code");
                                    int startTime = object.getIntValue("startTime");
                                    int endTime = object.getIntValue("endTime");
                                    long date = object.getLongValue("date");
                                    long doctorId = object.getLongValue("doctorId");
                                    SitDiagnose sitDiagnose = new SitDiagnose();
                                    sitDiagnose.setStartTime(startTime);
                                    sitDiagnose.setEndTime(endTime);
                                    createPayView(orderCode, TimeFormatUtils.getLocalTime(date), sitDiagnose, doctorId);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("预约失败 " + response.getMsg());
                        }
                        return;
                    }
                }
                ToastUtils.showToast("预约失败 " + response.getMsg());

            }
        };
        callback.setCancelable(false);
        callback.setContext(context);
        callback.setMainThread(false);

        HttpProtocol.orderVideo(sitDiagnose.getId(), callback);
    }

    private void getOrderInfo(String code, final String str, final SitDiagnose sitDiagnose) {
        RaspberryCallback<ObjectResponse<Order>> callback = new RaspberryCallback<ObjectResponse<Order>>() {
            @Override
            public void onSuccess(ObjectResponse<Order> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {

                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setOrder(response.getData());
                        ArrayList<Doctor> doctors = new ArrayList<>();
                        doctors.add(doctor);
                        orderInfo.setDoctors(doctors);
                        //                        orderInfo.setTime("10-31 周一 20:00--21:00");
                        Date date = DateUtil.getTimeFromString(TextUtils.isEmpty(str) ? time : str, "yyyy-MM-dd");
                        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", date.getTime()) + " " + DateUtil
                                .DateToWeek(date) + " " + SpliceUtils.getTime(sitDiagnose);
                        orderInfo.setTime(timeStr);
                        orderInfo.setOrderInfoType(OrderInfoType.VIDEO);

                        PayActivity.start(context, orderInfo);

                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                } else {
                    ToastUtils.showToast("预约失败，请稍后重试");
                }
            }
        };

        callback.setContext(context);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }

    private void getOrderInfo(String code, final String str, final SitDiagnose sitDiagnose, long doctorId) {

        OrderInfo orderInfo = new OrderInfo();
        Order order = new Order();
        order.setCode(code);
        orderInfo.setOrder(order);
        ArrayList<Doctor> doctors = new ArrayList<>();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctors.add(doctor);
        orderInfo.setDoctors(doctors);
        //                        orderInfo.setTime("10-31 周一 20:00--21:00");
        Date date = DateUtil.getTimeFromString(TextUtils.isEmpty(str) ? time : str, "yyyy-MM-dd");
        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", date.getTime()) + " " + DateUtil.DateToWeek(date) + " " +
                "" + SpliceUtils.getTime(sitDiagnose);
        orderInfo.setTime(timeStr);
        orderInfo.setOrderInfoType(OrderInfoType.VIDEO);

        PayActivity.start(context, orderInfo);

    }

    private void deleteOrder(String orderCode) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                ToastUtils.showToast("取消订单失败");
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("取消订单成功");
                    }
                }
            }
        };

        callback.setMainThread(false);
        callback.setContext(context);
        callback.setCancelable(false);

        HttpProtocol.deleteOrder(orderCode, callback);
    }

    /**
     * 创建支付dialog
     *
     * @param code
     * @param str
     * @param sitDiagnose
     */
    private void createPayView(final String code, final String str, final SitDiagnose sitDiagnose, final long
            doctorId) {
        final AlertDialog dialog = new RoundDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);
        dialog.setView(view);

        ((TextView) view.findViewById(R.id.dialog_msg)).setText("您有未支付的订单，点击“继续”完成上次订单，点击“取消”继续本次新订单");
        view.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteOrder(code);
            }
        });

        TextView sureTV = (TextView) view.findViewById(R.id.dialog_sure);
        sureTV.setText("继续");
        sureTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getOrderInfo(code, str, sitDiagnose, doctorId);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(context) - 2 * DisplayUtils.dip2px(context, 20);
        dialog.getWindow().setAttributes(params);
        //        CustomAlertDialog.dialogExSureCancel("存在未支付订单，去支付？", context, new CustomAlertDialog
        // .OnDialogClickListener() {
        //            @Override
        //            public void doSomeThings() {
        //                getOrderInfo(code, str, sitDiagnose);
        //
        //            }
        //        });
    }

    private void createSureView(final SitDiagnose sitDiagnose) {
        final AlertDialog dialog = new RoundDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_order_sure, null);
        dialog.setView(view);

        TextView doctorNameTV = (TextView) view.findViewById(R.id.doctor_name_tv);
        doctorNameTV.setText(doctor.getName() + "(" + doctor.getLevel().getMark() + ")");
        TextView orderTimeTV = (TextView) view.findViewById(R.id.doctor_time_tv);
        orderTimeTV.setText(time.substring(0, 10) + " 日 " + SpliceUtils.getTime(sitDiagnose));
        view.findViewById(R.id.cancel_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                orderVideo(sitDiagnose);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(context) - 2 * DisplayUtils.dip2px(context, 20);
        dialog.getWindow().setAttributes(params);
    }

    private void showView() {
        if (datalist.size() > 0) {
            noMsgTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noMsgTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
