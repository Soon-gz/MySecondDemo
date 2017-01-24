package com.shkjs.patient.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.FlyBanner;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.DepartmentActivity;
import com.shkjs.patient.activity.DoctorDetailActivity;
import com.shkjs.patient.activity.HospitalActivity;
import com.shkjs.patient.activity.OrderTimeActivity;
import com.shkjs.patient.activity.PayActivity;
import com.shkjs.patient.activity.SearchDoctorActivity;
import com.shkjs.patient.base.BaseAdapter;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.Advertisement;
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

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.raspberry.library.util.HandlerUtils.runOnUiThread;

/**
 * Created by LENOVO on 2016/8/17.
 * 诊疗室Fragment，展示医生相关信息，并提供搜索入口
 */

public class SearchDoctorFragment1 extends BaseFragment implements View.OnClickListener {

    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    private Context context;
    private int departmentSize;

    @Bind(R.id.fragment_search_doctor_listview)
    ListView listView;

    private RelativeLayout adRL;
    private LinearLayout departmentLL;
    private LinearLayout doctorLL;
    private LinearLayout departmentLL1;
    private LinearLayout departmentLL2;
    private LinearLayout departmentLL3;
    private LinearLayout departmentLL4;
    private LinearLayout departmentLL5;
    private LinearLayout departmentLL6;
    private LinearLayout departmentLL7;
    private LinearLayout departmentLL8;
    private LinearLayout departmentLL9;
    private FlyBanner banner;

    private BaseAdapter<Doctor> doctorAdapter;
    private ArrayList<String> adList;
    private ArrayList<Doctor> doctorList;

    private Handler handler;

    private static final int NOTIFYDATA_AD = 121;//更新广告
    private static final int NOTIFYDATA_DOCTOR = 122;//更新医生


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_doctor_1, null);
        context = getActivity();

        //绑定控件
        ButterKnife.bind(this, view);

        findView(inflater);
        initData();
        initDepartmentView();
        initListview();
        initHandle();

        isPrepared = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setlazyLoad();//加载数据
    }

    private void findView(LayoutInflater inflater) {

        View adLL = inflater.inflate(R.layout.ad_layout, null);
        View departmentLL = inflater.inflate(R.layout.deparment_layout_1, null);
        View doctorLL = inflater.inflate(R.layout.mine_doctor_layout, null);

        this.departmentLL = (LinearLayout) departmentLL.findViewById(R.id.deparment_layout);
        this.doctorLL = (LinearLayout) doctorLL.findViewById(R.id.mine_doctor_layout);
        this.departmentLL1 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_1);
        this.departmentLL2 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_2);
        this.departmentLL3 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_3);
        this.departmentLL4 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_4);
        this.departmentLL5 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_5);
        this.departmentLL6 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_6);
        this.departmentLL7 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_7);
        this.departmentLL8 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_8);
        this.departmentLL9 = (LinearLayout) departmentLL.findViewById(R.id.department_ll_9);
        this.adRL = (RelativeLayout) adLL.findViewById(R.id.ad_layout);
        this.banner = (FlyBanner) adLL.findViewById(R.id.search_doctor_ad_banner);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.banner.getLayoutParams();
        params.width = DisplayUtils.getScreenWidth(context);
        params.height = params.width * 8 / 27;
        this.banner.setLayoutParams(params);
    }

    private void initData() {

        departmentSize = (DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 12)) / 3;

        adList = new ArrayList<>();
        doctorList = new ArrayList<>();

        doctorAdapter = new BaseAdapter<Doctor>(context, doctorList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.doctor_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final Doctor item) {
                //  getView 中position 与数据源中数据是对应的
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DoctorDetailActivity.class);
                        intent.putExtra(Preference.DOCTOR_DETAIL, item.getId());
                        startActivity(intent);
                    }
                });
                holder.getTextView(R.id.doctor_name).setText(item.getName());
                holder.getTextView(R.id.doctor_technical).setText(item.getLevel().getMark());
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
                holder.getTextView(R.id.doctor_price).setText(SpliceUtils.formatBalance2(item.getAskHospitalFee()));
                holder.getTextView(R.id.doctor_price_video).setText(SpliceUtils.formatBalance2(item
                        .getViewHospitalFee()));
                Glide.with(context).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait()).transform(new
                        CircleTransform(context)).placeholder(R.drawable.main_headportrait_xlarge).error(R.drawable
                        .main_headportrait_xlarge).into(holder.getImageView(R.id.doctor_icon));
                holder.setClickListener(R.id.doctor_picture_consult, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderPicture(item);//图文咨询
                    }
                });
                holder.setClickListener(R.id.doctor_video_order, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OrderTimeActivity.start(context, item);//选择视频预约时间
                    }
                });
            }
        };
    }

    private void initListview() {
        listView.addHeaderView(adRL);
        listView.addHeaderView(departmentLL);
        listView.addHeaderView(doctorLL);
        listView.setAdapter(doctorAdapter);
    }

    private void initHandle() {
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case NOTIFYDATA_AD:
                        if (adList.size() > 0) {
                            banner.setImagesUrl(adList);
                        }
                        break;
                    case NOTIFYDATA_DOCTOR:
                        doctorAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
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

        if (adList.size() == 0) {
            getADs();
        }
        //        if (doctorList.size() == 0) {
        getDoctors();
        //        }
    }

    /**
     * 获取广告列表信息
     */
    private void getADs() {

        RaspberryCallback<ListPageResponse<Advertisement>> callback = new
                RaspberryCallback<ListPageResponse<Advertisement>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("AD", context.getString(R.string.get_ad_fail_text));
                ToastUtils.showToast(context.getString(R.string.get_ad_fail_text));
            }

            @Override
            public void onSuccess(ListPageResponse<Advertisement> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        for (Advertisement str : response.getData()) {
                            adList.add(HttpBase.BASE_OSS_URL + str.getImg());
                        }
                        //                        adList.addAll(response.getData());
                        //                        viewPager.setAdapter(viewPagerAdapter);
                        handler.sendEmptyMessage(NOTIFYDATA_AD);
                    } else {
                        Logger.e("AD", context.getString(R.string.get_ad_fail_text) + response.getMsg());
                        ToastUtils.showToast(context.getString(R.string.get_ad_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("AD", context.getString(R.string.get_ad_fail_text));
                    ToastUtils.showToast(context.getString(R.string.get_ad_fail_text));
                }

            }
        };

        callback.setMainThread(false);
        HttpProtocol.getADs(callback);
    }

    /**
     * 获取医生信息
     */
    private void getDoctors() {

        RaspberryCallback<ListPageResponse<Doctor>> callback = new RaspberryCallback<ListPageResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Doctor", context.getString(R.string.get_doctor_fail_text));
                ToastUtils.showToast(context.getString(R.string.get_doctor_fail_text));
            }

            @Override
            public void onSuccess(ListPageResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        //                        for (Doctor doctor : response.getData()) {
                        //                            if (!doctorList.contains(doctor)) {
                        //                                doctorList.add(doctor);
                        //                            }
                        //                        }
                        doctorList.clear();
                        handler.sendEmptyMessage(NOTIFYDATA_DOCTOR);
                        doctorList.addAll(response.getData());
                        handler.sendEmptyMessage(NOTIFYDATA_DOCTOR);
                    } else {
                        Logger.e("Doctor", context.getString(R.string.get_doctor_fail_text) + response.getMsg());
                        ToastUtils.showToast(context.getString(R.string.get_doctor_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Doctor", context.getString(R.string.get_doctor_fail_text));
                    ToastUtils.showToast(context.getString(R.string.get_doctor_fail_text));
                }

            }
        };
        callback.setMainThread(false);
        HttpProtocol.getDoctors(new Page(), callback);

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

    /**
     * 初始化科室列表界面与点击事件
     */
    private void initDepartmentView() {
        ViewGroup.LayoutParams params = departmentLL1.getLayoutParams();
        params.height = departmentSize;
        departmentLL1.setLayoutParams(params);
        departmentLL2.setLayoutParams(params);
        departmentLL3.setLayoutParams(params);
        departmentLL4.setLayoutParams(params);
        departmentLL5.setLayoutParams(params);
        departmentLL6.setLayoutParams(params);
        departmentLL7.setLayoutParams(params);
        departmentLL8.setLayoutParams(params);
        departmentLL9.setLayoutParams(params);
        departmentLL1.setOnClickListener(this);
        departmentLL2.setOnClickListener(this);
        departmentLL3.setOnClickListener(this);
        departmentLL4.setOnClickListener(this);
        departmentLL5.setOnClickListener(this);
        departmentLL6.setOnClickListener(this);
        departmentLL7.setOnClickListener(this);
        departmentLL8.setOnClickListener(this);
        departmentLL9.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.department_ll_1:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_1_text));
                break;
            case R.id.department_ll_2:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_2_text));
                break;
            case R.id.department_ll_3:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_3_text));
                break;
            case R.id.department_ll_4:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_4_text));
                break;
            case R.id.department_ll_5:
                intent.setClass(context, HospitalActivity.class);
                break;
            case R.id.department_ll_6:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_6_text));
                break;
            case R.id.department_ll_7:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_7_text));
                break;
            case R.id.department_ll_8:
                intent.setClass(context, SearchDoctorActivity.class);
                intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity.DEPARTMENT);
                intent.putExtra(Preference.SEARCH_MSG, context.getString(R.string.department_8_text));
                break;
            case R.id.department_ll_9:
                intent.setClass(context, DepartmentActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }

}