package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DESUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.data.em.RelationType;
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

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.captrue_personal_msg_headphoto_iv)
    ImageView userIcon;
    @Bind(R.id.captrue_personal_msg_hospital_tv)
    TextView userHospital;
    @Bind(R.id.captrue_personal_msg_department_tv)
    TextView userDepartment;
    @Bind(R.id.captrue_personal_msg_name_tv)
    TextView userName;
    @Bind(R.id.captrue_personal_msg_level_tv)
    TextView userLevel;
    @Bind(R.id.qr_result_addto_tv)
    TextView addTV;

    private Toolbar toolbar;
    private Doctor doctor;
    private String recode;

    private static final int UPDATE_VIEW = 121;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_VIEW) {
                userName.setText(doctor.getName());
                userLevel.setText(doctor.getLevel().getMark());
                userDepartment.setText(doctor.getMedicalCategory().getName());
                userHospital.setText(doctor.getHospital().getHospitalName());
                Glide.with(ResultActivity.this).load(HttpBase.BASE_OSS_URL + doctor.getHeadPortrait()).placeholder(R
                        .drawable.main_headportrait_xlarge).error(R.drawable.main_headportrait_xlarge).transform(new
                        CircleTransform(ResultActivity.this)).into(userIcon);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.user_info);
        //获得解析的图片
        recode = getIntent().getStringExtra(ResultActivity.class.getSimpleName());
        if (TextUtils.isEmpty(recode)) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        if (!decode()) {
            ToastUtils.showToast("无法识别的数据");
            finish();
            return;
        }
        initListener();
        getDoctorDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(userIcon);
        Http.cancel(ResultActivity.class.getSimpleName());
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addTV.setOnClickListener(this);
    }

    private boolean decode() {
        if (TextUtils.isEmpty(recode)) {
            return false;
        }
        Logger.d(recode);
        String json = DESUtils.decodeUrl(recode);
        try {
            JSONObject object = JSON.parseObject(json);
            if (null == object) {
                return false;
            }
            long id = 0l;
            String type = null;
            String userName = null;
            if (object.containsKey(Preference.ID)) {
                id = object.getLongValue(Preference.ID);
            }
            if (object.containsKey(Preference.TYPE)) {
                type = object.getString(Preference.TYPE);
            }
            if (object.containsKey(MyApplication.USER_NAME)) {
                userName = object.getString(MyApplication.USER_NAME);
            }
            if (!TextUtils.isEmpty(type) && type.equals("doctor")) {
                doctor = new Doctor();
                doctor.setId(id);
                doctor.setUserName(userName);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
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

        HttpProtocol.getDoctorDetail(doctor.getId(), ResultActivity.class.getSimpleName(), callback);
    }

    /**
     * 添加关系
     */
    private void addDoctor() {

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
                        ToastUtils.showToast("成功添加为我的医生");
                        Intent intent = new Intent();
                        intent.putExtra(ResultActivity.class.getSimpleName(), doctor);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtils.showToast(getString(R.string.network_anomaly));
                    }
                } else {
                    ToastUtils.showToast(getString(R.string.network_anomaly));
                }
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.addRelation(RelationType.UD, doctor.getId(), callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_result_addto_tv:
                addDoctor();
                break;
        }
    }

}
