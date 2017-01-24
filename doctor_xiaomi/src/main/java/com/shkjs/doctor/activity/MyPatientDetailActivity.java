package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.shkjs.doctor.R;
import com.shkjs.doctor.adapter.HeadRecyclerviewAdapter;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.data.ReportType;
import com.shkjs.doctor.bean.UserHealthReports;
import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.AudioHelper;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPatientDetailActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.text_ringht)
    TextView text_ringht;
    @Bind(R.id.patient_detail_table_tv)
    TextView patient_detail_table_tv;
    @Bind(R.id.patient_detail_heathdc_tv)
    TextView patient_detail_heathdc_tv;
    @Bind(R.id.my_patient_detail_recyclerview)
    RecyclerView recyclerViewleft;
    @Bind(R.id.my_patient_detail_table_recyclerview)
    RecyclerView recyclerViewright;
    @Bind(R.id.patient_detail_framelayout)
    FrameLayout patient_detail_framelayout;
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;
    @Bind(R.id.my_patient_detail_swiperl)
    SwipeRefreshLayout swipeRefreshLayout;

    private HeadRecyclerviewAdapter<UserHealthReports> adapterLeft;
    private HeadRecyclerviewAdapter<UserHealthReports> adapterRight;
    private RaspberryCallback<ListResponse<UserHealthReports>> healthCallbackLeft;
    private RaspberryCallback<ListResponse<UserHealthReports>> healthCallbackRight;
    private RaspberryCallback<BaseResponse>healthCallbackDelete;
    private List<UserHealthReports> datalistLeft;
    private List<UserHealthReports> datalistRight;
    private UserInfoBean userInfo;

    //语音播放
    private AudioPlayer audioPlayer;
    private String audioUrl = "";
    private int leftPage = 1;
    private int rightPage = 1;
    private boolean isLeftBottom = false;
    private boolean isRightBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patient_detail);
        ButterKnife.bind(this);
        toptitle_tv.setText("患者详情");
        text_ringht.setText("删除");
        userInfo = (UserInfoBean) getIntent().getSerializableExtra("userInfo");
        if (getIntent().getBooleanExtra("isPicture",false)){
            text_ringht.setVisibility(View.GONE);
        }
        initListener();
        loadReportLeft();
        loadReportRight();
    }

    //加载健康档案
    private void loadReportRight() {
        HttpProtocol.getReportRightList(userInfo.getId() + "", healthCallbackRight,rightPage);
    }

    //加载问诊表
    public void loadReportLeft() {
//        HttpProtocol.getReportList(ReportType.PLATFORM, userInfo.getId() + "", healthCallbackLeft,leftPage);
    }

    private void initListener() {
        datalistLeft = new ArrayList<>();
        datalistLeft.add(new UserHealthReports());
        datalistRight = new ArrayList<>();
        datalistRight.add(new UserHealthReports());
        audioPlayer = new AudioPlayer(this);
        AudioHelper.audioUrl = audioUrl;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leftPage = 1;
                rightPage = 1;
                datalistLeft.clear();
                datalistLeft.add(new UserHealthReports());
                datalistRight.clear();
                datalistRight.add(new UserHealthReports());
                loadReportRight();
                loadReportLeft();
            }
        });
        //初始化适配器
        adapterLeft = AudioHelper.initRecyclerAdapter(datalistLeft,userInfo,this,audioPlayer,true);
        adapterRight = AudioHelper.initRecyclerAdapter(datalistRight,userInfo,this,audioPlayer,false);
        //初始化recyclerview
        AudioHelper.initRecyclerView(recyclerViewleft,adapterLeft,this);
        AudioHelper.initRecyclerView(recyclerViewright,adapterRight,this);
        //初始化callback
//        healthCallbackLeft = AudioHelper.initCallback(datalistLeft,adapterLeft,this,swipeRefreshLayout);
//        healthCallbackRight = AudioHelper.initCallback(datalistRight,adapterRight,this,swipeRefreshLayout);
        healthCallbackDelete = AudioHelper.initDeleteCallback(this);

        initRecyclerScollView(recyclerViewleft,true);
        initRecyclerScollView(recyclerViewright,false);
    }

    public void initRecyclerScollView(RecyclerView recyclerView, final boolean isLeft){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLeft){
                    if (isLeftBottom && RecyclerView.SCROLL_STATE_IDLE == newState && isLeftCanNextAdd()){
                        leftPage++;
                        loadReportLeft();
                    }
                }else {
                    if (isRightBottom && RecyclerView.SCROLL_STATE_IDLE == newState && isRightCanNextAdd()){
                        rightPage++;
                        loadReportRight();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (isLeft){
                    isLeftBottom = ((layoutManager.findLastVisibleItemPosition()+1)==adapterLeft.getItemCount());
                }else {
                    isRightBottom = ((layoutManager.findLastVisibleItemPosition()+1)==adapterRight.getItemCount());
                }
            }
        });
    }

    private boolean isLeftCanNextAdd() {
        if (datalistLeft.size() < 20*leftPage){
            return false;
        }
        return true;
    }

    private boolean isRightCanNextAdd() {
        if (datalistRight.size() < 20*rightPage){
            return false;
        }
        return true;
    }




    @OnClick({R.id.back_iv, R.id.patient_detail_heathdc_tv, R.id.patient_detail_table_tv, R.id.text_ringht})
    public void patientDetailOnClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.patient_detail_table_tv:
                patient_detail_table_tv.setTextColor(getResources().getColor(R.color.white));
                patient_detail_table_tv.setBackgroundColor(getResources().getColor(R.color.color_blue_0888ff));
                patient_detail_heathdc_tv.setTextColor(getResources().getColor(R.color.black));
                patient_detail_heathdc_tv.setBackgroundColor(getResources().getColor(R.color.gray7));
                AudioHelper.showFrame(0,patient_detail_framelayout);
                break;
            case R.id.patient_detail_heathdc_tv:
                patient_detail_table_tv.setTextColor(getResources().getColor(R.color.black));
                patient_detail_table_tv.setBackgroundColor(getResources().getColor(R.color.gray7));
                patient_detail_heathdc_tv.setTextColor(getResources().getColor(R.color.white));
                patient_detail_heathdc_tv.setBackgroundColor(getResources().getColor(R.color.color_blue_0888ff));
                AudioHelper.showFrame(1,patient_detail_framelayout);
                adapterRight.notifyDataSetChanged();
                break;
            case R.id.text_ringht:
                HttpProtocol.postDeleteMyPatients(healthCallbackDelete,userInfo.getId()+"",this);
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }
}
