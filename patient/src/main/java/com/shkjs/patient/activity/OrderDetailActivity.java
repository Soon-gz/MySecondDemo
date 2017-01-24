package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.RefundOrderDetail;
import com.shkjs.patient.bean.UserOrder;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderStatus;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/8.
 * <p>
 * 订单详情
 */

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int PAY = 121;
    private static final int REFUND = 122;

    @Bind(R.id.order_detail_status_tv)
    TextView orderStatusTV;//订单状态
    @Bind(R.id.order_detail_number_tv)
    TextView orderCodeTV;//订单号
    @Bind(R.id.order_detail_time_tv)
    TextView orderTimeTV;//订单时间
    @Bind(R.id.order_detail_icon_iv)
    ImageView orderIconIV;//订单类型图标
    @Bind(R.id.order_detail_name_tv)
    TextView orderNameTV;//订单类型名字
    @Bind(R.id.order_detail_price_tv)
    TextView orderPriceTV;//订单价格
    @Bind(R.id.order_detail_doctor_name_tv)
    TextView orderOrderDoctorTV;//预约医生
    @Bind(R.id.order_detail_order_time_ll)
    LinearLayout orderOrderTimeLL;
    @Bind(R.id.order_detail_order_time_tv)
    TextView orderOrderTimeTV;//预约时间
    @Bind(R.id.order_detail_order_price_left_1)
    TextView orderPriceLeftTV1;//订单金额
    @Bind(R.id.order_detail_order_price_right_1)
    TextView orderPriceRightTV1;//订单金额
    @Bind(R.id.order_detail_order_price_left_2)
    TextView orderPriceLeftTV2;//优惠金额/扣除款项
    @Bind(R.id.order_detail_order_price_right_2)
    TextView orderPriceRightTV2;//优惠金额/扣除款项
    @Bind(R.id.order_detail_order_price_left_3)
    TextView orderPriceLeftTV3;//实际支付/实际退款
    @Bind(R.id.order_detail_order_price_right_3)
    TextView orderPriceRightTV3;//实际支付/实际退款
    @Bind(R.id.order_detail_action_ll)
    LinearLayout orderActionLL;
    @Bind(R.id.divider_line_iv)
    ImageView deleteIV;
    @Bind(R.id.order_detail_delete_ll)
    LinearLayout deleteLL;
    @Bind(R.id.order_detail_action_icon)
    ImageView orderActionIconIV;//订单操作图标
    @Bind(R.id.order_detail_action_right)
    ImageView orderActionRightIconIV;//订单操作图标
    @Bind(R.id.order_detail_action_name)
    TextView orderActionNameTV;//订单操作名字

    private static final int DOCTOR_DETAIL = 121;

    private Toolbar toolbar;

    private AlertDialog dialog;

    private UserOrder userOrder;
    private int position;

    private RefundOrderDetail refundOrder;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOCTOR_DETAIL:
                    showView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.order_detail);

        initData();
        initListener();

        if (null == userOrder || position < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        getDoctorDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(orderIconIV);
    }

    private void initData() {
        userOrder = (UserOrder) getIntent().getSerializableExtra(OrderDetailActivity.class.getSimpleName());
        position = getIntent().getIntExtra(Preference.POSITION, -1);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        orderActionLL.setOnClickListener(this);
        deleteLL.setOnClickListener(this);
    }

    private void submitCancelOrder() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        setResult();
                        finish();
                        return;
                    }
                }

                ToastUtils.showToast("取消失败，请重试");
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.cancelOrder(userOrder.getOrder().getCode(), userOrder.getOrder().getSource(), callback);
    }

    private void submitDeleteOrder() {
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
                        setResult();
                        finish();
                        ToastUtils.showToast("取消订单成功");
                        return;
                    }
                }
                ToastUtils.showToast("取消订单失败");
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(false);

        HttpProtocol.deleteOrder(userOrder.getOrder().getCode(), callback);
    }

    /**
     * 获取医生详情
     */
    private void getDoctorDetail() {
        RaspberryCallback<ObjectResponse<Doctor>> callback = new RaspberryCallback<ObjectResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        userOrder.getBusiness().setDoctor(response.getData());
                        handler.sendEmptyMessage(DOCTOR_DETAIL);
                    } else {
                        Logger.e("Doctor", getString(R.string.get_doctor_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                        finish();
                    }
                } else {
                    Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                    ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                    finish();
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getDoctorDetail(userOrder.getBusiness().getDoctor().getId(), OrderDetailActivity.class
                .getSimpleName(), callback);
    }

    /**
     * 获取退款订单
     **/
    private void getRefundOrder() {
        RaspberryCallback<ListPageResponse<RefundOrderDetail>> callback = new
                RaspberryCallback<ListPageResponse<RefundOrderDetail>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Refund", "未获取到退款订单");
            }

            @Override
            public void onSuccess(ListPageResponse<RefundOrderDetail> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData().size() > 0) {
                            for (RefundOrderDetail order : response.getData()) {
                                userOrder.getOrder().setRefundMoney(userOrder.getOrder().getRefundMoney() + order
                                        .getMoney());
                            }
                            refundOrder = response.getData().get(0);
                            handler.sendEmptyMessage(DOCTOR_DETAIL);
                        }
                    } else {
                        Logger.e("Refund", "未获取到退款订单" + response.getMsg());
                    }
                } else {
                    Logger.e("Refund", "未获取到退款订单");
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getRefundOrderByCode(userOrder.getOrder().getCode(), callback);
    }

    private void showView() {
        if (null != userOrder) {
            //每次都固定的
            orderCodeTV.setText(userOrder.getOrder().getCode());
            orderTimeTV.setText(TimeFormatUtils.getLocalTime("yyyy-MM-dd HH:mm:ss", Long.parseLong(userOrder.getOrder
                    ().getCreateDate())));
            orderNameTV.setText(userOrder.getOrder().getMark());
            orderPriceTV.setText(SpliceUtils.splicePrice(userOrder.getOrder().getMoney()));
            orderOrderDoctorTV.setText(userOrder.getBusiness().getDoctor().getName());
            orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(userOrder.getOrder().getMoney() - userOrder
                    .getOrder().getActuallyPaidMoney()));
            orderPriceRightTV3.setText(SpliceUtils.splicePrice(userOrder.getOrder().getActuallyPaidMoney()));
            orderPriceRightTV1.setText(SpliceUtils.splicePrice(userOrder.getOrder().getMoney()));

            orderPriceLeftTV1.setText("订单金额");
            orderPriceLeftTV2.setText("优惠金额");
            orderPriceLeftTV3.setText("实际支付");

            //不同状态不同展示
            orderActionRightIconIV.setVisibility(View.GONE);
            deleteLL.setVisibility(View.GONE);
            deleteIV.setVisibility(View.GONE);
            switch (userOrder.getOrder().getStatus()) {
                case INITIAL:
                    deleteIV.setVisibility(View.VISIBLE);
                    deleteLL.setVisibility(View.VISIBLE);
                    orderActionLL.setVisibility(View.VISIBLE);
                    orderStatusTV.setText(getString(R.string.wait_pay_order));
                    orderActionNameTV.setText("去支付");
                    orderActionIconIV.setImageResource(R.drawable.personalcenter_order_payment);
                    orderActionRightIconIV.setVisibility(View.VISIBLE);
                    orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(userOrder.getOrder()
                            .getActuallyPaidMoney()));
                    orderPriceRightTV3.setText(SpliceUtils.splicePrice(userOrder.getOrder().getActuallyPaidMoney()));
                    orderPriceRightTV1.setText(SpliceUtils.splicePrice(userOrder.getOrder().getMoney()));
                    break;
                case EXPIRE:
                    orderActionLL.setVisibility(View.GONE);
                    orderStatusTV.setText(OrderStatus.EXPIRE.getMark());
                    orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(userOrder.getOrder()
                            .getActuallyPaidMoney()));
                    orderPriceRightTV3.setText(SpliceUtils.splicePrice(userOrder.getOrder().getActuallyPaidMoney()));
                    orderPriceRightTV1.setText(SpliceUtils.splicePrice(userOrder.getOrder().getMoney()));
                    break;
                case PAID:
                    orderActionLL.setVisibility(View.VISIBLE);
                    orderStatusTV.setText(getString(R.string.no_complete_order));
                    orderActionNameTV.setText("取消订单");
                    orderActionIconIV.setImageResource(R.drawable.personalcenter_order_cancel);
                    if (userOrder.getOrder().getSource().equals(OrderSource.INQUIRY_RESERVE)) {//图文咨询不允许取消
                        orderActionLL.setVisibility(View.GONE);
                    }
                    break;
                case COMPLETE:
                    orderActionLL.setVisibility(View.GONE);
                    orderStatusTV.setText(getString(R.string.complete_order));
                    break;
                case CANCEL:
                    orderActionLL.setVisibility(View.VISIBLE);
                    orderStatusTV.setText(OrderStatus.CANCEL.getMark());
                    if (null != refundOrder) {
                        orderPriceLeftTV1.setText("实际支付");
                        orderPriceRightTV1.setText(SpliceUtils.splicePrice(userOrder.getOrder().getActuallyPaidMoney
                                ()));
                        orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(userOrder.getOrder()
                                .getActuallyPaidMoney() - userOrder.getOrder().getRefundMoney()));
                        orderPriceRightTV3.setText(SpliceUtils.splicePrice(userOrder.getOrder().getRefundMoney()));
                    } else {
                        getRefundOrder();
                    }
                    if (!userOrder.getBusiness().getCancelType().equals(CancelType.USER)) {
                        orderActionNameTV.setText("申请退款");
                        orderPriceLeftTV2.setText("扣除款项");
                        orderPriceLeftTV3.setText("实际退款");
                        orderActionIconIV.setImageResource(R.drawable.personalcenter_order_refund);
                        orderActionRightIconIV.setVisibility(View.VISIBLE);
                    } else {
                        orderActionLL.setVisibility(View.GONE);
                    }
                    break;
                case REFUND:
                    orderActionLL.setVisibility(View.GONE);
                    orderStatusTV.setText(userOrder.getOrder().getStatus().getMark());
                    if (null != refundOrder) {
                        orderPriceLeftTV1.setText("实际支付");
                        orderPriceRightTV1.setText(SpliceUtils.splicePrice(userOrder.getOrder().getActuallyPaidMoney
                                ()));
                        orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(userOrder.getOrder()
                                .getActuallyPaidMoney() - userOrder.getOrder().getRefundMoney()));
                        orderPriceRightTV3.setText(SpliceUtils.splicePrice(userOrder.getOrder().getRefundMoney()));
                    } else {
                        getRefundOrder();
                    }
                    orderPriceLeftTV2.setText("扣除款项");
                    orderPriceLeftTV3.setText("实际退款");
                    break;
                default:
                    orderActionLL.setVisibility(View.GONE);
                    break;
            }

            Integer id = R.drawable.personalcenter_order_pictureconsulting;
            if (userOrder.getOrder().getSource().equals(OrderSource.INQUIRY_RESERVE)) {
                id = R.drawable.personalcenter_order_pictureconsulting;
                orderOrderTimeLL.setVisibility(View.GONE);
            } else if (userOrder.getOrder().getSource().equals(OrderSource.SIT_DIAGNOSE_RESERVE)) {
                id = R.drawable.personalcenter_order_videoconsultation;
                orderOrderTimeTV.setText(TimeFormatUtils.getLocalTime(Long.parseLong(userOrder.getBusiness()
                        .getSitDiagnose().getDiagnoseDate())) + " " + SpliceUtils.getTime(userOrder.getBusiness()
                        .getSitDiagnose()));
            } else if (userOrder.getOrder().getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {
                id = R.drawable.personalcenter_order_videogroupconsultation;
                String name = "";
                for (int i = 0; i < userOrder.getBusiness().getDoctorList().size(); i++) {
                    name += userOrder.getBusiness().getDoctorList().get(i).getName() + " ";
                }
                orderOrderDoctorTV.setText(name);
                orderOrderTimeTV.setText(TimeFormatUtils.getLocalTime(Long.parseLong(userOrder.getBusiness()
                        .getStartDate())) + " " +
                        TimeFormatUtils.getLocalTime("HH:mm", Long.parseLong(userOrder.getBusiness().getStartDate())));
            }
            Glide.with(OrderDetailActivity.this).load(id).error(R.drawable.personalcenter_order_pictureconsulting)
                    .placeholder(R.drawable.personalcenter_order_pictureconsulting).into(orderIconIV);//

        }
    }

    private void action() {
        if (TextUtils.equals(orderActionNameTV, "去支付")) {
            payOrder();
        } else if (TextUtils.equals(orderActionNameTV, "申请退款")) {
            refundOrder();
        } else if (TextUtils.equals(orderActionNameTV, "取消订单")) {
            cancleOrder();
        } else if (TextUtils.equals(orderActionNameTV, "删除订单")) {
            deleteOrder();
        }
    }

    private void payOrder() {
        //        Intent intent = new Intent(this, PayActivity.class);
        //        intent.putExtra(PayActivity.class.getSimpleName(), order.getCode());
        //        startActivityForResult(intent, PAY);

        OrderInfo orderInfo = new OrderInfo();
        if (userOrder.getOrder().getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {
            orderInfo.setDoctors((ArrayList<Doctor>) userOrder.getBusiness().getDoctorList());
        } else {
            ArrayList<Doctor> doctors = new ArrayList<>();
            doctors.add(userOrder.getBusiness().getDoctor());
            orderInfo.setDoctors(doctors);
        }
        orderInfo.setOrder(userOrder.getOrder());
        orderInfo.setOrderInfoType(getOrderInfoType(userOrder.getOrder().getSource()));
        Date date = DateUtil.getTimeFromString(userOrder.getOrder().getCreateDate(), "yyyy-MM-dd");
        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", date.getTime()) + " " + DateUtil.DateToWeek(date);
        orderInfo.setTime(timeStr);
        PayActivity.startForResult(this, orderInfo, PAY);
    }

    private void refundOrder() {
        Intent intent = new Intent(this, RefundActivity.class);
        intent.putExtra(RefundActivity.class.getSimpleName(), userOrder.getOrder().getCode());
        startActivityForResult(intent, REFUND);
    }

    private void cancleOrder() {
        //        createDialog("是否确定取消订单?", new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                if (null != dialog && dialog.isShowing()) {
        //                    dialog.dismiss();
        //                }
        //                submitCancelOrder();
        //            }
        //        });

        Intent intent = new Intent(this, RefundActivity.class);
        intent.putExtra(RefundActivity.class.getSimpleName(), userOrder.getOrder().getCode());
        intent.putExtra(Preference.TYPE, CancelType.USER);
        startActivityForResult(intent, REFUND);
    }

    private void deleteOrder() {
        createDialog("是否确定取消订单?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                submitDeleteOrder();
            }
        });
    }

    private void createDialog(String title, View.OnClickListener listener) {
        if (null == dialog) {
            dialog = new RoundDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_detail, null);
            dialog.setView(view);

            TextView tilleTV = (TextView) view.findViewById(R.id.dialog_title_tv);
            tilleTV.setText(title);

            Button cancelBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            Button sureBtn = (Button) view.findViewById(R.id.dialog_sure_btn);
            sureBtn.setOnClickListener(listener);
        }
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_detail_action_ll:
                action();
                break;
            case R.id.order_detail_delete_ll:
                deleteOrder();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PAY:
                    setResult();
                    finish();
                    break;
                case REFUND:
                    setResult();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取订单type
     *
     * @param source
     * @return
     */
    private OrderInfoType getOrderInfoType(OrderSource source) {
        OrderInfoType type = null;
        switch (source) {
            case INQUIRY_RESERVE:
                type = OrderInfoType.PICTURE;
                break;
            case SIT_DIAGNOSE_RESERVE:
                type = OrderInfoType.VIDEO;
                break;
            case GROUP_SIT_DIAGNOSE:
                type = OrderInfoType.MULTIVIDEO;
                break;
            case GROUP_SIT_DIAGNOSE_DETAIL:
                type = OrderInfoType.MULTIVIDEO;
                break;
            case RECHARGE:
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 设置结果
     */
    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(Preference.POSITION, position);
        intent.putExtra(OrderDetailActivity.class.getSimpleName(), true);
        setResult(RESULT_OK, intent);
    }

}
