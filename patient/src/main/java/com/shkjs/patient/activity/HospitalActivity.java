package com.shkjs.patient.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Hospital;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
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
 * Created by xiaohu on 2016/11/14.
 * <p>
 * 医院列表界面
 */

public class HospitalActivity extends BaseActivity {

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<Hospital> datalist;
    private BaseRecyclerAdapter<Hospital> adapter;

    private int hospitalPageNum = 0;
    private Page page;

    private static final int UPDATE_VIEW = 121;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    adapter.notifyDataSetChanged();
                    complete();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.hospital_text);

        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {

        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);
        noMessageTV.setText("暂时没有相关医院信息~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<Hospital>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.department_list_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final Hospital item) {
                holder.getTextView(R.id.department_item_name_tv).setText(item.getHospitalName());
                holder.setClickListener(R.id.department_item_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(HospitalActivity.this, DepartmentActivity.class);
                        intent.putExtra(Preference.HOSPITAL_NAME, item);
                        startActivity(intent);
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

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHospitals();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.setAdapter(adapter);

        //主动获取数据
        getHospitals();
    }

    /**
     * 获取医院列表信息
     */
    private void getHospitals() {

        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<Hospital>> callback = new RaspberryCallback<ListPageResponse<Hospital>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                //                super.onFailure(request, response, e);
                Logger.e("Hospital", getString(R.string.get_hospital_fail_text));
                ToastUtils.showToast(getString(R.string.get_hospital_fail_text));
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ListPageResponse<Hospital> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        hospitalPageNum = response.getPageNum();
                        datalist.clear();
                        datalist.addAll(response.getData());
                        handler.sendEmptyMessage(UPDATE_VIEW);
                        return;
                    }
                }
                Logger.e("Hospital", getString(R.string.get_hospital_fail_text));
                ToastUtils.showToast(getString(R.string.get_hospital_fail_text));
                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);
        callback.setCancelable(false);
        if (page.getPageNum() == hospitalPageNum) {
            page.setPageNum(hospitalPageNum + 1);
        }

        //        HttpProtocol.getFreeHospitals(page, callback);//免费医生
        HttpProtocol.getLocalHospitals(page, callback);//本地医生
    }

    private void complete() {
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
