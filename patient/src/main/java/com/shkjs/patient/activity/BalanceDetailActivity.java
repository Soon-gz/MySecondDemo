package com.shkjs.patient.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.AccountFlowDetail;
import com.shkjs.patient.bean.Page;
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
 * Created by xiaohu on 2016/10/10.
 * <p>
 * 收支明细
 */

public class BalanceDetailActivity extends BaseActivity {

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<AccountFlowDetail> datalist;
    private BaseRecyclerAdapter<AccountFlowDetail> adapter;

    private Page page;
    private int pageNumber = 1;
    private boolean isFirst = true;//是否为第一次下拉刷新
    private boolean isPullDown = true;//是否为下拉刷新
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isAllowPullUp = true;//是否允许上拉加载更多
    private long accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_balance_detail);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.balance_detail);

        initData();
        initListener();

        if (accountId < 0) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
        getBalanceChangeDetail(true);
    }

    private void initData() {

        accountId = getIntent().getLongExtra(BalanceDetailActivity.class.getSimpleName(), -1);

        page = new Page();

        noMessageTV.setText("您还没有任何收支操作~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<AccountFlowDetail>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.balance_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, AccountFlowDetail item) {
                holder.getTextView(R.id.balance_item_name).setText(item.getMark());
                switch (item.getType()) {
                    case RECHARGE://充值
                    case INCOME://收入
                    case EXPENSEREFUND://消费退款
                    case INCOMEREFUND://收费退款
                        holder.getTextView(R.id.balance_item_price).setText("+" + SpliceUtils.formatBalance(item
                                .getMoney()));
                        break;
                    case DRAWMONEY://提现
                    case EXPENSE://消费
                        holder.getTextView(R.id.balance_item_price).setText("-" + SpliceUtils.formatBalance(item
                                .getMoney()));
                        break;
                    default:
                        break;
                }
                holder.getTextView(R.id.balance_item_time).setText(TimeFormatUtils.getLocalTime(Long.parseLong(item
                        .getCreateDate())) + " " + TimeFormatUtils.getLocalTime("HH:mm", Long.parseLong(item
                        .getCreateDate())));
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
                getBalanceChangeDetail(isPullDown);
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
                        getBalanceChangeDetail(isPullDown);
                    } else {
                        ToastUtils.showToast("暂无更多数据~");
                    }
                }
            }
        });
    }

    private void getBalanceChangeDetail(final boolean isPullDown) {

        if (isRefreshing) {
            ToastUtils.showToast("正在刷新，请稍后重试");
            return;
        }
        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<AccountFlowDetail>> callback = new
                RaspberryCallback<ListPageResponse<AccountFlowDetail>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                complete();
            }

            @Override
            public void onSuccess(ListPageResponse<AccountFlowDetail> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (isPullDown) {
                            int position;
                            for (AccountFlowDetail detail : response.getData()) {
                                if (datalist.contains(detail)) {
                                    position = datalist.indexOf(detail);
                                    datalist.remove(position);
                                    datalist.add(position, detail);
                                } else {
                                    if (isFirst) {
                                        datalist.add(detail);
                                    } else {
                                        datalist.add(0, detail);
                                    }
                                }
                            }
                            isFirst = false;
                        } else {
                            pageNumber = response.getPageNum();
                            for (AccountFlowDetail detail : response.getData()) {
                                if (!datalist.contains(detail)) {
                                    datalist.add(detail);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        page.setPageNum(response.getPageNum());
                        page.setPageSize(response.getPageSize());
                        page.setTotalCount(response.getTotalCount());
                        if (page.getPageNum() * page.getPageSize() >= page.getTotalCount()) {
                            isAllowPullUp = false;
                        }
                    } else {
                        Logger.e(getString(R.string.get_balance_detail_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_balance_detail_failed));
                    }
                } else {
                    Logger.e(getString(R.string.get_balance_detail_failed));
                    ToastUtils.showToast(getString(R.string.get_balance_detail_failed));
                }
                complete();
            }

        };

        callback.setMainThread(true);

        if (isPullDown) {
            page.setPageNum(1);
        } else {
            page.setPageNum(pageNumber + 1);
        }
        HttpProtocol.getAccountFlowDetail(accountId, page, callback);
        isRefreshing = true;
    }

    private void complete() {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        if (datalist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageTV.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noMessageTV.setVisibility(View.VISIBLE);
        }
    }
}
