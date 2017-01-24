package com.shkjs.patient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.OrderDetailActivity;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.bean.UserOrder;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderStatus;
import com.shkjs.patient.data.em.OrderType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/11/2.
 */

public class OrderFragment extends BaseFragment {

    private static final int ORDER_DETAIL = 121;
    private static final int UPDATE_DATA = 122;
    private static final String STATUS = "status";

    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private List<UserOrder> datalist;
    private BaseRecyclerAdapter<UserOrder> adapter;

    private Page page;
    private int pageNumber = 1;
    private boolean isFirstRefresh = true;//是否为第一次下拉刷新
    private boolean isPullDown = true;//是否为下拉刷新
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isAllowPullUp = true;//是否允许上拉加载更多

    private Context context;

    private OrderStatus status;
    private boolean isFirst = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DATA:
                    completeGetOrder();
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    public static OrderFragment getInstance(OrderStatus status) {
        OrderFragment instance = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(STATUS, status);
        instance.setArguments(bundle);
        return instance;
    }

    public OrderFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oreder, null);

        status = (OrderStatus) getArguments().getSerializable(STATUS);

        //绑定控件
        ButterKnife.bind(this, view);
        context = getActivity();
        initData();
        initListener();
        isPrepared = true;
        setlazyLoad();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        complete();
    }

    private void initData() {

        page = new Page();

        noMessageTV.setText("您暂时还没有订单~");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.no_orders);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<UserOrder>(context, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.order_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final UserOrder item) {
                View view = holder.getView(R.id.order_item);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        if (!item.getOrder().getType().equals(OrderType.RECHARGE)) {//充值订单无详情
                            intent.putExtra(OrderDetailActivity.class.getSimpleName(), item);
                            intent.putExtra(Preference.POSITION, position);
                            startActivityForResult(intent, ORDER_DETAIL);
                        }
                    }
                });

                holder.setText(R.id.order_number_tv, item.getOrder().getCode());
                TextView status = holder.getTextView(R.id.order_status_tv);
                status.setText(item.getOrder().getStatus().getMark());
                //                if (item.getOrder().getStatus().equals(OrderStatus.COMPLETE)) {//已完成
                //                    status.setTextColor(Color.GREEN);
                //                } else {//其他状态
                status.setTextColor(Color.RED);
                //                }
                holder.setText(R.id.order_time_tv, TimeFormatUtils.getLocalTime("yyyy-MM-dd HH:mm:ss", Long.parseLong
                        (item.getOrder().getCreateDate())));
                switch (item.getOrder().getStatus()) {
                    case INITIAL:
                    case EXPIRE:
                        holder.getTextView(R.id.order_price_tv).setVisibility(View.GONE);
                        holder.setText(R.id.order_real_price_tv, SpliceUtils.splicePrice(item.getOrder().getMoney()));
                        break;
                    case PAID:
                    case COMPLETE:
                    case CANCEL:
                    case REFUND:
                        holder.getTextView(R.id.order_price_tv).setVisibility(View.VISIBLE);
                        holder.setText(R.id.order_price_tv, SpliceUtils.splicePrice(item.getOrder().getMoney()));
                        holder.setText(R.id.order_real_price_tv, SpliceUtils.splicePrice(item.getOrder()
                                .getActuallyPaidMoney()));
                        break;
                    default:
                        break;
                }

                holder.setText(R.id.order_name_tv, item.getOrder().getMark());//哪一类订单

                Integer id = R.drawable.personalcenter_order_recharge;
                if (item.getOrder().getSource().equals(OrderSource.INQUIRY_RESERVE)) {
                    id = R.drawable.personalcenter_order_pictureconsulting;
                } else if (item.getOrder().getSource().equals(OrderSource.SIT_DIAGNOSE_RESERVE)) {
                    id = R.drawable.personalcenter_order_videoconsultation;
                } else if (item.getOrder().getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {
                    id = R.drawable.personalcenter_order_videogroupconsultation;
                }
                Glide.with(context).load(id).error(R.drawable.personalcenter_order_recharge).placeholder(R.drawable
                        .personalcenter_order_recharge).into(holder.getImageView(R.id.order_icon_iv));
            }
        };
    }

    private void initListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullDown = true;
                getMineOrder(isPullDown);
            }
        });

        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int pos) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));// 布局管理器。
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
                        getMineOrder(isPullDown);
                    } else {
                        ToastUtils.showToast("暂无更多数据~");
                    }
                }
            }
        });
    }

    @Override
    protected void setlazyLoad() {
        super.setlazyLoad();
        if (!isPrepared || !isVisible) {
            return;
        }
        if (isFirst) {
            isFirst = false;
            swipeRefreshLayout.setRefreshing(true);
            getMineOrder(true);
        }
    }

    private void complete() {
        if (datalist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageTV.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noMessageTV.setVisibility(View.VISIBLE);
        }
    }

    private void completeGetOrder() {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        if (datalist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageTV.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noMessageTV.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void getMineOrder(final boolean isPullDown) {
        if (isRefreshing) {
            ToastUtils.showToast("正在刷新，请稍后重试");
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        RaspberryCallback<ListPageResponse<UserOrder>> callback = new RaspberryCallback<ListPageResponse<UserOrder>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                handler.sendEmptyMessage(UPDATE_DATA);
            }

            @Override
            public void onSuccess(ListPageResponse<UserOrder> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (isPullDown) {
                            int position;
                            for (UserOrder order : response.getData()) {
                                if (order.getOrder().getStatus().equals(OrderStatus.AGREE) && order.getOrder()
                                        .getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {//视频会诊同意状态才付款
                                    order.getOrder().setStatus(OrderStatus.INITIAL);
                                }
                                if (datalist.contains(order)) {
                                    position = datalist.indexOf(order);
                                    datalist.remove(position);
                                    datalist.add(position, order);
                                } else {
                                    if (isFirstRefresh) {
                                        datalist.add(order);
                                    } else {
                                        datalist.add(0, order);
                                    }
                                }
                            }
                            isFirstRefresh = false;
                        } else {
                            pageNumber = response.getPageNum();
                            for (UserOrder order : response.getData()) {
                                if (order.getOrder().getStatus().equals(OrderStatus.AGREE) && order.getOrder()
                                        .getSource().equals(OrderSource.GROUP_SIT_DIAGNOSE)) {//视频会诊同意状态才付款
                                    order.getOrder().setStatus(OrderStatus.INITIAL);
                                }
                                if (!datalist.contains(order)) {
                                    datalist.add(order);
                                }
                            }
                        }
                        page.setPageNum(response.getPageNum());
                        page.setPageSize(response.getPageSize());
                        page.setTotalCount(response.getTotalCount());
                        if (page.getPageNum() * page.getPageSize() >= page.getTotalCount()) {
                            isAllowPullUp = false;
                        }
                    } else {
                        Logger.e(getString(R.string.get_order_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_order_failed));
                    }
                } else {
                    Logger.e(getString(R.string.get_order_failed));
                    ToastUtils.showToast(getString(R.string.get_order_failed));
                }
                handler.sendEmptyMessage(UPDATE_DATA);
            }
        };

        callback.setMainThread(false);
        callback.setCancelable(false);

        if (isPullDown) {
            page.setPageNum(1);
        } else {
            page.setPageNum(pageNumber + 1);
        }

        if (status.equals(OrderStatus.ALL)) {//全部订单
            HttpProtocol.getUserOrder(null, null, page, callback);
        } else {//特定状态订单
            HttpProtocol.getUserOrder("status", status.name(), page, callback);
        }
        isRefreshing = true;
    }

    private String getOrderStatus(OrderStatus status) {
        String str = status.getMark();
        switch (status) {
            case INITIAL:
                str = getString(R.string.wait_pay_order);
                break;
            case EXPIRE:
                break;
            case PAID:
                str = getString(R.string.no_complete_order);
                break;
            case COMPLETE:
                str = getString(R.string.complete_order);
                break;
            case CANCEL:
                str = getString(R.string.cancel);
                break;
            case REFUND:
                str = getString(R.string.refund_order);
                break;
            default:
                break;
        }
        return str;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode != ORDER_DETAIL) {
            return;
        }
        if (null == data) {
            return;
        }
        if (data.getBooleanExtra(OrderDetailActivity.class.getSimpleName(), false)) {
            datalist.remove(data.getIntExtra(Preference.POSITION, -1));
            adapter.notifyDataSetChanged();
            //            adapter.notifyItemRemoved(data.getIntExtra(Preference.POSITION, -1));
        }
    }
}
