package com.shkjs.patient.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.AdViewpagerUtil;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RecycleViewDecoration;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.DepartmentActivity;
import com.shkjs.patient.activity.DoctorDetailActivity;
import com.shkjs.patient.activity.OrderTimeActivity;
import com.shkjs.patient.activity.PayActivity;
import com.shkjs.patient.activity.SearchDoctorActivity;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.Advertisement;
import com.shkjs.patient.bean.Department;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
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
 * Created by LENOVO on 2016/8/17.
 * 诊疗室Fragment，展示医生相关信息，并提供搜索入口
 */

public class SearchDoctorFragment extends BaseFragment {

    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    private Context context;

    private int departmentSize;
    private int departmentPageNum = 0;
    private int doctorPageNum = 0;
    private Page departmentPage;
    private Page doctorPage;

    @Bind(R.id.search_doctor_ad_linearlayout)
    LinearLayout adLL;
    @Bind(R.id.department_layout)
    LinearLayout departmentLL;
    @Bind(R.id.search_doctor_ad_viewpager)
    ViewPager viewPager;
    @Bind(R.id.search_doctor_doctor_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.search_doctor_doctor_recyclerview)
    RecyclerView doctorRCV;
    @Bind(R.id.search_doctor_list_type_imageview)
    ImageView doctorTypeIV;
    @Bind(R.id.search_doctor_list_type_textview)
    TextView doctorTypeTV;

    private PagerAdapter viewPagerAdapter;
    private BaseRecyclerAdapter<Doctor> doctorAdapter;
    private List<String> adList;
    private List<Department> departmentList;
    private List<Doctor> doctorList;

    private AdViewpagerUtil viewpagerUtil;

    private Handler handler;

    private static final int NOTIFYDATA_DEPARTMENT = 121;//更新科室
    private static final int NOTIFYDATA_DOCTOR = 122;//更新医生
    private static final int NOTIFYDATA_AD = 123;//更新广告


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_doctor, null);
        context = getActivity();

        //绑定控件
        ButterKnife.bind(this, view);
        initData();
        initListener();
        initHandle();

        isPrepared = true;
        setlazyLoad();//加载数据
        return view;
    }

    private void initData() {

        departmentPage = new Page();
        departmentPage.setPageSize(8);
        doctorPage = new Page();

        departmentSize = (DisplayUtils.getScreenWidth(context) - 2 * DisplayUtils.dip2px(context, 10) - 8) / 4;

        adList = new ArrayList<>();
        departmentList = new ArrayList<>();
        doctorList = new ArrayList<>();
        //        for (int i = 0; i < 8; i++) {
        //            departmentList.add(new Department());
        //            doctorList.add(new Doctor());
        //        }

        viewpagerUtil = new AdViewpagerUtil(context, viewPager, adLL, 8, 4, adList);
        viewpagerUtil.setAllowLoop(true);

        viewPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return adList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                Glide.with(context).load(HttpBase.BASE_OSS_URL + adList.get(position)).into(imageView);
                Logger.d(HttpBase.BASE_OSS_URL + adList.get(position));
                container.addView(imageView, 0);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        doctorAdapter = new BaseRecyclerAdapter<Doctor>(context, doctorList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.doctor_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Doctor item) {
                holder.getTextView(R.id.doctor_name).setText(item.getName());
                holder.getTextView(R.id.doctor_technical).setText(item.getLevel().getMark());
                holder.getTextView(R.id.doctor_hospital).setText(item.getHospitalName());
                holder.getTextView(R.id.doctor_department).setText(item.getCategoryName());
                holder.getTextView(R.id.doctor_price).setText(item.getAskHospitalFee().toString());
                
                holder.getTextView(R.id.doctor_price_video).setText(item.getViewHospitalFee().toString());
                Glide.with(context).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait()).transform(new
                        CircleTransform(context)).placeholder(R.drawable.actionbar_headportrait_small).error(R
                        .drawable.actionbar_headportrait_small).into(holder.getImageView(R.id.doctor_icon));
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

    private void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDoctors();
            }
        });
        //设置item动画
        doctorRCV.setItemAnimator(new DefaultItemAnimator());
        doctorRCV.setLayoutManager(new LinearLayoutManager(context));
        doctorRCV.addItemDecoration(new RecycleViewDecoration(context));//分割线
        doctorRCV.setAdapter(doctorAdapter);
        doctorAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Intent intent = new Intent(context, DoctorDetailActivity.class);
                intent.putExtra(Preference.DOCTOR_DETAIL, doctorList.get(pos).getId());
                startActivity(intent);
            }
        });
    }

    private void initHandle() {
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case NOTIFYDATA_DEPARTMENT:
                        showDepartmentView();
                        break;
                    case NOTIFYDATA_DOCTOR:
                        doctorAdapter.notifyDataSetChanged();
                        break;
                    case NOTIFYDATA_AD:
                        viewPagerAdapter.notifyDataSetChanged();
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

        //TODO lxh 需修改，只实现了数据获取展示，未处理异常情况
        if (adList.size() == 0) {
            getADs();
        }
        if (departmentList.size() == 0) {
            getDepartments();
        }
        if (doctorList.size() == 0) {
            getDoctors();
        }
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
                Logger.e("AD", getString(R.string.get_ad_fail_text));
                ToastUtils.showToast(getString(R.string.get_ad_fail_text));
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
                        viewpagerUtil.setUrls(adList);
                        handler.sendEmptyMessage(NOTIFYDATA_AD);

                    } else {
                        Logger.e("AD", getString(R.string.get_ad_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_ad_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("AD", getString(R.string.get_ad_fail_text));
                    ToastUtils.showToast(getString(R.string.get_ad_fail_text));
                }

            }
        };

        callback.setMainThread(true);
        HttpProtocol.getADs(callback);
    }

    /**
     * 获取科室列表信息
     */
    private void getDepartments() {

        RaspberryCallback<ListPageResponse<Department>> callback = new
                RaspberryCallback<ListPageResponse<Department>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Department", getString(R.string.get_department_fail_text));
                ToastUtils.showToast(getString(R.string.get_department_fail_text));
            }

            @Override
            public void onSuccess(ListPageResponse<Department> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        departmentPageNum = response.getPageNum();
                        departmentList.clear();
                        departmentList.addAll(response.getData());
                        handler.sendEmptyMessage(NOTIFYDATA_DEPARTMENT);
                    } else {
                        Logger.e("Department", getString(R.string.get_department_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_department_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Department", getString(R.string.get_department_fail_text));
                    ToastUtils.showToast(getString(R.string.get_department_fail_text));
                }

            }
        };
        callback.setMainThread(true);
        if (departmentPage.getPageNum() == departmentPageNum) {
            departmentPage.setPageNum(departmentPageNum + 1);
        }
        HttpProtocol.getDepartments(departmentPage, callback);
    }

    /**
     * 获取医生信息
     */
    private void getDoctors() {

        RaspberryCallback<ListPageResponse<Doctor>> callback = new RaspberryCallback<ListPageResponse<Doctor>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(ListPageResponse<Doctor> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        doctorPageNum = response.getPageNum();
                        doctorTypeTV.setText(response.getDescription());
                        for (Doctor doctor : response.getData()) {
                            if (!doctorList.contains(doctor)) {
                                doctorList.add(doctor);
                            }
                        }
                        handler.sendEmptyMessage(NOTIFYDATA_DOCTOR);
                    } else {
                        Logger.e("Doctor", getString(R.string.get_doctor_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_doctor_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Doctor", getString(R.string.get_doctor_fail_text));
                    ToastUtils.showToast(getString(R.string.get_doctor_fail_text));
                }
                refreshLayout.setRefreshing(false);

            }
        };
        callback.setMainThread(true);
        if (doctorPage.getPageNum() == doctorPageNum) {
            doctorPage.setPageNum(doctorPageNum + 1);
        }
        if (null != DataCache.getInstance().getUserInfo()) {
            HttpProtocol.getDoctors(DataCache.getInstance().getUserInfo().getId(), doctorPage, callback);
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
                        getOrderInfo(response.getData(), doctor);
                        return;
                    } else if (response.getStatus().equals(ResultStatus.FAIL)) {
                        ToastUtils.showToast(response.getMsg());
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
     * 显示科室列表界面
     */
    private void showDepartmentView() {
        departmentLL.setVisibility(View.VISIBLE);
        if (null != departmentList && departmentList.size() > 0) {
            for (int i = 0; i < departmentList.size(); i++) {
                LinearLayout layout = (LinearLayout) departmentLL.findViewWithTag("ll_" + i);
                // Glide会给imageview设置tag，所以有bug
                //                ImageView icon = (ImageView) departmentLL.findViewWithTag("iv_" + i);
                ImageView icon = (ImageView) layout.getChildAt(0);
                TextView name = (TextView) departmentLL.findViewWithTag("tv_" + i);
                final Department department = departmentList.get(i);
                Glide.with(context).load(HttpBase.BASE_OSS_URL + department.getDeptImg()).placeholder(R.drawable
                        .actionbar_headportrait_small).error(R.drawable.actionbar_headportrait_small).into(icon);
                name.setText(department.getName());

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        if (department.getName().equals(getString(R.string.more_department_text))) {
                            intent.setClass(context, DepartmentActivity.class);
                        } else {
                            intent.setClass(context, SearchDoctorActivity.class);
                            intent.putExtra(SearchDoctorActivity.class.getSimpleName(), SearchDoctorActivity
                                    .DEPARTMENT);
                            intent.putExtra(Preference.SEARCH_MSG, department.getName());
                        }
                        startActivity(intent);
                    }
                });
            }
        }
    }

}