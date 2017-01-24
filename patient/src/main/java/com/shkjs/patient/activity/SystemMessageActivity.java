package com.shkjs.patient.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.bean.push.PushMessage;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.OperateType;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.PushMessageManager;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by xiaohu on 2016/9/23.
 * <p/>
 * 系统消息
 */
public class SystemMessageActivity extends BaseActivity {

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<PushMessage> datalist;
    private BaseRecyclerAdapter<PushMessage> adapter;

    private Page page;
    private int pageNumber = 1;
    private boolean isFirst = true;//是否为第一次下拉刷新
    private boolean isPullDown = true;//是否为下拉刷新
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isAllowPullUp = true;//是否允许上拉加载更多

    private static final int UPDATE_VIEW = 121;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    complete();
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_message);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.system_message_text);

        initData();
        initListener();

        PushMessageManager.getInstance().dismissAllNotify();
        queryUserPushMessage(isPullDown);
        if (DataCache.getInstance().getUnReadNum() > 0) {
            uploadPushMessageRead();
        }
    }

    private void initData() {

        page = new Page();

        noMessageTV.setText("暂无系统消息~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_message);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<PushMessage>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.system_message_item_1;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final PushMessage item) {
                holder.getTextView(R.id.system_message_time_tv).setText(TimeFormatUtils.getLocalTime("yyyy-MM-dd " +
                        "HH:mm", Long.parseLong(item.getCreateDate())));
                holder.setText(R.id.system_message_content_tv, item.getContent());
                holder.setText(R.id.system_message_description_tv, item.getTitle());
                //                BGABadgeImageView imageView = (BGABadgeImageView) holder.getView(R.id
                // .system_message_icon_iv);
                //                final LinearLayout layout = (LinearLayout) holder.getView(R.id
                // .system_message_item_ll);
                //                if (!TextUtils.isEmpty(item.getStatus()) && item.getStatus().equals("1")) {//已读
                //                    imageView.hiddenBadge();
                //                    //                    layout.setEnabled(false);
                //                    layout.setOnClickListener(new View.OnClickListener() {
                //                        @Override
                //                        public void onClick(View v) {
                //                            uploadPushMessageRead(item);
                //                            Intent intent = new Intent();
                //                            switch (item.getAction()) {
                //                                case RECHARGE:
                //                                case PAY:
                //                                case FAMILY_PAY:
                //                                    intent.setClass(SystemMessageActivity.this, MineAccountActivity
                // .class);
                //                                    startActivity(intent);
                //                                    break;
                //                                case FAMILY:
                //                                    intent.setClass(SystemMessageActivity.this, HomeGroupActivity
                // .class);
                //                                    startActivity(intent);
                //                                    break;
                //                                case ORDER_CANCEL:
                //                                case ORDER_OVERTIME:
                //                                    intent.setClass(SystemMessageActivity.this, RefundActivity.class);
                //                                    intent.putExtra(Preference.ID, item.getOrderId());
                //                                    startActivity(intent);
                //                                    break;
                //                                case DIAGNOSE:
                //                                    break;
                //                                case GROUP_DIAGNOSE:
                //                                    multVideoPay(item);
                //                                    break;
                //                                default:
                //                                    break;
                //                            }
                //                            DataCache.getInstance().setUnReadNum(DataCache.getInstance()
                // .getUnReadNum() - 1);
                //                            datalist.get(position).setStatus("1");
                //                            layout.setEnabled(false);
                //                            adapter.notifyItemChanged(position);
                //                        }
                //                    });
                //                } else {
                //                    imageView.showCirclePointBadge();
                //                    layout.setEnabled(true);
                //                    layout.setOnClickListener(new View.OnClickListener() {
                //                        @Override
                //                        public void onClick(View v) {
                //                            uploadPushMessageRead(item);
                //                            Intent intent = new Intent();
                //                            switch (item.getAction()) {
                //                                case RECHARGE:
                //                                case PAY:
                //                                case FAMILY_PAY:
                //                                    intent.setClass(SystemMessageActivity.this, MineAccountActivity
                // .class);
                //                                    startActivity(intent);
                //                                    break;
                //                                case FAMILY:
                //                                    intent.setClass(SystemMessageActivity.this, HomeGroupActivity
                // .class);
                //                                    startActivity(intent);
                //                                    break;
                //                                case ORDER_CANCEL:
                //                                case ORDER_OVERTIME:
                //                                    intent.setClass(SystemMessageActivity.this, RefundActivity.class);
                //                                    intent.putExtra(Preference.ID, item.getOrderId());
                //                                    startActivity(intent);
                //                                    break;
                //                                case DIAGNOSE:
                //                                    break;
                //                                case GROUP_DIAGNOSE:
                //                                    multVideoPay(item);
                //                                    break;
                //                                default:
                //                                    break;
                //                            }
                //                            DataCache.getInstance().setUnReadNum(DataCache.getInstance()
                // .getUnReadNum() - 1);
                //                            datalist.get(position).setStatus("1");
                //                            layout.setEnabled(false);
                //                            adapter.notifyItemChanged(position);
                //                        }
                //                    });
                //                }
            }
        };
    }

    private void initListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullDown = true;
                queryUserPushMessage(isPullDown);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && RecyclerViewUtlis.canPullUp(recyclerView)) {
                    if (isAllowPullUp) {
                        isPullDown = false;
                        queryUserPushMessage(isPullDown);
                    } else {
                        ToastUtils.showToast("暂无更多数据~");
                    }
                }
            }
        });
    }

    private void queryUserPushMessage(final boolean isPullDown) {

        if (isRefreshing) {
            ToastUtils.showToast("正在刷新，请稍后重试");
            return;
        }
        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<PushMessage>> callback = new
                RaspberryCallback<ListPageResponse<PushMessage>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                ToastUtils.showToast("网络异常，请稍后重试");
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ListPageResponse<PushMessage> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        messageFactory(response.getData());
                        if (isPullDown) {
                            int position;
                            for (PushMessage message : response.getData()) {
                                if (datalist.contains(message)) {
                                    position = datalist.indexOf(message);
                                    datalist.remove(position);
                                    datalist.add(position, message);
                                } else {
                                    if (isFirst) {
                                        datalist.add(message);
                                    } else {
                                        datalist.add(0, message);
                                    }
                                }
                            }
                            isFirst = false;
                        } else {
                            pageNumber = response.getPageNum();
                            for (PushMessage message : response.getData()) {
                                if (!datalist.contains(message)) {
                                    datalist.add(message);
                                }
                            }
                        }

                        page.setPageNum(response.getPageNum());
                        page.setPageSize(response.getPageSize());
                        page.setTotalCount(response.getTotalCount());
                        if (page.getPageNum() * page.getPageSize() >= page.getTotalCount()) {
                            isAllowPullUp = false;
                        }
                        handler.sendEmptyMessage(UPDATE_VIEW);
                        return;
                    }
                }
                ToastUtils.showToast("查询数据失败，请稍后重试");
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

        };

        callback.setMainThread(false);

        if (isPullDown) {
            page.setPageNum(1);
        } else {
            page.setPageNum(pageNumber + 1);
        }
        HttpProtocol.queryUserPushMessage(page, callback);
        isRefreshing = true;
    }

    private void complete() {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        if (datalist.size() == 0) {
            noMessageTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMessageTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void messageFactory(List<PushMessage> datalist) {
        int size = datalist.size();
        PushMessage message;
        for (int i = 0; i < size; i++) {
            message = datalist.get(i);
            messageFactory(message);
        }
    }

    private void messageFactory(PushMessage message) {
        JSONObject object = JSON.parseObject(message.getContent());
        String title = "";
        String type = "";
        String content = "";
        OrderSource source = null;
        switch (message.getAction()) {
            case RECHARGE:
                title = "充值成功";
                content = getString(R.string.notifiaction_recharge_success);
                content = String.format(content, object.getString("dateTime"), SpliceUtils.formatBalance(object
                        .getLong("money")));
                break;
            case PAY:
                title = "支付成功";
                source = OrderSource.values()[object.getIntValue("orderSource")];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                content = getString(R.string.notifiaction_pay_success);
                content = String.format(content, object.getString("dateTime"), type, SpliceUtils.formatBalance(object
                        .getLong("money")));
                break;
            case FAMILY_PAY:
                title = "支付成功";
                source = OrderSource.values()[object.getIntValue("orderSource")];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                if (!object.getBoolean("isUser")) {//户主
                    content = getString(R.string.notifiaction_household_head_pay_success);
                    content = String.format(content, object.getString("name"), object.getString("dateTime"), type,
                            SpliceUtils.formatBalance(object.getLong("money")));
                } else { //成员
                    content = getString(R.string.notifiaction_household_pay_success);
                    content = String.format(content, object.getString("dateTime"), type, SpliceUtils.formatBalance
                            (object.getLong("money")));
                }
                break;
            case FAMILY:
                title = "家庭组消息";
                OperateType operateType = OperateType.values()[object.getIntValue("oper")];
                switch (operateType) {
                    case ADD:
                        //被添加
                        content = getString(R.string.notifiaction_add_member_success);
                        content = String.format(content, object.getString("name"), object.getString("dateTime"));
                        break;
                    case DEL:
                        //被删除
                        content = getString(R.string.notifiaction_delete_member_success);
                        content = String.format(content, object.getString("name"), object.getString("dateTime"));
                        break;
                    case QUIT:
                        //退出
                        content = getString(R.string.notifiaction_quit_household_success);
                        content = String.format(content, object.getString("name"), object.getString("dateTime"));
                        break;
                    default:
                        break;
                }
                break;
            case ORDER_CANCEL:
                title = "订单取消";
                source = OrderSource.values()[object.getIntValue("orderSource")];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                CancelType cancelType = CancelType.values()[object.getIntValue("cancelType")];
                switch (cancelType) {
                    case ADMIN:
                        content = getString(R.string.notifiaction_expire_order_success);
                        content = String.format(content, object.getString("name"), type);
                        break;
                    case SYSTEM:
                        break;
                    case DOCTOR:
                        content = getString(R.string.notifiaction_cancel_order_success);
                        content = String.format(content, object.getString("name"), object.getString("dateTime"),
                                object.getString("subscribeTime"), type);
                        break;
                    case USER:
                        break;
                    default:
                        break;
                }
                message.setOrderId(object.getLongValue("orderId"));
                break;
            case ORDER_OVERTIME:
                title = "订单超时";
                source = OrderSource.values()[object.getIntValue("orderSource")];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                content = getString(R.string.notifiaction_expire_order_success);
                content = String.format(content, object.getString("subscribeTime"), type);
                message.setOrderId(object.getLongValue("orderId"));
                break;
            case GROUP_DIAGNOSE:
                title = "视频会诊";
                content = getString(R.string.notifiaction_group_sitdiagnose_order_success);
                String doctorName = "";
                JSONArray nameArray = object.getJSONArray("doctorNames");
                if (null != nameArray) {
                    for (int i = 0; i < nameArray.size(); i++) {
                        doctorName = doctorName + nameArray.getString(i) + " ";
                    }
                }
                content = String.format(content, object.getString("createName"), doctorName);
                message.setOrderId(object.getLongValue("orderId"));
                message.setDiagnoseTime(object.getLongValue("diagnoseTime"));
                JSONArray idArray = object.getJSONArray("doctorIds");
                if (null != nameArray && null != idArray) {
                    Doctor doctor;
                    ArrayList<Doctor> doctors = new ArrayList<>();
                    for (int i = 0; i < idArray.size(); i++) {
                        doctor = new Doctor();
                        if (i == 0) {
                            doctor.setName(object.getString("createName"));
                        } else {
                            doctor.setName(nameArray.getString(i - 1));
                        }
                        doctor.setId(idArray.getLong(i));
                        doctors.add(doctor);
                    }
                    message.setDoctors(doctors);
                }
                break;
            default:
                break;
        }
        message.setTitle(title);
        message.setContent(content);
    }

    private void multVideoPay(PushMessage message) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setDoctors((ArrayList<Doctor>) message.getDoctors());

        Order order = new Order();
        order.setId(message.getOrderId());
        orderInfo.setOrder(order);

        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", message.getDiagnoseTime()) + "" +
                " " +
                DateUtil.DateToWeek(new Date(message.getDiagnoseTime())) + " " +
                TimeFormatUtils.getLocalTime("HH:mm", message.getDiagnoseTime());
        orderInfo.setTime(timeStr);

        orderInfo.setOrderInfoType(OrderInfoType.MULTIVIDEO);

        PayActivity.start(SystemMessageActivity.this, orderInfo);
    }

    /**
     * 上传消息为已读
     */
    private void uploadPushMessageRead(PushMessage message) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
            }
        };

        callback.setMainThread(false);

        HttpProtocol.uploadPushMessageReadStatus(message.getId(), callback);
    }

    /**
     * 上传消息为已读
     */
    private void uploadPushMessageRead() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        DataCache.getInstance().setUnReadNum(0);
                        Intent intent = new Intent(Preference.UPDATE_VIEW_ACTION);
                        sendBroadcast(intent);
                    }
                }
            }
        };

        callback.setMainThread(false);

        HttpProtocol.uploadPushMessageReadStatus(callback);
    }
}
