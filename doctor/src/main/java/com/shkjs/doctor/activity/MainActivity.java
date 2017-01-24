package com.shkjs.doctor.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.qrcode.QRCodeUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DESUtils;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.JsonUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.LoginManager;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.UpdateManager;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.EarliestVideoBean;
import com.shkjs.doctor.bean.GroupSitDiagnoseRoomInfo;
import com.shkjs.doctor.bean.QRCodeBean;
import com.shkjs.doctor.bean.VideoConsultBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.DoctorClient;
import com.shkjs.doctor.data.DoctorTag;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.util.QRNormalPopup;
import com.shkjs.nim.chat.activity.AVChatActivity;
import com.shkjs.nim.chat.activity.AVChatRoomActivity;
import com.shkjs.nim.em.ClientType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private long currentBackPressedTime = 0;                   // 点击返回键时间
    private static final int BACK_PRESSED_INTERVAL = 1000;    // 两次点击返回键时间间隔

    @Bind(R.id.main_content_top_title_tv)
    TextView main_content_top_title_tv;                     //标题
    @Bind(R.id.main_content_cm_state_tv)
    TextView main_content_cm_state_tv;
    @Bind(R.id.main_content_head_iv)
    CircleImageView main_content_head_iv;                   //头像
    @Bind(R.id.main_content_authority_iv)
    ImageView main_content_authority_iv;                    //权威验证图标
    @Bind(R.id.main_content_cm_time_tv)
    TextView main_content_cm_time_tv;                       //待诊卡片时间
    @Bind(R.id.main_content_cm_username_tv)
    TextView main_content_cm_username_tv;                   //待诊患者姓名
    @Bind(R.id.main_content_cm_sex_tv)
    TextView main_content_cm_sex_tv;                        //待诊患者性别
    @Bind(R.id.main_content_cm_age_tv)
    TextView main_content_cm_age_tv;                        //待诊患者年龄
    @Bind(R.id.main_content_cm_msg_tv)
    TextView main_content_cm_msg_tv;                        //待诊患者病情描述
    @Bind(R.id.main_content_swiprefresh_srl)
    SwipeRefreshLayout main_content_swiprefresh_srl;        //下拉刷新
    @Bind(R.id.main_content_cm_refresh)
    ImageView main_content_cm_refresh;                      //点击刷新
    @Bind(R.id.main_content_cm_fl)
    FrameLayout main_content_cm_fl;                         //是否有患者预约
    @Bind(R.id.main_content_video_redot)
    ImageView main_content_video_redot;
    @Bind(R.id.main_content_group_redot)
    ImageView main_content_group_redot;
    @Bind(R.id.main_content_picture_redot)
    ImageView main_content_picture_redot;
    @Bind(R.id.main_content_system_redot)
    ImageView main_content_system_redot;

    private DoctorBean doctorBean;
    private RaspberryCallback<ObjectResponse<DoctorBean>> callback;
    private RaspberryCallback<ObjectResponse<EarliestVideoBean>> earliestCallback;
    private RaspberryCallback<ObjectResponse<HashMap<String, String>>> reddotCallback;
    private RaspberryCallback<ObjectResponse<String>> canableCallback;
    private RaspberryCallback<ListResponse<VideoConsultBean>> setTimesCallback;


    private EarliestVideoBean.UserInfoBean earliestUserinfo;
    private int orderId = 0;
    private String orderType = "";
    private String orderCode;
    private boolean isFirstIn = true;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("tag00", "收到广播:" + action);
            if (Preference.VIDEO_CONSULT_MAIN.equals(action)) {
                if (intent.getStringExtra("set_call_times") != null) {
                    final String userId = intent.getStringExtra("set_call_times");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    HttpProtocol.setCallTimes(setTimesCallback, userId, orderId + "");
                                }
                            });
                        }
                    }, 300);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, MedicalActivity.class);
                    intent1.putExtra("orderId", orderId + "");
                    intent1.putExtra("orderCode", orderCode);
                    intent1.putExtra(Preference.COMPLETE_TYPE, Preference.COMPLETE_SIT_VIDEO_CONSULT);
                    startActivity(intent1);
                }
            }
        }

    };


    /**
     * 启动MainActivity必须通过该方式，好统一初始化数据之类
     *
     * @param context
     */
    public static void startMainActivity(Context context) {
        startMainActivity(context, null);
    }

    /**
     * 启动MainActivity必须通过该方式，好统一初始化数据之类
     *
     * @param context
     * @param data
     */
    public static void startMainActivity(Context context, Intent data) {
        //TODO lxh
        if (null != data) {
            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            data = new Intent(context, MainActivity.class);
            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
        initData();
        //注册广播
        registerBoradcastReceiver();
        //检查更新
        queryUpdateVersion();
    }

    private void initListener() {

        callback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                stopSwipRefesh();
            }

            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                stopSwipRefesh();
                if (HttpProtocol.checkStatus(response, code) && response.getData() != null) {
                    doctorBean = response.getData();
                    DataCache.getInstance().setUserInfo(doctorBean);
                    MyApplication.doctorBean = doctorBean;
                    //加载医生头像
                    AudioHelper.setCircleImage(MainActivity.this, main_content_head_iv, doctorBean.getHeadPortrait(), R.drawable.main_message_headportrait);
                    main_content_top_title_tv.setText(doctorBean.getName());
                    if (Preference.AUTHORITY.equals(doctorBean.getPlatformLevel())) {
                        main_content_authority_iv.setImageResource(R.drawable.main_home_authority);
                    } else if (Preference.CERTIFICATION.equals(doctorBean.getPlatformLevel())) {
                        main_content_authority_iv.setImageResource(R.drawable.main_home_certification);
                    } else {
                        main_content_authority_iv.setImageResource(R.drawable.main_home_unauthorized);
                    }
                }
            }
        };
        AudioHelper.initCallBack(callback, this, false);

        earliestCallback = new RaspberryCallback<ObjectResponse<EarliestVideoBean>>() {
            @Override
            public void onFinish() {
                super.onFinish();
                stopSwipRefesh();
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onSuccess(ObjectResponse<EarliestVideoBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null) {
                        AudioHelper.showFrame(0, main_content_cm_fl);
                        orderId = response.getData().getId();
                        orderCode = response.getData().getCode();
                        earliestUserinfo = response.getData().getUserInfo();
                        orderType = response.getData().getMark();
                        //视频咨询时间
                        String startTime = DateUtil.getFormatTimeFromTimestamp(Long.parseLong(response.getData().getDiagnoseDate()), "HH:mm");
                        String endTime = (Integer.parseInt(startTime.split(":")[0]) + 1) + ":" + startTime.split(":")[1];
                        main_content_cm_time_tv.setText(startTime + "--" + endTime);
                        AudioHelper.setNameWithDefault(main_content_cm_username_tv, response.getData().getUserInfo().getName(), response.getData().getUserInfo().getNickName());
                        AudioHelper.setTextWithDefault(main_content_cm_sex_tv, Sex.getSexCalue(response.getData().getUserInfo().getSex() != null ? response.getData().getUserInfo().getSex() : ""));
                        AudioHelper.setAgeWithDefault(main_content_cm_age_tv, response.getData().getUserInfo().getBirthday());
                        AudioHelper.setTextWithDefault(main_content_cm_msg_tv, response.getData().getHealthReport().getContent());
                        if (orderType.equals("多人会诊子项")) {
                            main_content_cm_state_tv.setText("当前最近多人视频订单");
                            main_content_cm_time_tv.setText("开始时间：" + startTime);
                        } else {
                            main_content_cm_state_tv.setText("当前最近视频订单");
                        }
                    } else {
                        AudioHelper.showFrame(1, main_content_cm_fl);
                    }
                }
            }
        };
        AudioHelper.initCallBack(earliestCallback, this, false);

        //最早的视频咨询
        HttpProtocol.earliestVideo(earliestCallback);

        reddotCallback = new RaspberryCallback<ObjectResponse<HashMap<String, String>>>() {
            @Override
            public void onSuccess(ObjectResponse<HashMap<String, String>> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    initRedot(response.getData());
                }
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }
        };
        AudioHelper.initCallBack(reddotCallback, this, false);

        //获取红点数据
        HttpProtocol.redDot(reddotCallback);

        canableCallback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    UserInfoBean userInfoBean = createUserInfoBean(earliestUserinfo);
                    AVChatActivity.start(MainActivity.this, earliestUserinfo.getUserName() + "_user", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL, ClientType.DOCTORMAIN, userInfoBean, orderCode);
                } else {
                    ToastUtils.showToast(response.getMsg());
                }
            }
        };
        AudioHelper.initCallBack(canableCallback, this, true);

        setTimesCallback = new RaspberryCallback<ListResponse<VideoConsultBean>>() {
            @Override
            public void onSuccess(ListResponse<VideoConsultBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    Log.i("tag00", "记录该用户未接视频一次。");
                    ToastUtils.showToast("记录该用户未接视频一次。");
                }
            }
        };
        AudioHelper.initCallBack(setTimesCallback, this, true);

        main_content_swiprefresh_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDoctorData();
                HttpProtocol.earliestVideo(earliestCallback);
                HttpProtocol.redDot(reddotCallback);
            }
        });
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        Log.i("tag00", "网易账号状态：" + StatusCode.typeOfValue(status.getValue()));
                        if (status.getValue() == StatusCode.KICKOUT.getValue() ||
                                status.getValue() == StatusCode.UNLOGIN.getValue() ||
                                status.getValue() == StatusCode.NET_BROKEN.getValue()) {
                        }
                    }
                }, true);
    }

    private UserInfoBean createUserInfoBean(EarliestVideoBean.UserInfoBean userInfo) {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setHeadPortrait(userInfo.getHeadPortrait());
        userInfoBean.setId(userInfo.getId());
        userInfoBean.setName(userInfo.getName());
        userInfoBean.setNickName(userInfo.getNickName());
        userInfoBean.setSex(userInfo.getSex());
        return userInfoBean;
    }

    private void initRedot(HashMap<String, String> data) {
        initOneRedot(Integer.parseInt(data.get("sitDiagnoseReserveDotCount")), main_content_video_redot);
        initOneRedot(Integer.parseInt(data.get("groupSitDiagnoseDetailDotCount")), main_content_group_redot);
        initOneRedot(Integer.parseInt(data.get("inquiryReserveDotCount")), main_content_picture_redot);
        initOneRedot(Integer.parseInt(data.get("unReadMessageDotCount")), main_content_system_redot);
    }

    private void initOneRedot(int number, ImageView redDot) {
        if (number > 0) {
            redDot.setVisibility(View.VISIBLE);
        } else {
            redDot.setVisibility(View.GONE);
        }
    }

    public void stopSwipRefesh() {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                if (main_content_swiprefresh_srl.isRefreshing()) {
                    main_content_swiprefresh_srl.setRefreshing(false);
                }
            }
        });

    }

    private void loadDoctorData() {
        HttpProtocol.detail(callback);
    }


    /**
     * 初始化所有数据
     */
    private void initData() {
        if (getIntent().getSerializableExtra("doctorbean") != null) {
            doctorBean = (DoctorBean) getIntent().getSerializableExtra("doctorbean");
            //加载医生头像
            AudioHelper.setCircleImage(this, main_content_head_iv, doctorBean.getHeadPortrait(), R.drawable.main_message_headportrait);
            main_content_top_title_tv.setText(doctorBean.getName());
            if (Preference.AUTHORITY.equals(doctorBean.getPlatformLevel())) {
                main_content_authority_iv.setImageResource(R.drawable.main_home_authority);
            } else if (Preference.CERTIFICATION.equals(doctorBean.getPlatformLevel())) {
                main_content_authority_iv.setImageResource(R.drawable.main_home_certification);
            } else {
                main_content_authority_iv.setImageResource(R.drawable.main_home_unauthorized);
            }
        } else {
            loadDoctorData();
        }

    }


    /**
     * 所有的控件点击事件
     */
    @OnClick({R.id.main_content_qrcodesc_iv, R.id.main_content_system_msg_iv, R.id.main_content_qrcodemsg_iv,
            R.id.main_content_head_iv, R.id.main_content_videozx_tv, R.id.main_content_videohz_tv,
            R.id.main_content_picturezx_tv, R.id.main_content_history_tv, R.id.main_content_cm_refresh,
            R.id.main_content_mr_time_ll, R.id.main_content_mr_mymb_tv,
            R.id.main_content_mr_mypt_tv, R.id.main_content_mr_mywallet_tv, R.id.main_content_ot_setting_rl,
            R.id.main_content_ot_aboutus_rl, R.id.main_content_ot_clientct_rl, R.id.main_content_video_card_rl,
            R.id.main_content_mr_fee_tv,R.id.main_content_ot_clientct_rl_phone})
    public void onMianOnclick(View view) {
        switch (view.getId()) {
            //打开生成医生二维码
            case R.id.main_content_qrcodemsg_iv:
                QRNormalPopup normalPopup = new QRNormalPopup(MainActivity.this);
                QRCodeBean codeBean = new QRCodeBean();
                codeBean.setId(doctorBean.getId() + "");
                codeBean.setType("doctor");
                codeBean.setUserName(SharedPreferencesUtils.getString(Preference.USERNAME));
                Log.i("tag00", "生成二维码：" + JsonUtils.toJson(codeBean));
                Log.i("tag00", "生成二维码加密数据：" + DESUtils.encodeUrl(JsonUtils.toJson(codeBean)));
                //Log.i("tag00", "生成二维码加密数据：" + AesEncoder.encryptWithBase64(JsonUtils.toJson(codeBean)));
                //normalPopup.getImageView().setImageBitmap(QRCodeUtil.CreateTwoDCode(AesEncoder.encryptWithBase64(JsonUtils.toJson(codeBean))));
                normalPopup.getImageView().setImageBitmap(QRCodeUtil.CreateTwoDCode(DESUtils.encodeUrl(JsonUtils.toJson(codeBean))));
                normalPopup.showPopupWindow();
                break;
            //打开二维码扫描器
            case R.id.main_content_qrcodesc_iv:
                if (isCertificationed()) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                                            .READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission
                                            .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {

                                @Override
                                public void onGranted() {
                                    startActivity(new Intent(MainActivity.this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_ADDPATIENT));
                                }

                                @Override
                                public void onDenied(List<String> permissions) {
                                    ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                                            .toast_permission_hint));
                                }
                            });
                    } else {
                        startActivity(new Intent(this, QRscanDoctorActivity.class).putExtra(Preference.QRSCAN_INTENT_TYPE, Preference.QRSCAN_INTENT_ADDPATIENT));
                    }
                }
                break;
            //打开系统消息
            case R.id.main_content_system_msg_iv:
                startActivity(new Intent(this, MessageActivity.class));
                break;
            //视频咨询
            case R.id.main_content_videozx_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, VideoConsultActivity.class));
                }
                break;
            //视频会诊
            case R.id.main_content_videohz_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, VideoGroupActivity.class));
                }
                break;
            //图文咨询
            case R.id.main_content_picturezx_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, PictrueConsultActivity.class));
                }
                break;
            //历史订单
            case R.id.main_content_history_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, HistoryOrderActivity.class));
                }
                break;
            //设置界面
            case R.id.main_content_ot_setting_rl:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            //关于我们
            case R.id.main_content_ot_aboutus_rl:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            //客服中心
            case R.id.main_content_ot_clientct_rl:
                startActivity(new Intent(this, CustomerServiceActivity.class));
                break;
            //坐诊时间
            case R.id.main_content_mr_time_ll:
                if (isCertificationed() && isAllowSettingTime()) {
                    startActivity(new Intent(this, VisitTimeActivity.class).putExtra("doctorName", doctorBean.getName()));
                }
                break;
            //我的钱包
            case R.id.main_content_mr_mywallet_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, MyWalletActivity.class));
                }
                break;
            //我的模板
            case R.id.main_content_mr_mymb_tv:
                startActivity(new Intent(this, MyTemplateActivity.class));
                break;
            //我的患者
            case R.id.main_content_mr_mypt_tv:
                if (isCertificationed()) {
                    startActivity(new Intent(this, MyPatientsActivity.class));
                }
                break;
            //个人资料，认证
            case R.id.main_content_head_iv:
                startActivity(new Intent(this, PersonalMessageActivity.class).putExtra("doctorBean", doctorBean));
                break;
            //未完成的视频咨询点击进入
            case R.id.main_content_video_card_rl:
//                if (orderType.equals("多人会诊子项")){
//                    main_content_cm_state_tv.setText("当前最新多人视频会诊");
//                    CustomAlertDialog.dialogExSureCancel("是否开始多人会诊?", MainActivity.this, new CustomAlertDialog.OnDialogClickListener() {
//                        @Override
//                        public void doSomeThings() {
//                            UserInfoBean userInfoBean = createUserInfoBean(earliestUserinfo);
//                            queryEnterrRoom(orderId, HttpBase.IMGURL + earliestUserinfo.getHeadPortrait(), userInfoBean);
//                        }
//                    });
//                }else {
//                    main_content_cm_state_tv.setText("当前最新视频咨询");
//                    CustomAlertDialog.dialogExSureCancel("是否开始视频咨询?", MainActivity.this, new CustomAlertDialog.OnDialogClickListener() {
//                        @Override
//                        public void doSomeThings() {
//                            HttpProtocol.isCallableUser(canableCallback,earliestUserinfo.getId(),orderId);
//                        }
//                    });
//                }
                break;
            //点击刷新未完成的视频咨询
            case R.id.main_content_cm_refresh:
                Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_main_refresh);
                operatingAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //刷新开始
                        ToastUtils.showToast("刷新中...");
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //刷新结束
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //重复刷新
                    }
                });
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("刷新完成。");
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                main_content_cm_refresh.clearAnimation();
                            }
                        });
                    }
                }, 3000);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                main_content_cm_refresh.startAnimation(operatingAnim);
                break;
            case R.id.main_content_mr_fee_tv:
                if (isCertificationed()) {
                    Intent intent1 = new Intent(this, SettingFeeActivity.class);
                    intent1.putExtra(Preference.VIEW_VIDEO_FEE, doctorBean.getViewHospitalFee());
                    intent1.putExtra(Preference.PICTURE_FEE, doctorBean.getAskHospitalFee());
                    intent1.putExtra(Preference.AUTHENTATIC, doctorBean.getPlatformLevel());
                    intent1.putExtra(Preference.DOCTORTAG, doctorBean.getTag().name());
                    startActivity(intent1);
                }
                break;
            case R.id.main_content_ot_clientct_rl_phone:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:"+"4008859120"));
                startActivity(intent);
                break;
        }
    }

    private boolean isCertificationed() {
        if (DataCache.getInstance().getUserInfo() != null) {
            if (Preference.CERTIFICATION.equals(DataCache.getInstance().getUserInfo().getPlatformLevel()) || Preference.AUTHORITY.equals(DataCache.getInstance().getUserInfo().getPlatformLevel())) {
                return true;
            }
        }else if (doctorBean != null){
            if (Preference.CERTIFICATION.equals(doctorBean.getPlatformLevel()) || Preference.AUTHORITY.equals(doctorBean.getPlatformLevel())) {
                return true;
            }
        }
        CustomAlertDialog.dialogWithSure(this, "您还未通过医生认证，请等待认证结果。", null);
        return false;
    }
    private boolean isAllowSettingTime(){
        if (null != MyApplication.property && null != MyApplication.doctorBean){
            if (MyApplication.doctorBean.getTag().equals(DoctorTag.FREE)){
                if (!MyApplication.property.isOrderFreeDoctor()){
                    ToastUtils.showToast("您暂无该权限");
                }
                return MyApplication.property.isOrderFreeDoctor();
            }else if (MyApplication.doctorBean.getTag().equals(DoctorTag.PROMOTION)){
                if (!MyApplication.property.isOrderPromotionDoctor()){
                    ToastUtils.showToast("您暂无该权限");
                }
                return MyApplication.property.isOrderPromotionDoctor();
            }
        }else if (null == MyApplication.property){
            LoginManager.queryProperty(this);
        }
        return true;
    }

    private void queryEnterrRoom(final int orderId, final String s, final UserInfoBean earliestUserinfo) {
        RaspberryCallback<ObjectResponse<GroupSitDiagnoseRoomInfo>> callback = new RaspberryCallback<ObjectResponse<GroupSitDiagnoseRoomInfo>>() {
            @Override
            public void onSuccess(final ObjectResponse<GroupSitDiagnoseRoomInfo> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    Log.i("tag00", "orderCode:" + response.getData().getOrderCode() + " RoomId:" + response.getData().getRoomId() + " RoomName:" + response.getData().getRoomName() + " orderId:" + orderId);
                    if (!StringUtil.isEmpty(response.getData().getOrderCode())) {
                        SharedPreferencesUtils.put(response.getData().getOrderCode(), !StringUtil.isEmpty(response.getData().getRoomId()) ? response.getData().getRoomId() : "");
                    }
                    //进入视频会诊
                    if (DataCache.getInstance().isLoginNim()) {
                        AVChatRoomActivity.start(MainActivity.this, response.getData().getRoomName(), ClientType.DOCTORMAIN, s, earliestUserinfo, response.getData().getOrderCode());
                    } else {
                        LoginManager.loginNim(MainActivity.this, new LoginManager.LognimCallback() {
                            @Override
                            public void logNimCallback() {
                                AVChatRoomActivity.start(MainActivity.this, response.getData().getRoomName(), ClientType.DOCTORMAIN, s, earliestUserinfo, response.getData().getOrderCode());
                            }
                        });
                    }
                    return;
                }
                ToastUtils.showToast("暂时不能进入视频会诊，请稍后再试");
            }
        };

        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.queryEnterrRoom(orderId, callback,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            loadDoctorData();
            HttpProtocol.earliestVideo(earliestCallback);
            HttpProtocol.redDot(reddotCallback);
        }
        //当Android版本大于等于23时，动态授权
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                            .READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission
                            .READ_EXTERNAL_STORAGE).build(), new AcpListener() {

                @Override
                public void onGranted() {

                }

                @Override
                public void onDenied(List<String> permissions) {
                    ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                            .toast_permission_hint));
                    //不给权限就不给你用
                    ActivityManager.getInstance().finishAllActivity();
                    System.exit(0);
                }
            });
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Preference.VIDEO_CONSULT_MAIN);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 连续两次点击返回键，回到桌面
     */
    @Override
    public void onBackPressed() {
        // 判断时间间隔
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            ToastUtils.showToast(getResources().getString(R.string.toast_main_back_hint));
        } else {
            //返回桌面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            ActivityManager.getInstance().finishAllActivity();
            startActivity(intent);
            System.exit(0);
        }
    }

    /**
     * 查询是否有新版本
     */
    private void queryUpdateVersion() {
        RaspberryCallback<ObjectResponse<DoctorClient>> callback = new
                RaspberryCallback<ObjectResponse<DoctorClient>>() {
                    @Override
                    public void onFailure(Request request, Response response, Exception e) {
                    }

                    @Override
                    public void onSuccess(ObjectResponse<DoctorClient> response, int code) {
                        if (HttpProtocol.checkStatus(response,code)) {
                            if (null != response.getData()) {
                                Log.i("tag00","获得的医生端版本："+response.getData().getAndroidDoctorClientVersion().getCode());
                                UpdateManager.updateDialog(MainActivity.this, response.getData().getAndroidDoctorClientVersion());
                            }
                        }
                    }
                };

        callback.setMainThread(false);

        HttpProtocol.queryVersion(callback);
    }

}
