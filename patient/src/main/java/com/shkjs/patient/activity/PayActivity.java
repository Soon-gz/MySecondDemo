package com.shkjs.patient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.PayPwdEditText;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Account;
import com.shkjs.patient.bean.AccountPassword;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.PlatformDiscount;
import com.shkjs.patient.data.em.DiscountType;
import com.shkjs.patient.data.em.DoctorTag;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.PayType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.AliPayUtils;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.shkjs.patient.util.SpliceUtils.splicePrice;

/**
 * Created by xiaohu on 2016/10/9.
 * <p>
 * 支付页面
 */

public class PayActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int BALANCE = 121;//余额支付
    private static final int ALIPAY = 122;//支付宝支付
    private static final int GROUP_BALANCE = 123;//家庭组余额支付
    private static final int PAY_SUCCESS = 124;//支付成功
    private static final int PAY_FAIL = 125;//支付失败
    private static final int PAY_NO_PWD = 126;//没有支付密码
    private static final int PAY_FAIL_FREQUENCY = 127;//密码错误次数超限
    private static final int DOCTOR_DETAIL = 128;//医生详情
    private static final int ORDER_INFO = 129;//订单详情
    private static final int DISCOUNT = 130;//优惠

    private static final String ORDERINFO = "OrderInfo";

    @Bind(R.id.order_countdown_tv)
    TextView countdownTV;//订单倒计时
    @Bind(R.id.order_nember_tv)
    TextView numberTV;//订单编号
    @Bind(R.id.order_price_tv)
    TextView priceTV;//订单金额
    @Bind(R.id.order_time_title_tv)
    TextView timeTitleTV;//订单时间title
    @Bind(R.id.order_time_content_tv)
    TextView timeContentTV;//订单时间content
    @Bind(R.id.doctor_detail_ll)
    LinearLayout doctorDetailLL;//医生详情
    @Bind(R.id.doctor_icon_iv)
    ImageView doctorIconIV;//医生头像
    @Bind(R.id.doctor_name_tv)
    TextView doctorNameTV;//医生名字
    @Bind(R.id.doctor_level_tv)
    TextView doctorLevelTV;//医生级别
    @Bind(R.id.doctor_hospital_tv)
    TextView doctorHospitalTV;//医生医院
    @Bind(R.id.doctor_department_tv)
    TextView doctorDepartmentTV;//医生科室
    @Bind(R.id.balance_tv)
    TextView balanceTV;//余额
    @Bind(R.id.balance_discount_tv)
    TextView balanceDiscountTV;//余额优惠
    @Bind(R.id.balance_cb)
    CheckBox balanceCB;//余额
    @Bind(R.id.alipay_cb)
    CheckBox alipayCB;//支付宝
    @Bind(R.id.home_group_ll)
    LinearLayout homeGroupLL;//家庭组
    @Bind(R.id.home_group_discount_tv)
    TextView homeGroupDiscountTV;//家庭组优惠
    @Bind(R.id.home_group_tv)
    TextView homeGroupBalanceTV;//家庭组余额
    @Bind(R.id.home_group_cb)
    CheckBox homeGroupCB;//家庭组
    @Bind(R.id.agreement_tv)
    TextView agreementTV;//用户协议
    @Bind(R.id.submit_btn)
    Button submitBtn;//确认支付

    private Toolbar toolbar;
    private AlertDialog dialog;

    private int payType = BALANCE;//默认余额支付

    private OrderInfo orderInfo;
    private long balance = 0;
    private long groupBalance = 0;
    private String orderStr;

    private PlatformDiscount discount;
    private long realMoney;

    //支付宝支付结果监听
    private AliPayUtils.PayResultListener listener = new AliPayUtils.PayResultListener() {

        @Override
        public void onSuccess() {
            createPayResultDialog(PAY_SUCCESS);
        }

        @Override
        public void onFailed(String result) {
            ToastUtils.showToast2(result, Gravity.CENTER);
            createPayResultDialog(PAY_FAIL);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BALANCE:
                    balanceTV.setText(SpliceUtils.formatBalance2(balance));
                    break;
                case GROUP_BALANCE:
                    if (groupBalance > 0) {
                        homeGroupLL.setVisibility(View.VISIBLE);
                        homeGroupBalanceTV.setText(SpliceUtils.formatBalance2(groupBalance));
                    } else {
                        homeGroupLL.setVisibility(View.GONE);
                    }
                    break;
                case DOCTOR_DETAIL:
                    initData();
                    break;
                case ORDER_INFO:
                    if (orderInfo.isMuiltVideo()) {
                        initData();
                    } else {
                        if (orderInfo.getDoctors().size() > 1) {
                            initData();
                        } else {
                            getDoctorDetail();
                        }
                    }
                    break;
                case DISCOUNT:
                    if (null != discount) {
                        balanceDiscountTV.setText(SpliceUtils.getDiscount(discount));
                        homeGroupDiscountTV.setText(SpliceUtils.getDiscount(discount));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static void start(Context context, OrderInfo orderInfo) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(ORDERINFO, orderInfo);
        context.startActivity(intent);
    }

    public static void launch(Context context, OrderInfo orderInfo) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ORDERINFO, orderInfo);
        context.startActivity(intent);
    }

    public static void startForResult(Activity context, OrderInfo orderInfo, int requestCode) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(ORDERINFO, orderInfo);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderInfo = (OrderInfo) getIntent().getSerializableExtra(ORDERINFO);
        if (null == orderInfo) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        setContentView(R.layout.activity_pay);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.sure_pat);

        //初始化监听
        initListener();
        if (TextUtils.isEmpty(orderInfo.getOrder().getCode()) || TextUtils.isEmpty(orderInfo.getDoctors().get(0)
                .getHeadPortrait())) {
            getOrderInfo();
        } else {
            if (orderInfo.isMuiltVideo()) {
                getOrderInfo();
            } else {
                //初始化数据
                initData();
            }
        }

        //获取优惠
        //        getUserDiscount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取账户余额
        getBalance();
        //获取家庭组余额
        getGroupMasterBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(doctorIconIV);
    }

    private void initData() {
        if (orderInfo.getOrder().getMoney() <= 0) {
            ToastUtils.showToast("您已预约成功");
            WriteAskQuestionsActivity.start(PayActivity.this, orderInfo.getOrder(), orderInfo.getDoctors().get(0));
            setResult();
            finish();
        }
        setCountdown();
        numberTV.setText(orderInfo.getOrder().getCode());
        priceTV.setText(splicePrice(orderInfo.getOrder().getMoney()));
        if (orderInfo.getOrderInfoType().getMark().equals(OrderInfoType.PICTURE.getMark())) {
            timeTitleTV.setText("咨询时间:");
            timeContentTV.setText("24小时之内均可咨询");
        } else {
            timeTitleTV.setText("预约时间:");
            timeContentTV.setText(orderInfo.getTime());
        }

        if (orderInfo.getDoctors().size() > 1) {
            Glide.with(this).load(R.drawable.main_consultation_groupconsultation).placeholder(R.drawable
                    .main_consultation_groupconsultation).error(R.drawable.main_consultation_groupconsultation)
                    .transform(new CircleTransform(this)).dontAnimate().thumbnail(0.1f).into(doctorIconIV);
            String doctorName = "";
            for (Doctor doctor : orderInfo.getDoctors()) {
                doctorName = doctorName + doctor.getName() + " ";
            }
            doctorNameTV.setText(doctorName);
        } else {
            Glide.with(this).load(HttpBase.BASE_OSS_URL + orderInfo.getDoctors().get(0).getHeadPortrait())
                    .placeholder(R.drawable.actionbar_headportrait_small).error(R.drawable
                    .actionbar_headportrait_small).transform(new CircleTransform(this)).dontAnimate().thumbnail(0.1f)
                    .into(doctorIconIV);

            doctorNameTV.setText(orderInfo.getDoctors().get(0).getName());
        }
        if (orderInfo.getDoctors().get(0).getTag().equals(DoctorTag.NORMAL)) {
            balanceDiscountTV.setVisibility(View.VISIBLE);
            homeGroupDiscountTV.setVisibility(View.VISIBLE);
        } else {
            balanceDiscountTV.setVisibility(View.GONE);
            homeGroupDiscountTV.setVisibility(View.GONE);
        }
        if (null == orderInfo.getDoctors().get(0).getLevel()) {
            doctorLevelTV.setText("");
        } else {
            doctorLevelTV.setText(orderInfo.getDoctors().get(0).getLevel().getMark());
        }
        if (null == orderInfo.getDoctors().get(0).getHospital()) {
            doctorHospitalTV.setText(orderInfo.getDoctors().get(0).getHospitalName());
        } else {
            doctorHospitalTV.setText(orderInfo.getDoctors().get(0).getHospital().getHospitalName());
        }
        if (null == orderInfo.getDoctors().get(0).getMedicalCategory()) {
            doctorDepartmentTV.setText(orderInfo.getDoctors().get(0).getCategoryName());
        } else {
            doctorDepartmentTV.setText(orderInfo.getDoctors().get(0).getMedicalCategory().getName());
        }
        balanceCB.setChecked(true);
        if (null == discount) {
            getMaxDiscountByOrderId();
        }
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        balanceCB.setOnCheckedChangeListener(this);
        alipayCB.setOnCheckedChangeListener(this);
        homeGroupCB.setOnCheckedChangeListener(this);

        doctorDetailLL.setOnClickListener(this);
        agreementTV.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    /**
     * 获取用户账户余额
     */
    private void getBalance() {
        RaspberryCallback<ObjectResponse<Account>> callback = new RaspberryCallback<ObjectResponse<Account>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Account> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        balance = response.getData().getBalance();
                        handler.sendEmptyMessage(BALANCE);
                        return;
                    }
                }
                getBalance();
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getAccountDetail(callback);
    }

    /**
     * 通过ID查询order
     */
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
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        orderInfo.setOrder(response.getData());
                        handler.sendEmptyMessage(ORDER_INFO);
                        return;
                    }
                }
                ToastUtils.showToast("查询订单失败，请重试");
                finish();
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);
        if (TextUtils.isEmpty(orderInfo.getOrder().getCode())) {
            HttpProtocol.getOrderById(orderInfo.getOrder().getId(), callback);
        } else {
            HttpProtocol.getOrderByCode(orderInfo.getOrder().getCode(), callback);
        }

    }


    /**
     * 通过ID获取医生详情
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
                        orderInfo.getDoctors().clear();
                        orderInfo.getDoctors().add(response.getData());
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

        HttpProtocol.getDoctorDetail(orderInfo.getDoctors().get(0).getId(), PayActivity.class.getSimpleName(),
                callback);
    }

    /**
     * 获取用户家庭组账户余额
     */
    private void getGroupMasterBalance() {
        RaspberryCallback<ObjectResponse<Account>> callback = new RaspberryCallback<ObjectResponse<Account>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Account> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        if (null != response.getData()) {
                            groupBalance = response.getData().getBalance();
                        }
                        handler.sendEmptyMessage(GROUP_BALANCE);
                        return;
                    }
                }
                getGroupMasterBalance();
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getGroupMasterDetail(callback);
    }

    /**
     * 设置订单倒计时
     * <p>
     * //TODO 暂不实现倒计时
     */
    private void setCountdown() {
        SpannableString string = new SpannableString("请您在30分钟内完成支付，否则订单会自动取消!");
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_e84618));
        string.setSpan(span, 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        countdownTV.setText(string);
    }

    /**
     * 创建支付宝订单信息
     */
    private void createAlipayOreder(final boolean pay) {

        orderStr = "";

        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        orderStr = response.getData();
                        if (pay) {
                            AliPayUtils.alipay(PayActivity.this, orderStr, listener);
                        }
                    } else {
                    }
                } else {
                }
            }
        };

        callback.setMainThread(false);

        HttpProtocol.getOrderSignByCode(orderInfo.getOrder().getCode(), callback);
    }

    private void payByAccount(PayType type, String password) {
        RaspberryCallback<ObjectResponse<AccountPassword>> callback = new
                RaspberryCallback<ObjectResponse<AccountPassword>>() {
            @Override
            public void onSuccess(ObjectResponse<AccountPassword> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        createPayResultDialog(PAY_SUCCESS);
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        if (response.getData().isEmpty()) {
                            createPayResultDialog(PAY_NO_PWD);
                            return;
                        } else {
                            if (response.getData().isPasswordError()) {
                                createPayPwdErrorDialog(response.getData().getRemainTimes());
                                return;
                            }
                            if (response.getData().getRemainTimes() <= 0) {
                                createPayResultDialog(PAY_FAIL_FREQUENCY);
                                return;
                            }
                        }
                    }
                }
                createPayResultDialog(PAY_FAIL);
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.payByAccount(orderInfo.getOrder().getCode(), type, MD5Utils.encodeMD52(password), callback);
    }

    /**
     * 查询用户最优优惠
     */
    private void getUserDiscount() {
        RaspberryCallback<ObjectResponse<PlatformDiscount>> callback = new
                RaspberryCallback<ObjectResponse<PlatformDiscount>>() {
            @Override
            public void onSuccess(ObjectResponse<PlatformDiscount> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        discount = response.getData();
                        handler.sendEmptyMessage(DISCOUNT);
                        return;
                    }
                }
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryUserDiscount(callback);
    }

    /**
     * 查询用户最优优惠
     */
    private void getMaxDiscountByOrderId() {
        RaspberryCallback<ObjectResponse<PlatformDiscount>> callback = new
                RaspberryCallback<ObjectResponse<PlatformDiscount>>() {
            @Override
            public void onSuccess(ObjectResponse<PlatformDiscount> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        discount = response.getData();
                        handler.sendEmptyMessage(DISCOUNT);
                        return;
                    }
                }
            }
        };

        callback.setMainThread(false);

        HttpProtocol.getMaxDiscountByOrderId(orderInfo.getOrder().getId(), callback);
    }

    /**
     * 创建余额订单信息
     */
    private void createBalanceOreder() {
    }

    /**
     * 创建家庭组订单信息
     */
    private void createGroupBalanceOreder() {
    }

    /**
     * 创建支付dialog
     *
     * @param payType 支付类型
     * @param balance 余额
     */
    private void createPayDialog(final int payType, final long balance) {
        dialog = new RoundDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay, null);

        ImageView cancelIV = (ImageView) view.findViewById(R.id.cancel_iv);
        cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView oldPriceTV = (TextView) view.findViewById(R.id.old_price_tv);
        //        oldPriceTV.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        oldPriceTV.setText(SpliceUtils.splicePrice(orderInfo.getOrder().getMoney()));

        TextView priceTV = (TextView) view.findViewById(R.id.price_tv);

        if (null != discount && orderInfo.getDoctors().get(0).getTag().equals(DoctorTag.NORMAL)) {
            if (discount.getType().equals(DiscountType.FIX)) {
                realMoney = orderInfo.getOrder().getMoney() - discount.getDelta();
                priceTV.setText(SpliceUtils.splicePrice(realMoney));
            } else if (discount.getType().equals(DiscountType.PERCENT)) {
                realMoney = (long) (orderInfo.getOrder().getMoney() * ((float) discount.getDelta() / 100));
                priceTV.setText(SpliceUtils.splicePrice(realMoney));
                //                priceTV.setText(SpliceUtils.splicePrice((long) (orderInfo.getOrder().getMoney()
                // * (
                // (float) discount
                //                        .getDelta() / 100))));
            }
        } else {
            realMoney = orderInfo.getOrder().getMoney();
            priceTV.setText(SpliceUtils.splicePrice(realMoney));
        }

        TextView balanceTV = (TextView) view.findViewById(R.id.balance_tv);
        balanceTV.setText(SpliceUtils.formatBalance2(balance));

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.balance_ll);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType == BALANCE) {
                    //充值
                    Intent intent = new Intent(PayActivity.this, RechargeActivity.class);
                    intent.putExtra(RechargeActivity.class.getSimpleName(), balance);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });

        final PayPwdEditText payPwdEditText = (PayPwdEditText) view.findViewById(R.id.password_ppet);
        payPwdEditText.initStyle(R.drawable.shape_background, 6, 1f, R.color.green_d2d2d2, R.color.black_231815, 20);
        payPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener() {
            @Override
            public void onFinish(String str) {
                if (payType == BALANCE) {
                    if (balance < realMoney) {
                        ToastUtils.showToast("余额不足，请充值");
                        payPwdEditText.clearText();
                    } else {
                        payByAccount(PayType.BALANCE, str);
                        dialog.dismiss();
                    }
                } else {
                    if (groupBalance < realMoney) {
                        ToastUtils.showToast("家庭组余额不足，请选择其他支付方式");
                        dialog.dismiss();
                    } else {
                        payByAccount(PayType.FAMILYBALANCE, str);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.setView(view);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 20);
        dialog.getWindow().setAttributes(params);

    }

    /**
     * 创建支付密码错误dialog
     *
     * @param number 错误次数
     */
    private void createPayPwdErrorDialog(int number) {
        dialog = new RoundDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay_password_error, null);

        TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
        //        if (number < 5) {
        //            contentTV.setText("支付密码输入错误，请重试");
        //        } else {
        //            contentTV.setText("支付密码输入错误已达5次，请10分钟后重试或找回密码");
        //        }
        contentTV.setText("支付密码输入错误，您还能重试" + number + "次");
        Button retryBtn = (Button) view.findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (payType == BALANCE) {
                    createPayDialog(payType, balance);
                } else if (payType == GROUP_BALANCE) {
                    createPayDialog(payType, groupBalance);
                }
            }
        });

        Button retrieveBtn = (Button) view.findViewById(R.id.retrieve_password_btn);
        retrieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 找回密码
                Intent intent = new Intent(PayActivity.this, RetrievePayPwdActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 20);
        dialog.getWindow().setAttributes(params);

    }

    /**
     * 创建支付结果dialog
     *
     * @param isSucceed 是否成功
     */
    private void createPayResultDialog(int isSucceed) {
        dialog = new RoundDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pay_result, null);

        final ImageView imageView = (ImageView) view.findViewById(R.id.result_iv);
        TextView textView = (TextView) view.findViewById(R.id.result_tv);
        Button button = (Button) view.findViewById(R.id.result_btn);
        switch (isSucceed) {
            case PAY_SUCCESS:
                dialog.setCancelable(false);
                Glide.with(this).load(R.drawable.main_common_success).transform(new CircleTransform(this))
                        .dontAnimate().thumbnail(0.1f).into(imageView);
                textView.setText("支付成功");
                button.setText("填写问诊表");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //                    Intent intent = new Intent(PayActivity.this, WriteAskQuestionsActivity
                        // .class);
                        //                    startActivity(intent);
                        WriteAskQuestionsActivity.start(PayActivity.this, orderInfo.getOrder(), orderInfo.getDoctors
                                ().get(0));
                        dialog.dismiss();
                        setResult();
                        finish();
                    }
                });
                break;
            case PAY_FAIL:
                Glide.with(this).load(R.drawable.main_common_failure).transform(new CircleTransform(this))
                        .dontAnimate().thumbnail(0.1f).into(imageView);
                textView.setText("支付失败");
                button.setText("返回支付页面");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case PAY_FAIL_FREQUENCY:
                Glide.with(this).load(R.drawable.main_common_failure).transform(new CircleTransform(this))
                        .dontAnimate().thumbnail(0.1f).into(imageView);
                textView.setText("支付失败,密码错误次数超限");
                button.setText("返回支付页面");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case PAY_NO_PWD:
                imageView.setVisibility(View.GONE);
                view.setPadding(view.getPaddingLeft(), DisplayUtils.dip2px(PayActivity.this, 20), view
                        .getPaddingRight(), DisplayUtils.dip2px(PayActivity.this, 20));
                textView.setText("您还没有设置支付密码，快去设置吧");
                button.setText("设置支付密码");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PayActivity.this, SettingPayPwdActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }

        dialog.setView(view);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 20);
        dialog.getWindow().setAttributes(params);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Glide.clear(imageView);
            }
        });

    }

    /**
     * 查看医生详情
     *
     * @param doctorId 医生ID
     */
    private void showDoctorDetail(long doctorId) {
        Intent intent = new Intent(PayActivity.this, DoctorDetailDialogActivity.class);
        intent.putExtra(DoctorDetailDialogActivity.class.getSimpleName(), doctorId);
        startActivity(intent);
    }

    /**
     * 查看医生详情
     *
     * @param doctorIds 医生ID
     */
    private void showDoctorDetail(ArrayList<Long> doctorIds) {
        Intent intent = new Intent(PayActivity.this, DoctorsDetailDialogActivity.class);
        intent.putExtra(DoctorsDetailDialogActivity.class.getSimpleName(), doctorIds);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agreement_tv:
                Intent intent = new Intent(PayActivity.this, UserAgreementActivity.class);
                startActivity(intent);
                break;
            case R.id.submit_btn:
                if (!balanceCB.isChecked() && !alipayCB.isChecked() && !homeGroupCB.isChecked()) {
                    ToastUtils.showToast("请选择支付方式");
                    return;
                }
                switch (payType) {
                    case ALIPAY:
                        if (!TextUtils.isEmpty(orderStr)) {
                            AliPayUtils.alipay(PayActivity.this, orderStr, listener);
                        } else {
                            createAlipayOreder(true);
                        }
                        break;
                    case BALANCE:
                        createPayDialog(payType, balance);
                        break;
                    case GROUP_BALANCE:
                        createPayDialog(payType, groupBalance);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.doctor_detail_ll:
                if (orderInfo.getDoctors().size() > 1) {
                    ArrayList<Long> doctorIds = new ArrayList<>();
                    for (Doctor doctor : orderInfo.getDoctors()) {
                        doctorIds.add(doctor.getId());
                    }
                    showDoctorDetail(doctorIds);
                } else {
                    showDoctorDetail(orderInfo.getDoctors().get(0).getId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            switch (buttonView.getId()) {
                case R.id.balance_cb:
                    payType = BALANCE;
                    alipayCB.setChecked(false);
                    homeGroupCB.setChecked(false);
                    createBalanceOreder();
                    break;
                case R.id.alipay_cb:
                    payType = ALIPAY;
                    balanceCB.setChecked(false);
                    homeGroupCB.setChecked(false);
                    createAlipayOreder(false);
                    break;
                case R.id.home_group_cb:
                    payType = GROUP_BALANCE;
                    balanceCB.setChecked(false);
                    alipayCB.setChecked(false);
                    createGroupBalanceOreder();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置结果
     */
    private void setResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }
}
