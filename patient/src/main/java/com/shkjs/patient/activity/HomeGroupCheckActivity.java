package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.UserInfo;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/10.
 * <p>
 * 家庭组验证
 */

public class HomeGroupCheckActivity extends BaseActivity {

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<UserInfo> datalist;
    private BaseRecyclerAdapter<UserInfo> adapter;

    private RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
        @Override
        public void onFailure(Request request, Response response, Exception e) {
            super.onFailure(request, response, e);
        }

        @Override
        public void onSuccess(ObjectResponse<String> response, int code) {
            super.onSuccess(response, code);
            if (code == HttpBase.SUCCESS) {
                if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                    startActivity(new Intent(HomeGroupCheckActivity.this, HomeGroupActivity.class));
                    finish();
                } else {
                    Logger.e("HomeGroup", getString(R.string.get_home_group_failed) + response.getMsg());
                    ToastUtils.showToast(getString(R.string.network_anomaly));
                }
            } else {
                Logger.e("HomeGroup", getString(R.string.get_home_group_failed));
                ToastUtils.showToast(getString(R.string.network_anomaly));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_group_check);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.home_group_check);

        initData();
        initListener();
    }

    private void initData() {

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<UserInfo>(this, datalist) {

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.home_group_check_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, UserInfo item) {
                Glide.with(HomeGroupCheckActivity.this).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait())
                        .transform(new CircleTransform(HomeGroupCheckActivity.this)).into(holder.getImageView(R.id
                        .member_icon));

                holder.setText(R.id.member_name, item.getNickName());
                holder.setText(R.id.member_number, item.getUserName());
                holder.setClickListener(R.id.agree_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agreeJoin(position);
                    }
                });
                holder.setClickListener(R.id.disagree_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        disAgreeJoin(position);
                    }
                });
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

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        recyclerView.setAdapter(adapter);
    }

    /**
     * 同意加入
     */
    private void agreeJoin(int position) {

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.agreementJoin(datalist.get(position).getName(), callback);
    }

    /**
     * 拒绝加入
     */
    private void disAgreeJoin(int position) {

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.disagreeJoin(datalist.get(position).getName(), callback);
    }

}
