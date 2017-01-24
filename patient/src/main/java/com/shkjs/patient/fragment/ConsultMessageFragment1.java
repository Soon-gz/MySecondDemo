package com.shkjs.patient.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.viewholder.RecentViewHolder;
import com.netease.nim.uikit.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.session.helper.MessageHelper;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.PopupWindowUtils;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.raspberry.library.view.SwipeItemLayout;
import com.shkjs.nim.chat.activity.AVChatRoomActivity;
import com.shkjs.nim.chat.session.SessionHelper;
import com.shkjs.nim.em.ClientType;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.DoctorDetailActivity;
import com.shkjs.patient.activity.MainActivity;
import com.shkjs.patient.activity.OrderTimeActivity;
import com.shkjs.patient.activity.PayActivity;
import com.shkjs.patient.activity.RefundActivity;
import com.shkjs.patient.activity.SubmitPictureOrderActivity;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.ConsultMessage;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.GroupSitDiagnoseDoctors;
import com.shkjs.patient.bean.GroupSitDiagnoseDoctorsInfo;
import com.shkjs.patient.bean.MessInfo;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.DoctorPlatformLevel;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeLinearLayout;

import static android.app.Activity.RESULT_OK;
import static com.raspberry.library.util.HandlerUtils.runOnUiThread;
import static com.shkjs.patient.data.em.OrderSource.GROUP_SIT_DIAGNOSE;
import static com.shkjs.patient.data.em.OrderSource.GROUP_SIT_DIAGNOSE_DETAIL;
import static com.shkjs.patient.data.em.OrderSource.SIT_DIAGNOSE_RESERVE;

/**
 * Created by LENOVO on 2016/8/17.
 * <p/>
 * 咨询消息Fragment，展示咨询消息记录，包括视频和图文
 */

public class ConsultMessageFragment1 extends BaseFragment {

    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    private Context context;

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private BaseRecyclerAdapter<ConsultMessage> adapter;

    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag

    // data
    private List<ConsultMessage> dataList;
    private List<MessInfo> videoList;
    private List<MessInfo> pictureList;

    private boolean msgLoaded = false;
    private boolean isFirst = true;

    private UserInfoObservable.UserInfoObserver userInfoObserver;
    private List<RecentContact> loadedRecents;

    //分页信息
    private Page page;

    //视频位置
    private int videoPos = -1;

    private int position;
    private boolean noComplete = false;

    //退款界面
    private PopupWindow popupWindow;
    private final static int REORDER_TYPE_PICTURE = 121;//预约类型（图文）
    private final static int REORDER_TYPE_VIDEO = 122;//预约类型（视频）
    private final static int REFUND = 123;//退款
    private final static int CANCEL = 124;//取消
    private final static int PICTURE = 125;//图文
    private final static int VIDEO = 126;//视频
    private final static int PAY = 127;//支付

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PICTURE:
                    assemblingData();
                    //                    assemblingData(videoList);
                    //                    assemblingData(pictureList);
                    break;
                case VIDEO:
                    queryMessInquiryReserveList();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_consult_message, null);//使用container会异常

        //绑定控件
        ButterKnife.bind(this, view);
        initData();
        initListener();
        registerObservers(true);

        isPrepared = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setlazyLoad();//加载数据
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void initData() {

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);
        noMessageTV.setText("您还没有咨询过医生哦~");

        dataList = new ArrayList<>();
        videoList = new ArrayList<>();
        pictureList = new ArrayList<>();

        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);

        adapter = new BaseRecyclerAdapter<ConsultMessage>(context, dataList) {

            @Override
            public int getItemViewType(int position) {
                if (dataList.get(position).getMessInfo().getSource().equals(GROUP_SIT_DIAGNOSE)) {
                    return GROUP_SIT_DIAGNOSE.ordinal();//视频会诊
                } else if (dataList.get(position).getMessInfo().getSource().equals(GROUP_SIT_DIAGNOSE_DETAIL)) {
                    return GROUP_SIT_DIAGNOSE_DETAIL.ordinal();//视频子会诊
                } else if (dataList.get(position).getMessInfo().getSource().equals(SIT_DIAGNOSE_RESERVE)) {
                    return SIT_DIAGNOSE_RESERVE.ordinal();//视频坐诊
                } else {
                    return OrderSource.INQUIRY_RESERVE.ordinal();//图文咨询
                }
            }

            @Override
            public int getItemLayoutId(int viewType) {
                int id = 0;
                switch (OrderSource.values()[viewType]) {
                    case INQUIRY_RESERVE:
                        id = R.layout.consult_picture_message_item;
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        id = R.layout.consult_video_message_item;
                        break;
                    case GROUP_SIT_DIAGNOSE:
                    case GROUP_SIT_DIAGNOSE_DETAIL:
                        id = R.layout.consult_video_sitting_message_item;
                        break;
                    default:
                        break;
                }
                return id;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final ConsultMessage item) {

                switch (OrderSource.values()[getItemViewType(position)]) {
                    case INQUIRY_RESERVE:
                        initPictureView(holder, item);
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        initVideoView(holder, position, item);
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        initVideoSittingView(position, holder, item);
                        break;
                    case GROUP_SIT_DIAGNOSE_DETAIL:
                        initVideoSittingView(position, holder, item);
                        break;
                    default:
                        break;
                }
            }
        }

        ;

    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取数据
                for (ConsultMessage message : dataList) {
                    message.setDelete(true);
                }
                noComplete = false;
                queryMessVideoList();
                //                queryMessInquiryReserveList();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        //        recyclerView.addItemDecoration(new RecycleViewDecoration(context));// 添加分割线。
        // 添加滚动监听。
        recyclerView.addOnScrollListener(mOnScrollListener);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载数据的方法，只要保证isPrepared和isVisible都为true的时候才往下执行开始加载数据
     */
    @Override
    protected void setlazyLoad() {
        super.setlazyLoad();
        if (!isPrepared || !isVisible) {
            return;
        }
        //        if (isFirst) {
        //获取数据
        isFirst = false;
        swipeRefreshLayout.setRefreshing(true);
        for (ConsultMessage message : dataList) {
            message.setDelete(true);
        }
        noComplete = false;
        queryMessVideoList();
        //        queryMessInquiryReserveList();
        //        }
    }

    /**
     * 刷新界面
     */
    private void notifyDataSetChanged() {
        notifyDataSetChanged(false);
    }

    /**
     * 刷新界面
     */
    private void notifyDataSetChanged(boolean isRefresh) {
        boolean empty = dataList.isEmpty();
        noMessageTV.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(!empty ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        if (isRefresh) {
            countPosition();
        }
    }

    /**
     * 图文咨询界面展示与事件监听
     *
     * @param holder
     * @param consultMessage
     */
    private void initPictureView(BaseRecyclerViewHolder holder, final ConsultMessage consultMessage) {
        holder.setClickListener(R.id.consult_message_doctor_icon_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//进入医生详情界面
                Intent intent = new Intent(context, DoctorDetailActivity.class);
                intent.putExtra(Preference.DOCTOR_DETAIL, consultMessage.getMessInfo().getDoctor().getId());
                context.startActivity(intent);
            }
        });
        holder.setClickListener(R.id.message_sl, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//进入聊天界面
                P2PMessageActivity.Status status = P2PMessageActivity.Status.NOCOMPLETE;
                switch (consultMessage.getMessInfo().getStatus()) {
                    case EXPIRE:
                    case COMPLETE:
                    case CANCEL:
                    case REFUND:
                        status = P2PMessageActivity.Status.COMPLETE;
                        break;
                    default:
                        break;
                }
                if (!DataCache.getInstance().isLoginNim()) {
                    final P2PMessageActivity.Status finalStatus = status;
                    LoginManager.loginNim(context, new LoginManager.UserInfoListener() {
                        @Override
                        public void onFinish() {
                            SessionHelper.startP2PSession(context, consultMessage.getMessInfo().getDoctor()
                                    .getUserName() + MyApplication.NIM_DOCTOR_NAME, finalStatus, consultMessage
                                    .getMessInfo().getDoctor().getName());
                        }
                    });
                } else {
                    SessionHelper.startP2PSession(context, consultMessage.getMessInfo().getDoctor().getUserName() +
                            MyApplication.NIM_DOCTOR_NAME, status, consultMessage.getMessInfo().getDoctor().getName());
                }
            }
        });

        holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.GONE);
        if (null != consultMessage.getMessInfo().getDoctor()) {
            //医生姓名
            holder.getTextView(R.id.doctor_name_tv).setText(consultMessage.getMessInfo().getDoctor().getName());
            //头像
            Glide.with(context).load(HttpBase.BASE_OSS_URL + consultMessage.getMessInfo().getDoctor().getHeadPortrait
                    ()).error(R.drawable.main_headportrait_grey).placeholder(R.drawable.main_headportrait_grey)
                    .into(holder.getImageView(R.id.consult_message_doctor_icon_iv));
            if (null != consultMessage.getMessInfo().getDoctor().getPlatformLevel() && consultMessage.getMessInfo()
                    .getDoctor().getPlatformLevel().equals(DoctorPlatformLevel.AUTHORITY)) {
                holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.VISIBLE);
            }
        }
        //内容展示
        BGABadgeLinearLayout layout = (BGABadgeLinearLayout) holder.getView(R.id.message_number_ll);
        layout.hiddenBadge();
        if (null != consultMessage.getContact()) {
            holder.getTextView(R.id.message_content_tv).setText(consultMessage.getContact().getContent());
            holder.getTextView(R.id.message_time_tv).setText(TimeFormatUtils.getTimeShowString(consultMessage
                    .getContact().getTime(), false));
            if (consultMessage.getContact().getUnreadCount() > 99) {
                layout.showTextBadge("99+");
            } else if (consultMessage.getContact().getUnreadCount() > 0) {
                layout.showTextBadge("" + consultMessage.getContact().getUnreadCount());
            }
        } else {
            holder.getTextView(R.id.message_content_tv).setText("");
            //            holder.getTextView(R.id.message_time_tv).setText(TimeFormatUtils.getTimeShowString(Long
            // .parseLong
            //                    (consultMessage.getMessInfo().getDiagnoseDate()), false));
            holder.getTextView(R.id.message_time_tv).setText(TimeFormatUtils.getLocalTime("MM-dd", Long.parseLong
                    (consultMessage.getMessInfo().getDiagnoseDate())));
        }
        //是否可退款
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout) holder.getView(R.id.message_sl);
        swipeItemLayout.close();
        TextView statusTV = holder.getTextView(R.id.message_status_tv);
        statusTV.setVisibility(View.GONE);
        swipeItemLayout.setSwipeAble(false);
        swipeItemLayout.setOnLongClickListener(null);
        if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.CANCEL)) {
            statusTV.setVisibility(View.VISIBLE);
            statusTV.setText("可退款");
            swipeItemLayout.setSwipeAble(true);
            holder.setClickListener(R.id.reorder_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 再预约
                    //                    orderPicture(consultMessage.getMessInfo().getDoctor());
                    SubmitPictureOrderActivity.start(context, consultMessage.getMessInfo().getDoctor());
                    swipeItemLayout.close();
                }
            });
            holder.setClickListener(R.id.refund_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 退款
                    Intent intent = new Intent(context, RefundActivity.class);
                    intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                    position = dataList.indexOf(consultMessage);
                    context.startActivity(intent);
                    swipeItemLayout.close();
                }
            });
            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createDeletePopup(swipeItemLayout, consultMessage, REORDER_TYPE_PICTURE);
                    return true;
                }
            });
        } else if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.PAID)) {
            statusTV.setVisibility(View.VISIBLE);
            statusTV.setText("咨询中");
        } else if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.COMPLETE)) {
            statusTV.setVisibility(View.VISIBLE);
            statusTV.setText("已结束");
        }
    }

    /**
     * 视频坐诊界面展示与事件监听
     *
     * @param holder
     * @param consultMessage
     */
    private void initVideoView(BaseRecyclerViewHolder holder, final int position, final ConsultMessage consultMessage) {
        holder.setClickListener(R.id.consult_message_doctor_icon_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//进入医生详情界面
                Intent intent = new Intent(context, DoctorDetailActivity.class);
                intent.putExtra(Preference.DOCTOR_DETAIL, consultMessage.getMessInfo().getDoctor().getId());
                context.startActivity(intent);
            }
        });
        holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.GONE);
        if (null != consultMessage.getMessInfo().getDoctor()) {

            //医生姓名
            holder.getTextView(R.id.consult_message_doctor_name_tv).setText(consultMessage.getMessInfo().getDoctor()
                    .getName());

            //头像
            Glide.with(context).load(HttpBase.BASE_OSS_URL + consultMessage.getMessInfo().getDoctor().getHeadPortrait
                    ()).error(R.drawable.main_headportrait_grey).placeholder(R.drawable.main_headportrait_grey)
                    .into(holder.getImageView(R.id.consult_message_doctor_icon_iv));
            //医生级别
            holder.getTextView(R.id.consult_message_doctor_level_tv).setText(consultMessage.getMessInfo().getDoctor()
                    .getLevel().getMark());
            if (null != consultMessage.getMessInfo().getDoctor().getPlatformLevel() && consultMessage.getMessInfo()
                    .getDoctor().getPlatformLevel().equals(DoctorPlatformLevel.AUTHORITY)) {
                holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.VISIBLE);
            }
        }
        //时间展示
        holder.getTextView(R.id.consult_message_week_time_tv).setText(DateUtil.DateToWeek(new Date(Long.parseLong
                (consultMessage.getMessInfo().getDiagnoseDate()))));
        holder.getTextView(R.id.consult_message_specific_time_tv).setText(SpliceUtils.getTime(consultMessage
                .getMessInfo().getDiagnoseDate()));
        holder.getTextView(R.id.consult_message_date_time_tv).setText(TimeFormatUtils.getLocalTime(Long.parseLong
                (consultMessage.getMessInfo().getDiagnoseDate())));

        //是否可退款
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout) holder.getView(R.id.message_sl);
        swipeItemLayout.close();
        swipeItemLayout.setSwipeAble(false);
        //状态展示
        if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.PAID)) {
            holder.getTextView(R.id.consult_message_status_tv).setVisibility(View.VISIBLE);
            holder.getTextView(R.id.consult_message_status_tv).setTextColor(ContextCompat.getColor(context, R.color
                    .black_231815));
            holder.getTextView(R.id.consult_message_status_tv).setText("待诊");
            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createCancelPopup(swipeItemLayout, consultMessage, CANCEL);
                    return true;
                }
            });

            holder.setClickListener(R.id.message_sl, new View.OnClickListener() {
                @Override
                public void onClick(View v) {//进入医生详情界面
                    ToastUtils.showToast("待医生视频呼叫");
                }
            });
            swipeItemLayout.setSwipeAble(true);
            holder.getView(R.id.reorder_tv).setVisibility(View.GONE);
            TextView refundTV = holder.getTextView(R.id.refund_tv);
            refundTV.setText("取消");
            refundTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消订单
                    Intent intent = new Intent(context, RefundActivity.class);
                    intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                    intent.putExtra(Preference.TYPE, CancelType.USER);
                    ConsultMessageFragment1.this.position = dataList.indexOf(consultMessage);
                    startActivityForResult(intent, CANCEL);
                    swipeItemLayout.close();
                }
            });

        } else if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.CANCEL)) {
            holder.getTextView(R.id.consult_message_status_tv).setVisibility(View.VISIBLE);
            holder.getTextView(R.id.consult_message_status_tv).setTextColor(ContextCompat.getColor(context, R.color
                    .red_e84618));
            holder.getTextView(R.id.consult_message_status_tv).setText("可退款");
            swipeItemLayout.setSwipeAble(true);
            TextView reorderTV = holder.getTextView(R.id.reorder_tv);
            reorderTV.setVisibility(View.VISIBLE);
            reorderTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 再预约
                    OrderTimeActivity.start(context, consultMessage.getMessInfo().getDoctor());//选择视频预约时间
                    swipeItemLayout.close();
                }
            });
            TextView refundTV = holder.getTextView(R.id.refund_tv);
            refundTV.setText("退款");
            refundTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 退款
                    Intent intent = new Intent(context, RefundActivity.class);
                    intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                    ConsultMessageFragment1.this.position = dataList.indexOf(consultMessage);
                    context.startActivity(intent);
                    swipeItemLayout.close();
                }
            });
            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createDeletePopup(swipeItemLayout, consultMessage, REORDER_TYPE_VIDEO);
                    return true;
                }
            });
            holder.setClickListener(R.id.message_sl, null);
        } else {
            holder.getTextView(R.id.consult_message_status_tv).setVisibility(View.GONE);
            swipeItemLayout.setOnLongClickListener(null);
        }

        if (position == getVideoPosition()) {
            holder.getView(R.id.consult_message_divider_iv).setVisibility(View.VISIBLE);
        } else {
            holder.getView(R.id.consult_message_divider_iv).setVisibility(View.GONE);
        }
    }

    /**
     * 视频会诊界面展示与事件监听
     *
     * @param holder
     * @param consultMessage
     */
    private void initVideoSittingView(int position, BaseRecyclerViewHolder holder, final ConsultMessage
            consultMessage) {
        //头像
        Glide.with(context).load(R.drawable.main_consultation_groupconsultation).error(R.drawable
                .main_consultation_groupconsultation).placeholder(R.drawable.main_consultation_groupconsultation)
                .transform(new CircleTransform(context)).into(holder.getImageView(R.id.consult_message_doctor_icon_iv));
        //时间展示
        holder.getTextView(R.id.consult_message_week_time_tv).setText(DateUtil.DateToWeek(new Date(Long.parseLong
                (consultMessage.getMessInfo().getDiagnoseDate()))));
        holder.getTextView(R.id.consult_message_specific_time_tv).setText(TimeFormatUtils.getLocalTime("HH:mm", Long
                .parseLong(consultMessage.getMessInfo().getDiagnoseDate())));
        holder.getTextView(R.id.consult_message_date_time_tv).setText(TimeFormatUtils.getLocalTime(Long.parseLong
                (consultMessage.getMessInfo().getDiagnoseDate())));

        //是否可退款
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout) holder.getView(R.id.message_sl);
        swipeItemLayout.close();
        swipeItemLayout.setSwipeAble(false);
        if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.AGREE)) {
            holder.getTextView(R.id.consult_message_join_tv).setTextColor(ContextCompat.getColor(context, R.color
                    .red_e84618));
            holder.getTextView(R.id.consult_message_join_tv).setText("未支付");
            holder.getTextView(R.id.refund_tv).setText("支付");
            holder.getTextView(R.id.consult_message_join_tv).setBackground(null);
            swipeItemLayout.setSwipeAble(true);
            holder.setClickListener(R.id.refund_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //支付
                    multVideoPay(consultMessage);
                    swipeItemLayout.close();
                }
            });
            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    createCancelPopup(swipeItemLayout, consultMessage, PAY);
                    return true;
                }
            });
        } else if (consultMessage.getMessInfo().getStatus().equals(OrderStatus.CANCEL)) {
            holder.getTextView(R.id.consult_message_join_tv).setTextColor(ContextCompat.getColor(context, R.color
                    .red_e84618));
            holder.getTextView(R.id.consult_message_join_tv).setText("可退款");
            holder.getTextView(R.id.refund_tv).setText("退款");
            holder.getTextView(R.id.consult_message_join_tv).setBackground(null);
            swipeItemLayout.setSwipeAble(true);
            holder.setClickListener(R.id.refund_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 退款
                    Intent intent = new Intent(context, RefundActivity.class);
                    intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                    ConsultMessageFragment1.this.position = dataList.indexOf(consultMessage);
                    context.startActivity(intent);
                    swipeItemLayout.close();
                }
            });
            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    createCancelPopup(swipeItemLayout, consultMessage, REFUND);
                    return true;
                }
            });
        } else {
            holder.getTextView(R.id.consult_message_join_tv).setTextColor(ContextCompat.getColor(context, R.color
                    .white));
            holder.getTextView(R.id.consult_message_join_tv).setText("进入");
            holder.getTextView(R.id.consult_message_join_tv).setBackgroundResource(R.drawable.btn_select_style);
            holder.setClickListener(R.id.consult_message_join_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 是否可以进入视频会诊
                    //                    queryEnterrRoom(consultMessage.getMessInfo().getId());
                    queryEnterrRoomAndDoctorIds(consultMessage.getMessInfo().getId());
                }
            });

            swipeItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    createCancelPopup(swipeItemLayout, consultMessage, CANCEL);
                    return true;
                }
            });
        }

        if (position == getVideoPosition()) {
            holder.getView(R.id.consult_message_divider_iv).setVisibility(View.VISIBLE);
        } else {
            holder.getView(R.id.consult_message_divider_iv).setVisibility(View.GONE);
        }
    }

    /**
     * 创建退款界面
     */
    private void createDeletePopup(View rootView, final ConsultMessage consultMessage, final int type) {
        View view = LinearLayout.inflate(context, R.layout.popup_refund, null);
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable());//不设置，点击外部popup不消失
        popupWindow.setOutsideTouchable(true);

        view.findViewById(R.id.reorder_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 再预约
                if (type == REORDER_TYPE_PICTURE) {
                    //                    orderPicture(consultMessage.getMessInfo().getDoctor());
                    SubmitPictureOrderActivity.start(context, consultMessage.getMessInfo().getDoctor());
                } else if (type == REORDER_TYPE_VIDEO) {
                    OrderTimeActivity.start(context, consultMessage.getMessInfo().getDoctor());//选择视频预约时间
                }
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.refund_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RefundActivity.class);
                intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                position = dataList.indexOf(consultMessage);
                startActivityForResult(intent, REFUND);
                popupWindow.dismiss();
            }
        });
        int position[] = PopupWindowUtils.calculatePopWindowPos(rootView, view);
        popupWindow.showAtLocation(rootView, Gravity.TOP | Gravity.START, position[0] + rootView.getWidth() / 2 -
                view.getMeasuredWidth() / 2, position[1] - rootView.getHeight() - view.getMeasuredHeight());
    }

    /**
     * 取消订单
     *
     * @param rootView
     * @param consultMessage
     */

    private void createCancelPopup(View rootView, final ConsultMessage consultMessage, int type) {
        View view = LinearLayout.inflate(context, R.layout.popup_delete, null);
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable());//不设置，点击外部popup不消失
        popupWindow.setOutsideTouchable(true);
        TextView cancelTV = (TextView) view.findViewById(R.id.delete);
        switch (type) {
            case PAY:
                cancelTV.setText("支付");
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //支付
                        multVideoPay(consultMessage);
                        popupWindow.dismiss();
                    }
                });
                break;
            case REFUND:
                cancelTV.setText("退款");
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //退款
                        Intent intent = new Intent(context, RefundActivity.class);
                        intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                        position = dataList.indexOf(consultMessage);
                        startActivityForResult(intent, REFUND);
                        popupWindow.dismiss();
                    }
                });
                break;
            case CANCEL:
                cancelTV.setText("取消");
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消订单
                        Intent intent = new Intent(context, RefundActivity.class);
                        intent.putExtra(RefundActivity.class.getSimpleName(), consultMessage.getMessInfo().getCode());
                        intent.putExtra(Preference.TYPE, CancelType.USER);
                        position = dataList.indexOf(consultMessage);
                        startActivityForResult(intent, CANCEL);
                        popupWindow.dismiss();
                    }
                });
                break;
            default:
                break;
        }

        int position[] = PopupWindowUtils.calculatePopWindowPos(rootView, view);
        popupWindow.showAtLocation(rootView, Gravity.TOP | Gravity.START, position[0] + rootView.getWidth() / 2 -
                view.getMeasuredWidth() / 2, position[1] - rootView.getHeight() - view.getMeasuredHeight());
    }

    /**
     * 取消订单
     *
     * @param messInfo
     */
    private void submitCancelOrder(final MessInfo messInfo) {
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
                        ConsultMessage message = null;
                        for (ConsultMessage consultMessage : dataList) {
                            if (consultMessage.getMessInfo().getId().equals(messInfo.getId())) {
                                message = consultMessage;
                                break;
                            }
                        }
                        if (null != message) {
                            dataList.remove(message);
                            notifyDataSetChanged(true);
                        }
                        return;
                    }
                }

                ToastUtils.showToast("取消失败，请重试");
            }
        };

        callback.setContext(context);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.cancelOrder(messInfo.getCode(), messInfo.getSource(), callback);
    }


    /**
     * 查询视频会诊记录
     */

    private void queryMessGroupSitDiagnoseReserveList() {
        RaspberryCallback<ListPageResponse<MessInfo>> callback = new RaspberryCallback<ListPageResponse<MessInfo>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(ListPageResponse<MessInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //TODO  根据返回数据，组装数据
                        return;
                    }
                }
                ToastUtils.showToast("查询失败，请重试");
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        swipeRefreshLayout.setRefreshing(true);
        callback.setMainThread(false);

        HttpProtocol.queryMessGroupSitDiagnoseReserveList(page, callback);
    }

    /**
     * 查询视频坐诊记录
     */
    private void queryMessSitDiagnoseReserveList() {
        RaspberryCallback<ListPageResponse<MessInfo>> callback = new RaspberryCallback<ListPageResponse<MessInfo>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(ListPageResponse<MessInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //TODO  根据返回数据，组装数据
                        return;
                    }
                }
                ToastUtils.showToast("查询失败，请重试");
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryMessSitDiagnoseReserveList(page, callback);
    }

    /**
     * 查询图文咨询记录
     */
    private void queryMessInquiryReserveList() {
        RaspberryCallback<ListPageResponse<MessInfo>> callback = new RaspberryCallback<ListPageResponse<MessInfo>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                handler.sendEmptyMessage(PICTURE);
            }

            @Override
            public void onSuccess(ListPageResponse<MessInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //TODO  根据返回数据，组装数据
                        pictureList.clear();
                        pictureList.addAll(response.getData());
                        handler.sendEmptyMessage(PICTURE);
                        return;
                    }
                }
                ToastUtils.showToast("查询失败，请重试");
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryMessInquiryReserveList(page, callback);
    }

    /**
     * 查询视频坐诊和会诊记录
     */
    private void queryMessVideoList() {
        RaspberryCallback<ListPageResponse<MessInfo>> callback = new RaspberryCallback<ListPageResponse<MessInfo>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                handler.sendEmptyMessage(VIDEO);
            }

            @Override
            public void onSuccess(ListPageResponse<MessInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //TODO  根据返回数据，组装数据
                        videoList.clear();
                        videoList.addAll(response.getData());
                        handler.sendEmptyMessage(VIDEO);
                        return;
                    }
                }
                ToastUtils.showToast("查询失败，请重试");
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryMessVideoList(page, callback);
    }

    private void queryEnterrRoom(long orderId) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //进入视频会诊
                        AVChatRoomActivity.start(context, response.getData(), ClientType.PATIENT, HttpBase
                                .BASE_OSS_URL + DataCache.getInstance().getUserInfo().getHeadPortrait());
                        return;
                    }
                }
                ToastUtils.showToast("暂时不能进入视频会诊，请稍后再试");
            }
        };

        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.queryEnterrRoom(orderId, callback);
    }

    private void queryEnterrRoomAndDoctorIds(long orderId) {
        RaspberryCallback<ObjectResponse<GroupSitDiagnoseDoctorsInfo>> callback = new
                RaspberryCallback<ObjectResponse<GroupSitDiagnoseDoctorsInfo>>() {
            @Override
            public void onSuccess(final ObjectResponse<GroupSitDiagnoseDoctorsInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {

                        if (null != response.getData().getDoctors() && !com.raspberry.library.util.TextUtils.isEmpty
                                (response.getData().getRoomName())) {//进入视频会诊
                            final ArrayList<Long> doctorIds = new ArrayList<>();
                            for (GroupSitDiagnoseDoctors doctors : response.getData().getDoctors()) {
                                doctorIds.add(doctors.getDoctorId());
                            }
                            if (!DataCache.getInstance().isLoginNim()) {
                                LoginManager.loginNim(context, new LoginManager.UserInfoListener() {
                                    @Override
                                    public void onFinish() {
                                        AVChatRoomActivity.start(context, response.getData().getRoomName(),
                                                ClientType.PATIENT, HttpBase.BASE_OSS_URL + DataCache.getInstance()
                                                        .getUserInfo().getHeadPortrait(), doctorIds);
                                    }
                                });
                            } else {
                                AVChatRoomActivity.start(context, response.getData().getRoomName(), ClientType
                                        .PATIENT, HttpBase.BASE_OSS_URL + DataCache.getInstance().getUserInfo()
                                        .getHeadPortrait(), doctorIds);
                            }
                            return;
                        }
                    }
                }
                ToastUtils.showToast("暂时不能进入视频会诊，请稍后再试");
            }
        };

        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.queryEnterrRoomAndDoctorIds(orderId, callback);
    }

    /**
     * 预约图文咨询
     *
     * @param doctor 预约的医生
     */
    private void orderPicture(final Doctor doctor) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        try {
                            String json = response.getData();
                            final JSONObject object = JSON.parseObject(json);
                            final String orderCode = object.getString("code");
                            getOrderInfo(orderCode, doctor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("预约失败，请重试");
                        }
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        if (null != response.getData()) {
                            try {
                                String json = response.getData();
                                final JSONObject object = JSON.parseObject(json);
                                final String orderCode = object.getString("code");
                                OrderStatus status = OrderStatus.valueOf(object.getString("status"));
                                if (status.equals(OrderStatus.INITIAL)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            createPayView(orderCode, doctor);
                                        }
                                    });
                                } else {
                                    ToastUtils.showToast(response.getMsg());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showToast(response.getMsg());
                            }
                        } else {
                            ToastUtils.showToast(response.getMsg());
                        }
                        return;
                    }
                }
                ToastUtils.showToast("预约失败，请重试");
            }
        };
        callback.setCancelable(false);
        callback.setContext(context);
        callback.setMainThread(false);

        HttpProtocol.orderPicture(doctor.getId(), callback);
    }

    /**
     * 创建支付dialog
     *
     * @param code
     * @param doctor
     */
    private void createPayView(final String code, final Doctor doctor) {
        final AlertDialog dialog = new RoundDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);
        dialog.setView(view);

        ((TextView) view.findViewById(R.id.dialog_msg)).setText("存在未支付订单，去支付？");
        view.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getOrderInfo(code, doctor);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(context) - 2 * DisplayUtils.dip2px(context, 20);
        dialog.getWindow().setAttributes(params);
    }

    private void getOrderInfo(String code, final Doctor doctor) {
        RaspberryCallback<ObjectResponse<Order>> callback = new RaspberryCallback<ObjectResponse<Order>>() {
            @Override
            public void onSuccess(ObjectResponse<Order> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {

                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setOrder(response.getData());
                        ArrayList<Doctor> doctors = new ArrayList<>();
                        doctors.add(doctor);
                        orderInfo.setDoctors(doctors);
                        //                        orderInfo.setTime("10-31 周一 20:00--21:00");
                        orderInfo.setTime("24小时内均可咨询");
                        orderInfo.setOrderInfoType(OrderInfoType.PICTURE);

                        PayActivity.start(context, orderInfo);

                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                } else {
                    ToastUtils.showToast("预约失败，请稍后重试");
                }
            }
        };

        callback.setContext(context);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }

    private void multVideoPay(ConsultMessage message) {

        OrderInfo orderInfo = new OrderInfo();
        ArrayList<Doctor> doctors = new ArrayList<>();
        doctors.add(message.getMessInfo().getDoctor());
        orderInfo.setDoctors(doctors);

        Order order = new Order();
        order.setCode(message.getMessInfo().getCode());
        orderInfo.setOrder(order);
        orderInfo.setMuiltVideo(true);

        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", Long.parseLong(message.getMessInfo().getDiagnoseDate()
        )) + "" +
                " " +
                DateUtil.DateToWeek(new Date(Long.parseLong(message.getMessInfo().getDiagnoseDate()))) + " " +
                TimeFormatUtils.getLocalTime("HH:mm", Long.parseLong(message.getMessInfo().getDiagnoseDate()));
        orderInfo.setTime(timeStr);

        orderInfo.setOrderInfoType(OrderInfoType.MULTIVIDEO);

        PayActivity.start(context, orderInfo);
    }

    /**
     * 获取最后一条视频坐诊消息位置
     *
     * @return
     */
    private int getVideoPosition() {
        return videoPos;
    }

    /**
     * 计算最后一条视频坐诊位置
     */
    private void countPosition() {
        int temp = 0;
        int size = dataList.size();
        for (int i = size - 1; i >= 0; i--) {
            ConsultMessage message = dataList.get(i);
            if (message.isDelete()) {
                dataList.remove(i);
                continue;
            }
            if (message.getMessInfo().getStatus().equals(OrderStatus.PAID) || message.getMessInfo().getStatus()
                    .equals(OrderStatus.CANCEL)) {
                noComplete = true;
            }
            switch (message.getMessInfo().getSource()) {
                case SIT_DIAGNOSE_RESERVE:
                case GROUP_SIT_DIAGNOSE:
                case GROUP_SIT_DIAGNOSE_DETAIL:
                    temp++;
                    break;
                default:
                    break;
            }
        }
        videoPos = temp - 1;
        adapter.notifyDataSetChanged();
    }

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    /**
     * 组装数据
     */
    private void assemblingData() {
        if (null != videoList) {
            for (MessInfo messInfo : videoList) {
                ConsultMessage message = new ConsultMessage();
                message.setMessInfo(messInfo);
                if (dataList.contains(message)) {
                    dataList.remove(message);
                    dataList.add(message);
                } else {
                    dataList.add(message);
                }
            }

        }
        if (null != pictureList) {
            for (MessInfo messInfo : pictureList) {
                ConsultMessage message = new ConsultMessage();
                message.setMessInfo(messInfo);
                if (dataList.contains(message)) {
                    dataList.remove(message);
                    dataList.add(message);
                } else {
                    dataList.add(message);
                }
            }

        }
        msgLoaded = false;
        requestMessages(true);
        notifyDataSetChanged();
    }

    /**
     * 组装数据
     *
     * @param list
     */
    private void assemblingData(List<MessInfo> list) {
        if (null != list) {
            for (MessInfo messInfo : list) {
                ConsultMessage message = new ConsultMessage();
                message.setMessInfo(messInfo);
                if (dataList.contains(message)) {
                    dataList.remove(message);
                    dataList.add(message);
                } else {
                    dataList.add(message);
                }
            }
            msgLoaded = false;
            requestMessages(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 从网易获取最近聊天记录
     *
     * @param delay 延时
     */
    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (msgLoaded) {
                    return;
                }
                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || recents == null) {
                            return;
                        }
                        loadedRecents = recents;

                        // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
                        //
                        msgLoaded = true;
                        if (isAdded()) {
                            onRecentContactsLoaded();
                        }
                    }
                });
            }
        }, delay ? 250 : 0);
    }

    /**
     * 将网易最近聊天记录同步至图文咨询
     */
    private void onRecentContactsLoaded() {
        //        dataList.clear();
        if (loadedRecents != null) {
            for (int i = 0; i < dataList.size(); i++) {
                ConsultMessage message = dataList.get(i);
                for (RecentContact contact : loadedRecents) {
                    if (message.getMessInfo().getSource().equals(OrderSource.INQUIRY_RESERVE)) {//图文咨询才有聊天记录

                        if (null != message.getMessInfo().getDoctor() && (message.getMessInfo().getDoctor()
                                .getUserName() + MyApplication.NIM_DOCTOR_NAME).equals(contact.getContactId())) {
                            message.setContact(contact);
                        }
                    }
                }
            }
            //            dataList.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true);

        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(dataList);
        notifyDataSetChanged(true);

        if (unreadChanged) {

            // 方式一：累加每个最近联系人的未读（快）
            /*
            int unreadNum = 0;
            for (RecentContact r : dataList) {
                unreadNum += r.getUnreadCount();
            }
            */

            // 方式二：直接从SDK读取（相对慢）
            int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();

            if (callback != null) {
                callback.onUnreadCountChange(unreadNum);
            }
        }
    }

    /**
     * **************************** 排序 ***********************************
     */
    private void sortRecentContacts(List<ConsultMessage> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<ConsultMessage> comp = new Comparator<ConsultMessage>() {

        @Override
        public int compare(ConsultMessage o1, ConsultMessage o2) {
            if (o1.getMessInfo().getSource().equals(o2.getMessInfo().getSource())) {
                if (o1.getMessInfo().getSource().equals(OrderSource.INQUIRY_RESERVE)) {//图文时间倒序
                    if (o1.getMessInfo().getStatus().equals(o2.getMessInfo().getStatus())) {
                        if (null != o1.getContact() && null != o2.getContact()) {
                            // 先比较置顶tag
                            long sticky = (o1.getContact().getTag() & RECENT_TAG_STICKY) - (o2.getContact().getTag()
                                    & RECENT_TAG_STICKY);
                            if (sticky != 0) {
                                return sticky > 0 ? -1 : 1;
                            } else {
                                long time = o1.getContact().getTime() - o2.getContact().getTime();
                                return time == 0 ? 0 : (time > 0 ? -1 : 1);
                            }
                        } else if (null != o1.getContact()) {
                            long time = o1.getContact().getTime() - Long.parseLong(o2.getMessInfo().getDiagnoseDate());
                            return time == 0 ? 0 : (time > 0 ? -1 : 1);
                        } else if (null != o2.getContact()) {
                            long time = Long.parseLong(o1.getMessInfo().getDiagnoseDate()) - o2.getContact().getTime();
                            return time == 0 ? 0 : (time > 0 ? -1 : 1);
                        } else {
                            long time = Long.parseLong(o1.getMessInfo().getDiagnoseDate()) - Long.parseLong(o2
                                    .getMessInfo().getDiagnoseDate());
                            return time == 0 ? 0 : (time > 0 ? -1 : 1);
                        }
                    } else {
                        if (o1.getMessInfo().getStatus().equals(OrderStatus.PAID)) {
                            return -1;
                        } else if (o2.getMessInfo().getStatus().equals(OrderStatus.PAID)) {
                            return 1;
                        } else if (o1.getMessInfo().getStatus().equals(OrderStatus.CANCEL)) {
                            return -1;
                        } else if (o2.getMessInfo().getStatus().equals(OrderStatus.CANCEL)) {
                            return 1;
                        }
                    }
                } else {//视频时间升序
                    long time = Long.parseLong(o1.getMessInfo().getDiagnoseDate()) - Long.parseLong(o2.getMessInfo()
                            .getDiagnoseDate());
                    return time == 0 ? 0 : (time > 0 ? 1 : -1);
                }
            } else {
                if (o1.getMessInfo().getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {
                    return -1;
                } else if (o2.getMessInfo().getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {
                    return 1;
                } else if (o1.getMessInfo().getSource().equals(GROUP_SIT_DIAGNOSE_DETAIL)) {
                    return -1;
                } else if (o2.getMessInfo().getSource().equals(GROUP_SIT_DIAGNOSE_DETAIL)) {
                    return 1;
                } else if (o1.getMessInfo().getSource().equals(SIT_DIAGNOSE_RESERVE)) {
                    return -1;
                } else if (o2.getMessInfo().getSource().equals(SIT_DIAGNOSE_RESERVE)) {
                    return 1;
                }
            }
            return 0;
        }
    };

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
    }

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            for (RecentContact msg : messages) {
                for (int i = 0; i < dataList.size(); i++) {
                    RecentContact contact = dataList.get(i).getContact();
                    if (null != contact) {
                        if (msg.getContactId().equals(contact.getContactId()) && msg.getSessionType() == (dataList
                                .get(i).getContact().getSessionType())) {
                            dataList.get(i).setContact(msg);
                            break;
                        }
                    }
                }
            }
            //SoundPlayUtils.getInstance().play(SoundPlayUtils.SoundSource.MSG.getId());
            refreshMessages(true);
        }
    };

    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < dataList.size()) {
                RecentContact item = dataList.get(index).getContact();
                if (null != item) {
                    item.setMsgStatus(message.getStatus());
                }
                refreshViewHolderByIndex(index);
            }
        }
    };

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    ConsultMessage message = dataList.get(i);
                    RecentContact item = message.getContact();
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) && item.getSessionType()
                            == recentContact.getSessionType()) {
                        dataList.remove(message);
                        refreshMessages(true);
                        break;
                    }
                }
            } else {
                //                dataList.clear();
                refreshMessages(true);
            }
        }
    };

    Observer<IMMessage> revokeMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (message == null) {
                return;
            }

            MessageHelper.getInstance().onRevokeMessage(message);
        }
    };

    private int getItemIndex(String uuid) {
        for (int i = 0; i < dataList.size(); i++) {
            RecentContact item = dataList.get(i).getContact();
            if (null != item) {
                if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                    return i;
                }
            }
        }

        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //                Object tag = ListViewUtil.getViewHolderByIndex(listView, index);
                Object tag = RecyclerViewUtlis.getViewHolderByIndex(recyclerView, index);
                if (tag instanceof RecentViewHolder) {
                    RecentViewHolder viewHolder = (RecentViewHolder) tag;
                    viewHolder.refreshCurrentItem();
                }
            }
        });
    }

    private void registerUserInfoObserver() {
        if (userInfoObserver == null) {
            userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    refreshMessages(false);
                }
            };
        }

        UserInfoHelper.registerObserver(userInfoObserver);
    }

    private void unregisterUserInfoObserver() {
        if (userInfoObserver != null) {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache
            .FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            refreshMessages(false);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            refreshMessages(false);
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            refreshMessages(false);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            refreshMessages(false);
        }
    };

    private RecentContactsCallback callback = new RecentContactsCallback() {
        @Override
        public void onRecentContactsLoaded() {
            for (ConsultMessage message : dataList) {
                switch (message.getMessInfo().getStatus()) {
                    case INITIAL:
                    case PAID:
                        DataCache.getInstance().setImperfectOrder(true);
                        break;
                    default:
                        break;
                }
                if (DataCache.getInstance().isImperfectOrder()) {
                    break;
                }
            }
            ((MainActivity) getActivity()).setUnreadRed();
        }

        @Override
        public void onUnreadCountChange(int unreadCount) {
            if (unreadCount > 0 || noComplete) {
                DataCache.getInstance().setImperfectOrder(true);
            } else {
                DataCache.getInstance().setImperfectOrder(false);
            }
            ((MainActivity) getActivity()).setUnreadRed();
        }

        @Override
        public void onItemClick(final RecentContact recent) {

        }

        @Override
        public String getDigestOfAttachment(MsgAttachment attachment) {
            return null;
        }

        @Override
        public String getDigestOfTipMsg(RecentContact recent) {
            return null;
        }
    };

    @Override
    public boolean onBackPressed() {
        if (null != popupWindow && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CANCEL || requestCode == REFUND) {
                dataList.remove(position);
                notifyDataSetChanged(true);
            }
        }
    }
}
