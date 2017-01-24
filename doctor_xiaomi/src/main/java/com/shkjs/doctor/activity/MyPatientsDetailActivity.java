package com.shkjs.doctor.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.adapter.HeadRecyclerviewAdapter;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.UserHealthReports;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.doctor.util.AudioHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPatientsDetailActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.text_ringht)
    TextView text_ringht;
    @Bind(R.id.my_patient_detail_swiperl)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.my_patient_detail_recyclerview)
    RecyclerView recyclerViewleft;
    @Bind(R.id.picture_table_recycelrview_ll)
    LinearLayout picture_table_recycelrview_ll;

    private UserInfoBean userInfo;
    private HeadRecyclerviewAdapter<UserHealthReports> adapterLeft;
    private RaspberryCallback<ListResponse<UserHealthReports>> healthCallbackLeft;
    private RaspberryCallback<BaseResponse>healthCallbackDelete;
    private List<UserHealthReports> datalistLeft;
    //语音播放
    private AudioPlayer audioPlayer;
    private String audioUrl = "";
    private int leftPage = 1;
    private boolean isLeftBottom = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients_detail);
        ButterKnife.bind(this);
        toptitle_tv.setText("患者详情");
        text_ringht.setText("删除");
        userInfo = (UserInfoBean) getIntent().getSerializableExtra(Preference.USER_INFO);
        initListener();
        loadReportLeft();
    }

    //加载问诊表
    public void loadReportLeft() {
        HttpProtocol.getReportRightList(userInfo.getId() + "", healthCallbackLeft,leftPage);
    }

    private void initListener() {
        datalistLeft = new ArrayList<>();
        datalistLeft.add(new UserHealthReports());
        audioPlayer = new AudioPlayer(this);
        AudioHelper.audioUrl = audioUrl;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leftPage = 1;
                datalistLeft.clear();
                adapterLeft.notifyDataSetChanged();
                healthCallbackLeft.setShowDialog(false);
                datalistLeft.add(new UserHealthReports());
                loadReportLeft();
            }
        });
        //初始化适配器
        adapterLeft = AudioHelper.initRecyclerAdapter(datalistLeft,userInfo,this,audioPlayer,true);
        //初始化recyclerview
        AudioHelper.initRecyclerView(recyclerViewleft,adapterLeft,this);
        //初始化callback
        healthCallbackLeft = AudioHelper.initCallback(datalistLeft,adapterLeft,this,swipeRefreshLayout,picture_table_recycelrview_ll);
        healthCallbackDelete = AudioHelper.initDeleteCallback(this);
        initRecyclerScollView(recyclerViewleft);
    }

    public void initRecyclerScollView(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLeftBottom && RecyclerView.SCROLL_STATE_IDLE == newState && isLeftCanNextAdd()){
                        leftPage++;
                        loadReportLeft();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isLeftBottom = ((layoutManager.findLastVisibleItemPosition()+1)==adapterLeft.getItemCount());
            }
        });
    }

    private boolean isLeftCanNextAdd() {
        if (datalistLeft.size() < 20*leftPage){
            return false;
        }
        return true;
    }

    @OnClick({R.id.back_iv, R.id.text_ringht})
    public void patientDetailOnClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.text_ringht:
                HttpProtocol.postDeleteMyPatients(healthCallbackDelete,userInfo.getId()+"",this);
                break;
        }
    }
}
