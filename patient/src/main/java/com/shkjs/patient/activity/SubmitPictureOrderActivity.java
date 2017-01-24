package com.shkjs.patient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;

/**
 * Created by xiaohu on 2016/12/27.
 * <p>
 * 预约图文咨询
 */

public class SubmitPictureOrderActivity extends BaseActivity {

    private Toolbar toolbar;
    private Doctor doctor;
    private TextView textView;

    public static void start(Context context, Doctor doctor) {
        Intent intent = new Intent(context, SubmitPictureOrderActivity.class);
        intent.putExtra(SubmitPictureOrderActivity.class.getSimpleName(), doctor);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doctor = (Doctor) getIntent().getSerializableExtra(SubmitPictureOrderActivity.class.getSimpleName());
        if (null == doctor) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        setContentView(R.layout.activity_submit_picture_order);

        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.order_picture_text);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.subimt_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderPicture(doctor);
            }
        });
        textView = ((TextView) findViewById(R.id.text_tv));

        initString();
    }

    /**
     * 预约图文咨询
     *
     * @param doctor 预约的医生
     */

    private void orderPicture(final Doctor doctor) {
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
                        try {
                            String json = response.getData();
                            final JSONObject object = JSON.parseObject(json);
                            final String orderCode = object.getString("code");
                            getOrderInfo(orderCode, doctor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("预约失败，请重试");
                        }
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        if (null != response.getData()) {
                            try {
                                String json = response.getData();
                                final JSONObject object = JSON.parseObject(json);
                                final String orderCode = object.getString("code");
                                OrderStatus status = OrderStatus.valueOf(object.getString("status"));
                                if (status.equals(OrderStatus.INITIAL)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            createPayView(orderCode, doctor);
                                        }
                                    });
                                } else {
                                    ToastUtils.showToast(response.getMsg());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showToast(response.getMsg());
                            }
                        } else {
                            ToastUtils.showToast(response.getMsg());
                        }
                        return;
                    }
                }
                ToastUtils.showToast("预约失败，请重试");
            }
        };
        callback.setCancelable(false);
        callback.setContext(SubmitPictureOrderActivity.this);
        callback.setMainThread(false);

        HttpProtocol.orderPicture(doctor.getId(), callback);
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
        callback.setContext(SubmitPictureOrderActivity.this);
        callback.setCancelable(false);

        HttpProtocol.deleteOrder(orderCode, callback);
    }

    /**
     * 创建支付dialog
     *
     * @param code
     * @param doctor
     */
    private void createPayView(final String code, final Doctor doctor) {
        final AlertDialog dialog = new RoundDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
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
                getOrderInfo(code, doctor);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 20);
        dialog.getWindow().setAttributes(params);
    }

    private void getOrderInfo(String code, final Doctor doctor) {
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

                        PayActivity.start(SubmitPictureOrderActivity.this, orderInfo);
                        finish();

                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                } else {
                    ToastUtils.showToast("预约失败，请稍后重试");
                }
            }
        };

        callback.setContext(SubmitPictureOrderActivity.this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }

    private void initString() {
        String str = "1、在医生未根据您的问诊给出合理的报告之前均可咨询，若医生在您开始咨询后24小时内未回复给您诊疗建议报告，则全额退款。\n2、咨询期间不能取消该订单。\n3" +
                "、若您点击提交订单，则表示您已知晓以上条款并同意《医星汇用户服务协议》。";
        String key = "《医星汇用户服务协议》";
        int index = str.indexOf(key);
        SpannableString string = new SpannableString(str);
        string.setSpan(new Clickable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubmitPictureOrderActivity.this, UserAgreementActivity.class));
            }
        }), index, index + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new NoUnderlineSpan(), index, index + key.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(string);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    class NoUnderlineSpan extends UnderlineSpan {

        @Override

        public void updateDrawState(TextPaint ds) {
            //设置可点击文本的字体颜色
            ds.setColor(ContextCompat.getColor(SubmitPictureOrderActivity.this, R.color.blue_2bbbe6));
            //没有下划线
            ds.setUnderlineText(false);

        }
    }
}
