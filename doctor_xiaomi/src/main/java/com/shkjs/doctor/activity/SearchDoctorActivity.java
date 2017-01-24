package com.shkjs.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.module.VCardAttachment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.CreateAVPatientBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.nim.chat.session.SessionHelper;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class SearchDoctorActivity extends BaseActivity {

    @Bind(R.id.search_result_rv)
    RelativeLayout search_result_rv;
    @Bind(R.id.search_doctor_name_tv)
    TextView search_doctor_name_tv;
    @Bind(R.id.search_doctor_iv)
    CircleImageView search_doctor_iv;
    @Bind(R.id.search_doctor_department)
    TextView search_doctor_department;
    @Bind(R.id.search_doctor_hospital_tv)
    TextView search_doctor_hospital_tv;
    @Bind(R.id.search_doctor_title_tv)
    TextView search_doctor_title_tv;
    @Bind(R.id.search_doctor_edit)
    EditText search_doctor_edit;


    private String name = "";
    private RaspberryCallback<ObjectResponse<DoctorBean>>getDoctorCallback;
    private RaspberryCallback<ObjectResponse<CreateAVPatientBean>>getPatientCallback;
    private RaspberryCallback<BaseResponse>add2MyPatientsCallback;


    private  VCardAttachment attachment;
    private boolean isFromMyPatientsActivity = false;
    private CreateAVPatientBean createAVPatientBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getStringExtra("account")!=null){
            name = getIntent().getStringExtra("account");
            if ("MyPatientsActivity".equals(name)){
                isFromMyPatientsActivity = true;
            }
        }

        initListener();
    }

    private void initListener() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) search_doctor_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        },400);
        getDoctorCallback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData() != null){
                        search_result_rv.setVisibility(View.VISIBLE);
                        Log.i("tag00","数据："+response.getData().toString());
                        Glide.with(SearchDoctorActivity.this).load(HttpBase.IMGURL + response.getData().getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(search_doctor_iv);
                        search_doctor_title_tv.setText(response.getData().getCategoryName());
                        search_doctor_department.setText(Preference.getLevelValue(response.getData().getLevel()));
                        search_doctor_hospital_tv.setText(response.getData().getHospitalName());
                        search_doctor_name_tv.setText(response.getData().getName());
                        attachment = new VCardAttachment(response.getData().getId(),response.getData().getName(), HttpBase.IMGURL + response.getData().getHeadPortrait());
                    }else {
                        ToastUtils.showToast("不存在该用户，请确保手机号已注册医星汇平台。");
                    }
                }else {
                    ToastUtils.showToast("不存在该用户，请确保手机号已注册医星汇平台。");
                }
            }
        };
        AudioHelper.initCallBack(getDoctorCallback,this,true);

        getPatientCallback = new RaspberryCallback<ObjectResponse<CreateAVPatientBean>>() {
            @Override
            public void onSuccess(ObjectResponse<CreateAVPatientBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData() != null){
                        search_result_rv.setVisibility(View.VISIBLE);
                        createAVPatientBean = response.getData();
                        Log.i("tag00","数据："+response.getData().toString());
                        Glide.with(SearchDoctorActivity.this).load(HttpBase.IMGURL + response.getData().getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(search_doctor_iv);
                        AudioHelper.setAgeWithDefault(search_doctor_title_tv,response.getData().getBirthday());
                        AudioHelper.setSexText(search_doctor_department,response.getData().getSex());
                        search_doctor_hospital_tv.setVisibility(View.GONE);
                        AudioHelper.setNameWithDefault(search_doctor_name_tv,response.getData().getName(),response.getData().getNickName());
                    }else {
                        ToastUtils.showToast("不存在该用户，请确保手机号已注册医星汇平台。");
                    }
                }
            }
        };
        AudioHelper.initCallBack(getPatientCallback,this,true);

        add2MyPatientsCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    startActivity(new Intent(SearchDoctorActivity.this,MyPatientsActivity.class));
                    finish();
                }
            }
        };
        AudioHelper.initCallBack(add2MyPatientsCallback,this,true);

    }

    @OnClick({R.id.search_doctor_return_ibtn,R.id.search_doctor_searchbtn,R.id.search_doctor_add_delete})
    public void searchOnClick(View view){
        switch (view.getId()){
            case R.id.search_doctor_return_ibtn:
                finish();
                break;
            case R.id.search_doctor_searchbtn:
                if (!StringUtil.isEmpty(search_doctor_edit.getText().toString())){
                    if (isFromMyPatientsActivity){
                        HttpProtocol.getPatientByUserName(getPatientCallback,search_doctor_edit.getText().toString().trim());
                    }else {
                        HttpProtocol.getDoctorInfoFromName(getDoctorCallback,search_doctor_edit.getText().toString().trim());
                    }
                }else {
                    ToastUtils.showToast("请填入电话号码！");
                }
                break;
            case R.id.search_doctor_add_delete:
                if (isFromMyPatientsActivity){
                    HttpProtocol.postAddMypatients(add2MyPatientsCallback,createAVPatientBean.getId()+"",SearchDoctorActivity.this);
                }else {
                    if (attachment != null){
                        IMMessage message = MessageBuilder.createCustomMessage(name, SessionTypeEnum.P2P, attachment);
                        NIMClient.getService(MsgService.class).sendMessage(message,false);
                        SessionHelper.startP2PSession(this,name);
                        finish();
                    }
                }
                break;
        }
    }
}
