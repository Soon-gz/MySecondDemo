package com.shkjs.patient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.data.em.DoctorTag;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/14.
 * <p>
 * 对话框形式医生详情
 */

public class DoctorDetailDialogActivity extends BaseActivity implements View.OnClickListener {

    private final static String FINISH_DOCTOR = "com.shkjs.patient.finish_doctor";

    @Bind(R.id.doctor_icon_iv)
    ImageView doctorIconIV;
    @Bind(R.id.doctor_tag_iv)
    ImageView doctorTagIV;
    @Bind(R.id.doctor_name_tv)
    TextView doctorNameTV;
    @Bind(R.id.doctor_platform_level_tv)
    TextView doctorPlatformTV;
    @Bind(R.id.doctor_department_tv)
    TextView departmentTV;
    @Bind(R.id.doctor_level_tv)
    TextView doctorLevelTV;
    @Bind(R.id.doctor_hospital_tv)
    TextView hospitalTV;
    @Bind(R.id.content_tv)
    TextView contentTV;
    @Bind(R.id.doctor_excel_btn)
    Button excelBtn;
    @Bind(R.id.doctor_introduce_btn)
    Button introduceBtn;
    @Bind(R.id.doctor_detail_ll)
    LinearLayout linearLayout;

    private String userName;
    private long doctorId;
    private Doctor doctor;

    private IntentFilter filter;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_detail_dialog);

        //设置为true点击区域外消失
        setFinishOnTouchOutside(true);

        userName = getIntent().getStringExtra(Preference.DOCTOR_DETAIL);
        doctorId = getIntent().getLongExtra(DoctorDetailDialogActivity.class.getSimpleName(), -1);
        if (TextUtils.isEmpty(userName) && doctorId < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        //绑定控件
        ButterKnife.bind(this);

        initData();
        initListener();
        if (TextUtils.isEmpty(userName)) {
            getDoctorDetailById();
        } else {
            getDoctorDetailByUserName();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            unregisterReceiver(receiver);
        }
        Glide.clear(doctorIconIV);
        Http.cancel(DoctorDetailDialogActivity.class.getSimpleName());
    }

    /**
     * 初始化数据
     */
    private void initData() {
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        excelBtn.setOnClickListener(this);
        introduceBtn.setOnClickListener(this);

        filter = new IntentFilter();
        filter.addAction(FINISH_DOCTOR);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    /**
     * 通过userName获取医生详情
     */
    private void getDoctorDetailByUserName() {
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
                        doctorNameTV.setText(doctor.getName());
                        doctorPlatformTV.setText(doctor.getPlatformLevel().getMark());
                        doctorLevelTV.setText(doctor.getLevel().getMark());
                        departmentTV.setText(doctor.getCategoryName());
                        hospitalTV.setText(doctor.getHospitalName());
                        contentTV.setText(doctor.getSkilled());
                        excelBtn.setSelected(true);
                        introduceBtn.setSelected(false);
                        Glide.with(DoctorDetailDialogActivity.this).load(HttpBase.BASE_OSS_URL + doctor
                                .getHeadPortrait()).placeholder(R.drawable.main_headportrait_xlarge).error(R.drawable
                                .main_headportrait_xlarge).transform(new CircleTransform(DoctorDetailDialogActivity
                                .this)).into(doctorIconIV);
                        if (doctor.getTag().equals(DoctorTag.PROMOTION)) {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.drawable.extension).placeholder(R
                                    .color.transparent).error(R.color.transparent).into(doctorTagIV);
                        } else if (doctor.getTag().equals(DoctorTag.FREE)) {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.drawable.free).placeholder(R.color
                                    .transparent).error(R.color.transparent).into(doctorTagIV);
                        } else {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.color.transparent).placeholder(R.color
                                    .transparent).error(R.color.transparent).into(doctorTagIV);
                        }
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
        callback.setMainThread(true);
        callback.setCancelable(true);

        if (userName.contains(MyApplication.NIM_DOCTOR_NAME)) {
            userName = userName.replace(MyApplication.NIM_DOCTOR_NAME, "");
        }
        HttpProtocol.queryDoctorDetailByUserName(userName, DoctorDetailDialogActivity.class.getSimpleName(), callback);
    }

    /**
     * 通过Id获取医生详情
     */
    private void getDoctorDetailById() {
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
                        doctorNameTV.setText(doctor.getName());
                        doctorPlatformTV.setText(doctor.getPlatformLevel().getMark());
                        doctorLevelTV.setText(doctor.getLevel().getMark());
                        departmentTV.setText(doctor.getMedicalCategory().getName());
                        hospitalTV.setText(doctor.getHospital().getHospitalName());
                        contentTV.setText(doctor.getSkilled());
                        excelBtn.setSelected(true);
                        introduceBtn.setSelected(false);
                        Glide.with(DoctorDetailDialogActivity.this).load(HttpBase.BASE_OSS_URL + doctor
                                .getHeadPortrait()).placeholder(R.drawable.main_headportrait_xlarge).error(R.drawable
                                .main_headportrait_xlarge).transform(new CircleTransform(DoctorDetailDialogActivity
                                .this)).into(doctorIconIV);
                        if (doctor.getTag().equals(DoctorTag.PROMOTION)) {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.drawable.extension).placeholder(R.color
                                    .transparent).error(R.color.transparent).into(doctorTagIV);
                        } else if (doctor.getTag().equals(DoctorTag.FREE)) {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.drawable.free).placeholder(R.color.transparent)
                                    .error(R.color.transparent).into(doctorTagIV);
                        } else {
                            Glide.with(DoctorDetailDialogActivity.this).load(R.color.transparent).placeholder(R.color
                                    .transparent).error(R.color.transparent).into(doctorTagIV);
                        }
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
        callback.setMainThread(true);
        callback.setCancelable(true);

        HttpProtocol.getDoctorDetail(doctorId, DoctorDetailDialogActivity.class.getSimpleName(), callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doctor_excel_btn:
                contentTV.setText(doctor.getSkilled());
                excelBtn.setSelected(true);
                introduceBtn.setSelected(false);
                break;
            case R.id.doctor_introduce_btn:
                contentTV.setText(doctor.getIntroduce());
                excelBtn.setSelected(false);
                introduceBtn.setSelected(true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10) || event.getX() >= linearLayout.getWidth() + 10 ||
                    event.getY() >= getWindow().getDecorView().getHeight() + 20) {//如果点击位置在当前View外部则销毁当前视图,
                // 其中10与20为微调距离
                finish();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
