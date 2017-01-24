package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.CertificationBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.EventsBus;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.util.QRNormalPopup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalMessageActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                           //标题
    @Bind(R.id.personal_msg_name_edittext_tv)
    EditText personal_msg_name_edittext_tv;         //输入姓名
    @Bind(R.id.personal_msg_yiyuan_edittext_tv)
    EditText personal_msg_yiyuan_edittext_tv;         //输入医院
    @Bind(R.id.personal_msg_keshi_edittext_tv)
    TextView personal_msg_keshi_edittext_tv;         //输入科室
    @Bind(R.id.personal_msg_zhicheng_edittext_tv)
    TextView personal_msg_zhicheng_edittext_tv;     //选择职称
    @Bind(R.id.personal_msg_jianjie_edittext_tv)
    TextView personal_msg_jianjie_edittext_tv;     //填写简介
    @Bind(R.id.personal_msg_shanchang_edittext_tv)
    TextView personal_msg_shanchang_edittext_tv;     //填写擅长
    @Bind(R.id.personal_msg_next_tv)
    Button personal_msg_next_tv;
    @Bind(R.id.personal_msg_headview_rl)
    RelativeLayout personal_msg_headview_rl;        //已验证之后的头像
    @Bind(R.id.personal_msg_zhicheng_rela_rl)
    RelativeLayout personal_msg_zhicheng_rela_rl;
    @Bind(R.id.personal_msg_shanchang_rela_rl)
    RelativeLayout personal_msg_shanchang_rela_rl;
    @Bind(R.id.personal_msg_jianjie_rela_rl)
    RelativeLayout personal_msg_jianjie_rela_rl;
    @Bind(R.id.personal_msg_renzheng_rl)
    RelativeLayout personal_msg_renzheng_rl;
    @Bind(R.id.personal_msg_headview_iv)
    CircleImageView personal_msg_headview_iv;
    @Bind(R.id.personal_msg_certificating_tv)
    TextView personal_msg_certificating_tv;
    @Bind(R.id.personal_msg_renzheng_tv)
    TextView personal_msg_renzheng_tv;
    @Bind(R.id.personal_msg_doctor_certification_tv)
    TextView personal_msg_doctor_certification_tv;
    @Bind(R.id.personal_msg_zhicheng_more_iv)
    ImageView personal_msg_zhicheng_more_iv;


    private DoctorBean doctorBean;


    private static final String INTRODCUTION = "0";//填写个人简介
    private static final String GOODATSOMETHING = "1";//填写擅长领域
    private static final String DOCTORSUBJECT = "2";//医生科室
    private static final String INTRODUCTION_HINT = "请填写个人简介,以便于患者更了解您";
    private static final String GOODSOMETHING_HINT = "请简要描述您所擅长的疾病领域";


    private static final String AUTHORITY = Preference.AUTHORITY;
    private static final String CERTIFICATED = Preference.CERTIFICATION;
    private static final String CERTIFICATE_ING = Preference.CERTIFICATIONING;
    private static final String CERTIFICATE_NOT = Preference.UNCERTIFICATION;
    private static final String CERTIFICATE_FAILED = Preference.NOTPASS;
    //医生是否认证，CERTIFICATION=已验证，CERTIFICATIONING=正在验证，UNCERTIFICATION=未验证，NOTPASS=认证失败

    private View.OnClickListener canNext;
    private View.OnClickListener cannotNext;
    private String isComeFromWelcome = "";
    private boolean jianjieCanchange = true;
    private boolean shanchangCanchange = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_message);
        //初始化
        ButterKnife.bind(this);
        if (getIntent().getSerializableExtra("doctorBean") != null){
            doctorBean = (DoctorBean) getIntent().getSerializableExtra("doctorBean");
            //设置监听
            Log.i("tag00","DoctorBean "+ doctorBean.getPlatformLevel());
            chooseCertificated(doctorBean.getPlatformLevel());
        }

        if (getIntent().getStringExtra(Preference.IS_FROM_WELCOME) != null){
            isComeFromWelcome = getIntent().getStringExtra(Preference.IS_FROM_WELCOME);
            Log.i("tag00","isComeFromWelcome:"+isComeFromWelcome);
        }
        if (getIntent().getStringExtra("certificate") != null){
            chooseCertificated(getIntent().getStringExtra("certificate"));
        }
    }

    private void chooseCertificated(String typeOfCertificated) {
        switch (typeOfCertificated){
            case CERTIFICATED:
                setEditEnable();
                break;
            case CERTIFICATE_ING:
                setCertificating();
                break;
            case CERTIFICATE_NOT:
                initListener();
                break;
            case CERTIFICATE_FAILED:
                setCertificateFailed();
                break;
            case AUTHORITY:
                setEditEnable();
                break;
        }
    }

    //资料正在认证中
    public void setCertificating(){
        toptitle_tv.setText("认证");
        setEditEnable();
        personal_msg_certificating_tv.setVisibility(View.VISIBLE);
        personal_msg_renzheng_tv.setText("正在审核中");
        personal_msg_doctor_certification_tv.setText("证件审核");
    }

    //资料认证失败
    public void setCertificateFailed(){

        initListener();
        toptitle_tv.setText("认证");
        personal_msg_certificating_tv.setVisibility(View.VISIBLE);
        personal_msg_certificating_tv.setText("认证失败，您提交的资料不符合规格，请重新上传。");
        personal_msg_name_edittext_tv.setText(doctorBean.getName());
        personal_msg_yiyuan_edittext_tv.setText(doctorBean.getHospitalName());
        personal_msg_keshi_edittext_tv.setText(doctorBean.getCategoryName());
        personal_msg_zhicheng_edittext_tv.setText(Preference.getLevelValue(doctorBean.getLevel()));
        personal_msg_jianjie_edittext_tv.setText(doctorBean.getIntroduce());
        personal_msg_shanchang_edittext_tv.setText(doctorBean.getSkilled());
        personal_msg_jianjie_edittext_tv.setTextColor(getResources().getColor(R.color.black));
        personal_msg_shanchang_edittext_tv.setTextColor(getResources().getColor(R.color.black));
        setCanNext();
    }

    //未验证之后的逻辑
    private void initListener() {

        toptitle_tv.setText("填写个人信息");

        //注册事件总线
        EventBus.getDefault().register(this);

        canNext = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CertificationBean bean = new CertificationBean();
                bean.setCertificateName(personal_msg_name_edittext_tv.getText().toString().trim());
                bean.setCertificateHospital(personal_msg_yiyuan_edittext_tv.getText().toString().trim());
                bean.setCertificateSubject(personal_msg_keshi_edittext_tv.getText().toString().trim());
                bean.setCertificateZhicheng(getEmLevel(personal_msg_zhicheng_edittext_tv.getText().toString().trim()));
                bean.setCertificateShanchang(personal_msg_shanchang_edittext_tv.getText().toString().trim());
                bean.setCertificateJianjie(personal_msg_jianjie_edittext_tv.getText().toString().trim());
                Intent intent = new Intent(PersonalMessageActivity.this,UploadAuthenDataActivity.class);
                intent.putExtra("personal",bean);
                if (doctorBean != null){
                    intent.putExtra("doctorbean",doctorBean);
                }
                startActivity(intent);
            }
        };
        cannotNext = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showToast("请完善所有信息，才能进入下一步");
            }
        };
        personal_msg_next_tv.setOnClickListener(cannotNext);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCanNext();
            }
        };
        //设置监听
        personal_msg_name_edittext_tv.addTextChangedListener(textWatcher);
        personal_msg_yiyuan_edittext_tv.addTextChangedListener(textWatcher);
    }

    //已认证之后，数据不可修改
    public void setEditEnable(){

        toptitle_tv.setText("个人资料");
        personal_msg_name_edittext_tv.setText(doctorBean.getName());
        personal_msg_yiyuan_edittext_tv.setText(doctorBean.getHospitalName());
        personal_msg_keshi_edittext_tv.setText(doctorBean.getCategoryName());
        personal_msg_zhicheng_edittext_tv.setText(Preference.getLevelValue(doctorBean.getLevel()));
        personal_msg_jianjie_edittext_tv.setText(doctorBean.getIntroduce());
        personal_msg_shanchang_edittext_tv.setText(doctorBean.getSkilled());
        personal_msg_jianjie_edittext_tv.setTextColor(getResources().getColor(R.color.black));
        personal_msg_shanchang_edittext_tv.setTextColor(getResources().getColor(R.color.black));
        personal_msg_zhicheng_more_iv.setVisibility(View.GONE);
        Glide.with(this).load(HttpBase.IMGURL+doctorBean.getHeadPortrait()).placeholder(R.drawable.main_message_headportrait).dontAnimate().thumbnail(0.1f).into(personal_msg_headview_iv);

        personal_msg_next_tv.setVisibility(View.GONE);
        personal_msg_headview_rl.setVisibility(View.VISIBLE);
        personal_msg_renzheng_rl.setVisibility(View.VISIBLE);

        personal_msg_name_edittext_tv.setEnabled(false);
        personal_msg_yiyuan_edittext_tv.setEnabled(false);
        personal_msg_keshi_edittext_tv.setEnabled(false);
        personal_msg_zhicheng_rela_rl.setEnabled(false);

        //可查看，不可修改
        jianjieCanchange = false;
        shanchangCanchange = false;
    }


    //设置可点击下一步
    public void setCanNext(){
        if (isCheckNullMsg()){
            personal_msg_next_tv.setBackground(getResources().getDrawable(R.drawable.btn_select_style));
            personal_msg_next_tv.setOnClickListener(canNext);
        }else{
            personal_msg_next_tv.setBackground(getResources().getDrawable(R.drawable.shap_personal_next));
            personal_msg_next_tv.setOnClickListener(cannotNext);
        }
    }



    public String getEmLevel(String level){
        String levelEm = "";
        switch (level){
            case "住院医师":
                levelEm = "RESIDENTDOCTOR";
                break;
            case "主治医师":
                levelEm = "VISITINGDOCTOR";
                break;
            case "主任医师":
                levelEm = "ARCHIATERDOCTOR";
                break;
            case "副主任医师":
                levelEm = "VICEARCHIATERDOCTOR";
                break;
        }
        return levelEm;
    }

    //事件总线回调，两个Activity之间交互数据
    public void onEventMainThread(EventsBus event) {
        switch (event.getType()){
            case INTRODCUTION:
                personal_msg_jianjie_edittext_tv.setText(event.getMessage());
                personal_msg_jianjie_edittext_tv.setTextColor(getResources().getColor(R.color.black));
                break;
            case GOODATSOMETHING:
                personal_msg_shanchang_edittext_tv.setText(event.getMessage());
                personal_msg_shanchang_edittext_tv.setTextColor(getResources().getColor(R.color.black));
                break;
            case DOCTORSUBJECT:
                personal_msg_keshi_edittext_tv.setText(event.getMessage());
                personal_msg_keshi_edittext_tv.setTextColor(getResources().getColor(R.color.black));
                break;
        }
        setCanNext();
    }


    @OnClick({R.id.back_iv,R.id.personal_msg_jianjie_rela_rl,R.id.personal_msg_shanchang_rela_rl,R.id.personal_msg_zhicheng_rela_rl,R.id.personal_msg_keshi_edittext_tv,R.id.personal_msg_headview_rl})
    public void personalOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                if (isComeFromWelcome.equals("yes")){
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }else {
                    finish();
                }
                break;
            case R.id.personal_msg_jianjie_rela_rl:
                startActivity(new Intent(this,IntroductGoodActivity.class).putExtra("type",INTRODCUTION).putExtra("msg",personal_msg_jianjie_edittext_tv.getText().toString()).putExtra("canchange",jianjieCanchange));
                break;
            case R.id.personal_msg_shanchang_rela_rl:
                startActivity(new Intent(this,IntroductGoodActivity.class).putExtra("type",GOODATSOMETHING).putExtra("msg",personal_msg_shanchang_edittext_tv.getText().toString()).putExtra("canchange",shanchangCanchange));
                break;
            case R.id.personal_msg_zhicheng_rela_rl:
                CustomAlertDialog.dialogChooseDoctor(this, new CustomAlertDialog.onDialogItemClickListener() {
                    @Override
                    public void residentDoctorOnClick() {
                        personal_msg_zhicheng_edittext_tv.setText("住院医师");
                        setCanNext();
                    }

                    @Override
                    public void visitingStaffOnClick() {
                        personal_msg_zhicheng_edittext_tv.setText("主治医师");
                        setCanNext();
                    }

                    @Override
                    public void associateChiefPhysicianOnClick() {
                        personal_msg_zhicheng_edittext_tv.setText("副主任医师");
                        setCanNext();
                    }

                    @Override
                    public void chiefPhysicianOnClick() {
                        personal_msg_zhicheng_edittext_tv.setText("主任医师");
                        setCanNext();
                    }
                });

                break;
            case R.id.personal_msg_keshi_edittext_tv:
                startActivity(new Intent(PersonalMessageActivity.this, SearchActivity.class));
                break;
            case R.id.personal_msg_headview_rl:
                QRNormalPopup normalPopup = new QRNormalPopup(PersonalMessageActivity.this);
                normalPopup.getTextView().setVisibility(View.GONE);
                Glide.with(this).load(HttpBase.IMGURL+doctorBean.getHeadPortrait())
                        .placeholder(R.drawable.main_message_headportrait)
                        .into(normalPopup.getImageView());
                normalPopup.showPopupWindow();
                break;
        }
    }


    //检查是否输入所有信息
    public boolean isCheckNullMsg(){
        if ("".equals(personal_msg_name_edittext_tv.getText().toString().trim())){
            return false;
        }
        if ("".equals(personal_msg_yiyuan_edittext_tv.getText().toString().trim())){
            return false;
        }
        if ("".equals(personal_msg_keshi_edittext_tv.getText().toString().trim())){
            return false;
        }
        if ("".equals(personal_msg_yiyuan_edittext_tv.getText().toString().trim())){
            return false;
        }
        if ("".equals(personal_msg_zhicheng_edittext_tv.getText().toString().trim())){
            return false;
        }
        if (GOODSOMETHING_HINT.equals(personal_msg_shanchang_edittext_tv.getText().toString().trim())){
            return false;
        }
        if (INTRODUCTION_HINT.equals(personal_msg_jianjie_edittext_tv.getText().toString().trim())){
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
