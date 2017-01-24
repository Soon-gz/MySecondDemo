package com.shkjs.patient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.CutDownSetting;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.RefundOrder;
import com.shkjs.patient.bean.RefundOrderDetail;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/9.
 * <p>
 * 申请退款界面
 */


public class RefundActivity extends BaseActivity implements View.OnClickListener {

    private static final int ORDER_DETAIL = 121;
    private static final int USER_CANCEL_ORDER_DETAIL = 122;
    private static final int OTHER_CANCEL_ORDER_DETAIL = 123;

    @Bind(R.id.refund_total_tv)
    TextView totalTV;
    @Bind(R.id.refund_deduct_tv)
    TextView deductTV;
    @Bind(R.id.refund_real_tv)
    TextView realTV;
    @Bind(R.id.refund_explain_tv)
    TextView explainTV;
    @Bind(R.id.refund_problem_iv)
    ImageView problemIV;
    @Bind(R.id.refund_btn)
    Button refundBtn;

    private Toolbar toolbar;

    private AlertDialog dialog;

    private String orderCode;
    private long orderId;
    private CancelType type;
    private Order order;
    private RefundOrderDetail refundOrderDetail;

    private List<CutDownSetting> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_refund);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.apply_for_refund);

        initData();
        initListener();

        if (TextUtils.isEmpty(orderCode) && orderId < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        getOrderDetail();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ORDER_DETAIL:
                    if (null != type && type.equals(CancelType.USER)) {
                        getRefundOrderMoneyByCode();
                    } else {
                        getRefundOrderDetail();
                        //                        if (null != order) {
                        //                            totalTV.setText(SpliceUtils.formatBalance2(order
                        // .getActuallyPaidMoney()));
                        //                            realTV.setText(SpliceUtils.formatBalance2(order.getRefundMoney
                        // ()));
                        //                            deductTV.setText(SpliceUtils.formatBalance2(order
                        // .getActuallyPaidMoney() - order
                        //                                    .getRefundMoney()));
                        //                        }
                    }
                    queryCutDown();
                    break;
                case USER_CANCEL_ORDER_DETAIL:
                    if (null != order) {
                        totalTV.setText(SpliceUtils.formatBalance2(order.getActuallyPaidMoney()));
                        realTV.setText(SpliceUtils.formatBalance2(order.getRefundMoney()));
                        deductTV.setText(SpliceUtils.formatBalance2(order.getActuallyPaidMoney() - order
                                .getRefundMoney()));
                    }
                    break;
                case OTHER_CANCEL_ORDER_DETAIL:
                    if (null != order) {
                        totalTV.setText(SpliceUtils.formatBalance2(order.getActuallyPaidMoney()));
                        realTV.setText(SpliceUtils.formatBalance2(order.getRefundMoney()));
                        deductTV.setText(SpliceUtils.formatBalance2(order.getActuallyPaidMoney() - order
                                .getRefundMoney()));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        orderCode = getIntent().getStringExtra(RefundActivity.class.getSimpleName());
        orderId = getIntent().getLongExtra(Preference.ID, -1);
        type = (CancelType) getIntent().getSerializableExtra(Preference.TYPE);
        if (null != type && type.equals(CancelType.USER)) {
            toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.cancel_order);
            refundBtn.setText("确认取消");
        }

        SpannableString string = new SpannableString("1.申请退款成功后，会将退款金额返回您所使用的支付平台账户中；\n2" + "" +
                ".到账时间为1-15个工作日，具体情况视平台而定，如有疑问，请咨询客服 400-885-9120 。");
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_e84618));
        string.setSpan(span, string.length() - 14, string.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        explainTV.setText(string);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        problemIV.setOnClickListener(this);
        refundBtn.setOnClickListener(this);
        explainTV.setOnClickListener(this);
    }

    private void createDialog() {
        if (null == dialog) {
            dialog = new RoundDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_refund, null);
            dialog.setView(view);

            ImageView cancelIV = (ImageView) view.findViewById(R.id.dialog_cancel_iv);
            cancelIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            TextView price1 = (TextView) view.findViewById(R.id.price_tv_1);
            TextView price2 = (TextView) view.findViewById(R.id.price_tv_2);
            TextView price3 = (TextView) view.findViewById(R.id.price_tv_3);
            TextView price4 = (TextView) view.findViewById(R.id.price_tv_4);
            TextView price5 = (TextView) view.findViewById(R.id.price_tv_5);
            TextView price6 = (TextView) view.findViewById(R.id.price_tv_6);
            TextView price7 = (TextView) view.findViewById(R.id.price_tv_7);
            TextView price8 = (TextView) view.findViewById(R.id.price_tv_8);

            if (null != dataList && dataList.size() > 0) {
                for (CutDownSetting setting : dataList) {
                    switch (setting.getType()) {
                        case CT:
                            price1.setText(SpliceUtils.formatBalance2(setting.getInnerOneDay()));
                            price2.setText(SpliceUtils.formatBalance2(setting.getOverOneDay()));
                            break;
                        case CV:
                            price3.setText(SpliceUtils.formatBalance2(setting.getInnerOneDay()));
                            price4.setText(SpliceUtils.formatBalance2(setting.getOverOneDay()));
                            break;
                        case AT:
                            price5.setText(SpliceUtils.formatBalance2(setting.getInnerOneDay()));
                            price6.setText(SpliceUtils.formatBalance2(setting.getOverOneDay()));
                            break;
                        case AV:
                            price7.setText(SpliceUtils.formatBalance2(setting.getInnerOneDay()));
                            price8.setText(SpliceUtils.formatBalance2(setting.getOverOneDay()));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        dialog.show();
    }

    private void getOrderDetail() {
        RaspberryCallback<ObjectResponse<Order>> callback = new RaspberryCallback<ObjectResponse<Order>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<Order> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        order = response.getData();
                        handler.sendEmptyMessage(ORDER_DETAIL);
                        return;
                    }
                }
                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(false);

        if (TextUtils.isEmpty(orderCode)) {
            HttpProtocol.getOrderById(orderId, callback);
        } else {
            HttpProtocol.getOrderByCode(orderCode, callback);
        }
    }

    private void getRefundOrderDetail() {
        RaspberryCallback<ListPageResponse<RefundOrderDetail>> callback = new
                RaspberryCallback<ListPageResponse<RefundOrderDetail>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }

            @Override
            public void onSuccess(ListPageResponse<RefundOrderDetail> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (response.getData().size() > 0) {
                            refundOrderDetail = response.getData().get(0);
                            for (RefundOrderDetail order1 : response.getData()) {
                                order.setRefundMoney(order.getRefundMoney() + order1.getMoney());
                            }
                            handler.sendEmptyMessage(OTHER_CANCEL_ORDER_DETAIL);
                            return;
                        }
                    }
                }
                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(false);

        HttpProtocol.getRefundOrderByCode(order.getCode(), callback);
    }

    private void getRefundOrderMoneyByCode() {
        RaspberryCallback<ObjectResponse<RefundOrder>> callback = new RaspberryCallback<ObjectResponse<RefundOrder>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<RefundOrder> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        order.setActuallyPaidMoney(response.getData().getActuallyPaidMoney());
                        order.setRefundMoney(response.getData().getUserRefundMoney());
                        handler.sendEmptyMessage(USER_CANCEL_ORDER_DETAIL);
                        return;
                    }
                }

                ToastUtils.showToast("获取订单失败，请重试");
                finish();
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(false);

        HttpProtocol.getRefundOrderMoneyByCode(order.getCode(), callback);
    }

    /**
     * 查询扣款规则
     */

    private void queryCutDown() {
        RaspberryCallback<ListPageResponse<CutDownSetting>> callback = new
                RaspberryCallback<ListPageResponse<CutDownSetting>>() {
            @Override
            public void onSuccess(ListPageResponse<CutDownSetting> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        dataList = response.getData();
                    }
                }
            }
        };
        HttpProtocol.queryCutDown(callback);
    }

    /**
     * 申请退款
     */
    private void refund() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("申请退款成功");
                        setResult();
                        finish();
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        ToastUtils.showToast("申请退款失败，" + response.getMsg());
                        return;
                    }
                }

                ToastUtils.showToast("申请退款失败，请重试");
            }
        };

        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setContext(this);

        if (null != refundOrderDetail) {
            HttpProtocol.refund(refundOrderDetail.getCode(), callback);
        } else {
            ToastUtils.showToast("未查询到退款订单");
        }
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
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        ToastUtils.showToast("取消失败，" + response.getMsg());
                        return;
                    }
                }
                ToastUtils.showToast("取消失败，请重试");
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.cancelOrder(order.getCode(), order.getSource(), callback);
    }

    /**
     * 设置结果
     */
    private void setResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + "400-885-9120");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refund_problem_iv:
                createDialog();
                break;
            case R.id.refund_btn:
                if (refundBtn.getText().toString().trim().contains("取消")) {
                    submitCancelOrder();
                } else {
                    refund();
                }
                break;
            case R.id.refund_explain_tv:
                callPhone();
                break;
            default:
                break;
        }
    }
}
