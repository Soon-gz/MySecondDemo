package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.BasePopupWindow;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.UserRelation;
import com.shkjs.patient.data.em.DoctorTag;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.RelationType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.shkjs.patient.view.QRNormalPopup;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/25.
 */
public class DoctorDetailActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.doctor_icon_iv)
    ImageView doctorIconIV;//头像
    @Bind(R.id.doctor_name_tv)
    TextView doctorNameTV;//名字
    @Bind(R.id.doctor_vip_icon_iv)
    ImageView doctorPlatformLevelIV;//医生平台等级
    @Bind(R.id.doctor_tag_iv)
    ImageView doctorTagIV;//医生标签
    @Bind(R.id.doctor_department_tv)
    TextView departmentTV;//科室
    @Bind(R.id.doctor_level_tv)
    TextView doctorLevelTV;//医生等级
    @Bind(R.id.doctor_hospital_tv)
    TextView hospitalTV;//医院
    @Bind(R.id.doctor_follow_cb)
    CheckBox followCB;//是否关注
    @Bind(R.id.doctor_follow_tv)
    TextView followTV;//是否关注
    @Bind(R.id.doctor_price)
    TextView picturePriceTV;//图文咨询价格
    @Bind(R.id.doctor_price_video)
    TextView videoPriceTV;//视频咨询价格
    @Bind(R.id.doctor_excel_tv)
    TextView excelTV;//擅长
    @Bind(R.id.doctor_introduce_tv)
    TextView synopsisTV;//简介
    @Bind(R.id.doctor_picture_consult)
    Button pictureBtn;//图文咨询
    @Bind(R.id.doctor_video_order)
    Button videoBtn;//视频预约

    private Toolbar toolbar;

    private long doctorId;
    private String doctorName;
    private Doctor doctor;

    private boolean isUpdateRelation = false;
    private boolean isRelation = true;

    private static final int UPDATE_VIEW = 121;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    doctorNameTV.setText(doctor.getName());
                    doctorPlatformLevelIV.setVisibility(View.GONE);
                    switch (doctor.getPlatformLevel()) {
                        case AUTHORITY:
                            doctorPlatformLevelIV.setVisibility(View.VISIBLE);
                            doctorPlatformLevelIV.setImageResource(R.drawable.main_doctordetails_authority);
                            break;
                        case CERTIFICATION:
                            doctorPlatformLevelIV.setVisibility(View.VISIBLE);
                            doctorPlatformLevelIV.setImageResource(R.drawable.main_doctordetails_certification);
                            break;
                        case NOTPASS:
                            break;
                        case CERTIFICATIONING:
                            break;
                        case UNCERTIFICATION:
                            break;
                        default:
                            break;
                    }
                    if (doctor.getTag().equals(DoctorTag.PROMOTION)) {
                        Glide.with(DoctorDetailActivity.this).load(R.drawable.extension).placeholder(R.color
                                .transparent).error(R.color.transparent).into(doctorTagIV);
                    } else if (doctor.getTag().equals(DoctorTag.FREE)) {
                        Glide.with(DoctorDetailActivity.this).load(R.drawable.free).placeholder(R.color.transparent)
                                .error(R.color.transparent).into(doctorTagIV);
                    } else {
                        Glide.with(DoctorDetailActivity.this).load(R.color.transparent).placeholder(R.color
                                .transparent).error(R.color.transparent).into(doctorTagIV);
                    }
                    doctorLevelTV.setText(doctor.getLevel().getMark());
                    if (TextUtils.isEmpty(doctorName)) {
                        departmentTV.setText(doctor.getMedicalCategory().getName());
                        hospitalTV.setText(doctor.getHospital().getHospitalName());
                    } else {
                        departmentTV.setText(doctor.getCategoryName());
                        hospitalTV.setText(doctor.getHospitalName());
                    }
                    picturePriceTV.setText(SpliceUtils.formatBalance2(doctor.getAskHospitalFee()));
                    videoPriceTV.setText(SpliceUtils.formatBalance2(doctor.getViewHospitalFee()));
                    excelTV.setText(doctor.getSkilled());
                    synopsisTV.setText(doctor.getIntroduce());
                    Glide.with(DoctorDetailActivity.this).load(HttpBase.BASE_OSS_URL + doctor.getHeadPortrait())
                            .placeholder(R.drawable.main_headportrait_xlarge).error(R.drawable
                            .main_headportrait_xlarge).transform(new CircleTransform(DoctorDetailActivity.this)).into
                            (doctorIconIV);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_detail);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.doctor_detail);

        initData();
        initListener();
        getDoctorDetail();
        getFollowInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(doctorIconIV);
        Http.cancel(DoctorDetailActivity.class.getSimpleName());
    }

    private void initData() {
        doctorId = getIntent().getLongExtra(Preference.DOCTOR_DETAIL, -1);
        doctorName = getIntent().getStringExtra(DoctorDetailActivity.class.getSimpleName());
        if (doctorId < 0 && TextUtils.isEmpty(doctorName)) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        setToolbarTitle("");
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult();
                finish();
            }
        });

        pictureBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
        doctorIconIV.setOnClickListener(this);

        followCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isUpdateRelation) {
                    return;
                }
                if (isChecked) {
                    isRelation = true;
                    addRelation();
                    followCB.setText("已关注");        //曾黎涛修改--此处为添加
                } else {
                    isRelation = false;
                    removeRelation();
                    followCB.setText("关注我");         //曾黎涛修改--此处为添加
                }
            }
        });
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
                        doctor = response.getData();
                        handler.sendEmptyMessage(UPDATE_VIEW);
                        return;
                    }
                }
                ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                finish();
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);
        if (TextUtils.isEmpty(doctorName)) {
            HttpProtocol.getDoctorDetail(doctorId, DoctorDetailActivity.class.getSimpleName(), callback);
        } else {
            HttpProtocol.queryDoctorDetailByUserName(doctorName, DoctorDetailActivity.class.getSimpleName(), callback);
        }
    }

    /**
     * 获取与医生的关系
     */
    private void getFollowInfo() {
        RaspberryCallback<ObjectResponse<UserRelation>> callback = new
                RaspberryCallback<ObjectResponse<UserRelation>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<UserRelation> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        followCB.setChecked(true);
                        followCB.setText("已关注");
                        return;
                    }
                }
                followCB.setChecked(false);
                followCB.setText("关注我");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isUpdateRelation = true;
            }
        };

        callback.setMainThread(true);

        HttpProtocol.queryRelation(RelationType.UD, doctorId, callback);
    }

    /**
     * 关注
     */
    private void addRelation() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        followCB.setChecked(true);
                    }
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.addRelation(RelationType.UD, doctor.getId(), callback);
    }

    /**
     * 取消关注
     */
    private void removeRelation() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        followCB.setChecked(false);
                    }
                }
            }
        };

        callback.setMainThread(true);

        HttpProtocol.removeRelation(RelationType.UD, doctor.getId(), callback);
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
        callback.setContext(DoctorDetailActivity.this);
        callback.setMainThread(false);

        HttpProtocol.orderPicture(doctor.getId(), callback);
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

                        PayActivity.start(DoctorDetailActivity.this, orderInfo);

                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                } else {
                    ToastUtils.showToast("预约失败，请稍后重试");
                }
            }
        };

        callback.setContext(DoctorDetailActivity.this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doctor_picture_consult:
                //                orderPicture(doctor);
                SubmitPictureOrderActivity.start(DoctorDetailActivity.this, doctor);
                break;
            case R.id.doctor_video_order:
                OrderTimeActivity.start(DoctorDetailActivity.this, doctor);
                break;
            case R.id.doctor_icon_iv:
                final QRNormalPopup normalPopup = new QRNormalPopup(DoctorDetailActivity.this);
                normalPopup.getTextView().setVisibility(View.GONE);
                Glide.with(DoctorDetailActivity.this).load(HttpBase.BASE_OSS_URL + doctor.getHeadPortrait())
                        .transform(new CircleTransform(DoctorDetailActivity.this)).into(normalPopup.getImageView());
                normalPopup.showPopupWindow();
                normalPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Glide.clear(normalPopup.getImageView());
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    private void setResult() {
        if (!isRelation) {
            Intent intent = new Intent();
            intent.putExtra(DoctorDetailActivity.class.getSimpleName(), doctor);
            intent.putExtra("isRelation", isRelation);
            setResult(RESULT_OK, intent);
        }
    }
}
