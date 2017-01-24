package com.shkjs.patient.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.nineoldandroids.view.ViewHelper;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.qrcode.QRCodeUtil;
import com.raspberry.library.util.DESUtils;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.NoScrollViewPager;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.UpdateManager;
import com.shkjs.patient.adapter.MyViewPagerAdapter;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Message;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.bean.OrderInfo;
import com.shkjs.patient.bean.PatientClient;
import com.shkjs.patient.bean.UnreadMessage;
import com.shkjs.patient.bean.push.GroupDiagnosePayPushDto;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.PushMessageTypeEm;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.fragment.ConsultMessageFragment1;
import com.shkjs.patient.fragment.HealthReportsFragment;
import com.shkjs.patient.fragment.SearchDoctorFragment2;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.ActivityManager;
import com.shkjs.patient.view.QRNormalPopup;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeLinearLayout;
import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;

public class MainActivity extends BaseActivity implements View.OnClickListener, BaseFragment.BackHandledInterface {

    @Bind(R.id.activity_main)
    DrawerLayout drawerLayout;
    @Bind(R.id.main_content)
    LinearLayout contentLL;
    @Bind(R.id.main_content_ll)
    LinearLayout linearLayout;
    @Bind(R.id.main_content_rl)
    RelativeLayout relativeLayout;
    @Bind(R.id.main_left)
    LinearLayout leftLL;
    @Bind(R.id.main_left_user_info)
    LinearLayout userInfoLL;                                //个人基本信息
    @Bind(R.id.main_left_system_message_ll)
    LinearLayout systemMessageLL;                           //系统消息
    @Bind(R.id.main_left_mine_doctor_ll)
    LinearLayout mineDoctorLL;                              //我的医生
    @Bind(R.id.main_left_mine_order_ll)
    LinearLayout mineOrderLL;                               //我的订单
    @Bind(R.id.main_left_mine_account_ll)
    LinearLayout mineAccountLL;                             //我的账户
    @Bind(R.id.main_left_call_center_ll)
    LinearLayout userHelpLL;                                //使用帮助
    @Bind(R.id.main_left_settings_ll)
    LinearLayout settingLL;                                 //设置
    @Bind(R.id.main_left_about_ll)
    LinearLayout aboutLL;                                   //关于
    @Bind(R.id.system_message_icon_ll)
    BGABadgeLinearLayout systemMessageUnReadLL;            //系统消息未读标志
    @Bind(R.id.main_content_user_icon_iv)
    ImageView contentIconIV;                                //左侧用户头像
    @Bind(R.id.main_left_icon_iv)
    ImageView leftIconIV;                                   //左侧用户头像
    @Bind(R.id.main_left_user_qr_code_iv)
    ImageView qrCodeIV;                                     //二维码
    @Bind(R.id.main_content_search_iv)
    ImageView searchDoctorIV;                               //打开搜索页
    @Bind(R.id.main_content_search_ll)
    LinearLayout searchLL;                               //打开搜索页
    @Bind(R.id.main_content_search_et)
    EditText searchET;                               //打开搜索页
    @Bind(R.id.main_content_health_iv)
    ImageView addHealthArchivesIV;                         //添加健康档案
    @Bind(R.id.main_left_user_vip_iv)
    ImageView userLevelIV;                                  //用户等级
    @Bind(R.id.main_content_top_title_tv)
    TextView contentTitleTV;                                //顶部title
    @Bind(R.id.main_left_user_name_tv)
    TextView userNickNameTV;                                //昵称
    @Bind(R.id.main_conmtent_vp)
    NoScrollViewPager viewPager;
    @Bind(R.id.main_content_consult_ll)
    LinearLayout consultLL;                                      //咨询消息
    @Bind(R.id.main_content_consult_unread_ll)
    BGABadgeLinearLayout consultUnReadLL;                        //咨询消息
    @Bind(R.id.main_content_consult_iv)
    ImageView consultIV;                                      //咨询消息
    @Bind(R.id.main_content_room_ll)
    LinearLayout searchDoctorLL;                                 //找医生
    @Bind(R.id.main_content_room_iv)
    ImageView searchDoctorIv;                                 //找医生
    @Bind(R.id.main_content_order_ll)
    LinearLayout archiveLL;                                      //健康档案
    @Bind(R.id.main_content_order_unread_ll)
    BGABadgeLinearLayout reportUnReadLL;                        //健康档案
    @Bind(R.id.main_content_order_iv)
    ImageView archiveIV;                                      //健康档案
    @Bind(R.id.main_left_user_info_ll)
    LinearLayout userLL;                                      //个人资料
    @Bind(R.id.main_left_help_ll)
    LinearLayout helpLL;                                      //使用帮助

    private MyViewPagerAdapter adapter;
    private List<Fragment> fragments;

    private final int CONSULT = 0;                               //问诊消息
    private final int ROOM = 1;                                  //诊疗室
    private final int ORDER = 2;                                 //预约消息

    private long currentBackPressedTime = 0;                   // 点击返回键时间
    private static final int BACK_PRESSED_INTERVAL = 1000;    // 两次点击返回键时间间隔
    private static final int UPDATE_VIEW = 121;
    private BaseFragment fragment;

    private PopupWindow popupWindow;

    private UnreadMessage message;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    if (null != message) {
                        if (message.getGroupSitDiagnoseDotCount() + message.getSitDiagnoseReserveDotCount() + message
                                .getInquiryReserveDotCount() > 0) {
                            DataCache.getInstance().setImperfectOrder(true);
                        }
                        if (message.getUnReadMessageDotCount() > 0) {
                            DataCache.getInstance().setUnReadNum(message.getUnReadMessageDotCount());
                        }
                        if (message.getHealthReportCount() > 0) {
                            DataCache.getInstance().setNewReport(true);
                        }
                        message = null;
                    }
                    showView();
                    break;
                default:
                    break;
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
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (null != data) {
            intent.putExtras(data);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        ButterKnife.bind(this);

        //        initEvents();
        initData();
        initListener();

        onParseIntent();

        queryUpdateVersion();

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        queryUnreadMessaage();
        showView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }

    /**
     * 初始化侧滑栏事件监听与相关设置
     */
    private void initEvents() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);//背景透明，去除阴影
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //设置左侧侧滑栏滑动动画效果，暂时只有平移和透明度变化
                float scale = 1 - slideOffset;
                //                float rightScale = 0.8f + scale * 0.2f;
                //                float leftScale = 1 - 0.3f * scale;

                ViewHelper.setScaleX(leftLL, 1);
                ViewHelper.setScaleY(leftLL, 1);
                ViewHelper.setAlpha(leftLL, 0.6f + 0.4f * slideOffset);
                ViewHelper.setTranslationX(contentLL, leftLL.getMeasuredWidth() * slideOffset);
                ViewHelper.setPivotX(contentLL, 0);
                ViewHelper.setPivotY(contentLL, contentLL.getMeasuredHeight() / 2);
                contentLL.invalidate();
                ViewHelper.setScaleX(contentLL, 1);
                ViewHelper.setScaleY(contentLL, 1);
                ViewHelper.setAlpha(contentIconIV, scale);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        });
    }

    private void initData() {
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //            linearLayout.setPadding(linearLayout.getPaddingLeft(), statusBarHetght, linearLayout
        // .getPaddingRight(),
        //                    linearLayout.getPaddingBottom());
        //            ViewGroup.LayoutParams params = userInfoLL.getLayoutParams();
        //            params.height = DisplayUtils.dip2px(this, 120) + statusBarHetght;
        //            userInfoLL.setLayoutParams(params);
        //            userInfoLL.setPadding(userInfoLL.getPaddingLeft(), statusBarHetght, userInfoLL.getPaddingRight(),
        //                    userInfoLL.getPaddingBottom());
        //        }

        fragments = new ArrayList<>();

        ConsultMessageFragment1 consultMessageFragment = new ConsultMessageFragment1();
        SearchDoctorFragment2 searchDoctorFragment = new SearchDoctorFragment2();
        HealthReportsFragment healthReportsFragment = new HealthReportsFragment();

        fragments.add(consultMessageFragment);
        fragments.add(searchDoctorFragment);
        fragments.add(healthReportsFragment);

        adapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setNoScroll(true);//禁止左右滑动
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(1, false);
        setCurrentItem(1);
    }

    private void initListener() {
        userInfoLL.setOnClickListener(this);
        systemMessageLL.setOnClickListener(this);
        mineDoctorLL.setOnClickListener(this);
        mineOrderLL.setOnClickListener(this);
        mineAccountLL.setOnClickListener(this);
        userHelpLL.setOnClickListener(this);
        settingLL.setOnClickListener(this);
        aboutLL.setOnClickListener(this);
        contentIconIV.setOnClickListener(this);
        qrCodeIV.setOnClickListener(this);
        searchDoctorIV.setOnClickListener(this);
        addHealthArchivesIV.setOnClickListener(this);
        consultLL.setOnClickListener(this);
        searchDoctorLL.setOnClickListener(this);
        searchLL.setOnClickListener(this);
        archiveLL.setOnClickListener(this);
        userLL.setOnClickListener(this);
        helpLL.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //同步更新title和底部背景
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        systemMessageUnReadLL.setDragDismissDelegage(new BGADragDismissDelegate() {
            @Override
            public void onDismiss(BGABadgeable badgeable) {
                //未读标识拖动消失后，未读数置为0
                DataCache.getInstance().setUnReadNum(0);
            }
        });

        searchET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == searchET.getId() && hasFocus) {
                    startSearchDoctorActivity();
                }
            }
        });
    }

    private void showView() {
        if (null != DataCache.getInstance().getUserInfo()) {//显示用户信息
            userNickNameTV.setText(DataCache.getInstance().getUserInfo().getNickName());
            if (!TextUtils.isEmpty(DataCache.getInstance().getUserInfo().getHeadPortrait())) {
                Glide.with(MainActivity.this).load(HttpBase.BASE_OSS_URL + DataCache.getInstance().getUserInfo()
                        .getHeadPortrait()).placeholder(R.drawable.main_headportrait_xxlarge).error(R.drawable
                        .main_headportrait_xxlarge).transform(new CircleTransform(MainActivity.this)).into
                        (contentIconIV);
                Glide.with(MainActivity.this).load(HttpBase.BASE_OSS_URL + DataCache.getInstance().getUserInfo()
                        .getHeadPortrait()).placeholder(R.drawable.main_headportrait_xxlarge).error(R.drawable
                        .main_headportrait_xxlarge).transform(new CircleTransform(MainActivity.this)).into(leftIconIV);
            } else {
                Glide.with(MainActivity.this).load(R.drawable.main_headportrait_xxlarge).placeholder(R.drawable
                        .main_headportrait_xxlarge).error(R.drawable.main_headportrait_xxlarge).transform(new
                        CircleTransform(MainActivity.this)).into(contentIconIV);
                Glide.with(MainActivity.this).load(R.drawable.main_headportrait_xxlarge).placeholder(R.drawable
                        .main_headportrait_xxlarge).error(R.drawable.main_headportrait_xxlarge).transform(new
                        CircleTransform(MainActivity.this)).into(leftIconIV);
            }
            if (TextUtils.isEmpty(DataCache.getInstance().getUserInfo().getVip()) || DataCache.getInstance()
                    .getUserInfo().getVip().equals("0")) {//TODO 1为VIP，0为其他
                userLevelIV.setVisibility(View.GONE);
            } else {
                userLevelIV.setVisibility(View.VISIBLE);
                userLevelIV.setImageResource(R.drawable.main_personalcenter_vip);
            }
        } else {
            LoginManager.queryUserInfo(new LoginManager.UserInfoListener() {
                @Override
                public void onFinish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showView();
                        }
                    });
                }
            });
        }
        if (DataCache.getInstance().getUnReadNum() > 0) {//显示系统消息未读条数
            //            if (DataCache.getInstance().getUnReadNum() > 99) {
            //                systemMessageUnReadLL.showTextBadge("99+");
            //            } else {
            //                systemMessageUnReadLL.showTextBadge(DataCache.getInstance().getUnReadNum() + "");
            //            }
            systemMessageUnReadLL.showCirclePointBadge();
        } else {
            systemMessageUnReadLL.hiddenBadge();
        }

        setUnreadRed();//显示未读咨询消息

        if (DataCache.getInstance().isNewReport()) {//显示健康报告红点
            showReportUnreadRed();
        } else {
            hideReportUnreadRed();
        }

    }

    /**
     * 根据当前显示页面，设置相应的title和底部按钮背景
     *
     * @param position
     */
    private void setCurrentItem(int position) {
        switch (position) {
            case CONSULT:
                contentTitleTV.setVisibility(View.VISIBLE);
                searchLL.setVisibility(View.GONE);
                contentTitleTV.setText(getResources().getString(R.string.title_consult_text));
                consultIV.setImageResource(R.drawable.tabbar_consultation_sel);
                searchDoctorIv.setImageResource(R.drawable.tabbar_seekdoctor_nor);
                archiveIV.setImageResource(R.drawable.tabbar_healthreport_nor);
                searchDoctorIV.setVisibility(View.GONE);
                addHealthArchivesIV.setVisibility(View.GONE);
                break;
            case ROOM:
                contentTitleTV.setVisibility(View.GONE);
                searchLL.setVisibility(View.VISIBLE);
                contentTitleTV.setText(getResources().getString(R.string.title_search_doctor_text));
                consultIV.setImageResource(R.drawable.tabbar_consultation_nor);
                searchDoctorIv.setImageResource(R.drawable.tabbar_seekdoctor_sel);
                archiveIV.setImageResource(R.drawable.tabbar_healthreport_nor);
                searchDoctorIV.setVisibility(View.VISIBLE);
                addHealthArchivesIV.setVisibility(View.GONE);
                break;
            case ORDER:
                contentTitleTV.setVisibility(View.VISIBLE);
                searchLL.setVisibility(View.GONE);
                contentTitleTV.setText(getResources().getString(R.string.title_health_report_text));
                consultIV.setImageResource(R.drawable.tabbar_consultation_nor);
                searchDoctorIv.setImageResource(R.drawable.tabbar_seekdoctor_nor);
                archiveIV.setImageResource(R.drawable.tabbar_healthreport_sel);
                searchDoctorIV.setVisibility(View.GONE);
                addHealthArchivesIV.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_content_user_icon_iv://点击左侧用户头像，打开侧滑栏
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.main_content_consult_ll://切换为动态消息界面
                viewPager.setCurrentItem(CONSULT);
                break;
            case R.id.main_content_room_ll://切换为诊疗室界面
                viewPager.setCurrentItem(ROOM);
                break;
            case R.id.main_content_order_ll://切换为我的预约界面
                viewPager.setCurrentItem(ORDER);
                makeAllReportReaded();
                break;
            case R.id.main_content_search_ll://打开搜索页面
                searchET.setFocusable(true);
                searchET.setFocusableInTouchMode(true);
                searchET.requestFocus();
                break;
            case R.id.main_content_search_iv://打开二维码扫描页面
                //                startSearchDoctorActivity();
                startScanActivity();
                break;
            case R.id.main_content_health_iv://添加健康报告界面
                createHealthPop();
                break;
            case R.id.main_left_system_message_ll://系统消息
                startSystemMessageActivity();
                break;
            case R.id.main_left_user_info://个人信息
            case R.id.main_left_user_info_ll://个人信息
                startUserInfoActivity();
                break;
            case R.id.main_left_mine_doctor_ll://我的医生
                startMineDoctorActivity();
                break;
            case R.id.main_left_mine_order_ll://我的订单
                startMineOrderActivity();
                break;
            case R.id.main_left_mine_account_ll://我的账户
                startMineAccountActivity();
                break;
            case R.id.main_left_call_center_ll://客服中心
                callPhone();
                break;
            case R.id.main_left_help_ll://使用帮助
                startCallCenterActivity();
                break;
            case R.id.main_left_settings_ll://设置
                startSettingsActivity();
                break;
            case R.id.main_left_about_ll://关于
                startAboutActivity();
                break;
            case R.id.main_left_user_qr_code_iv://生成用户二维码
                createUserQRCode();
                break;
            default:
                break;
        }

    }

    /**
     * 连续两次点击返回键，回到桌面
     */
    @Override
    public void onBackPressed() {
        //如果左侧个人中心被打开，则关闭
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (null != fragment && fragment.onBackPressed()) {
            return;
        }
        if (null != popupWindow && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        // 判断时间间隔
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            ToastUtils.showToast(getResources().getString(R.string.toast_main_back_hint));
        } else {
            //返回桌面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }


    private void startUserInfoActivity() {
        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        startActivity(intent);
    }

    private void startSystemMessageActivity() {
        Intent intent = new Intent(MainActivity.this, SystemMessageActivity.class);
        startActivity(intent);
    }

    private void startMineDoctorActivity() {
        Intent intent = new Intent(MainActivity.this, MineDoctorActivity.class);
        startActivity(intent);
    }

    private void startMineOrderActivity() {
        Intent intent = new Intent(MainActivity.this, MineOrderActivity.class);
        startActivity(intent);
    }

    private void startMineAccountActivity() {
        Intent intent = new Intent(MainActivity.this, MineAccountActivity.class);
        startActivity(intent);
    }

    private void startCallCenterActivity() {
        Intent intent = new Intent(MainActivity.this, CallCenterActivity.class);
        startActivity(intent);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void startSearchDoctorActivity() {
        Intent intent = new Intent(MainActivity.this, SearchDoctorActivity.class);
        startActivity(intent);
        searchET.clearFocus();
    }

    private void startScanActivity() {
        Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
        intent.putExtra("hint", "将医生的二维码放入框内,即可自动扫描");
        //        startActivityForResult(intent, QRCODE);
        startActivity(intent);
    }

    private void callPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + "400-8859-120");
            intent.setData(data);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showToast("您的设备没有拨号功能");
        }
    }

    /**
     * 展示用户二维码
     */
    private void createUserQRCode() {
        if (null == DataCache.getInstance().getUserInfo()) {
            ToastUtils.showToast(getString(R.string.data_error));
            return;
        }
        QRNormalPopup normalPopup = new QRNormalPopup(MainActivity.this);
        String str = "{\"" + Preference.ID + "\":" + DataCache.getInstance().getUserInfo().getId() + "," +
                "\"" + Preference.TYPE + "\":\"user\"," +
                "\"" + MyApplication.USER_NAME + "\":" + SharedPreferencesUtils.getString(MyApplication.USER_NAME) +
                "}";
        str = DESUtils.encodeUrl(str);
        normalPopup.getImageView().setImageBitmap(QRCodeUtil.CreateTwoDCode(str));
        normalPopup.showPopupWindow();
    }


    /**
     * 用户上传健康报告，包括体检报告、病历报告两类
     */
    private void createHealthPop() {
        View view = getLayoutInflater().inflate(R.layout.popup_health, null);
        if (null == popupWindow) {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setContentView(view);
            popupWindow.setClippingEnabled(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable());//不设置，点击外部popup不消失
            popupWindow.setOutsideTouchable(true);

            //        BubbleRelativeLayout bubbleView = (BubbleRelativeLayout) view.findViewById(R.id.bubbleView);
            //        int bubbleOffset = DisplayUtils.getScreenWidth(this) - (addHealthArchivesIV.getRight() +
            // addHealthArchivesIV
            //                .getLeft()) / 2;
            //        bubbleView.setPadding(0, DisplayUtils.dip2px(this, 10), 0, 0);
            //        bubbleView.setPathEffect(DisplayUtils.dip2px(this, 8));
            //        bubbleView.setBubbleParams(BubbleRelativeLayout.BubbleLegOrientation.TOP, bubbleOffset);
            //        bubbleView.postInvalidate();

            //体检报告
            view.findViewById(R.id.health_physical).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadHealthReportActivity.class);
                    intent.putExtra(UploadHealthReportActivity.class.getSimpleName(), UploadHealthReportActivity
                            .PHYSICAL);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
            //病历报告
            view.findViewById(R.id.health_history).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UploadHealthReportActivity.class);
                    intent.putExtra(UploadHealthReportActivity.class.getSimpleName(), UploadHealthReportActivity
                            .HISTORY);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
            view.findViewById(R.id.popup_photo_click_to_dismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.TOP | Gravity.START, 0, DisplayUtils
                    .getStatusBarHeight2(MainActivity.this) + relativeLayout.getMeasuredHeight());
        }
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.fragment = selectedFragment;
    }

    /**
     * 显示咨询消息未读消息红点
     */
    private void showConsultUnreadRed() {
        consultUnReadLL.showCirclePointBadge();
    }

    /**
     * 隐藏咨询消息未读消息红点
     */
    private void hideConsultUnreadRed() {
        consultUnReadLL.hiddenBadge();
    }

    /**
     * 显示健康报告未读消息红点
     */
    private void showReportUnreadRed() {
        reportUnReadLL.showCirclePointBadge();
    }

    /**
     * 隐藏健康报告未读消息红点
     */
    private void hideReportUnreadRed() {
        reportUnReadLL.hiddenBadge();
    }

    /**
     * 设置未读消息红点
     */
    public void setUnreadRed() {
        if (DataCache.getInstance().isImperfectOrder()) {
            showConsultUnreadRed();
        } else {
            hideConsultUnreadRed();
        }
    }

    /**
     * 处理Intent
     */
    private void onParseIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(Preference.RECODE)) {//微信扫一扫进入
                String recode = intent.getStringExtra(Preference.RECODE);
                if (!TextUtils.isEmpty(recode)) {
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    resultIntent.putExtra(ResultActivity.class.getSimpleName(), recode);
                    startActivity(resultIntent);
                }
            } else if (intent.hasExtra(Preference.EXTRA) && intent.hasExtra(Preference.DATA)) {//通知栏进入
                PushMessageTypeEm em = (PushMessageTypeEm) intent.getSerializableExtra(Preference.EXTRA);
                Message message = (Message) intent.getSerializableExtra(Preference.DATA);
                if (null != em && null != message) {
                    onParseIntentData(em, message);
                }
            }
        }
    }

    /**
     * 处理Intent 数据，根据不同数据跳转不同页面
     *
     * @param em      推送消息类型
     * @param message 推送消息
     */
    private void onParseIntentData(PushMessageTypeEm em, Message message) {
        //        Intent intent = new Intent();
        //        switch (em) {
        //            case RECHARGE:
        //            case PAY:
        //            case FAMILY_PAY:
        //            case FAMILY:
        //                intent.setClass(MainActivity.this, MessageDetailActivity.class);
        //                intent.putExtra(MessageDetailActivity.class.getSimpleName(), message);
        //                break;
        //            case ORDER_CANCEL:
        //                OrderCancelPushDto orderCancelPushDto = (OrderCancelPushDto) message;
        //                intent.setClass(MainActivity.this, RefundOrderDetailActivity.class);
        //                intent.putExtra(RefundOrderDetailActivity.class.getSimpleName(), orderCancelPushDto
        // .getOrderId());
        //                break;
        //            case ORDER_OVERTIME:
        //                OrderExpirePushDto orderExpirePushDto = (OrderExpirePushDto) message;
        //                intent.setClass(MainActivity.this, RefundOrderDetailActivity.class);
        //                intent.putExtra(RefundOrderDetailActivity.class.getSimpleName(), orderExpirePushDto
        // .getOrderId());
        //                break;
        //            case DIAGNOSE:
        //                break;
        //            case GROUP_DIAGNOSE:
        //                multVideoPay((GroupDiagnosePayPushDto) message);
        //                break;
        //            default:
        //                break;
        //        }
        //        if (null != intent.getExtras()) {
        //            startActivity(intent);
        //        }

        Intent intent = new Intent(this, SystemMessageActivity.class);
        startActivity(intent);
    }

    private void multVideoPay(GroupDiagnosePayPushDto groupDiagnosePayPushDto) {

        OrderInfo orderInfo = new OrderInfo();

        ArrayList<Doctor> doctors = new ArrayList<>();
        Doctor doctor;
        for (long id : groupDiagnosePayPushDto.getDoctorIds()) {
            doctor = new Doctor();
            doctor.setId(id);
            doctors.add(doctor);
        }
        orderInfo.setDoctors(doctors);

        Order order = new Order();
        order.setId(groupDiagnosePayPushDto.getOrderId());
        orderInfo.setOrder(order);

        String timeStr = TimeFormatUtils.getLocalTime("MM-dd", groupDiagnosePayPushDto.getDiagnoseTime()) + "" +
                " " +
                DateUtil.DateToWeek(new Date(groupDiagnosePayPushDto.getDiagnoseTime())) + " " +
                TimeFormatUtils.getLocalTime("HH:mm", groupDiagnosePayPushDto.getDiagnoseTime());
        orderInfo.setTime(timeStr);

        orderInfo.setOrderInfoType(OrderInfoType.MULTIVIDEO);

        PayActivity.start(MainActivity.this, orderInfo);
    }

    @Override
    protected void updateView(Intent data) {
        super.updateView(data);
        queryUnreadMessaage();
        showView();
    }

    private void queryUnreadMessaage() {
        RaspberryCallback<ObjectResponse<UnreadMessage>> callback = new
                RaspberryCallback<ObjectResponse<UnreadMessage>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<UnreadMessage> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        message = response.getData();
                        Logger.d(message);
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                }
            }
        };
        callback.setMainThread(false);

        HttpProtocol.queryUnreadMessage(callback);
    }

    /**
     * 标记健康报告为已读
     */
    private void makeAllReportReaded() {
        if (!DataCache.getInstance().isNewReport()) {
            return;
        }
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        DataCache.getInstance().setNewReport(false);
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                }
            }
        };
        callback.setMainThread(false);
        HttpProtocol.makeAllReportReaded(callback);
    }

    /**
     * 查询是否有新版本
     */
    private void queryUpdateVersion() {
        RaspberryCallback<ObjectResponse<PatientClient>> callback = new
                RaspberryCallback<ObjectResponse<PatientClient>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
            }

            @Override
            public void onSuccess(ObjectResponse<PatientClient> response, int code) {
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        PatientClient client = response.getData();
                        UpdateManager.updateDialog(MainActivity.this, client.getAndroidUserClientVersion());
                    }
                }
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryVersion(callback);
    }
}
