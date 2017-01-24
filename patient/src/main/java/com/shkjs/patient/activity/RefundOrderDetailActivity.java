package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/21.
 * <p>
 * 退款订单详情
 */

public class RefundOrderDetailActivity extends BaseActivity implements View.OnClickListener {

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
    @Bind(R.id.order_detail_action_icon)
    ImageView orderActionIconIV;//订单操作图标
    @Bind(R.id.order_detail_action_right)
    ImageView orderActionRightIconIV;//订单操作图标
    @Bind(R.id.order_detail_action_name)
    TextView orderActionNameTV;//订单操作名字

    private Toolbar toolbar;
    private long orderId;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.order_detail);

        initData();
        initListener();

        if (orderId < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        getOrderInfo();
    }

    private void initData() {
        orderId = getIntent().getLongExtra(RefundOrderDetailActivity.class.getSimpleName(), -1);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        orderActionLL.setOnClickListener(this);
    }


    private void showView() {
        if (null != order) {
            //每次都固定的
            orderCodeTV.setText(order.getCode());
            orderTimeTV.setText(TimeFormatUtils.getLocalTime(Long.parseLong(order.getCreateDate())));
            orderNameTV.setText(order.getMark());
            orderPriceTV.setText(SpliceUtils.splicePrice(order.getMoney()));
            //            orderOrderDoctorTV.setText(order.getName());
            orderOrderTimeTV.setText(order.getCreateDate());
            orderPriceRightTV2.setText("-" + SpliceUtils.splicePrice(order.getMoney() - order.getActuallyPaidMoney()));
            orderPriceRightTV3.setText(SpliceUtils.splicePrice(order.getActuallyPaidMoney()));
            orderPriceRightTV1.setText(SpliceUtils.splicePrice(order.getMoney()));
            orderActionRightIconIV.setVisibility(View.GONE);
            orderActionNameTV.setText("申请退款");
            orderPriceLeftTV2.setText("扣除款项");
            orderPriceLeftTV3.setText("实际退款");
            orderActionIconIV.setImageResource(R.drawable.personalcenter_order_refund);
            orderActionRightIconIV.setVisibility(View.VISIBLE);

            Integer id = R.drawable.personalcenter_order_pictureconsulting;
            Glide.with(RefundOrderDetailActivity.this).load(id).error(R.drawable
                    .personalcenter_order_pictureconsulting).placeholder(R.drawable
                    .personalcenter_order_pictureconsulting).into(orderIconIV);

        }
    }

    private void getOrderInfo() {
        RaspberryCallback<ObjectResponse<Order>> callback = new RaspberryCallback<ObjectResponse<Order>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                ToastUtils.showToast("网络异常，请重试");
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<Order> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response
                            .getData()) {
                        order = response.getData();
                        showView();
                        return;
                    }
                }
                ToastUtils.showToast("查询订单失败，请重试");
                finish();
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.getOrderById(orderId, callback);

    }

    private void refundOrder() {
        Intent intent = new Intent(this, RefundActivity.class);
        intent.putExtra(RefundActivity.class.getSimpleName(), order.getCode());
        startActivityForResult(intent, REFUND);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_detail_action_ll:
                refundOrder();
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
                case REFUND:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}