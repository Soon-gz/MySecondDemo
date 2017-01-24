package com.shkjs.doctor.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.activity.HistoryOrderDetailActivity;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.OrderbeanData;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.OrderStatus;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.BalanceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryOrderFragment extends Fragment {

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


    private String ISFINISHED = "0";   //订单是否已完成 0=完成 1=未完成

    private final String UNFINISHED = "0";
    private final String FINISHED = "1";

    private BaseRecyclerAdapter<OrderbeanData> adapter;
    private List<OrderbeanData> datalist;
    private List<String> businessList;
    private List<String> timeList;
    private RaspberryCallback<BaseResponse> cancelCallback;
    private RaspberryCallback<ListResponse<OrderbeanData>> callback;
    private int page = 1;
    private boolean isBottom = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_order, container, false);
        //注解
        ButterKnife.bind(this, view);
        //获取数据什么类型
        ISFINISHED = getArguments().getString("finish");
        //初始化监听事件
        initListener();
        //根据数据类型加载数据
        load(ISFINISHED);
        return view;
    }

    public void load(String isfinished) {
        switch (isfinished) {
            case FINISHED:
                loadFinishedNetData();
                break;
            case UNFINISHED:
                loadUnFinishedNetData();
                break;
        }
    }

    private void loadFinishedNetData() {
        HttpProtocol.getHistoryOrderFinish(callback, page);
    }

    private void loadUnFinishedNetData() {
        getHttpData(Preference.PAID);
    }

    /**
     * 更新数据
     **/
    public void dataNotifycation() {
        if (datalist.size() > 0) {
            adapter.notifyDataSetChanged();
            no_message_layout_relative.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            no_message_layout_textview.setText("您还没有历史订单~");
            no_message_layout_image.setImageResource(R.drawable.no_orders);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 发起网络请求，根据typa获取网络数据
     **/
    public void getHttpData(String type) {
        HttpProtocol.getHistoryOrder(callback, type, page);
    }


    private void initListener() {

        callback = new RaspberryCallback<ListResponse<OrderbeanData>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                dataNotifycation();
            }

            @Override
            public void onSuccess(ListResponse<OrderbeanData> response, int code) {
                super.onSuccess(response, code);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (HttpProtocol.checkStatus(response, code) && response.getData() != null) {
                    if (response.getData().size() > 0) {
                        for (int i = 0; i < response.getData().size(); i++) {
                            if (!"RECHARGE".equals(response.getData().get(i).getOrder().getType())) {
                                datalist.add(response.getData().get(i));
                                if (response.getData().get(i).getBusiness().getSitDiagnose() != null) {
                                    businessList.add(response.getData().get(i).getBusiness().getSitDiagnose().getDiagnoseDate());
                                    timeList.add(" " + response.getData().get(i).getBusiness().getSitDiagnose().getStartTime() + ":00-" + response.getData().get(i).getBusiness().getSitDiagnose().getEndTime() + ":00");
                                } else if (response.getData().get(i).getBusiness().getStartDate() != null) {
                                    Log.i("tag00", "开始日期：" + response.getData().get(i).getBusiness().getStartDate());
                                    businessList.add(response.getData().get(i).getBusiness().getStartDate() + "");
                                    timeList.add(" " + DateUtil.getStringFromTime(new Date(response.getData().get(i).getBusiness().getStartDate()), "HH:mm"));
                                } else if (response.getData().get(i).getBusiness().getGroupSitDiagnose() != null) {
                                    businessList.add(response.getData().get(i).getBusiness().getGroupSitDiagnose().getStartDate() + "");
                                    timeList.add(" " + DateUtil.getStringFromTime(new Date(response.getData().get(i).getBusiness().getGroupSitDiagnose().getStartDate()), "HH:mm"));
                                } else {
                                    businessList.add("0");
                                    timeList.add("0");
                                }
                            }
                        }
                    }
                }
                dataNotifycation();
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
        callback.setContext(getActivity());
        callback.setShowDialog(true);
        callback.setMainThread(true);

        businessList = new ArrayList<>();
        datalist = new ArrayList<>();
        timeList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                callback.setShowDialog(false);
                datalist.clear();
                adapter.notifyDataSetChanged();
                load(ISFINISHED);
            }
        });

        adapter = new BaseRecyclerAdapter<OrderbeanData>(getActivity(), datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_hidtory_order_rv;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, OrderbeanData item) {
                if ("视频咨询".equals(item.getOrder().getMark())) {
                    holder.getImageView(R.id.item_history_image_iv).setImageResource(R.drawable.historicalorder_videoconsultation);
                } else if ("图文咨询".equals(item.getOrder().getMark())) {
                    holder.getImageView(R.id.item_history_image_iv).setImageResource(R.drawable.historicalorder_pictureconsulting);
                } else {
                    holder.getImageView(R.id.item_history_image_iv).setImageResource(R.drawable.historicalorder_groupconsultation);
                }
                holder.getTextView(R.id.item_history_dingdanid_tv).setText(item.getOrder().getCode());
                holder.getTextView(R.id.item_history_money_tv).setText(BalanceUtils.formatBalance2(Long.parseLong(item.getOrder().getMoney() + "")));
                holder.getTextView(R.id.item_history_time_tv).setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getOrder().getCreateDate()), "yyyy-MM-dd HH:mm"));
                if ("多人会诊子项".equals(item.getOrder().getMark())) {
                    holder.getTextView(R.id.item_history_name_tv).setText("视频会诊");
                } else {
                    holder.getTextView(R.id.item_history_name_tv).setText(item.getOrder().getMark());
                }
                if (ISFINISHED.equals(FINISHED)) {
                    OrderStatus status = OrderStatus.valueOf(item.getOrder().getStatus());
                    switch (status) {
                        case COMPLETE:
                            holder.getTextView(R.id.item_history_status_tv).setText("已完成");
                            break;
                        case CANCEL:
                            holder.getTextView(R.id.item_history_status_tv).setText("已取消");
                            break;
                        case REFUND:
                            holder.getTextView(R.id.item_history_status_tv).setText("已取消");
                            break;
                    }
                    holder.getButton(R.id.item_history_unsure_tv).setVisibility(View.INVISIBLE);
                } else {
                    holder.getButton(R.id.item_history_unsure_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.dialogExSureIgnore("是否确定取消订单？", getActivity(), new CustomAlertDialog.OnDialogClickListener() {
                                @Override
                                public void doSomeThings() {
                                    //确定之后的数据处理
                                    HttpProtocol.postCancelOrder(cancelCallback, datalist.get(position).getOrder().getCode(), getSource(datalist.get(position).getOrder().getSource()), getActivity());
                                }
                            });
                        }
                    });
                    holder.getTextView(R.id.item_history_status_tv).setText("未完成");
                    holder.getButton(R.id.item_history_unsure_tv).setText("取消订单");
                }
            }
        };

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Intent intent = new Intent(getActivity(), HistoryOrderDetailActivity.class);
                intent.putExtra("orderbean", datalist.get(pos).getOrder());
                intent.putExtra("date", businessList.get(pos));
                intent.putExtra("time", timeList.get(pos));
                if (DataCache.getInstance().getUserInfo() != null) {
                    intent.putExtra(Preference.DOCTORTAG, DataCache.getInstance().getUserInfo().getTag().name());
                } else {
                    intent.putExtra(Preference.DOCTORTAG, MyApplication.doctorBean.getTag().name());
                }
                startActivity(intent);
            }
        });

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        cancelCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("取消订单成功。");
                    page = 1;
                    datalist.clear();
                    swipeRefreshLayout.setRefreshing(true);
                    adapter.notifyDataSetChanged();
                    loadUnFinishedNetData();
                }
            }

        };
        cancelCallback.setContext(getActivity());
        cancelCallback.setMainThread(true);
        cancelCallback.setCancelable(false);
        cancelCallback.setShowDialog(true);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isBottom && newState == RecyclerView.SCROLL_STATE_IDLE && canNextAdd()) {
                    page++;
                    load(ISFINISHED);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isBottom = ((layoutManager.findLastVisibleItemPosition() + 1) == adapter.getItemCount());
            }
        });
    }


    public boolean canNextAdd() {
        //默认加载10个，如果不满10个就不加载更多了
        if (datalist.size() < 20 * page) {
            return false;
        }
        return true;
    }

    public String getSource(String source) {
        if ("GROUP_SIT_DIAGNOSE_DETAIL".equals(source)) {
            return "GROUP_SIT_DIAGNOSE";
        }
        return source;
    }

}
