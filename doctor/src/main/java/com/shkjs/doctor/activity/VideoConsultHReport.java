package com.shkjs.doctor.activity;

import android.Manifest;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.data.ReportType;
import com.shkjs.doctor.bean.UserHealthReports;
import com.shkjs.doctor.data.Sex;
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
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class VideoConsultHReport extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.doctor_popup_circle_iv)
    CircleImageView doctor_popup_circle_iv;
    @Bind(R.id.doctor_popup_name_tv)
    TextView doctor_popup_name_tv;
    @Bind(R.id.doctor_popup_sex)
    TextView doctor_popup_sex;
    @Bind(R.id.doctor_popup_age)
    TextView doctor_popup_age;
    @Bind(R.id.doctor_popup_is_vip)
    TextView doctor_popup_is_vip;
    @Bind(R.id.item_patient_detail_voice_time_tv)
    TextView item_patient_detail_voice_time_tv;
    @Bind(R.id.item_patient_detail_name_tv)
    TextView item_patient_detail_name_tv;
    @Bind(R.id.item_patient_detail_symptom_tv)
    TextView item_patient_detail_symptom_tv;
    @Bind(R.id.item_patient_detail_time_tv)
    TextView item_patient_detail_time_tv;
    @Bind(R.id.health_report_voice_ll)
    LinearLayout health_report_voice_ll;
    @Bind(R.id.health_report_voice_btn)
    AudioButton health_report_voice_btn;
    @Bind(R.id.item_patient_detail_pictures_multiimageview)
    MultiImageView item_patient_detail_pictures_multiimageview;
    @Bind(R.id.doctor_popup_recyclerview_frame)
    FrameLayout doctor_popup_recyclerview_frame;
    @Bind(R.id.doctor_popup_radiogroup)
    RadioGroup doctor_popup_radiogroup;
    @Bind(R.id.doctor_popup_radiogbtn_wz)
    RadioButton doctor_popup_radiogbtn_wz;
    @Bind(R.id.doctor_popup_radiogbtn_jk)
    RadioButton doctor_popup_radiogbtn_jk;
    @Bind(R.id.video_consult_recyclerview)
    RecyclerView video_consult_recyclerview;
    @Bind(R.id.picture_table_recycelrview_ll)
    LinearLayout picture_table_recycelrview_ll;


    private String orderCode;
    private RaspberryCallback<ObjectResponse<UserHealthReports>>reportCallback;
    private BaseRecyclerAdapter<UserHealthReports> adapterRight;
    private RaspberryCallback<ListResponse<UserHealthReports>> healthCallbackRight;
    private List<UserHealthReports> datalistRight;

    private UserInfoBean userInfo;
    //语音播放
    private AudioPlayer audioPlayer;
    private String audioUrl = "";
    private int rightPage = 1;
    private boolean isRightBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_consult_hreport);
        ButterKnife.bind(this);
        userInfo = (UserInfoBean) getIntent().getSerializableExtra(Preference.USER_INFO);
        orderCode = getIntent().getStringExtra(Preference.ORDER_CODE);
        Log.i("tag00","VideoConsultHReport:"+userInfo.getId());
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        //设置为true点击区域外消失
        setFinishOnTouchOutside(true);
        initView();
        initListener();
        loadReportLeft();
    }

    private void initView() {
        datalistRight = new ArrayList<>();
        AudioHelper.setCircleImage(this,doctor_popup_circle_iv,userInfo.getHeadPortrait(),R.drawable.default_head_rect);
        AudioHelper.setNameWithDefault(doctor_popup_name_tv,userInfo.getName(),userInfo.getNickName());
        AudioHelper.setAgeWithDefault(doctor_popup_age,userInfo.getBirthday());
        AudioHelper.setSexText(doctor_popup_sex,userInfo.getSex());
        doctor_popup_is_vip.setVisibility(!StringUtil.isEmpty(userInfo.getVip()) && "1".equals(userInfo.getVip())?View.VISIBLE:View.GONE);
        audioPlayer = new AudioPlayer(this);
        doctor_popup_radiogroup.setOnCheckedChangeListener(this);
        AudioHelper.audioUrl = audioUrl;
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
            public void onSuccess(final ObjectResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            AudioHelper.setNameWithDefault(item_patient_detail_name_tv,response.getData().getPatientName(),response.getData().getPatientName());
                            AudioHelper.setDateWithDefaultHHmm(item_patient_detail_time_tv,response.getData().getCreateDate());
                            AudioHelper.setTextWithDefault(item_patient_detail_symptom_tv,response.getData().getContent());
                            AudioHelper.initAudio(health_report_voice_ll,health_report_voice_btn,item_patient_detail_voice_time_tv,response.getData(), audioPlayer, VideoConsultHReport.this);
                            AudioHelper.initMuitlPicture(item_patient_detail_pictures_multiimageview, response.getData(), VideoConsultHReport.this);
                        }
                    });
                }
            }
        };

        //初始化适配器
        adapterRight = AudioHelper.initBaseRecyAdapter(datalistRight,this,audioPlayer);
        //初始化recyclerview
        AudioHelper.initBaseRecyclerView(video_consult_recyclerview,adapterRight,this);
        //初始化callback
        healthCallbackRight = AudioHelper.initCallback(datalistRight,adapterRight,this,picture_table_recycelrview_ll);
        initRecyclerScollView(video_consult_recyclerview);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.doctor_popup_radiogbtn_wz:
                AudioHelper.showFrame(0,doctor_popup_recyclerview_frame);
                doctor_popup_radiogbtn_wz.setTextColor(getResources().getColor(R.color.white));
                doctor_popup_radiogbtn_wz.setBackgroundResource(R.drawable.shape_left_circle_bg);
                doctor_popup_radiogbtn_jk.setTextColor(getResources().getColor(R.color.black));
                doctor_popup_radiogbtn_jk.setBackgroundResource(R.drawable.shape_right_circle_gray);
                break;
            case R.id.doctor_popup_radiogbtn_jk:
                AudioHelper.showFrame(1,doctor_popup_recyclerview_frame);
                rightPage = 1;
                datalistRight.clear();
                adapterRight.notifyDataSetChanged();
                loadReportRight();
                doctor_popup_radiogbtn_jk.setTextColor(getResources().getColor(R.color.white));
                doctor_popup_radiogbtn_jk.setBackgroundResource(R.drawable.shape_right_circle_bg);
                doctor_popup_radiogbtn_wz.setTextColor(getResources().getColor(R.color.black));
                doctor_popup_radiogbtn_wz.setBackgroundResource(R.drawable.shape_left_circle_gray);
                break;
        }
    }
}
