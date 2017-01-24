package com.shkjs.doctor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.bean.EventBusCreateAV;
import com.shkjs.doctor.data.Sex;
import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyPatientsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.imageview_right)
    ImageView imageViewRight;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;              //展示数据
    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;  //下拉刷新
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;    //没有数据显示
    @Bind(R.id.no_message_layout_relative)
    RelativeLayout no_message_layout_relative;
    @Bind(R.id.no_message_layout_image)
    ImageView no_message_layout_image;
    @Bind(R.id.imageview_right_2)
    ImageView imageview_right_2;

    private BaseRecyclerAdapter<UserInfoBean> adapter;
    private List<UserInfoBean> datalist;
    private RaspberryCallback<ListResponse<UserInfoBean>> callback;
    private String MYPATIENT_TYPE = "1";
    private int page = 1;
    private boolean isBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("我的患者");
        imageview_right_2.setBackgroundResource(R.drawable.actionbar_search_selector);
        imageViewRight.setBackgroundResource(R.drawable.qrscan_selector_style);
        MYPATIENT_TYPE = getIntent().getStringExtra(Preference.MYPATIENT_TYPE);

        initData();
        //设置监听事件
        initListener();
        //加载数据
        loadNetData();
    }

    private void initData() {
        if (Preference.ADD_PATIENT.equals(MYPATIENT_TYPE)) {
            imageViewRight.setVisibility(View.GONE);
            imageview_right_2.setVisibility(View.GONE);
        }
    }

    private void loadNetData() {
        HttpProtocol.getMypatients(callback, page);
    }

    public void adapterNotifycation() {
        if (datalist.size() > 0) {
            no_message_layout_relative.setVisibility(View.GONE);
        } else {
            no_message_layout_relative.setVisibility(View.VISIBLE);
            no_message_layout_image.setImageResource(R.drawable.no_data);
            no_message_layout_textview.setText("您还没有添加任何患者。");
        }
        adapter.notifyDataSetChanged();
    }

    private void initListener() {

        datalist = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                datalist.clear();
                adapter.notifyDataSetChanged();
                callback.setShowDialog(false);
                loadNetData();
            }
        });

        callback = new RaspberryCallback<ListResponse<UserInfoBean>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                adapterNotifycation();
            }

            @Override
            public void onSuccess(ListResponse<UserInfoBean> response, int code) {
                super.onSuccess(response, code);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        datalist.addAll(response.getData());
                    }
                }
                adapterNotifycation();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setShowDialog(true);

        adapter = new BaseRecyclerAdapter<UserInfoBean>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_my_patient;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final UserInfoBean item) {
                Glide.with(MyPatientsActivity.this).load(HttpBase.IMGURL + item.getHeadPortrait()).dontAnimate().thumbnail(0.1f).placeholder(R.drawable.default_head_rect).into(holder.getCircleImageView(R.id.item_mypatient_head_iv));
                AudioHelper.setNameWithDefault(holder.getTextView(R.id.item_mypatient_name_tv), item.getName(), item.getNickName());
                AudioHelper.setAgeWithDefault(holder.getTextView(R.id.item_mypatient_age_tv), item.getBirthday());
                if (!StringUtil.isEmpty(item.getSex())) {
                    holder.getTextView(R.id.item_mypatient_sex_tv).setText(Sex.getSexCalue(item.getSex()));
                }
                final Button item_mypatient_add_btn = holder.getButton(R.id.item_mypatient_add_btn);
                if (Preference.ADD_PATIENT.equals(MYPATIENT_TYPE)) {
                    item_mypatient_add_btn.setVisibility(View.VISIBLE);
                    item_mypatient_add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EventBus.getDefault().post(new EventBusCreateAV(Preference.ADD_PATIENT, item.getId() + "", item.getNickName(), item.getBirthday(), item.getSex() != null ? item.getSex() : "", HttpBase.IMGURL + item.getHeadPortrait()));
                            finish();
                        }
                    });
                } else {
                    item_mypatient_add_btn.setVisibility(View.GONE);
                }
            }
        };

        if (!Preference.ADD_PATIENT.equals(MYPATIENT_TYPE)) {
            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    startActivityForResult(new Intent(MyPatientsActivity.this, MyPatientsDetailActivity.class).putExtra(Preference.USER_INFO, datalist.get(pos)), Preference.MYPATINET_REQUEST_CODE);
                }
            });
        }

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isBottom = ((layoutManager.findLastVisibleItemPosition() + 1) == adapter.getItemCount());
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isBottom && isCannextAdd()) {
                    page++;
                    loadNetData();
                }
            }
        });
    }

    private boolean isCannextAdd() {
        if (datalist.size() < 20 * page) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Preference.MYPATINET_REQUEST_CODE && resultCode == Preference.MYPATIENT_RESULT_CODE) {
            page = 1;
            datalist.clear();
            callback.setShowDialog(false);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(true);
            loadNetData();
        }
    }

    @OnClick({R.id.back_iv, R.id.imageview_right, R.id.imageview_right_2})
    public void onMyPainetClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.imageview_right:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                            .READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission
                            .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {

                        @Override
                        public void onGranted() {
                            startActivity(new Intent(MyPatientsActivity.this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_ADDPATIENT));
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                                    .toast_permission_hint));
                        }
                    });
                } else {
                    startActivity(new Intent(MyPatientsActivity.this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_ADDPATIENT));
                }
                break;
            case R.id.imageview_right_2:
                Intent intent = new Intent(this, SearchDoctorActivity.class);
                intent.putExtra("account", "MyPatientsActivity");
                startActivity(intent);
                break;
        }
    }
}
