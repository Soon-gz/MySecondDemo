package com.shkjs.nim.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.orhanobut.logger.Logger;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.R;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.Extras;
import com.shkjs.nim.em.ClientType;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AVChatRoomActivity extends AppCompatActivity implements AVChatStateObserver, View.OnClickListener {

    private AVChatOptionalConfig config;
    private AVChatCallback<AVChatChannelInfo> callback;
    private AVChatCallback<AVChatData> callback2;
    private AVChatCallback<Void> callback3;
    //render
    private List<String> accountList;

    private Map<String, AVChatVideoRender> renderMap;
    //    private AVChatVideoRender smallRender;
    private AVChatVideoRender largeRender;
    private boolean hasOnpause = false; // 是否暂停音视频

    private String largeAccount = AppCache.getAccount(); // 大图显示的用户

    //患者端还是医生端
    private String roomName;
    private ClientType type;
    private String iconUrl;
    private ArrayList<Long> doctorIds;

    private RelativeLayout largeRL;
    private LinearLayout littleLL;

    private LinearLayout controlLL;
    private LinearLayout switchCameraLL;
    private LinearLayout switchSpeakLL;
    private ImageView quitAvchatRoomIV;
    private ImageView iconIV;
    private CheckBox switchCameraCB;
    private TextView switchCameraTV;
    private CheckBox switchSpeakCB;
    private TextView switchSpeakTV;
    //医生端聊天按钮，以及一些医生端需要的数据
    private ImageButton message_button;
    private RelativeLayout input_message_rl;
    private TextView buttonSendMessage_group;
    private EditText editTextMessage_group;
    private FrameLayout avchat_fl;
    private RecyclerView avchat_room_message;
    private String doctorsRoomIdkey = "";
    private BaseRecyclerAdapter<IMMessage> baseRecyclerAdapter;
    private List<IMMessage> dataList;
    private boolean isCanFinish = false;

    private boolean isFrontCamera = true;//默认前置摄像头
    private boolean isMute = false;//默认不静音
    private boolean isShowControl = true;//默认显示控制界面
    private UserInfoBean userInfoBean;

    public static void start(Context context, String roomName, ClientType type, String iconUrl) {
        Intent intent = new Intent(context, AVChatRoomActivity.class);
        intent.putExtra(Extras.EXTRA_ROOMNAME, roomName);
        intent.putExtra(Extras.EXTRA_CLENTTYPE, type);
        intent.putExtra(Extras.EXTRA_ICONURL, iconUrl);
        context.startActivity(intent);
    }

    public static void start(Context context, String roomName, ClientType type, String iconUrl, UserInfoBean
            userInfoBean, String doctorsRoomIdkey) {
        Intent intent = new Intent(context, AVChatRoomActivity.class);
        intent.putExtra(Extras.EXTRA_ROOMNAME, roomName);
        intent.putExtra(Extras.EXTRA_DOCTORS_ROOM_ID, doctorsRoomIdkey);
        intent.putExtra(Extras.EXTRA_DATA, userInfoBean);
        intent.putExtra(Extras.EXTRA_CLENTTYPE, type);
        intent.putExtra(Extras.EXTRA_ICONURL, iconUrl);
        context.startActivity(intent);
    }

    public static void start(Context context, String roomName, ClientType type, String iconUrl, ArrayList<Long>
            doctorIds) {
        Intent intent = new Intent(context, AVChatRoomActivity.class);
        intent.putExtra(Extras.EXTRA_ROOMNAME, roomName);
        intent.putExtra(Extras.EXTRA_CLENTTYPE, type);
        intent.putExtra(Extras.EXTRA_ICONURL, iconUrl);
        intent.putExtra(Extras.EXTRA_DOCTORIDS, doctorIds);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avchat_room);
        //隐藏状态栏
        //        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
        //                .FLAG_FULLSCREEN);
        roomName = getIntent().getStringExtra(Extras.EXTRA_ROOMNAME);
        type = (ClientType) getIntent().getSerializableExtra(Extras.EXTRA_CLENTTYPE);
        iconUrl = getIntent().getStringExtra(Extras.EXTRA_ICONURL);
        doctorIds = (ArrayList<Long>) getIntent().getSerializableExtra(Extras.EXTRA_DOCTORIDS);
        if (getIntent().getSerializableExtra(Extras.EXTRA_DATA) != null) {
            userInfoBean = (UserInfoBean) getIntent().getSerializableExtra(Extras.EXTRA_DATA);
        }
        if (getIntent().getStringExtra(Extras.EXTRA_DOCTORS_ROOM_ID) != null) {
            doctorsRoomIdkey = getIntent().getStringExtra(Extras.EXTRA_DOCTORS_ROOM_ID);
        }

        findView();
        initData();
        initListener();

        showCameraView(true);
        showSpeakView(false);

        //2S后关闭控制界面
        controlLL.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeControlLL();
                showMessageButton();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasOnpause) {
            resumeVideo();
            hasOnpause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hasOnpause = true;
        pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVChatManager.getInstance().observeAVChatState(this, false);
        //结束医生详情界面
        Intent intent = new Intent("com.shkjs.patient.finish_doctor");
        sendBroadcast(intent);
    }

    private void findView() {
        largeRL = (RelativeLayout) findViewById(R.id.avchat_large_view_rl);
        littleLL = (LinearLayout) findViewById(R.id.avchat_little_view_ll);
        controlLL = (LinearLayout) findViewById(R.id.control_ll);
        //医生端聊天
        message_button = (ImageButton) findViewById(R.id.message_button);
        input_message_rl = (RelativeLayout) findViewById(R.id.input_message_rl);
        buttonSendMessage_group = (TextView) findViewById(R.id.buttonSendMessage_group);
        editTextMessage_group = (EditText) findViewById(R.id.editTextMessage_group);
        avchat_fl = (FrameLayout) findViewById(R.id.avchat_fl);
        avchat_room_message = (RecyclerView) findViewById(R.id.avchat_room_message);

        switchCameraLL = (LinearLayout) findViewById(R.id.switch_cameras_ll);
        switchSpeakLL = (LinearLayout) findViewById(R.id.switch_speak_ll);
        quitAvchatRoomIV = (ImageView) findViewById(R.id.quit_avchat_room_iv);
        iconIV = (ImageView) findViewById(R.id.icon_iv);
        switchCameraCB = (CheckBox) findViewById(R.id.switch_cameras_cb);
        switchCameraTV = (TextView) findViewById(R.id.switch_cameras_tv);
        switchSpeakCB = (CheckBox) findViewById(R.id.switch_speak_cb);
        switchSpeakTV = (TextView) findViewById(R.id.switch_speak_tv);

        largeRender = new AVChatVideoRender(this);
    }

    private void initData() {

        accountList = new ArrayList<>();
        renderMap = new HashMap<>();

        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    for (IMMessage imMessage : messages) {
                        if (imMessage.getSessionType().equals(SessionTypeEnum.Team)) {
                            dataList.add(imMessage);
                        }
                    }
                    baseRecyclerAdapter.notifyDataSetChanged();
                    avchat_room_message.scrollToPosition(baseRecyclerAdapter.getItemCount() - 1);
                }
            };
            NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
        }

        config = new AVChatOptionalConfig();
        /**
         * 非观众模式，观众模式无法绘出视频图像
         */
        config.enableAudienceRole(false);

        callback = new AVChatCallback<AVChatChannelInfo>() {
            @Override
            public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                Logger.d(avChatChannelInfo.getTimetagMs());
                joinRoom();
            }

            @Override
            public void onFailed(int i) {
                Logger.e(getString(R.string.faile) + i);
                if (i == 417) {
                    joinRoom();
                } else {
                    ToastUtils.showToast(getString(R.string.create_team_failed));
                    finish();
                }
            }

            @Override
            public void onException(Throwable throwable) {
                Logger.e(getString(R.string.faile) + throwable.getLocalizedMessage());
            }
        };

        callback2 = new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                Logger.e(avChatData.getAccount() + "\n" + avChatData.getExtra() + "\n" +
                        avChatData.getChatId());
            }

            @Override
            public void onFailed(int i) {
                Logger.e(getString(R.string.faile) + i);
                if (i == 404) {
                    createRoom();
                } else {
                    finish();
                }
            }

            @Override
            public void onException(Throwable throwable) {
                Logger.e(getString(R.string.faile) + throwable.getLocalizedMessage());
            }
        };

        callback3 = new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.d(getString(R.string.success));
                finish();
            }

            @Override
            public void onFailed(int i) {
                Logger.e(getString(R.string.faile) + " " + i);
                finish();
            }

            @Override
            public void onException(Throwable throwable) {
                Logger.d(getString(R.string.faile) + " " + throwable.getLocalizedMessage());
                finish();
            }
        };

        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            Glide.with(this).load(iconUrl).transform(new CircleTransform(this)).placeholder(R.drawable
                    .main_headportrait_large).error(R.drawable.main_headportrait_large).into(iconIV);
        } else {
            Glide.with(this).load(R.drawable.main_videochat_groupconsultation).transform(new CircleTransform(this))
                    .placeholder(R.drawable.main_videochat_groupconsultation).error(R.drawable
                    .main_videochat_groupconsultation).into(iconIV);
        }
    }

    private void initListener() {

        switchCameraLL.setOnClickListener(this);
        switchSpeakLL.setOnClickListener(this);
        quitAvchatRoomIV.setOnClickListener(this);
        iconIV.setOnClickListener(this);
        message_button.setOnClickListener(this);
        buttonSendMessage_group.setOnClickListener(this);
        avchat_fl.setOnClickListener(this);
        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            dataList = new ArrayList<>();
            baseRecyclerAdapter = new BaseRecyclerAdapter<IMMessage>(this, dataList) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.avchat_room_doctors_msg;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, IMMessage item) {
                    holder.getTextView(R.id.avchat_room_msg_name).setText(UserInfoHelper.getUserTitleName(item
                            .getFromAccount(), SessionTypeEnum.P2P) + "：");
                    holder.getTextView(R.id.avchat_room_msg_content).setText(item.getContent());
                }
            };
            avchat_room_message.setHasFixedSize(true);
            avchat_room_message.setLayoutManager(new LinearLayoutManager(this));
            avchat_room_message.setItemAnimator(new DefaultItemAnimator());
            avchat_room_message.setAdapter(baseRecyclerAdapter);
        }

        /**
         * 非观众模式，观众模式无法绘出视频图像
         */
        AVChatManager.getInstance().enableAudienceRole(false);
        AVChatManager.getInstance().observeAVChatState(this, true);

        //先创建房间，重复创建则加入
        createRoom();
        //        joinRoom();//先加入，未创建房间则创建
    }

    private void createRoom() {
        AVChatManager.getInstance().createRoom(roomName, null, callback);
    }

    private void joinRoom() {
        AVChatManager.getInstance().joinRoom(roomName, AVChatType.VIDEO, config, callback2);
    }

    private void leaveRoom() {
        Log.i("tag00", "离开房间");
        if (isCanFinish) {
            Log.i("tag00", "能完成");
            Intent mIntent1 = new Intent("VIDEO_CONSULT_GROUP");
            //发送广播,结束视频咨询
            sendBroadcast(mIntent1);
        }
        AVChatManager.getInstance().leaveRoom(callback3);
    }

    /**
     * 切换摄像头（主要用于前置和后置摄像头切换）
     */
    private void switchCamera() {
        AVChatManager.getInstance().switchCamera();
        isFrontCamera = !isFrontCamera;
        showCameraView(isFrontCamera);
    }

    /**
     * 设置扬声器是否开启
     */
    private void setSpeaker() {
        AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled());
    }

    /**
     * 静音
     */
    private void switchSpeak() {

        isMute = !isMute;
        //不播放其他用户语音
        for (String account : accountList) {
            //            if (!account.equals(AppCache.getAccount())) {
            AVChatManager.getInstance().muteRemoteAudio(account, isMute);
            //            }
        }
        //不播放本地语音
        AVChatManager.getInstance().muteLocalAudio(isMute);
        showSpeakView(isMute);
    }

    /**
     * 恢复视频和语音发送
     */
    public void resumeVideo() {
        if (hasOnpause) {
            AVChatManager.getInstance().muteLocalVideo(false);
            AVChatManager.getInstance().muteLocalAudio(false);
        }
    }

    /**
     * 关闭视频和语音发送
     */
    public void pauseVideo() {

        if (!AVChatManager.getInstance().isLocalVideoMuted()) {
            AVChatManager.getInstance().muteLocalVideo(true);
        }

        if (!AVChatManager.getInstance().isLocalAudioMuted()) {
            AVChatManager.getInstance().muteLocalAudio(true);
        }
    }

    private void showLargeSurfaceView() {
        if (!accountList.contains(AppCache.getAccount())) {
            accountList.add(0, AppCache.getAccount());
        }
        AVChatManager.getInstance().setupVideoRender(AppCache.getAccount(), largeRender, false,
                AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addLargeSurfaceView(largeRender);
        renderMap.put(AppCache.getAccount(), largeRender);
    }

    /**
     * 添加视频大图像
     *
     * @param surfaceView
     */
    private void addLargeSurfaceView(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        largeRL.addView(surfaceView);
        surfaceView.setZOrderOnTop(false);
        surfaceView.setZOrderMediaOverlay(false);//底层
    }

    /**
     * 添加视频小图像
     */
    private void addLittleSurfaceView(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        int size = DisplayUtils.dip2px(AVChatRoomActivity.this, 76);
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        surfaceView.setPadding(10, 10, 10, 10);
        littleLL.addView(surfaceView);
        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);//顶层
        //添加一个视频图像界面，则重新设置点击事件
        switchRender();
    }

    /**
     * 用户退出后，移除view
     *
     * @param surfaceView
     */
    private void removeSurfaceView(SurfaceView surfaceView) {
        if ((null != surfaceView) && (null != surfaceView.getParent())) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
    }

    /**
     * 点击小图像，切换视频
     */
    private void switchRender() {
        View view;
        for (int i = 0; i < littleLL.getChildCount(); i++) {
            view = littleLL.getChildAt(i);
            view.setOnClickListener(null);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchRender(finalI + 1, accountList.get(finalI + 1), largeAccount);
                }
            });
        }
    }

    /**
     * 大小图像显示切换
     *
     * @param user1 用户1的account
     * @param user2 用户2的account
     */
    private void switchRender(int position, String user1, final String user2) {

        accountList.remove(position);
        accountList.add(position, user2);
        largeAccount = user1;

        //先取消用户的画布
        AVChatManager.getInstance().setupVideoRender(user1, null, false, 0);
        AVChatManager.getInstance().setupVideoRender(user2, null, false, 0);

        //交换画布
        AVChatVideoRender render1 = renderMap.get(user2);
        AVChatVideoRender render2 = renderMap.get(user1);

        //重新设置上画布
        AVChatManager.getInstance().setupVideoRender(user1, render1, false, AVChatVideoScalingType
                .SCALE_ASPECT_BALANCED);
        AVChatManager.getInstance().setupVideoRender(user2, render2, false, AVChatVideoScalingType
                .SCALE_ASPECT_BALANCED);

        renderMap.put(user1, render1);
        renderMap.put(user2, render2);
        //切换之后，重新设置点击事件
        switchRender();
    }

    /**
     * 切换摄像头界面显示
     *
     * @param isFrontCamera 是否为前置摄像头
     */
    private void showCameraView(boolean isFrontCamera) {
        if (!isFrontCamera) {
            switchCameraCB.setChecked(true);
            switchCameraTV.setTextColor(ContextCompat.getColor(this, R.color.blue_2bbbe6));
        } else {
            switchCameraCB.setChecked(false);
            switchCameraTV.setTextColor(Color.WHITE);
        }
    }

    /**
     * 是否静音界面显示
     *
     * @param isMute 是否静音
     */
    private void showSpeakView(boolean isMute) {
        if (!isMute) {
            switchSpeakCB.setChecked(false);
            switchSpeakTV.setTextColor(Color.WHITE);
        } else {
            switchSpeakCB.setChecked(true);
            switchSpeakTV.setTextColor(ContextCompat.getColor(this, R.color.blue_2bbbe6));
        }
    }

    /**
     * 打开视频控制界面
     */
    private void openControlLL() {
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation
                .RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);

        controlLL.setAnimation(mShowAction);
        controlLL.setVisibility(View.VISIBLE);
        isShowControl = true;
        if (message_button.getVisibility() == View.VISIBLE) {
            dissMessageButton();
        }
        if (input_message_rl.getVisibility() == View.VISIBLE) {
            dissInputMessageRl();
        }

    }

    /**
     * 显示聊天按钮
     */
    public void showMessageButton() {
        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation
                    .RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
            message_button.setAnimation(mShowAction);
            message_button.setVisibility(View.VISIBLE);
            if (input_message_rl.getVisibility() == View.VISIBLE) {
                dissInputMessageRl();
            }
        }
    }

    /**
     * 显示聊天输入
     */
    public void showInputMessageRl() {
        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            input_message_rl.setVisibility(View.VISIBLE);
            editTextMessage_group.setFocusable(true);
            editTextMessage_group.setText("");
            editTextMessage_group.requestFocus();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) editTextMessage_group.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 500);
        }
    }

    /**
     * 隐藏聊天按钮
     */
    public void dissMessageButton() {
        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation
                    .RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            mHiddenAction.setDuration(300);
            if (input_message_rl.getVisibility() == View.VISIBLE) {
                dissInputMessageRl();
            }
            message_button.setAnimation(mHiddenAction);
            message_button.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏聊天输入
     */
    public void dissInputMessageRl() {
        if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
            input_message_rl.setVisibility(View.GONE);
            InputMethodManager inputManager = (InputMethodManager) editTextMessage_group.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editTextMessage_group.getWindowToken(), 0);
        }
    }

    /**
     * 关闭视频控制界面
     */
    private void closeControlLL() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation
                .RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(500);

        controlLL.setAnimation(mHiddenAction);
        controlLL.setVisibility(View.GONE);
        isShowControl = false;
    }

    /**
     * 点击头像事件处理
     */
    private void sendBroadcast() {
        if (type == ClientType.PATIENT) {
            Intent intent = new Intent("com.shkjs.patient.doctors_detail");
            intent.putExtra("doctorIds", doctorIds);
            sendBroadcast(intent);
        } else {
            Intent mIntent = new Intent("com.shkjs.doctor.activity.VideoConsultHReport");
            mIntent.putExtra("userInfo", userInfoBean);
            mIntent.putExtra("orderCode", doctorsRoomIdkey);
            //发送广播
            sendBroadcast(mIntent);
        }
    }

    /* ************************************视频回调*********************************************** */

    @Override
    public void onTakeSnapshotResult(String s, boolean b, String s1) {
        Logger.d("onTakeSnapshotResult:" + s + " " + b + " " + s1);
    }

    @Override
    public void onConnectionTypeChanged(int i) {
        Logger.d("onConnectionTypeChanged:" + i);
    }

    @Override
    public void onLocalRecordEnd(String[] strings, int i) {
        Logger.d("onLocalRecordEnd:" + i);
    }

    @Override
    public void onFirstVideoFrameAvailable(String s) {
        Logger.d("onFirstVideoFrameAvailable:" + s);
    }

    @Override
    public void onVideoFpsReported(String s, int i) {
        Logger.d("onVideoFpsReported:" + s);
    }

    @Override
    public void onJoinedChannel(int i, String s, String s1) {
        Logger.d("onJoinedChannel:" + i + " " + s + " " + s1);
        //        showLargeSurfaceView();
    }

    @Override
    public void onLeaveChannel() {
        Logger.d("onLeaveChannel:");
        accountList.remove(0);
        removeSurfaceView(renderMap.get(AppCache.getAccount()));
        finish();
    }

    @Override
    public void onUserJoined(String s) {
        Log.i("tag00", "新加入的用户：" + s);
        if (!accountList.contains(s)) {
            accountList.add(s);
            if (type.equals(ClientType.DOCTOR) || type.equals(ClientType.DOCTORMAIN)) {
                String accunt = userInfoBean.getUserName() + "_user";
                Log.i("tag00", "accunt:" + accunt);
                if (accunt.equals(s)) {
                    isCanFinish = true;
                }
            }
        }
        AVChatVideoRender smallRender = new AVChatVideoRender(AVChatRoomActivity.this);
        AVChatManager.getInstance().setupVideoRender(s, smallRender, false, AVChatVideoScalingType
                .SCALE_ASPECT_BALANCED);
        removeSurfaceView(renderMap.get(s));
        addLittleSurfaceView(smallRender);
        renderMap.put(s, smallRender);
    }

    @Override
    public void onUserLeave(String s, int i) {
        accountList.remove(s);
        removeSurfaceView(renderMap.get(s));
        Logger.d("onUserLeave:" + s + " " + i);
    }

    @Override
    public void onProtocolIncompatible(int i) {
        Logger.d("onProtocolIncompatible:" + i);
    }

    @Override
    public void onDisconnectServer() {
        Logger.d("onDisconnectServer:");
    }

    @Override
    public void onNetworkQuality(String s, int i) {
        Logger.d("onNetworkQuality:" + s + " " + i);
    }

    @Override
    public void onCallEstablished() {
        Logger.d("onCallEstablished:");
        showLargeSurfaceView();
    }

    @Override
    public void onDeviceEvent(int i, String s) {

    }

    @Override
    public void onFirstVideoFrameRendered(String s) {

    }

    @Override
    public void onVideoFrameResolutionChanged(String s, int i, int i1, int i2) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.quit_avchat_room_iv) {
            leaveRoom();
        }
        if (i == R.id.switch_cameras_ll) {
            switchCamera();
        }
        if (i == R.id.switch_speak_ll) {
            switchSpeak();
        }
        if (i == R.id.icon_iv) {
            sendBroadcast();
        }
        if (i == R.id.message_button) {
            dissMessageButton();
            showInputMessageRl();
        }
        if (i == R.id.buttonSendMessage_group) {
            showMessageButton();
            dissInputMessageRl();
            if (!StringUtil.isEmpty(editTextMessage_group.getText().toString())) {
                Log.i("tag00", "会诊 doctorsRoomIdkey:" + doctorsRoomIdkey);
                String doctorsRoomId = SharedPreferencesUtils.getString(doctorsRoomIdkey);
                Log.i("tag00", "会诊 doctorsRoomId:" + doctorsRoomId);
                if (!StringUtil.isEmpty(doctorsRoomId)) {
                    // 创建文本消息
                    IMMessage message = MessageBuilder.createTextMessage(doctorsRoomId, // 聊天对象的
                            // ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                            SessionTypeEnum.Team, // 聊天类型，单聊或群组
                            editTextMessage_group.getText().toString() // 文本内容
                    );
                    NIMClient.getService(MsgService.class).sendMessage(message, false);
                    dataList.add(message);
                    baseRecyclerAdapter.notifyDataSetChanged();
                    avchat_room_message.scrollToPosition(baseRecyclerAdapter.getItemCount() - 1);
                } else {
                    ToastUtils.showToast("创建该会诊的医师还未进入房间，请稍等~~");
                }

            } else {
                ToastUtils.showToast("不能发送空消息");
            }

        }

        if (i == R.id.avchat_fl) {
            if (isShowControl) {
                closeControlLL();
                showMessageButton();
            } else {
                openControlLL();
            }
        }

    }


    @Override
    public void onBackPressed() {
        leaveRoom();
    }
}
