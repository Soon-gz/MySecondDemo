package com.shkjs.patient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.DoctorDetailActivity;
import com.shkjs.patient.activity.DoctorDetailDialogActivity;
import com.shkjs.patient.activity.DoctorsDetailDialogActivity;
import com.shkjs.patient.activity.PayActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;

/**
 * Created by xiaohu on 2016/10/31.
 * <p>
 * 1.聊天界面点击名片消息，跳转到医生详情界面
 * 2.视频界面点击医生头像，跳转到医生信息页面
 */

public class PatientReceiver extends BroadcastReceiver {

    private final static String DOCTOR_DETAIL = "com.shkjs.patient.doctor_detail";
    private final static String DOCTORS_DETAIL = "com.shkjs.patient.doctors_detail";
    private final static String ORDER_PICTURE = "com.shkjs.patient.order_picture";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case DOCTOR_DETAIL://视频就诊医生详情
                long doctorId = intent.getLongExtra("doctorId", -1);
                String userName = intent.getStringExtra("doctorUserName");
                Intent doctorDetail = new Intent();
                if (TextUtils.isEmpty(userName)) {
                    doctorDetail.setClass(context, DoctorDetailActivity.class);
                    doctorDetail.putExtra(Preference.DOCTOR_DETAIL, doctorId);
                } else {
                    doctorDetail.setClass(context, DoctorDetailDialogActivity.class);
                    doctorDetail.putExtra(Preference.DOCTOR_DETAIL, userName);
                }
                doctorDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(doctorDetail);
                break;
            case DOCTORS_DETAIL://视频会诊医生详情
                ArrayList<Long> doctorIds = (ArrayList<Long>) intent.getSerializableExtra("doctorIds");
                Intent doctorsDetail = new Intent(context, DoctorsDetailDialogActivity.class);
                doctorsDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                doctorsDetail.putExtra(DoctorsDetailDialogActivity.class.getSimpleName(), doctorIds);
                context.startActivity(doctorsDetail);
                break;
            case ORDER_PICTURE://重新预约图文咨询
                String doctorName = intent.getStringExtra("doctorName");
                if (!TextUtils.isEmpty(doctorName) && doctorName.contains("doctor")) {
                    doctorName = doctorName.split("_")[0];
                    //                    getDoctorDetail(context, doctorName);
                    Intent doctorIntent = new Intent(context, DoctorDetailActivity.class);
                    doctorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    doctorIntent.putExtra(DoctorDetailActivity.class.getSimpleName(), doctorName);
                    context.startActivity(doctorIntent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取医生详情
     */
    private void getDoctorDetail(final Context context, String userName) {
        RaspberryCallback<ObjectResponse<Doctor>> callback = new RaspberryCallback<ObjectResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onSuccess(ObjectResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        orderPicture(context, response.getData());
                    }
                }
            }
        };

        callback.setMainThread(false);
        callback.setCancelable(true);

        HttpProtocol.queryDoctorDetailByUserName(userName, null, callback);
    }

    /**
     * 预约图文咨询
     *
     * @param doctor 预约的医生
     */
    private void orderPicture(final Context context, final Doctor doctor) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        try {
                            String json = response.getData();
                            final JSONObject object = JSON.parseObject(json);
                            final String orderCode = object.getString("code");
                            getOrderInfo(orderCode, context, doctor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("预约失败，请重试");
                        }
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        //                        if (null != response.getData()) {
                        //                            try {
                        //                                String json = response.getData();
                        //                                final JSONObject object = JSON.parseObject(json);
                        //                                final String orderCode = object.getString("code");
                        //                                OrderStatus status = OrderStatus.valueOf(object.getString
                        // ("status"));
                        //                                if (status.equals(OrderStatus.INITIAL)) {
                        //                                    runOnUiThread(new Runnable() {
                        //                                        @Override
                        //                                        public void run() {
                        //                                            createPayView(context, orderCode, doctor);
                        //                                        }
                        //                                    });
                        //                                } else {
                        //                        ToastUtils.showToast("预约失败 " + response.getMsg());
                        //                                }
                        //                            } catch (JSONException e) {
                        //                                e.printStackTrace();
                        //                                ToastUtils.showToast("预约失败 " + response.getMsg());
                        //                            }
                        //                        } else {
                        ToastUtils.showToast(response.getMsg());
                        //                        }
                        return;
                    }
                }
                ToastUtils.showToast("预约失败，请重试");
            }
        };
        callback.setCancelable(false);
        callback.setMainThread(false);

        HttpProtocol.orderPicture(doctor.getId(), callback);
    }

    /**
     * 创建支付dialog
     *
     * @param context
     * @param code
     * @param doctor
     */
    private void createPayView(final Context context, final String code, final Doctor doctor) {
        final AlertDialog dialog = new RoundDialog(context);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);
        dialog.setView(view);

        ((TextView) view.findViewById(R.id.dialog_msg)).setText("存在未支付订单，去支付？");
        view.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getOrderInfo(code, context, doctor);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(context) - 2 * DisplayUtils.dip2px(context, 20);
        dialog.getWindow().setAttributes(params);
    }

    private void getOrderInfo(String code, final Context context, final Doctor doctor) {
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
                        orderInfo.setTime("24小时内均可咨询");
                        orderInfo.setOrderInfoType(OrderInfoType.PICTURE);

                        PayActivity.launch(context, orderInfo);
                        return;
                    }
                }
                ToastUtils.showToast("预约失败，请重试");
            }
        };

        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }
}
