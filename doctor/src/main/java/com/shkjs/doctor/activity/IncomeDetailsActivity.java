package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.bean.InComeDetailBean;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.BalanceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomeDetailsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;
    @Bind(R.id.no_message_layout_relative)
    RelativeLayout no_message_layout_relative;
    @Bind(R.id.no_message_layout_image)
    ImageView no_message_layout_image;

    private BaseRecyclerAdapter<InComeDetailBean>adapter;
    private List<InComeDetailBean>dataList;
    private RaspberryCallback<ListResponse<InComeDetailBean>>callback;
    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_details);

        ButterKnife.bind(this);
        toptitle_tv.setText("收入明细");
        accountId = getIntent().getStringExtra("accountId");
        initEvents();
        loadNetData();
    }

    private void loadNetData() {
        HttpProtocol.getAccountFlow(callback,accountId);
    }

    public void initView(){
        if (dataList.size() > 0){
            adapter.notifyDataSetChanged();
            no_message_layout_relative.setVisibility(View.GONE);
        }else {
            no_message_layout_textview.setText("您还没有资金流水账单~");
            no_message_layout_image.setImageResource(R.drawable.no_data);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    private void initEvents() {

        dataList = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataList.clear();
                callback.setShowDialog(false);
                adapter.notifyDataSetChanged();
                HttpProtocol.getAccountFlow(callback,accountId);
            }
        });
        adapter = new BaseRecyclerAdapter<InComeDetailBean>(this,dataList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_my_wallet_income_detail;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, InComeDetailBean item) {
                holder.getTextView(R.id.income_detail_title_tv).setText(item.getMark());
                String money = "";
                if (Long.parseLong(item.getMoney()+"") > 0){
                    money = "+"+BalanceUtils.formatBalance2(Long.parseLong(item.getMoney()+""));
                }else {
                    money = "-"+BalanceUtils.formatBalance2(Math.abs(Long.parseLong(item.getMoney()+"")));
                }
                holder.getTextView(R.id.income_detail_money_tv).setText(money);
                holder.getTextView(R.id.income_detail_time_tv).setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(item.getCreateDate()),"yyyy-MM-dd"));
            }
        };

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        callback = new RaspberryCallback<ListResponse<InComeDetailBean>>() {

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
            public void onSuccess(ListResponse<InComeDetailBean> response, int code) {
                super.onSuccess(response, code);
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (HttpProtocol.checkStatus(response,code)){
                    dataList.addAll(response.getData());
                    initView();
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setShowDialog(true);
    }

    @OnClick(R.id.back_iv)
    public void incomeOnClick(){
        finish();
    }
}
