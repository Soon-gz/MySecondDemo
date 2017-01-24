package com.shkjs.patient.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/14.
 * <p>
 * 对话框形式医生详情
 */

public class DoctorsDetailDialogActivity extends BaseActivity implements View.OnClickListener {

    private final static String FINISH_DOCTOR = "com.shkjs.patient.finish_doctor";

    @Bind(com.shkjs.patient.R.id.doctor_icon_iv)
    ImageView doctorIconIV;
    @Bind(com.shkjs.patient.R.id.doctor_tag_iv)
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
    @Bind(R.id.doctor_excel_tv)
    TextView excelTV;
    @Bind(R.id.doctor_introduce_tv)
    TextView introduceTV;
    @Bind(R.id.doctors_tablayout)
    TabLayout tabLayout;
    @Bind(R.id.doctor_detail_ll)
    LinearLayout linearLayout;

    private ArrayList<Long> doctorIds;
    private long doctorId;
    private Doctor doctor;
    private ArrayList<Doctor> doctors;

    private IntentFilter filter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctors_detail_dialog);

        //设置为true点击区域外消失
        setFinishOnTouchOutside(true);

        doctorIds = (ArrayList<Long>) getIntent().getSerializableExtra(DoctorsDetailDialogActivity.class
                .getSimpleName());
        if (null == doctorIds || doctorIds.size() <= 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        //绑定控件
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            unregisterReceiver(receiver);
        }
        Glide.clear(doctorIconIV);
        Http.cancel(DoctorsDetailDialogActivity.class.getSimpleName());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        doctors = new ArrayList<>();
        for (int i = 0; i < doctorIds.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText("医生" + (i + 1)));
        }
        if (doctorIds.size() <= 2) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                doctorId = doctorIds.get(position);
                if (doctors.size() > 0) {
                    Doctor doctor = doctors.get(doctors.size() - 1);
                    if (null != doctor && doctor.getId().equals(doctorId)) {
                        showData(doctor);
                        return;
                    }
                }
                getDoctorDetail();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                doctorId = doctorIds.get(position);
                if (doctors.size() > 0) {
                    Doctor doctor = doctors.get(doctors.size() - 1);
                    if (null != doctor && doctor.getId().equals(doctorId)) {
                        showData(doctor);
                        return;
                    }
                }
                getDoctorDetail();
            }
        });

        //默认选择第一个医生
        tabLayout.getTabAt(0).select();

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
                        if (doctors.contains(doctor)) {
                            doctors.remove(doctor);
                            doctors.add(doctor);
                        } else {
                            doctors.add(doctor);
                        }
                        showData(doctor);
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
        callback.setCancelable(false);

        HttpProtocol.getDoctorDetail(doctorId, DoctorsDetailDialogActivity.class.getSimpleName(), callback);
    }

    private void showData(Doctor doctor) {
        doctorNameTV.setText(doctor.getName());
        doctorPlatformTV.setText(doctor.getPlatformLevel().getMark());
        doctorLevelTV.setText(doctor.getLevel().getMark());
        departmentTV.setText(doctor.getMedicalCategory().getName());
        hospitalTV.setText(doctor.getHospital().getHospitalName());
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_888888));
        SpannableString str = new SpannableString("擅长: " + doctor.getSkilled());
        str.setSpan(span, 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        excelTV.setText(str);
        str = new SpannableString("简介: " + doctor.getIntroduce());
        str.setSpan(span, 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        introduceTV.setText(str);
        Glide.with(DoctorsDetailDialogActivity.this).load(HttpBase.BASE_OSS_URL + doctor.getHeadPortrait())
                .placeholder(R.drawable.main_headportrait_xlarge).error(R.drawable.main_headportrait_xlarge)
                .transform(new CircleTransform(DoctorsDetailDialogActivity
                .this)).into(doctorIconIV);
        if (doctor.getTag().equals(DoctorTag.PROMOTION)) {
            Glide.with(DoctorsDetailDialogActivity.this).load(R.drawable.extension).placeholder(R.color.transparent)
                    .error(R.color.transparent).into(doctorTagIV);
        } else if (doctor.getTag().equals(DoctorTag.FREE)) {
            Glide.with(DoctorsDetailDialogActivity.this).load(R.drawable.free).placeholder(R.color.transparent).error
                    (R.color.transparent).into(doctorTagIV);
        } else {
            Glide.with(DoctorsDetailDialogActivity.this).load(R.color.transparent).placeholder(R.color.transparent)
                    .error(R.color.transparent).into(doctorTagIV);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10) || event.getX() >= linearLayout.getWidth() + 10 ||
                    event.getY() >= linearLayout.getHeight() + 10) {//如果点击位置在当前View外部则销毁当前视图,
                // 其中10与10为微调距离
                finish();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
