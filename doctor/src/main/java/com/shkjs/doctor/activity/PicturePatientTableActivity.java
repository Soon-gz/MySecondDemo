package com.shkjs.doctor.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.bean.UserHealthReports;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.view.AudioButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PicturePatientTableActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.mypatient_detail_headimg_iv)
    CircleImageView mypatient_detail_headimg_iv;
    @Bind(R.id.item_patient_detail_sex)
    TextView item_patient_detail_sex;
    @Bind(R.id.item_patient_detail_age)
    TextView item_patient_detail_age;
    @Bind(R.id.item_patient_detail_height)
    TextView item_patient_detail_height;
    @Bind(R.id.item_patient_detail_weight)
    TextView item_patient_detail_weight;
    @Bind(R.id.item_patient_detail_blood)
    TextView item_patient_detail_blood;
    @Bind(R.id.item_patient_detail_name_tv)
    TextView item_patient_detail_name_tv;
    @Bind(R.id.item_patient_detail_username)
    TextView item_patient_detail_username;
    @Bind(R.id.item_patient_detail_time_tv)
    TextView item_patient_detail_time_tv;
    @Bind(R.id.item_patient_detail_symptom_tv)
    TextView item_patient_detail_symptom_tv;
    @Bind(R.id.main_mypatients_vip)
    ImageView main_mypatients_vip;
    @Bind(R.id.item_patient_detail_voice_time_tv)
    TextView item_patient_detail_voice_time_tv;
    @Bind(R.id.health_report_voice_ll)
    LinearLayout health_report_voice_ll;
    @Bind(R.id.health_report_voice_btn)
    AudioButton health_report_voice_btn;
    @Bind(R.id.item_patient_detail_pictures_multiimageview)
    MultiImageView item_patient_detail_pictures_multiimageview;
    @Bind(R.id.doctor_recyclerview_frame)
    FrameLayout doctor_recyclerview_frame;
    @Bind(R.id.doctor_radiogroup)
    RadioGroup doctor_radiogroup;
    @Bind(R.id.doctor_radiogbtn_wz)
    RadioButton doctor_radiogbtn_wz;
    @Bind(R.id.doctor_radiogbtn_jk)
    RadioButton doctor_radiogbtn_jk;
    @Bind(R.id.picture_table_recycelrview)
    RecyclerView picture_table_recycelrview;
    @Bind(R.id.picture_table_recycelrview_ll)
    LinearLayout picture_table_recycelrview_ll;


    private UserInfoBean userInfo;
    private String orderCode;
    private RaspberryCallback<ObjectResponse<UserHealthReports>>reportCallback;
    private BaseRecyclerAdapter<UserHealthReports> adapterRight;
    private RaspberryCallback<ListResponse<UserHealthReports>> healthCallbackRight;
    private List<UserHealthReports> datalistRight;

    //语音播放
    private AudioPlayer audioPlayer;
    private String audioUrl = "";
    private int rightPage = 1;
    private boolean isRightBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_patient_table);
        ButterKnife.bind(this);
        toptitle_tv.setText("患者详情");
        userInfo = (UserInfoBean) getIntent().getSerializableExtra(Preference.USER_INFO);
        orderCode = getIntent().getStringExtra(Preference.ORDER_CODE);
        initData();
        initListener();
        loadReportLeft();
    }

    //加载健康档案
    private void loadReportRight() {
        HttpProtocol.getReportRightList(userInfo.getId() + "", healthCallbackRight,rightPage);
    }

    //加载问诊表
    public void loadReportLeft() {
        HttpProtocol.medicalReport(reportCallback, orderCode);
    }

    private void initListener() {
        reportCallback = new RaspberryCallback<ObjectResponse<UserHealthReports>>() {
            @Override
            public void onSuccess(ObjectResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    AudioHelper.setNameWithDefault(item_patient_detail_name_tv,response.getData().getPatientName(),response.getData().getPatientName());
                    AudioHelper.setDateWithDefaultHHmm(item_patient_detail_time_tv,response.getData().getCreateDate());
                    AudioHelper.setTextWithDefault(item_patient_detail_symptom_tv,response.getData().getContent());
                    AudioHelper.initAudio(health_report_voice_ll,health_report_voice_btn,item_patient_detail_voice_time_tv,response.getData(), audioPlayer, PicturePatientTableActivity.this);
                    AudioHelper.initMuitlPicture(item_patient_detail_pictures_multiimageview, response.getData(), PicturePatientTableActivity.this);
                }
            }
        };
        AudioHelper.initCallBack(reportCallback,this,true);

        //初始化适配器
        adapterRight = AudioHelper.initRecyclerAdapter(datalistRight,userInfo,this,audioPlayer,false);
        //初始化recyclerview
        AudioHelper.initBaseRecyclerView(picture_table_recycelrview,adapterRight,this);
        //初始化callback
        healthCallbackRight = AudioHelper.initCallback(datalistRight,adapterRight,this,picture_table_recycelrview_ll);
        initRecyclerScollView(picture_table_recycelrview);
        doctor_radiogroup.setOnCheckedChangeListener(this);
    }

    public void initRecyclerScollView(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRightBottom && RecyclerView.SCROLL_STATE_IDLE == newState && isLeftCanNextAdd()){
                    rightPage++;
                    loadReportRight();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isRightBottom = ((layoutManager.findLastVisibleItemPosition()+1)==adapterRight.getItemCount());
            }
        });
    }

    private boolean isLeftCanNextAdd() {
        if (datalistRight.size() < 20*rightPage){
            return false;
        }
        return true;
    }

    private void initData() {
        AudioHelper.setCircleImage(this,mypatient_detail_headimg_iv,userInfo.getHeadPortrait(),R.drawable.default_head_rect);
        AudioHelper.setSexText(item_patient_detail_sex,userInfo.getSex());
        AudioHelper.setNameWithDefault(item_patient_detail_username,userInfo.getName(),userInfo.getNickName());
        AudioHelper.setAgeWithDefault(item_patient_detail_age,userInfo.getBirthday());
        if (!StringUtil.isEmpty(userInfo.getVip()) && "1".equals(userInfo.getVip())){
            main_mypatients_vip.setVisibility(View.VISIBLE);
        }
        AudioHelper.setNumWithDefault(item_patient_detail_height,userInfo.getHeight(),"cm");
        AudioHelper.setNumWithDefault(item_patient_detail_weight,userInfo.getWeight(),"kg");
        AudioHelper.setTextWithDefault(item_patient_detail_blood,userInfo.getBloodType(),"型");
        audioPlayer = new AudioPlayer(this);
        AudioHelper.audioUrl = audioUrl;
        datalistRight = new ArrayList<>();
        datalistRight.add(new UserHealthReports());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }

    @OnClick(R.id.back_iv)
    public void patientOnClick(){
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.doctor_radiogbtn_wz:
                AudioHelper.showFrame(0,doctor_recyclerview_frame);
                doctor_radiogbtn_wz.setTextColor(getResources().getColor(R.color.white));
                doctor_radiogbtn_wz.setBackgroundColor(getResources().getColor(R.color.color_blue_0888ff));
                doctor_radiogbtn_jk.setTextColor(getResources().getColor(R.color.black));
                doctor_radiogbtn_jk.setBackgroundColor(getResources().getColor(R.color.gray_d2d2d2));
                break;
            case R.id.doctor_radiogbtn_jk:
                AudioHelper.showFrame(1,doctor_recyclerview_frame);
                rightPage = 1;
                datalistRight.clear();
                datalistRight.add(new UserHealthReports());
                adapterRight.notifyDataSetChanged();
                loadReportRight();
                doctor_radiogbtn_jk.setTextColor(getResources().getColor(R.color.white));
                doctor_radiogbtn_jk.setBackgroundColor(getResources().getColor(R.color.color_blue_0888ff));
                doctor_radiogbtn_wz.setTextColor(getResources().getColor(R.color.black));
                doctor_radiogbtn_wz.setBackgroundColor(getResources().getColor(R.color.gray_d2d2d2));
                break;
        }
    }
}
