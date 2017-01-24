package com.shkjs.doctor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.bumptech.glide.Glide;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.CreateAVPatientBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.EventBusCreateAV;
import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAVActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;                                   //标题
    @Bind(R.id.create_avchart_addpatient_fl)
    FrameLayout create_avchart_addpatient_fl;               //添加患者医生的扫描和我的联系人（患者）
    @Bind(R.id.create_avchart_addpatient_allfl)
    FrameLayout create_avchart_addpatient_allfl;            //添加患者和医生的三个途径
    @Bind(R.id.item_createav_head_iv)
    CircleImageView item_createav_head_iv;                  //添加患者结果姓名
    @Bind(R.id.item_createav_name_tv)
    TextView item_createav_name_tv;
    @Bind(R.id.item_createav_sex_tv)
    TextView item_createav_sex_tv;
    @Bind(R.id.item_createav_age_tv)
    TextView item_createav_age_tv;
    @Bind(R.id.create_avchart_up_next_fl)                   //显示下一步，上一步，完成
            FrameLayout create_avchart_up_next_fl;
    @Bind(R.id.create_avchart_addpatient_rv)
    RecyclerView recyclerViewPatient;                       //展示患者搜索数据
    @Bind(R.id.create_avchart_adddoctor_tv)
    Button create_avchart_adddoctor_tv;                   //邀请专家标题
    @Bind(R.id.create_avchart_adddoctor_rv)
    RecyclerView recyclerViewDoctor;                        //展示医生搜索数据
    @Bind(R.id.create_avchart_settime_tv)
    Button create_avchart_settime_tv;
    @Bind(R.id.create_avchart_settime_year)
    TextView create_avchart_settime_year;
    @Bind(R.id.create_avchart_settime_month)
    TextView create_avchart_settime_month;
    @Bind(R.id.create_avchart_settime_day)
    TextView create_avchart_settime_day;
    @Bind(R.id.create_avchart_settime_hour)
    TextView create_avchart_settime_hour;
    @Bind(R.id.create_avchart_settime_minute)
    TextView create_avchart_settime_minute;
    @Bind(R.id.create_avchart_adddoctor_search)
    EditText create_avchart_adddoctor_search;
    @Bind(R.id.create_avchart_addpatient_search)
    EditText create_avchart_addpatient_search;


    //最后需要上传到服务器的患者医生数据对象
    private UserInfoBean resultPatientBean;
    private List<DoctorBean> resultDoctorList;
    private String resultTime;

    //下一步或没有下一步
    private static final int NONEXT = -1;
    private static final int NEXT = 0;
    private static final int UP_NEXT = 1;
    private static final int FINISH = 2;

    //添加患者医生的三个途径界面
    private static final int CREATE_FIND_PATIENT = 0;
    private static final int CREATE_FIND_PATIENT_RESULT = 1;
    private static final int CREATE_FIND_DOCTOR = 2;
    private static final int CREATE_SETTING_TIME = 3;

    //显示隐藏扫一扫和我的患者
    private static final int QRSCAN_MYPATIENT = 0;

    //患者显示的数据源
    private BaseRecyclerAdapter<CreateAVPatientBean> adapterPatient;
    private List<CreateAVPatientBean> AVPatientBeanList;
    //医生显示的数据源
    private BaseRecyclerAdapter<DoctorBean> adapterSearchDocotr;
    private BaseRecyclerAdapter<DoctorBean> adapterResultDoctor;
    private List<DoctorBean> AVDoctorBeanList;
    //当前系统时间
    private String currentTime;

    private RaspberryCallback<BaseResponse> createCallback;
    private RaspberryCallback<ObjectResponse<DoctorBean>> getDoctorCallback;
    private RaspberryCallback<ObjectResponse<CreateAVPatientBean>> getPatientCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_av);
        ButterKnife.bind(this);
        initViews();
        toptitle_tv.setText(R.string.create_avchart_title);
        EventBus.getDefault().register(this);
    }

    //Activity和Fragment数据交互
    public void onEventMainThread(EventBusCreateAV event) {
        switch (event.getType()) {
            case Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT:
                setNumVisible(CREATE_FIND_PATIENT_RESULT);
                initAddPatientResult(event.getName(), event.getAge(), event.getHead(), event.getSex(), event.getUserId());
                setNextUpVisible(NEXT);
                break;
            case Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR:
                DoctorBean bean = new DoctorBean();
                bean.setName(event.getName());
                bean.setId(Integer.parseInt(event.getUserId()));
                bean.setHeadPortrait(event.getHead());
                bean.setCategoryName(event.getAge());
                bean.setHospitalName(event.getSex());
                bean.setLevel(event.getLevel());
                if (!checkHasAlready(Integer.parseInt(event.getUserId()))) {
                    resultDoctorList.add(bean);
                }
                recyclerViewDoctor.setAdapter(adapterResultDoctor);
                setNextUpVisible(UP_NEXT);
                break;
            case Preference.ADD_PATIENT:
                setNumVisible(CREATE_FIND_PATIENT_RESULT);
                initAddPatientResult(event.getName(), event.getAge(), event.getHead(), event.getSex(), event.getUserId());
                setNextUpVisible(NEXT);
                break;
        }
    }

    //显示最终添加患者结果
    public void initAddPatientResult(String name, String birthday, String head, String sex, String UserId) {

        Glide.with(this).load(head).thumbnail(0.1f).placeholder(R.drawable.default_head_rect).dontAnimate().into(item_createav_head_iv);
        AudioHelper.setNameWithDefault(item_createav_name_tv, name, name);
        if (!StringUtil.isEmpty(sex)) {
            AudioHelper.setTextWithDefault(item_createav_sex_tv, Sex.getSexCalue(sex));
        }
        AudioHelper.setAgeWithDefault(item_createav_age_tv, birthday);
        //保存最后的患者信息，就算重置，也会更新最终数据
        resultPatientBean.setSex(sex);
        resultPatientBean.setName(name);
        resultPatientBean.setHeadPortrait(head);
        resultPatientBean.setId(Integer.parseInt(UserId));
    }

    //设置界面是否可见
    public void setNumVisible(int index) {
        for (int i = 0; i < create_avchart_addpatient_allfl.getChildCount(); i++) {
            if (index == i) {
                create_avchart_addpatient_allfl.getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                create_avchart_addpatient_allfl.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    //设置下一步和上一步显示
    public void setNextUpVisible(int index) {
        for (int i = 0; i < create_avchart_up_next_fl.getChildCount(); i++) {
            if (index == i) {
                create_avchart_up_next_fl.getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                create_avchart_up_next_fl.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    //搜索患者
    public void loadNetPatientSearch() {
        HttpProtocol.getPatientByUserName(getPatientCallback, create_avchart_addpatient_search.getText().toString().trim());
    }

    //搜索医生
    public void loadNetDoctorSearch(String useName) {
        AVDoctorBeanList.clear();
        HttpProtocol.getDoctorInfoFromName(getDoctorCallback, useName);
    }

    //初始化界面控件
    public void initViews() {

        resultDoctorList = new ArrayList<>();
        resultPatientBean = new UserInfoBean();

        AVPatientBeanList = new ArrayList<>();
        AVDoctorBeanList = new ArrayList<>();

        //搜索医生数据显示
        adapterSearchDocotr = new BaseRecyclerAdapter<DoctorBean>(this, AVDoctorBeanList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_create_avchart_adddoctor;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final DoctorBean item) {
                Glide.with(CreateAVActivity.this).load(HttpBase.IMGURL + item.getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(holder.getCircleImageView(R.id.item_create_avchart_iv));
                holder.getTextView(R.id.item_create_avchart_name_tv).setText(item.getName());
                holder.getTextView(R.id.item_create_avchart_hospital_tv).setText(item.getHospitalName());
                holder.getTextView(R.id.item_create_avchart_department).setText(item.getCategoryName());
                holder.getTextView(R.id.item_create_avchart_title_tv).setText(Preference.getLevelValue(item.getLevel()));
                Button item_create_avchart_add_delete = holder.getButton(R.id.item_create_avchart_add_delete);
                item_create_avchart_add_delete.setText(getResources().getString(R.string.add));
                item_create_avchart_add_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //添加搜索的医生
                        if (DataCache.getInstance().getUserInfo() != null) {
                            if (!checkHasAlready(item.getId()) && item.getId() != DataCache.getInstance().getUserInfo().getId()) {
                                resultDoctorList.add(item);
                                ToastUtils.showToast("成功添加医生。");
                                setNextUpVisible(UP_NEXT);
                            } else {
                                ToastUtils.showToast("不能重复添加医生，或添加创建医生！");
                            }
                        } else {
                            if (!checkHasAlready(item.getId())) {
                                resultDoctorList.add(item);
                                ToastUtils.showToast("成功添加医生。");
                                setNextUpVisible(UP_NEXT);
                            } else {
                                ToastUtils.showToast("不能重复添加医生！");
                            }
                        }

                        AVDoctorBeanList.clear();
                        recyclerViewDoctor.setAdapter(adapterResultDoctor);
                    }
                });

            }
        };

        //邀请医生最终数据显示
        adapterResultDoctor = new BaseRecyclerAdapter<DoctorBean>(this, resultDoctorList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_create_avchart_adddoctor;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, DoctorBean item) {
                Glide.with(CreateAVActivity.this).load(HttpBase.IMGURL + item.getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(holder.getCircleImageView(R.id.item_create_avchart_iv));
                holder.getTextView(R.id.item_create_avchart_name_tv).setText(item.getName());
                holder.getTextView(R.id.item_create_avchart_hospital_tv).setText(item.getHospitalName());
                holder.getTextView(R.id.item_create_avchart_department).setText(item.getCategoryName());
                holder.getTextView(R.id.item_create_avchart_title_tv).setText(Preference.getLevelValue(item.getLevel()));
                Button item_create_avchart_add_delete = holder.getButton(R.id.item_create_avchart_add_delete);
                item_create_avchart_add_delete.setText(getResources().getString(R.string.delete));
                item_create_avchart_add_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除已添加医生
                        resultDoctorList.remove(position);
                        adapterResultDoctor.notifyDataSetChanged();
                        if (resultDoctorList.size() == 0) {
                            setNextUpVisible(NONEXT);
                        }
                    }
                });
            }
        };

        createCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    CustomAlertDialog.dialogWithSure(CreateAVActivity.this, "会诊创建成功，待被邀医生确认和被邀患者付款。", new CustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            Intent intent = getIntent();
                            setResult(Preference.VIDEO_GROUP_CREATE_RESULT, intent);
                            finish();
                        }
                    });
                }
            }
        };
        createCallback.setCancelable(false);
        createCallback.setContext(this);
        createCallback.setShowDialog(true);
        createCallback.setMainThread(true);

        getDoctorCallback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null) {
                        AVDoctorBeanList.clear();
                        AVDoctorBeanList.add(response.getData());
                        adapterSearchDocotr.notifyDataSetChanged();
                        recyclerViewDoctor.setAdapter(adapterSearchDocotr);
                    } else {
                        ToastUtils.showToast("搜索失败，没有相应的医生。");
                    }
                }
            }
        };
        getDoctorCallback.setCancelable(false);
        getDoctorCallback.setContext(this);
        getDoctorCallback.setShowDialog(true);
        getDoctorCallback.setMainThread(true);

        getPatientCallback = new RaspberryCallback<ObjectResponse<CreateAVPatientBean>>() {
            @Override
            public void onSuccess(ObjectResponse<CreateAVPatientBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null) {
                        AVPatientBeanList.add(response.getData());
                        //扫一扫和我的患者隐藏
                        create_avchart_addpatient_fl.getChildAt(QRSCAN_MYPATIENT).setVisibility(View.GONE);
                        adapterPatient.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast("不存在该用户，请确保手机号已注册医星汇平台。");
                    }
                }
            }
        };
        getPatientCallback.setCancelable(false);
        getPatientCallback.setContext(this);
        getPatientCallback.setShowDialog(true);
        getPatientCallback.setMainThread(true);

        //搜索患者显示数据
        adapterPatient = new BaseRecyclerAdapter<CreateAVPatientBean>(this, AVPatientBeanList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_my_patient;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final CreateAVPatientBean item) {
                Glide.with(CreateAVActivity.this).load(HttpBase.IMGURL + item.getHeadPortrait()).placeholder(R.drawable.default_head_rect).dontAnimate().thumbnail(0.1f).into(holder.getCircleImageView(R.id.item_mypatient_head_iv));
                AudioHelper.setNameWithDefault(holder.getTextView(R.id.item_mypatient_name_tv), item.getName(), item.getNickName());
                AudioHelper.setAgeWithDefault(holder.getTextView(R.id.item_mypatient_age_tv), item.getBirthday());
                AudioHelper.setSexText(holder.getTextView(R.id.item_mypatient_sex_tv), item.getSex());
                Button item_mypatient_add_btn = holder.getButton(R.id.item_mypatient_add_btn);
                item_mypatient_add_btn.setVisibility(View.VISIBLE);
                item_mypatient_add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initAddPatientResult(!StringUtil.isEmpty(item.getName()) ? item.getName() : item.getNickName(), item.getBirthday(), HttpBase.IMGURL + item.getHeadPortrait(), item.getSex(), item.getId() + "");
                        //点击添加，显示结果
                        setNumVisible(CREATE_FIND_PATIENT_RESULT);
                        //清空搜索数据
                        AVPatientBeanList.clear();
                        //更新列表
                        adapterPatient.notifyDataSetChanged();
                        //显示下一步
                        setNextUpVisible(NEXT);
                    }
                });
            }
        };

        recyclerViewPatient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPatient.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewPatient.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPatient.setAdapter(adapterPatient);

        recyclerViewDoctor.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDoctor.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctor.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    //所有按钮点击事件
    @OnClick({R.id.back_iv, R.id.create_avchart_addpatient_searchbtn, R.id.create_avchart_addpatient_scanqrcode
            , R.id.create_avchart_addpatient_mypatient, R.id.create_avchart_addpatient_delete, R.id.create_avchart_addpatient_next
            , R.id.create_avchart_adddoctor_scanqrcode, R.id.create_avchart_adddoctor_searchbtn, R.id.create_avchart_adddoctor_up
            , R.id.create_avchart_adddoctor_next, R.id.create_avchart_date_ll, R.id.create_avchart_addpatient_finish
            , R.id.create_avchart_adddoctor_finish_up, R.id.create_avchart_time_ll, R.id.create_avchart_adddoctor_contacts})
    public void createOnClick(View view) {
        switch (view.getId()) {
            //返回键
            case R.id.back_iv:
                finish();
                break;
            //添加患者搜索键
            case R.id.create_avchart_addpatient_searchbtn:
                loadNetPatientSearch();
                break;
            //添加患者扫描二维码
            case R.id.create_avchart_addpatient_scanqrcode:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        startActivity(new Intent(CreateAVActivity.this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT));
                    }
                },Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT);
                break;
            //添加患者我的患者
            case R.id.create_avchart_addpatient_mypatient:
                startActivity(new Intent(this, MyPatientsActivity.class).putExtra(Preference.MYPATIENT_TYPE, Preference.ADD_PATIENT));
                break;
            //添加患者最后结果删除按钮
            case R.id.create_avchart_addpatient_delete:
                setNumVisible(CREATE_FIND_PATIENT);
                setNextUpVisible(NONEXT);
                create_avchart_addpatient_fl.getChildAt(QRSCAN_MYPATIENT).setVisibility(View.VISIBLE);
                break;
            //添加患者下一步，进入添加医生的环节
            case R.id.create_avchart_addpatient_next:
                //设置标题
                create_avchart_adddoctor_tv.setBackgroundResource(R.drawable.shape_sure);
                //显示添加三个途径
                setNumVisible(CREATE_FIND_DOCTOR);
                //显示结果
                recyclerViewDoctor.setAdapter(adapterResultDoctor);
                if (resultDoctorList.size() > 0) {
                    setNextUpVisible(UP_NEXT);
                } else {
                    //隐藏下一步
                    setNextUpVisible(NONEXT);
                }
                break;
            //邀请专家扫描二维码
            case R.id.create_avchart_adddoctor_scanqrcode:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        startActivity(new Intent(CreateAVActivity.this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR));
                    }
                },Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR);
                break;
            //搜索医生
            case R.id.create_avchart_adddoctor_searchbtn:
                loadNetDoctorSearch(create_avchart_adddoctor_search.getText().toString());
                break;
            //邀请专家上一步
            case R.id.create_avchart_adddoctor_up:
                //修改标题
                create_avchart_adddoctor_tv.setBackgroundResource(R.drawable.shap_personal_next);
                //显示添加三个途径
                setNumVisible(CREATE_FIND_PATIENT_RESULT);
                //显示添加患者的下一步
                setNextUpVisible(NEXT);
                break;
            //邀请专家下一步
            case R.id.create_avchart_adddoctor_next:
                //设置标题
                create_avchart_settime_tv.setBackgroundResource(R.drawable.shape_sure);
                //显示设置时间
                setNumVisible(CREATE_SETTING_TIME);
                //获取当前时间
                currentTime = DateUtil.getCurrentTime("yyyy-MM-dd HH:mm");
                String[] times = currentTime.split(" ");
                String[] dates = times[0].split("-");
                String[] hours = times[1].split(":");
                create_avchart_settime_year.setText(dates[0]);
                create_avchart_settime_month.setText(dates[1]);
                create_avchart_settime_day.setText(dates[2]);
                create_avchart_settime_hour.setText(hours[0]);
                create_avchart_settime_minute.setText(hours[1]);
                //显示完成
                setNextUpVisible(FINISH);
                break;
            //点击选择日期
            case R.id.create_avchart_date_ll:
                String strDate = DateUtil.getCurrentTime("yyyy-MM-dd");
                DatePickerDialog.newInstance(this, Integer.parseInt(strDate.split("-")[0]), Integer.parseInt(strDate.split("-")[1]) - 1, Integer.parseInt(strDate.split("-")[2])).show(getFragmentManager(), "datePicker");
                break;
            case R.id.create_avchart_time_ll:
                String strTime = DateUtil.getCurrentTime("HH:mm");
                TimePickerDialog.newInstance(this, Integer.parseInt(strTime.split(":")[0]), Integer.parseInt(strTime.split(":")[1]), true).show(getFragmentManager(), "timePicker");
                break;
            //完成
            case R.id.create_avchart_addpatient_finish:
                resultTime = getResultTime();
                Long currentTime = null;
                try {
                    currentTime = DateUtil.String2Long(resultTime, "yyyy-MM-dd HH:mm");
                    //创建时间必须在当前时间向后1个半小时
                    if (currentTime >= System.currentTimeMillis() + 5400000) {
                        HttpProtocol.putCreateGroupAv(createCallback, resultTime + ":00", resultTime + ":00", resultPatientBean.getId() + "", resultDoctorList,this);
                    } else {
                        CustomAlertDialog.dialogWithSure(CreateAVActivity.this, "会诊开始时间需要在当前时间的一个半小时之后，请重新选择会诊时间。", null);
                    }
                } catch (Exception e) {
                    ToastUtils.showToast("时间解析异常,请重新尝试。");
                }

                break;
            //设置时间的上一步
            case R.id.create_avchart_adddoctor_finish_up:
                //修改标题
                create_avchart_settime_tv.setBackgroundResource(R.drawable.shap_personal_next);
                //显示设置时间
                setNumVisible(CREATE_FIND_DOCTOR);
                //显示上一步下一步
                setNextUpVisible(UP_NEXT);
                break;
            //通讯录
            case R.id.create_avchart_adddoctor_contacts:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission
                            .READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS).build(), new AcpListener() {

                        @Override
                        public void onGranted() {
                            startActivityForResult(new Intent(CreateAVActivity.this, ContactsActivity.class), Preference.CREATEAV_CONTACTS);
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                                    .toast_permission_hint));
                        }
                    });
                } else {
                    startActivityForResult(new Intent(CreateAVActivity.this, ContactsActivity.class), Preference.CREATEAV_CONTACTS);
                }
                break;
        }
    }


    public void checkPermission(final CheckpermissionCallback checkpermissionCallback,String code) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                    .READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission
                    .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {

                @Override
                public void onGranted() {
                    checkpermissionCallback.onGrantedSuccess();
                }

                @Override
                public void onDenied(List<String> permissions) {
                    ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                            .toast_permission_hint));
                }
            });
        } else {
            startActivity(new Intent(this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, code));
        }
    }

    public interface CheckpermissionCallback {
        void onGrantedSuccess();
    }

    //防止重复添加同一个医生
    public boolean checkHasAlready(int doctorId) {
        boolean hasMyself = false;
        for (DoctorBean doctorBean : resultDoctorList) {
            if (doctorBean.getId() == doctorId) {
                hasMyself = true;
                Log.i("tag00", "已经存在本人");
                break;
            }
        }
        return hasMyself;
    }


    public String getResultTime() {
        String time = create_avchart_settime_year.getText().toString() + "-" + create_avchart_settime_month.getText().toString() +
                "-" + create_avchart_settime_day.getText().toString() + " " + create_avchart_settime_hour.getText().toString() +
                ":" + create_avchart_settime_minute.getText().toString();
        return time;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        create_avchart_settime_year.setText(year + "");
        create_avchart_settime_month.setText((monthOfYear + 1) + "");
        create_avchart_settime_day.setText(dayOfMonth + "");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        create_avchart_settime_hour.setText(hourOfDay + "");
        create_avchart_settime_minute.setText(minute + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Preference.CREATEAV_CONTACTS && resultCode == Preference.CREATEAV_CONTACTS_REAULT) {
            String phoneNumber = data.getStringExtra("phoneNumber");
            create_avchart_adddoctor_search.setText(phoneNumber.replaceAll(" ", ""));
            loadNetDoctorSearch(phoneNumber.replaceAll(" ", ""));
        }
    }
}
