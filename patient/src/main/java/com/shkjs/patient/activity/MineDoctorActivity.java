package com.shkjs.patient.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.data.em.DoctorPlatformLevel;
import com.shkjs.patient.data.em.DoctorTag;
import com.shkjs.patient.data.em.OrderInfoType;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p>
 * 我的医生
 */
public class MineDoctorActivity extends BaseActivity {

    private static final int QRCODE = 121;//二维码
    private static final int DETAIL = 122;//医生详情关系处理

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<Doctor> datalist;
    private BaseRecyclerAdapter<Doctor> adapter;

    private Page page;
    private int pageNumber = 1;
    private boolean isFirst = true;//是否为第一次下拉刷新
    private boolean isPullDown = true;//是否为下拉刷新
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isAllowPullUp = true;//是否允许上拉加载更多

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mine_doctor);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_doctor_text, R.drawable
                .scan_select_style);

        initData();
        initListener();

        swipeRefreshLayout.setRefreshing(true);
        getDoctors(isPullDown);
    }

    private void initData() {

        noMessageTV.setText("暂时还没有您的医生哦~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        page = new Page();

        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<Doctor>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.doctor_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Doctor item) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MineDoctorActivity.this, DoctorDetailActivity.class);
                        intent.putExtra(Preference.DOCTOR_DETAIL, item.getId());
                        startActivityForResult(intent, DETAIL);
                    }
                });

                holder.getTextView(R.id.doctor_name).setText(item.getName());
                holder.getTextView(R.id.doctor_technical).setText(item.getLevel().getMark());
                holder.getTextView(R.id.doctor_hospital).setText(item.getHospitalName());
                holder.getTextView(R.id.doctor_department).setText(item.getCategoryName());
                //                holder.getView(R.id.doctor_price_video_ll).setVisibility(View.GONE);
                //                holder.getView(R.id.doctor_price_ll).setVisibility(View.GONE);
                if (null != item.getPlatformLevel() && item.getPlatformLevel().equals(DoctorPlatformLevel.AUTHORITY)) {
                    holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.VISIBLE);
                } else {
                    holder.getImageView(R.id.doctor_platform_level_iv).setVisibility(View.GONE);
                }
                if (item.getTag().equals(DoctorTag.PROMOTION)) {
                    holder.getImageView(R.id.doctor_tag_iv).setImageResource(R.drawable.extension);
                } else if (item.getTag().equals(DoctorTag.FREE)) {
                    holder.getImageView(R.id.doctor_tag_iv).setImageResource(R.drawable.free);
                } else {
                    holder.getImageView(R.id.doctor_tag_iv).setImageResource(R.color.transparent);
                }
                holder.getTextView(R.id.doctor_price).setText(SpliceUtils.formatBalance2(item.getAskHospitalFee()));
                holder.getTextView(R.id.doctor_price_video).setText(SpliceUtils.formatBalance2(item
                        .getViewHospitalFee()));
                Glide.with(MineDoctorActivity.this).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait()).error(R
                        .drawable.main_headportrait_white).placeholder(R.drawable.main_headportrait_white).into
                        (holder.getImageView(R.id.doctor_icon));
                holder.setClickListener(R.id.doctor_picture_consult, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderPicture(item);//图文咨询
                    }
                });
                holder.setClickListener(R.id.doctor_video_order, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OrderTimeActivity.start(MineDoctorActivity.this, item);
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
        setMenuBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineDoctorActivity.this, QRCodeActivity.class);
                intent.putExtra("hint", "将医生的二维码放入框内,即可自动扫描");
                startActivityForResult(intent, QRCODE);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullDown = true;
                getDoctors(isPullDown);
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
                        getDoctors(isPullDown);
                    } else {
                        ToastUtils.showToast("暂无更多数据~");
                    }
                }
            }
        });
    }

    /**
     * 获取医生信息
     */
    private void getDoctors(final boolean isPullDown) {
        if (isRefreshing) {
            ToastUtils.showToast("正在刷新，请稍后重试");
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        RaspberryCallback<ListPageResponse<Doctor>> callback = new RaspberryCallback<ListPageResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                completeGetDoctor();
            }

            @Override
            public void onSuccess(ListPageResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (isPullDown) {
                            int position;
                            for (Doctor doctor : response.getData()) {
                                if (datalist.contains(doctor)) {
                                    position = datalist.indexOf(doctor);
                                    datalist.remove(position);
                                    datalist.add(position, doctor);
                                } else {
                                    if (isFirst) {
                                        datalist.add(doctor);
                                    } else {
                                        datalist.add(0, doctor);
                                    }
                                }
                            }
                            isFirst = false;
                        } else {
                            pageNumber = response.getPageNum();
                            for (Doctor doctor : response.getData()) {
                                if (!datalist.contains(doctor)) {
                                    datalist.add(doctor);
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
                        Logger.e("Doctor", getString(R.string.get_doctor_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_doctor_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                    ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                }
                completeGetDoctor();

            }
        };
        callback.setMainThread(true);
        if (isPullDown) {
            page.setPageNum(1);
        } else {
            page.setPageNum(pageNumber + 1);
        }
        HttpProtocol.getDoctors(page, callback);
        isRefreshing = true;
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
        callback.setContext(this);
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
        final AlertDialog dialog = new RoundDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_layout, null);
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
        params.width = DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 20);
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

                        PayActivity.start(MineDoctorActivity.this, orderInfo);

                    } else {
                        ToastUtils.showToast(response.getMsg());
                    }
                } else {
                    ToastUtils.showToast("预约失败，请稍后重试");
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getOrderByCode(code, callback);

    }

    private void completeGetDoctor() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QRCODE) {
                if (null != data) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(ResultActivity.class.getSimpleName());
                    if (null != doctor) {
                        if (!datalist.contains(doctor)) {
                            datalist.add(0, doctor);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (requestCode == DETAIL) {
                if (null != data) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(DoctorDetailActivity.class.getSimpleName());
                    if (null != doctor && !data.getBooleanExtra("isRelation", true)) {
                        datalist.remove(doctor);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}
