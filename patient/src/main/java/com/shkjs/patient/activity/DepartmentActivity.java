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
import com.shkjs.patient.bean.Department;
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
 * Created by xiaohu on 2016/9/27.
 * <p>
 * 科室列表界面
 */

public class DepartmentActivity extends BaseActivity {

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<Department> datalist;
    private BaseRecyclerAdapter<Department> adapter;

    private int departmentPageNum = 0;
    private Page page;
    private Hospital hospital;

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
        setContentView(R.layout.activity_department);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.department_text);

        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {

        hospital = (Hospital) getIntent().getSerializableExtra(Preference.HOSPITAL_NAME);

        noMessageTV.setText("暂时没有相关科室信息~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<Department>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.department_list_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final Department item) {
                holder.getTextView(R.id.department_item_name_tv).setText(item.getName());
                holder.setClickListener(R.id.department_item_ll, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(DepartmentActivity.this, SearchDoctorActivity.class);
                        if (null == hospital) {
                            intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity
                                    .DEPARTMENT);
                            intent.putExtra(Preference.SEARCH_MSG, item.getName());
                        } else {
                            intent.putExtra(Preference.HOSPITAL_NAME, hospital.getHospitalName());
                            intent.putExtra(Preference.CATEGORY_NAME, item.getName());
                        }
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
                getDepartments();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.setAdapter(adapter);

        //主动获取数据
        getDepartments();
    }

    /**
     * 获取科室列表信息
     */
    private void getDepartments() {

        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<Department>> callback = new
                RaspberryCallback<ListPageResponse<Department>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Department", getString(R.string.get_department_fail_text));
                ToastUtils.showToast(getString(R.string.get_department_fail_text));
                complete();
            }

            @Override
            public void onSuccess(ListPageResponse<Department> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        departmentPageNum = response.getPageNum();
                        datalist.clear();
                        datalist.addAll(response.getData());
                    } else {
                        Logger.e("Department", getString(R.string.get_department_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_department_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Department", getString(R.string.get_department_fail_text));
                    ToastUtils.showToast(getString(R.string.get_department_fail_text));
                }
                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);
        callback.setCancelable(false);
        if (page.getPageNum() == departmentPageNum) {
            page.setPageNum(departmentPageNum + 1);
        }
        if (null == hospital) {//所有科室
            HttpProtocol.getDepartments(page, callback);
            //        } else {//免费医生科室
            //            HttpProtocol.getFreeDepartments(hospital.getId(), page, callback);
            //        }
        } else {//本地医生科室
            HttpProtocol.getLocalDepartments(hospital.getId(), page, callback);
        }
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