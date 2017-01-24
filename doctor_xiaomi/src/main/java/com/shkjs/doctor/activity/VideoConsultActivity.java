package com.shkjs.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.bean.VideoConsultBean;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.nim.chat.activity.AVChatActivity;
import com.shkjs.nim.em.ClientType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoConsultActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                   //标题
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


    private BaseRecyclerAdapter<VideoConsultBean> adapter; //适配器
    private RaspberryCallback<ListResponse<VideoConsultBean>> callback;
    private RaspberryCallback<ObjectResponse<String>>canableCallback;
    private RaspberryCallback<ListResponse<VideoConsultBean>>setTimesCallback;
    private RaspberryCallback<BaseResponse>cancelCallback;

    private int CurrentPosition = 0;
    private int pageNum = 1;
    private boolean isBottom = false;


    private List<VideoConsultBean> datalist;                //数据源
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("tag00", "收到广播:" + action);
            if (Preference.VIDEO_CONSULT.equals(action)) {
                if (intent.getStringExtra("set_call_times") != null) {
                    final String userId = intent.getStringExtra("set_call_times");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    HttpProtocol.setCallTimes(setTimesCallback,userId,datalist.get(CurrentPosition).getId()+"");
                                }
                            });
                        }
                    },300);
                }else {
                    Intent intent1 = new Intent(VideoConsultActivity.this, MedicalActivity.class);
                    intent1.putExtra("orderId", datalist.get(CurrentPosition).getId() + "");
                    intent1.putExtra("orderCode", datalist.get(CurrentPosition).getCode());
                    intent1.putExtra(Preference.COMPLETE_TYPE, Preference.COMPLETE_SIT_VIDEO_CONSULT);
                    startActivityForResult(intent1,Preference.VIDEO_COMPLETE);
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_consult);
        //初始化控件
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("视频咨询");
        //设置监听器
        initListener();
        //加载数据
        loadNetData();
        //注册广播
        registerBoradcastReceiver();
    }

    private void loadNetData() {
        HttpProtocol.getVideoConsult(callback,pageNum);
    }

    private void initListener() {

        SharedPreferencesUtils.put(Preference.CALL_TIMES,0);

        datalist = new ArrayList<>();
        //设计下拉舒刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新完成后的数据处理
                pageNum = 1;
                datalist.clear();
                adapter.notifyDataSetChanged();
                callback.setShowDialog(false);
                loadNetData();
            }
        });

        //设置适配器
        adapter = new BaseRecyclerAdapter<VideoConsultBean>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_video_consultation_rv;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, final int position, final VideoConsultBean item) {
                AudioHelper.setCircleImage(VideoConsultActivity.this,holder.getCircleImageView(R.id.item_video_consult_head_iv),item.getUserInfo().getHeadPortrait(),R.drawable.default_head_rect);
                AudioHelper.setNameWithDefault(holder.getTextView(R.id.item_video_consult_name_tv),item.getUserInfo().getName(),item.getUserInfo().getNickName());
                AudioHelper.setTextWithDefault(holder.getTextView(R.id.item_video_consult_sex_tv),Sex.getSexCalue(item.getUserInfo().getSex() != null ?item.getUserInfo().getSex():"SECRECY"));
                AudioHelper.setAgeWithDefault(holder.getTextView(R.id.item_video_consult_age_tv),item.getUserInfo().getBirthday());
                AudioHelper.setDateWithDefault(holder.getTextView(R.id.item_video_consult_time_tv),item.getDiagnoseDate());
                Log.i("tag00","坐诊日期："+DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getCreateDate()), "yyyy-MM-dd"));
                AudioHelper.setWeekDayWithDefault(holder.getTextView(R.id.item_video_consult_weekday_tv),item.getDiagnoseDate(),item.getCreateDate());
                Log.i("tag00","是否已完成："+item.isDoctorCanFinish());

                holder.getTextView(R.id.item_video_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", VideoConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                            @Override
                            public void doSomeThings() {
                                HttpProtocol.postCancelOrder(cancelCallback,datalist.get(position).getCode(),datalist.get(position).getSource(),VideoConsultActivity.this);
                                holder.getSwipeItemLayout(R.id.item_video_swipe).closeWithAnim();
                            }
                        });
                    }
                });

                holder.getCircleImageView(R.id.item_video_consult_head_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserInfoBean userInfoBean = AudioHelper.createUserInfo(item.getUserInfo());
                        Intent intent = new Intent(VideoConsultActivity.this,PicturePatientTableActivity.class);
                        intent.putExtra(Preference.USER_INFO,userInfoBean);
                        intent.putExtra(Preference.ORDER_CODE,item.getCode());
                        startActivity(intent);
                    }
                });
                if (!StringUtil.isEmpty(item.getDiagnoseDate())) {
                    String startTime = DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getDiagnoseDate()), "HH:mm");
                    String endTime = (Integer.parseInt(startTime.split(":")[0]) + 1) + ":" + startTime.split(":")[1];
                    holder.getTextView(R.id.item_video_consult_hourminute_tv).setText(startTime + "--" + endTime);
                }
                if (canAccept(Long.parseLong(item.getDiagnoseDate()))){
                    holder.getTextView(R.id.item_video_consult_wait_tv).setVisibility(View.GONE);
                        if (item.isDoctorCanFinish()){
                            holder.getRelativeLayout(R.id.item_video_consult_sure_canfinish_rl).setVisibility(View.VISIBLE);
                            holder.getTextView(R.id.item_video_consult_sure_tv).setVisibility(View.GONE);
                            holder.getTextView(R.id.item_video_consult_sure_canfinish_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CurrentPosition = position;
                                    CustomAlertDialog.dialogExSureCancel("是否开始视频咨询?", VideoConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                                        @Override
                                        public void doSomeThings() {
                                            HttpProtocol.isCallableUser(canableCallback,item.getUserInfo().getId(),datalist.get(CurrentPosition).getId(),VideoConsultActivity.this);
                                        }
                                    });
                                }
                            });
                            holder.getTextView(R.id.item_video_consult_sure_canfinish_tv).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CurrentPosition = position;
                                    Intent intent1 = new Intent(VideoConsultActivity.this, MedicalActivity.class);
                                    intent1.putExtra("orderId", datalist.get(CurrentPosition).getId() + "");
                                    intent1.putExtra("orderCode", datalist.get(CurrentPosition).getCode());
                                    intent1.putExtra(Preference.COMPLETE_TYPE, Preference.COMPLETE_SIT_VIDEO_CONSULT);
                                    startActivityForResult(intent1,Preference.VIDEO_COMPLETE);
                                }
                            });
                    }else {
                        holder.getTextView(R.id.item_video_consult_sure_tv).setVisibility(View.VISIBLE);
                        holder.getRelativeLayout(R.id.item_video_consult_sure_canfinish_rl).setVisibility(View.GONE);
                        holder.getTextView(R.id.item_video_consult_sure_tv).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CurrentPosition = position;
                                CustomAlertDialog.dialogExSureCancel("是否开始视频咨询?", VideoConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                                    @Override
                                    public void doSomeThings() {
                                        HttpProtocol.isCallableUser(canableCallback,item.getUserInfo().getId(),datalist.get(CurrentPosition).getId(),VideoConsultActivity.this);
                                    }
                                });
                            }
                        });
                    }
                }else {
                    holder.getRelativeLayout(R.id.item_video_consult_sure_canfinish_rl).setVisibility(View.GONE);
                    holder.getTextView(R.id.item_video_consult_wait_tv).setVisibility(View.VISIBLE);
                    holder.getTextView(R.id.item_video_consult_sure_tv).setVisibility(View.GONE);
                    holder.getSwipeItemLayout(R.id.item_video_swipe).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showToast("未到接诊时间。");
                        }
                    });

                }
            }
        };

        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int pos) {
                CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", VideoConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        HttpProtocol.postCancelOrder(cancelCallback,datalist.get(pos).getCode(),datalist.get(pos).getSource(),VideoConsultActivity.this);
                    }
                });
            }
        });

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isBottom && newState == RecyclerView.SCROLL_STATE_IDLE && isCannextAdd()) {
                    pageNum++;
                    loadNetData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isBottom = ((layoutManager.findLastVisibleItemPosition() +1) == adapter.getItemCount());
            }
        });

        cancelCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    ToastUtils.showToast("取消订单成功。");
                    pageNum = 1;
                    datalist.clear();
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(true);
                    loadNetData();
                }
            }

        };
        AudioHelper.initCallBack(cancelCallback,this,true);

        callback = new RaspberryCallback<ListResponse<VideoConsultBean>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                initView();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(ListResponse<VideoConsultBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        datalist.addAll(response.getData());
                    }
                }
                initView();
            }
        };
        AudioHelper.initCallBack(callback,this,true);

        setTimesCallback = new RaspberryCallback<ListResponse<VideoConsultBean>>() {
            @Override
            public void onSuccess(ListResponse<VideoConsultBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    //刷新完成后的数据处理
                    datalist.clear();
                    adapter.notifyDataSetChanged();
                    datalist.addAll(response.getData());
                    initView();
                    Log.i("tag00","记录该用户未接视频一次。");
                    ToastUtils.showToast("记录该用户未接视频一次。");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        setTimesCallback.setCancelable(false);
        setTimesCallback.setContext(this);
        setTimesCallback.setMainThread(true);
        setTimesCallback.setShowDialog(false);

        canableCallback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    final UserInfoBean userInfoBean = createUserInfoBean(datalist.get(CurrentPosition).getUserInfo());
                    if (DataCache.getInstance().isLoginNim()){
                        AVChatActivity.start(VideoConsultActivity.this, datalist.get(CurrentPosition).getUserInfo().getUserName()+"_user", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL, ClientType.DOCTOR, userInfoBean,datalist.get(CurrentPosition).getCode());
                    }else {
                        LoginManager.loginNim(VideoConsultActivity.this, new LoginManager.LognimCallback() {
                            @Override
                            public void logNimCallback() {
                                AVChatActivity.start(VideoConsultActivity.this, datalist.get(CurrentPosition).getUserInfo().getUserName()+"_user", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL, ClientType.DOCTOR, userInfoBean,datalist.get(CurrentPosition).getCode());
                            }
                        });
                    }
                }else {
                    ToastUtils.showToast(response.getMsg());
                }
            }
        };
        canableCallback.setCancelable(false);
        canableCallback.setContext(this);
        canableCallback.setMainThread(true);
        canableCallback.setShowDialog(true);

    }

    private UserInfoBean createUserInfoBean(VideoConsultBean.UserInfoBean userInfo) {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setHeadPortrait(userInfo.getHeadPortrait());
        userInfoBean.setId(userInfo.getId());
        userInfoBean.setBirthday(userInfo.getBirthday());
        userInfoBean.setName(userInfo.getName());
        userInfoBean.setNickName(userInfo.getNickName());
        userInfoBean.setSex(userInfo.getSex());
        return userInfoBean;
    }

    public void initView() {
        if (datalist.size() > 0) {
            no_message_layout_relative.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetChanged();
            no_message_layout_textview.setText("您还没有视频咨询订单，请合理设置您的坐诊时间~");
            no_message_layout_image.setImageResource(R.drawable.no_data);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    public boolean canAccept(long time){
        return System.currentTimeMillis() >= time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Preference.VIDEO_COMPLETE && resultCode == Preference.VIDEO_COMPLETE_RESULT){
            //刷新完成后的数据处理
            datalist.clear();
            adapter.notifyDataSetChanged();
            callback.setShowDialog(false);
            swipeRefreshLayout.setRefreshing(true);
            loadNetData();
        }
    }

    @OnClick(R.id.back_iv)
    public void videoClickEvents(View view) {
        finish();
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Preference.VIDEO_CONSULT);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public boolean isCannextAdd() {
        if (datalist.size() < 20*pageNum){
            return false;
        }
        return true;
    }
}
