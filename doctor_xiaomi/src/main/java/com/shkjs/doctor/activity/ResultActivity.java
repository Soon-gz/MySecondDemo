package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.AesEncoder;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.CreateAVPatientBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.EventBusCreateAV;
import com.shkjs.doctor.bean.QRCodeBean;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.doctor.util.AudioHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class ResultActivity extends BaseActivity {

	@Bind(R.id.captrue_personal_msg_headphoto_iv)
	ImageView captrue_personal_msg_headphoto_iv;
	@Bind(R.id.captrue_personal_msg_sex_tv)
	TextView captrue_personal_msg_sex_tv;
	@Bind(R.id.captrue_personal_msg_age_tv)
	TextView captrue_personal_msg_age_tv;
	@Bind(R.id.captrue_personal_msg_name_tv)
	TextView captrue_personal_msg_name_tv;
	@Bind(R.id.toptitle_tv)
	TextView toptitle_tv;
	@Bind(R.id.qr_result_addto_tv)
	TextView qr_result_addto_tv;
	@Bind(R.id.captrue_personal_sex_tv)
	TextView captrue_personal_sex_tv;
	@Bind(R.id.captrue_personal_age_tv)
	TextView captrue_personal_age_tv;

	private String INTENT_TYPE;
	private RaspberryCallback<BaseResponse>add2MyPatientsCallback;
	private RaspberryCallback<ObjectResponse<CreateAVPatientBean>>raspberryCallback;
	private RaspberryCallback<ObjectResponse<DoctorBean>>getDoctorCallback;
	private QRCodeBean codeBean;
	private CreateAVPatientBean createAVPatientBean;
	private DoctorBean doctorBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		//注解
		ButterKnife.bind(this);
		//获得解析的图片
		Bundle extras = getIntent().getExtras();
		//设置标题
		toptitle_tv.setText("个人信息");
		if (null != extras) {
			INTENT_TYPE = getIntent().getStringExtra(Preference.QRSCAN_INTENT_TYPE);
			String result = extras.getString("result");
			Gson gson = new Gson();
//			//获得病人的信息
			codeBean = gson.fromJson(result,QRCodeBean.class);
		}else{
			ToastUtils.showToast("获得病人信息失败！");
		}
		initListener();

	}

	private void initListener() {

		qr_result_addto_tv.setEnabled(false);
		qr_result_addto_tv.setBackgroundResource(R.drawable.shap_personal_next);
		add2MyPatientsCallback = new RaspberryCallback<BaseResponse>() {
			@Override
			public void onSuccess(BaseResponse response, int code) {
				super.onSuccess(response, code);
				if (HttpProtocol.checkStatus(response,code)){
					startActivity(new Intent(ResultActivity.this,MyPatientsActivity.class));
				}
			}
		};
		add2MyPatientsCallback.setContext(this);
		add2MyPatientsCallback.setShowDialog(true);
		add2MyPatientsCallback.setCancelable(false);
		add2MyPatientsCallback.setMainThread(true);

		raspberryCallback = new RaspberryCallback<ObjectResponse<CreateAVPatientBean>>() {
			@Override
			public void onSuccess(ObjectResponse<CreateAVPatientBean> response, int code) {
				super.onSuccess(response, code);
				if (HttpProtocol.checkStatus(response,code)){
					if (response.getData() != null){
						qr_result_addto_tv.setEnabled(true);
						qr_result_addto_tv.setBackgroundResource(R.drawable.btn_select_style);
						createAVPatientBean = response.getData();
						Glide.with(ResultActivity.this).load(HttpBase.IMGURL + response.getData().getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(captrue_personal_msg_headphoto_iv);
						AudioHelper.setSexText(captrue_personal_msg_sex_tv,response.getData().getSex());
						AudioHelper.setNameWithDefault(captrue_personal_msg_name_tv,response.getData().getName(),response.getData().getNickName());
						AudioHelper.setAgeWithDefault(captrue_personal_msg_age_tv,response.getData().getBirthday());
					}else {
						ToastUtils.showToast("扫描二维码失败，没有相应的用户。");
					}
				}
			}
		};
		raspberryCallback.setContext(this);
		raspberryCallback.setShowDialog(true);
		raspberryCallback.setCancelable(false);
		raspberryCallback.setMainThread(true);

		getDoctorCallback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
			@Override
			public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
				super.onSuccess(response, code);
				if (HttpProtocol.checkStatus(response,code)){
					if (response.getData() != null){
						qr_result_addto_tv.setEnabled(true);
						qr_result_addto_tv.setBackgroundResource(R.drawable.btn_select_style);
						doctorBean = response.getData();
						Glide.with(ResultActivity.this).load(HttpBase.IMGURL + response.getData().getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(captrue_personal_msg_headphoto_iv);
						AudioHelper.setTextWithDefault(captrue_personal_msg_sex_tv, response.getData().getHospitalName());
						AudioHelper.setNameWithDefault(captrue_personal_msg_name_tv,response.getData().getName(),response.getData().getUserName());
						AudioHelper.setTextWithDefault(captrue_personal_msg_age_tv,response.getData().getCategoryName());
					}else {
						ToastUtils.showToast("扫描二维码失败，没有相应的医生。");
					}
				}
			}
		};
		getDoctorCallback.setCancelable(false);
		getDoctorCallback.setContext(this);
		getDoctorCallback.setShowDialog(true);
		getDoctorCallback.setMainThread(true);

		switch (INTENT_TYPE){
			case Preference.QRSCAN_INTENT_ADDPATIENT:
				HttpProtocol.getPatientByUserName(raspberryCallback,codeBean.getUserName());
				break;
			case Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT:
				HttpProtocol.getPatientByUserName(raspberryCallback,codeBean.getUserName());
				qr_result_addto_tv.setText("添加");
				break;
			case Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR:
				qr_result_addto_tv.setText("添加至会诊医生名单");
				captrue_personal_sex_tv.setText("医院");
				captrue_personal_age_tv.setText("科室");
				HttpProtocol.getDoctorInfoFromName(getDoctorCallback,codeBean.getUserName());
				break;
		}
	}

	@OnClick({R.id.back_iv,R.id.qr_result_addto_tv})
	public void personalBack(View view){
		switch (view.getId()){
			case R.id.back_iv:
				ActivityManager.getInstance().finishACtivity(ResultActivity.this);
				break;
			case R.id.qr_result_addto_tv:
				switch (INTENT_TYPE){
					case Preference.QRSCAN_INTENT_ADDPATIENT:
						if (createAVPatientBean != null){
							HttpProtocol.postAddMypatients(add2MyPatientsCallback,createAVPatientBean.getId()+"",ResultActivity.this);
						}
						break;
					case Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT:
						EventBus.getDefault().post(new EventBusCreateAV(Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT,createAVPatientBean.getId()+"",createAVPatientBean.getNickName(),createAVPatientBean.getBirthday(),createAVPatientBean.getSex(),HttpBase.IMGURL + createAVPatientBean.getHeadPortrait()));
						break;
					case Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR:
						EventBus.getDefault().post(new EventBusCreateAV(Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR,doctorBean.getId()+"",doctorBean.getName(),doctorBean.getHospitalName(),doctorBean.getCategoryName(),doctorBean.getHeadPortrait(),doctorBean.getLevel()));
						break;
				}
				ActivityManager.getInstance().finishACtivity(ResultActivity.this);
				break;
		}
	}
}
