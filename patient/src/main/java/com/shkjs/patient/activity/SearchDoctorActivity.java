package com.shkjs.patient.activity;

import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.InputMethodUtils;
import com.raspberry.library.util.TextUtils;
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
 * <p/>
 * 搜索医生
 */
public class SearchDoctorActivity extends BaseActivity {

    public static final int DOCTOR = 1;//医生
    public static final int HOSPITAL = 2;//医院
    public static final int DEPARTMENT = 3;//科室
    public static final int DISEASE = 4;//疾病

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;

    @Bind(R.id.recyclerview_layout_relativelayout)
    RelativeLayout relativeLayout;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar_title)
    TextView titleTV;
    @Bind(R.id.toolbar_menu)
    ImageButton titleMenuBtn;
    @Bind(R.id.search_doctor_et)
    EditText editText;

    private Toolbar toolbar;

    private List<Doctor> dataList;
    private BaseRecyclerAdapter<Doctor> adapter;

    private String searchMsg;
    private String hospitalName;
    private String categoryName;
    private boolean isShowToolbar = true;
    private boolean isGetStr = false;
    private boolean isAlowSearch = true;
    private int type = DOCTOR;
    private Page page;

    public static void start(Context context, int type) {
        Intent intent = new Intent(context, SearchDoctorActivity.class);
        intent.putExtra(SearchDoctorActivity.class.getSimpleName(), type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);

        type = getIntent().getIntExtra(SearchDoctorActivity.class.getSimpleName(), DOCTOR);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.search_doctor_result, R.drawable
                .search_doctor_style);

        initData();
        initListener();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        showMenu(isAlowSearch);
        return super.onPrepareOptionsMenu(menu);
    }

    private void initData() {

        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);

        searchMsg = getIntent().getStringExtra(Preference.SEARCH_MSG);
        hospitalName = getIntent().getStringExtra(Preference.HOSPITAL_NAME);
        categoryName = getIntent().getStringExtra(Preference.CATEGORY_NAME);

        noMessageTV.setText("没有您要找的医生");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_search_results);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        dataList = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<Doctor>(this, dataList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.doctor_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final Doctor item) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchDoctorActivity.this, DoctorDetailActivity.class);
                        intent.putExtra(Preference.DOCTOR_DETAIL, item.getId());
                        startActivity(intent);
                    }
                });
                holder.getTextView(R.id.doctor_name).setText(item.getName());
                if (null != item.getLevel()) {
                    holder.getTextView(R.id.doctor_technical).setText(item.getLevel().getMark());
                }
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
                holder.getTextView(R.id.doctor_hospital).setText(item.getHospitalName());
                holder.getTextView(R.id.doctor_department).setText(item.getCategoryName());
                //                holder.getView(R.id.doctor_price_video_ll).setVisibility(View.GONE);
                //                holder.getView(R.id.doctor_price_ll).setVisibility(View.GONE);
                holder.getTextView(R.id.doctor_price).setText(SpliceUtils.formatBalance2(item.getAskHospitalFee()));
                holder.getTextView(R.id.doctor_price_video).setText(SpliceUtils.formatBalance2(item
                        .getViewHospitalFee()));
                Glide.with(SearchDoctorActivity.this).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait())
                        .placeholder(R.drawable.main_headportrait_white).error(R.drawable.main_headportrait_white)
                        .into(holder.getImageView(R.id.doctor_icon));
                holder.setClickListener(R.id.doctor_picture_consult, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderPicture(item);//图文咨询
                    }
                });
                holder.setClickListener(R.id.doctor_video_order, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OrderTimeActivity.start(SearchDoctorActivity.this, item);
                    }
                });
            }
        };
    }

    private void initListener() {

        swipeRefreshLayout.setEnabled(false);//禁止下拉刷新

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setMenuBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowToolbar) {
                    showView(false);
                } else {
                    searchDoctor();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.setAdapter(adapter);

        //        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //            @Override
        //            public void onRefresh() {
        //                searchDoctor();
        //            }
        //        });
        //        swipeRefreshLayout.setRefreshing(true);//只是动画，不会执行onRefresh方法
        //        searchDoctor();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isGetStr) {
                    searchMsg = TextUtils.getText(editText);
                }
            }
        });

        if (!TextUtils.isEmpty(searchMsg)) {
            setToolbarTitle(searchMsg);
            searchDoctor();
            isAlowSearch = false;
        } else if (!TextUtils.isEmpty(hospitalName) && !TextUtils.isEmpty(categoryName)) {
            setToolbarTitle(getString(R.string.department_5_text));
            searchDoctor();
            isAlowSearch = false;
        } else {//点击搜索医生，默认打开
            showView(false);
            isAlowSearch = true;
        }
    }

    private void searchDoctor() {
        if (TextUtils.isEmpty(searchMsg) && TextUtils.isEmpty(hospitalName) && TextUtils.isEmpty(categoryName)) {
            ToastUtils.showToast(getString(R.string.search_doctor_isempty_hint));
            return;
        }

        showView(true);

        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<Doctor>> callback = new RaspberryCallback<ListPageResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Doctor", getString(R.string.search_doctor_fail_text) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.search_doctor_fail_text));
                completeSearch();
            }

            @Override
            public void onSuccess(ListPageResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        dataList.clear();
                        dataList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Logger.e("Doctor", getString(R.string.search_doctor_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.search_doctor_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Doctor", getString(R.string.search_doctor_fail_text));
                    ToastUtils.showToast(getString(R.string.search_doctor_fail_text));
                }
                completeSearch();
            }
        };

        callback.setMainThread(true);
        if (type == DEPARTMENT) {
            HttpProtocol.searchDoctorsBycategoryName(searchMsg, page, callback);
        } else {
            if (!TextUtils.isEmpty(hospitalName) && !TextUtils.isEmpty(categoryName)) {
                //                HttpProtocol.searchDoctors(hospitalName, categoryName, page, callback);//免费医生
                HttpProtocol.searchLocalDoctors(hospitalName, categoryName, page, callback);//本地医生
            } else {
                HttpProtocol.searchDoctors(searchMsg, page, callback);
                searchMsg = "";
            }
        }
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

                        PayActivity.start(SearchDoctorActivity.this, orderInfo);

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


    private void showView(boolean isShowToolbar) {
        this.isShowToolbar = isShowToolbar;
        if (isShowToolbar) {
            editText.setVisibility(View.GONE);
            titleTV.setVisibility(View.VISIBLE);
            isGetStr = false;
            editText.setText("");
            InputMethodUtils.hideSoftInput(this);
        } else {
            editText.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.GONE);
            isGetStr = true;
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            InputMethodUtils.showSoftInput(this);
        }
    }

    private void showMenu(boolean isShow) {
        if (isShow) {
            titleMenuBtn.setVisibility(View.VISIBLE);
        } else {
            titleMenuBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void completeSearch() {
        swipeRefreshLayout.setRefreshing(false);
        if (dataList.size() > 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            noMessageTV.setVisibility(View.GONE);
        } else {
            relativeLayout.setVisibility(View.GONE);
            noMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //        if (!isShowToolbar) {
        //            showView(true);
        //            return;
        //        }
        super.onBackPressed();
    }
}
