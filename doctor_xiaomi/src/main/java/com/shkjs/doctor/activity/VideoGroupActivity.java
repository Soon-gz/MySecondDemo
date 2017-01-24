package com.shkjs.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.GroupSitDiagnoseRoomInfo;
import com.shkjs.doctor.bean.VideoGroupBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.OrderSource;
import com.shkjs.doctor.data.OrderStatus;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.nim.chat.activity.AVChatRoomActivity;
import com.shkjs.nim.em.ClientType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoGroupActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                   //标题
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;              //展示数据
    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;  //下拉刷新
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;    //没有数据显示
    @Bind(R.id.video_consult_create_rv)
    RelativeLayout video_consult_create_rv;//创建视频会议
    @Bind(R.id.no_message_layout_relative)
    RelativeLayout no_message_layout_relative;
    @Bind(R.id.no_message_layout_image)
    ImageView no_message_layout_image;

    private BaseRecyclerAdapter<VideoGroupBean> adapter; //适配器

    private List<VideoGroupBean> datalist;                //数据源
    private RaspberryCallback<ListResponse<VideoGroupBean>> groupCallback;
    private RaspberryCallback<BaseResponse> cancelCallback;
    private RaspberryCallback<BaseResponse> agreeCallback;
    private RaspberryCallback<BaseResponse> disagreeCallback;


    private int CurrentPosition = 0;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("tag00", "收到广播:" + action);
            if (Preference.VIDEO_CONSULT_GROUP.equals(action)) {
                Intent intent1 = new Intent(VideoGroupActivity.this, MedicalActivity.class);
                intent1.putExtra("orderId", datalist.get(CurrentPosition).getId() + "");
                intent1.putExtra("orderCode", datalist.get(CurrentPosition).getCode());
                intent1.putExtra(Preference.COMPLETE_TYPE, Preference.COMPLETE_VIDEO_GROUP_CONSULT);
                startActivityForResult(intent1, Preference.VIDEO_GROUP_COMPLETE);
            }
        }

    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Preference.VIDEO_CONSULT_GROUP);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_group);
        //初始化控件
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("视频会诊");
        //设置监听器
        initListener();
        //加载数据
        loadNetData();
        //注册广播
        registerBoradcastReceiver();
    }


    private void loadNetData() {
        HttpProtocol.getVideoGroupConsult(groupCallback);
    }

    private void initListener() {
        datalist = new ArrayList<>();
        video_consult_create_rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(VideoGroupActivity.this, CreateAVActivity.class),Preference.VIDEO_GROUP_CREATE);
            }
        });
        //设计下拉舒刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新完成后的数据处理
                datalist.clear();
                adapter.notifyDataSetChanged();
                groupCallback.setShowDialog(false);
                loadNetData();
            }
        });


        cancelCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("取消订单成功。");
                    datalist.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    adapter.notifyDataSetChanged();
                    loadNetData();
                }
            }

        };
        AudioHelper.initCallBack(cancelCallback, this, true);

        agreeCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("同意会诊成功。");
                    //刷新完成后的数据处理
                    datalist.clear();
                    adapter.notifyDataSetChanged();
                    groupCallback.setShowDialog(false);
                    swipeRefreshLayout.setRefreshing(true);
                    loadNetData();
                }
            }
        };
        AudioHelper.initCallBack(agreeCallback, this, true);

        disagreeCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("已拒绝视频会诊邀请。");
                    //刷新完成后的数据处理
                    datalist.clear();
                    adapter.notifyDataSetChanged();
                    groupCallback.setShowDialog(false);
                    swipeRefreshLayout.setRefreshing(true);
                    loadNetData();
                }
            }
        };
        AudioHelper.initCallBack(disagreeCallback, this, true);

        //设置适配器
        adapter = new BaseRecyclerAdapter<VideoGroupBean>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_video_consultation_rv;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, final int position, final VideoGroupBean item) {
                AudioHelper.setNameWithDefault(holder.getTextView(R.id.item_video_consult_name_tv), item.getUserInfo
                        ().getName(), item.getUserInfo().getNickName());
                AudioHelper.setTextWithDefault(holder.getTextView(R.id.item_video_consult_sex_tv), Sex.getSexCalue
                        (item.getUserInfo().getSex() != null ? item.getUserInfo().getSex() : "SECRECY"));
                AudioHelper.setAgeWithDefault(holder.getTextView(R.id.item_video_consult_age_tv), item.getUserInfo()
                        .getBirthday());
                AudioHelper.setDateWithDefault(holder.getTextView(R.id.item_video_consult_time_tv), item
                        .getDiagnoseDate());
                AudioHelper.setTextWithDefault(holder.getTextView(R.id.item_video_consult_weekday_tv), DateUtil
                        .getWeekDay(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getCreateDate()),
                                "yyyy-MM-dd"), "yyyy-MM-dd"));
                if (!StringUtil.isEmpty(item.getDiagnoseDate())) {
                    String startTime = DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getDiagnoseDate()),
                            "HH:mm");
                    holder.getTextView(R.id.item_video_consult_hourminute_tv).setText(startTime);
                }
//                Log.i("tag00", "Status：" + item.getStatus() + "Source:" + item.getSource());
                if (OrderStatus.PAID.name().equals(datalist.get(position).getStatus())){
                    holder.getSwipeItemLayout(R.id.item_video_swipe).setSwipeAble(true);
                    holder.getTextView(R.id.item_video_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", VideoGroupActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                                @Override
                                public void doSomeThings() {
                                    HttpProtocol.postCancelOrder(cancelCallback,datalist.get(position).getCode(),datalist.get(position).getSource(),VideoGroupActivity.this);
                                    holder.getSwipeItemLayout(R.id.item_video_swipe).closeWithAnim();
                                }
                            });
                        }
                    });
                }else {
                    holder.getSwipeItemLayout(R.id.item_video_swipe).setSwipeAble(false);
                }

                if (item.isDoctorCanFinish()) {
                    holder.getTextView(R.id.item_video_consult_sure_tv).setVisibility(View.GONE);
                    holder.getRelativeLayout(R.id.item_video_consult_sure_canfinish_rl).setVisibility(View.VISIBLE);
                    holder.getTextView(R.id.item_video_consult_sure_canfinish_btn).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.dialogExSureCancel("是否开始视频会诊?", VideoGroupActivity.this, new
                                    CustomAlertDialog.OnDialogClickListener() {
                                        @Override
                                        public void doSomeThings() {
                                            UserInfoBean userInfoBean = createUserInfoBean(datalist.get(position).getUserInfo
                                                    ());
                                            queryEnterrRoom(item.getId(), HttpBase.IMGURL + item.getUserInfo()
                                                    .getHeadPortrait(), userInfoBean);
                                        }
                                    });
                        }
                    });
                    holder.getTextView(R.id.item_video_consult_sure_canfinish_tv).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CurrentPosition = position;
                            Intent intent1 = new Intent(VideoGroupActivity.this, MedicalActivity.class);
                            intent1.putExtra("orderId", datalist.get(CurrentPosition).getId() + "");
                            intent1.putExtra("orderCode", datalist.get(CurrentPosition).getCode());
                            intent1.putExtra(Preference.COMPLETE_TYPE, Preference.COMPLETE_VIDEO_GROUP_CONSULT);
                            startActivityForResult(intent1, Preference.VIDEO_GROUP_COMPLETE);
                        }
                    });
                } else {
                    holder.getTextView(R.id.item_video_consult_sure_tv).setVisibility(View.VISIBLE);
                    holder.getRelativeLayout(R.id.item_video_consult_sure_canfinish_rl).setVisibility(View.GONE);
                    switch (OrderStatus.valueOf(item.getStatus())) {
                        case INITIAL:
                            initCannotFinishInital(holder, item);
                            break;
                        case PAID:
                            initCannotFinishPaid(holder, position, item);
                            break;
                        case AGREE:
                            holder.getTextView(R.id.item_video_consult_sure_tv).setText("待患者付款");
                            holder.getTextView(R.id.item_video_consult_sure_tv).setBackgroundColor(getResources().getColor(R.color.gray_f2f2f2));
                            holder.getTextView(R.id.item_video_consult_sure_tv).setTextColor(getResources().getColor(R.color.gray_888888));
                            break;
                    }
                }
            }
        };

        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int pos) {
                if (OrderStatus.PAID.name().equals(datalist.get(pos).getStatus())){
                    CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", VideoGroupActivity.this, new CustomAlertDialog
                            .OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            Log.i("tag00", "code:" + datalist.get(pos).getCode() + " source:" + datalist.get(pos)
                                    .getSource());
                            HttpProtocol.postCancelOrder(cancelCallback, datalist.get(pos).getCode(), getSource(datalist
                                    .get(pos).getSource()),VideoGroupActivity.this);
                        }
                    });
                }
            }
        });

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        groupCallback = new RaspberryCallback<ListResponse<VideoGroupBean>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                initView();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(ListResponse<VideoGroupBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        datalist.addAll(response.getData());
                    }
                }
                initView();
            }
        };
        groupCallback.setCancelable(false);
        groupCallback.setContext(this);
        groupCallback.setMainThread(true);
        groupCallback.setShowDialog(true);
    }

    //初始化支付状态
    public void initCannotFinishPaid(BaseRecyclerViewHolder holder, final int position, final VideoGroupBean item) {
        holder.getTextView(R.id.item_video_consult_sure_tv).setBackgroundResource(R.drawable.btn_select_style);
        holder.getTextView(R.id.item_video_consult_sure_tv).setTextColor(getResources().getColor(R.color.gray_f2f2f2));
        holder.getTextView(R.id.item_video_consult_sure_tv).setText("接诊");
        holder.getTextView(R.id.item_video_consult_sure_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAlertDialog.dialogExSureCancel("是否开始视频会诊?", VideoGroupActivity.this, new
                        CustomAlertDialog.OnDialogClickListener() {
                            @Override
                            public void doSomeThings() {
                                UserInfoBean userInfoBean = createUserInfoBean(datalist.get(position).getUserInfo());
                                queryEnterrRoom(item.getId(), HttpBase.IMGURL + item.getUserInfo()
                                        .getHeadPortrait(), userInfoBean);
                            }
                        });
            }
        });
    }

    //初始化原始状态
    public void initCannotFinishInital(BaseRecyclerViewHolder holder, final VideoGroupBean item) {
        if (OrderSource.GROUP_SIT_DIAGNOSE.name().equals(item.getSource())) {
            holder.getTextView(R.id.item_video_consult_sure_tv).setText("待医生确认");
            holder.getTextView(R.id.item_video_consult_sure_tv).setBackgroundColor(getResources().getColor(R.color.gray_f2f2f2));
            holder.getTextView(R.id.item_video_consult_sure_tv).setTextColor(getResources().getColor(R.color.gray_888888));
        } else {
            holder.getTextView(R.id.item_video_consult_sure_tv).setText("同意会诊邀请");
            holder.getTextView(R.id.item_video_consult_sure_tv).setBackgroundResource(R.drawable.btn_select_style);
            holder.getTextView(R.id.item_video_consult_sure_tv).setTextColor(getResources().getColor(R.color.white));
            holder.getTextView(R.id.item_video_consult_sure_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomAlertDialog.dialogGroupCheck("视频会诊邀请", "是否同意视频会诊邀请?", VideoGroupActivity.this, new CustomAlertDialog.onDialogSureCancelListener() {
                        @Override
                        public void agreeGroup() {
                            HttpProtocol.agreeGroupDiagnose(agreeCallback, item.getId() + "",VideoGroupActivity.this);
                        }

                        @Override
                        public void disAgree() {
                            HttpProtocol.disAgreeGroupDiagnose(disagreeCallback, item.getId() + "",VideoGroupActivity.this);
                        }
                    });
                }
            });
        }
    }

    public String getSource(String source) {
        if ("GROUP_SIT_DIAGNOSE_DETAIL".equals(source)) {
            return "GROUP_SIT_DIAGNOSE";
        }
        return source;
    }

    private UserInfoBean createUserInfoBean(VideoGroupBean.UserInfoBean userInfo) {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setHeadPortrait(userInfo.getHeadPortrait());
        userInfoBean.setId(userInfo.getId());
        userInfoBean.setBirthday(userInfo.getBirthday());
        userInfoBean.setUserName(userInfo.getUserName());
        userInfoBean.setName(userInfo.getName());
        userInfoBean.setNickName(userInfo.getNickName());
        userInfoBean.setSex(userInfo.getSex());
        return userInfoBean;
    }

    private void queryEnterrRoom(final long orderId, final String head, final UserInfoBean userInfoBean) {
        RaspberryCallback<ObjectResponse<GroupSitDiagnoseRoomInfo>> callback = new
                RaspberryCallback<ObjectResponse<GroupSitDiagnoseRoomInfo>>() {
                    @Override
                    public void onSuccess(final ObjectResponse<GroupSitDiagnoseRoomInfo> response, int code) {
                        super.onSuccess(response, code);
                        if (HttpProtocol.checkStatus(response, code)) {
                            Log.i("tag00", "orderCode:" + response.getData().getOrderCode() + " RoomId:" + response.getData()
                                    .getRoomId() + " RoomName:" + response.getData().getRoomName());
                            if (response.getData().getOrderCode() != null) {
                                SharedPreferencesUtils.put(response.getData().getOrderCode(), !StringUtil.isEmpty(response
                                        .getData().getRoomId()) ? response.getData().getRoomId() : "");
                            }
                            if (TextUtils.isEmpty(response.getData().getRoomName())) {
                                ToastUtils.showToast("未到会诊时间，请稍候");
                            } else {
                                //进入视频会诊
                                if (DataCache.getInstance().isLoginNim()) {
                                    AVChatRoomActivity.start(VideoGroupActivity.this, response.getData().getRoomName(),
                                            ClientType.DOCTOR, head, userInfoBean, response.getData().getOrderCode());
                                } else {
                                    LoginManager.loginNim(VideoGroupActivity.this, new LoginManager.LognimCallback() {
                                        @Override
                                        public void logNimCallback() {
                                            AVChatRoomActivity.start(VideoGroupActivity.this, response.getData().getRoomName
                                                    (), ClientType.DOCTOR, head, userInfoBean, response.getData()
                                                    .getOrderCode());
                                        }
                                    });
                                }
                            }
                            return;
                        }
                        ToastUtils.showToast("未到会诊时间，请稍候");
                    }
                };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.queryEnterrRoom(orderId, callback,this);
    }

    public void initView() {
        if (datalist.size() > 0) {
            no_message_layout_relative.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
            no_message_layout_textview.setText("您还没创建会诊，请您合理创建会诊咨询~");
            no_message_layout_image.setImageResource(R.drawable.no_data);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Preference.VIDEO_GROUP_COMPLETE && resultCode == Preference.VIDEO_GROUP_COMPLETE_RESULT) {
            //刷新完成后的数据处理
            datalist.clear();
            adapter.notifyDataSetChanged();
            groupCallback.setShowDialog(false);
            swipeRefreshLayout.setRefreshing(true);
            loadNetData();
        }else if (requestCode == Preference.VIDEO_GROUP_CREATE && resultCode == Preference.VIDEO_GROUP_CREATE_RESULT){
            //刷新完成后的数据处理
            datalist.clear();
            adapter.notifyDataSetChanged();
            groupCallback.setShowDialog(false);
            swipeRefreshLayout.setRefreshing(true);
            loadNetData();
        }
    }

    @OnClick(R.id.back_iv)
    public void videoClickEvents(View view) {
        finish();
    }
}
