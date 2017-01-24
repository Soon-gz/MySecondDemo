package com.shkjs.doctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import com.netease.nim.uikit.session.activity.P2PMessageActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.ConsultMessage;
import com.shkjs.doctor.bean.PictureListBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.nim.chat.session.SessionHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 图文咨询，展示数据
 */
public class PictrueConsultActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;           //标题
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;      //数据展示
    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;  //无数据显示
    @Bind(R.id.no_message_layout_relative)
    RelativeLayout no_message_layout_relative;
    @Bind(R.id.no_message_layout_image)
    ImageView no_message_layout_image;

    private BaseRecyclerAdapter<ConsultMessage>adapter;
    private List<ConsultMessage> datalist;
    private List<RecentContact>recentContactList;
    private List<PictureListBean>pictureListBeanList;
    private RaspberryCallback<ListResponse<PictureListBean>>callback;
    private RaspberryCallback<BaseResponse>cancelCallback;

    private int CurrentPosition = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("tag00","收到广播:"+action);
            if(action.equals(Preference.ACTION_NAME)){
                Intent intent1 = new Intent(PictrueConsultActivity.this,MedicalActivity.class);
                intent1.putExtra("orderId",datalist.get(CurrentPosition).getMessInfo().getId()+"");
                intent1.putExtra("orderCode",datalist.get(CurrentPosition).getMessInfo().getCode());
                intent1.putExtra(Preference.COMPLETE_TYPE,Preference.COMPLETE_PICTURE);
                startActivityForResult(intent1,Preference.PICTURE_COMPLETE);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictrue_consult);
        //注解
        ButterKnife.bind(this);
        //设置标题
        toptitle_tv.setText("图文咨询");
        //设置监听事件
        initListener();
        //加载数据
        loadNetData();
        //注册广播
        registerBoradcastReceiver();

        registerObservers(true);

    }

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(messageObserver, register);
    }

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            for (RecentContact msg : messages) {
                for (int i = 0; i < datalist.size(); i++) {
                    RecentContact contact = datalist.get(i).getContact();
                    if (null != contact) {
                        if (msg.getContactId().equals(contact.getContactId()) && msg.getSessionType() == (datalist.get(i).getContact().getSessionType())) {
                            datalist.get(i).setContact(msg);
                            break;
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    };


    private void loadNetData() {
        HttpProtocol.getPictureConsult(callback);
    }

    public void initView(){
        if (datalist.size() > 0){
            adapter.notifyDataSetChanged();
            no_message_layout_relative.setVisibility(View.GONE);
        }else{
            adapter.notifyDataSetChanged();
            no_message_layout_textview.setText("您最近没有图文订单~");
            no_message_layout_image.setImageResource(R.drawable.no_data);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {

        datalist = new ArrayList<>();
        recentContactList = new ArrayList<>();
        pictureListBeanList = new ArrayList<>();

        cancelCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    ToastUtils.showToast("取消订单成功。");
                    datalist.clear();
                    pictureListBeanList.clear();
                    recentContactList.clear();
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(true);
                    callback.setShowDialog(false);
                    loadNetData();
                }
            }

        };
        AudioHelper.initCallBack(cancelCallback,this,true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datalist.clear();
                pictureListBeanList.clear();
                recentContactList.clear();
                adapter.notifyDataSetChanged();
                callback.setShowDialog(false);
                loadNetData();
            }
        });

        adapter = new BaseRecyclerAdapter<ConsultMessage>(this,datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_pictrue_chart_rv;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, final int position, final ConsultMessage item) {
                AudioHelper.setCircleImage(PictrueConsultActivity.this,holder.getCircleImageView(R.id.item_pictrue_consult_head_iv),item.getMessInfo().getUserInfo().getHeadPortrait(),R.drawable.default_head_rect);
                holder.getCircleImageView(R.id.item_pictrue_consult_head_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserInfoBean userInfoBean = AudioHelper.createUserInfo(item.getMessInfo().getUserInfo());
                        Intent intent = new Intent(PictrueConsultActivity.this,PicturePatientTableActivity.class);
                        intent.putExtra(Preference.USER_INFO,userInfoBean);
                        intent.putExtra(Preference.ORDER_CODE,item.getMessInfo().getCode());
                        startActivity(intent);
                    }
                });
                AudioHelper.setNameWithDefault(holder.getTextView(R.id.item_pictrue_consult_name_tv),item.getMessInfo().getUserInfo().getName(),item.getMessInfo().getUserInfo().getNickName());
                if (item.getContact() != null){
                    AudioHelper.setTextWithDefaultBySelf( holder.getTextView(R.id.item_pictrue_consult_content_tv),item.getContact().getContent(),"");
                    if (item.getContact().getUnreadCount() != 0){
                        holder.getTextView(R.id.item_pictrue_consult_tipnum_tc).setVisibility(View.VISIBLE);
                        if (item.getContact().getUnreadCount() > 99){
                            holder.getTextView(R.id.item_pictrue_consult_tipnum_tc).setText("99+");
                        }else {
                            holder.getTextView(R.id.item_pictrue_consult_tipnum_tc).setText(item.getContact().getUnreadCount()+"");
                        }
                    }else {
                        holder.getTextView(R.id.item_pictrue_consult_tipnum_tc).setVisibility(View.GONE);
                    }
                }else {
                    holder.getTextView(R.id.item_pictrue_consult_tipnum_tc).setVisibility(View.GONE);
                    AudioHelper.setTextWithDefaultBySelf( holder.getTextView(R.id.item_pictrue_consult_content_tv),item.getMessInfo().getHealthReport().getContent(),"");
                }
                if (item.getMessInfo().getStatus().equals("PAID")){
                    holder.getSwipeItemLayout(R.id.item_picture_sil).setSwipeAble(true);
                    holder.getTextView(R.id.item_picture_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", PictrueConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                                @Override
                                public void doSomeThings() {
                                    HttpProtocol.postCancelOrder(cancelCallback,datalist.get(position).getMessInfo().getCode(),datalist.get(position).getMessInfo().getSource(),PictrueConsultActivity.this);
                                    holder.getSwipeItemLayout(R.id.item_picture_sil).closeWithAnim();
                                }
                            });
                        }
                    });
                    AudioHelper.showFrame(1,holder.getFrameLayout(R.id.item_pictrue_consult_fl));
                }else {
                    holder.getSwipeItemLayout(R.id.item_picture_sil).setSwipeAble(false);
                    AudioHelper.showFrame(0,holder.getFrameLayout(R.id.item_pictrue_consult_fl));
                }
                holder.getTextView(R.id.item_pictrue_consult_time_tv).setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getMessInfo().getCreateDate()),"MM-dd"));
            }
        };
        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int pos) {
                if (datalist.get(pos).getMessInfo().getStatus().equals("PAID")){
                    CustomAlertDialog.dialogExSureIgnore("是否取消该订单?", PictrueConsultActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            HttpProtocol.postCancelOrder(cancelCallback,datalist.get(pos).getMessInfo().getCode(),datalist.get(pos).getMessInfo().getSource(),PictrueConsultActivity.this);
                        }
                    });
                }
            }
        });

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                CurrentPosition = pos;
                final boolean complete = !datalist.get(pos).getMessInfo().getStatus().equals("PAID");
                final String name = !StringUtil.isEmpty(datalist.get(pos).getMessInfo().getUserInfo().getName())?datalist.get(pos).getMessInfo().getUserInfo().getName():datalist.get(pos).getMessInfo().getUserInfo().getNickName();
                if (DataCache.getInstance().isLoginNim()){
                    Log.i("tag00","网易在线");
                    if (complete){
                        SessionHelper.startP2PSession(PictrueConsultActivity.this, datalist.get(pos).getMessInfo().getUserInfo().getUserName()+"_user", P2PMessageActivity.Status.COMPLETEDOCTOR,name);
                    }else {
                        SessionHelper.startP2PSession(PictrueConsultActivity.this, datalist.get(CurrentPosition).getMessInfo().getUserInfo().getUserName()+"_user",P2PMessageActivity.Status.NOCOMPLETE,name);
                    }
                }else {
                    Log.i("tag00","网易不在线");
                    LoginManager.loginNim(PictrueConsultActivity.this, new LoginManager.LognimCallback() {
                        @Override
                        public void logNimCallback() {
                            if (complete){
                                SessionHelper.startP2PSession(PictrueConsultActivity.this, datalist.get(CurrentPosition).getMessInfo().getUserInfo().getUserName()+"_user", P2PMessageActivity.Status.COMPLETEDOCTOR,name);
                            }else {
                                SessionHelper.startP2PSession(PictrueConsultActivity.this, datalist.get(CurrentPosition).getMessInfo().getUserInfo().getUserName()+"_user",P2PMessageActivity.Status.NOCOMPLETE,name);
                            }
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

        callback = new RaspberryCallback<ListResponse<PictureListBean>>() {

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                initView();
            }

            @Override
            public void onSuccess(ListResponse<PictureListBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData() != null && response.getData().size() > 0){
                        pictureListBeanList.clear();
                        pictureListBeanList.addAll(response.getData());
                        assemblingData(pictureListBeanList);
                    }else {
                        initView();
                    }
                }else {
                    initView();
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setShowDialog(true);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Preference.PICTURE_COMPLETE && resultCode == Preference.PICTURE_COMPLETE_RESULT){
            datalist.clear();
            pictureListBeanList.clear();
            recentContactList.clear();
            callback.setShowDialog(false);
            swipeRefreshLayout.setRefreshing(true);
            adapter.notifyDataSetChanged();
            loadNetData();
        }
    }

    /**
     * 组装数据
     *
     * @param pictureListBeanList
     */
    private void assemblingData(List<PictureListBean> pictureListBeanList) {
        if (null != pictureListBeanList) {
            for (PictureListBean messInfo : pictureListBeanList) {
                ConsultMessage message = new ConsultMessage();
                message.setMessInfo(messInfo);
                Log.i("tag00","用户名："+messInfo.getUserInfo().getNickName());
                datalist.add(message);
            }
            requestMessages(true);
        }else {
            initView();
        }
    }

    private void requestMessages(boolean b) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || recents == null) {
                            initView();
                            return;
                        }
                        recentContactList = recents;

                        // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
                        onRecentContactsLoaded();
                    }
                });
            }
        }, b ? 250 : 0);
    }

    /**
     * 将网易最近聊天记录同步至图文咨询
     */
    private void onRecentContactsLoaded() {
        //        dataList.clear();
        if (recentContactList != null) {
            for (int i = 0; i < datalist.size(); i++) {
                ConsultMessage message = datalist.get(i);
                for (RecentContact contact : recentContactList) {
                    if (null != message.getMessInfo().getUserInfo() && (message.getMessInfo().getUserInfo().getUserName()+"_user").equals(contact.getContactId())) {
                        message.setContact(contact);
                    }
                }
            }
        }
        initView();
    }

    @OnClick(R.id.back_iv)
    public void picOnClick(View view){
        finish();
    }


    public void registerBoradcastReceiver(){
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Preference.ACTION_NAME);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        registerObservers(false);
    }
}
