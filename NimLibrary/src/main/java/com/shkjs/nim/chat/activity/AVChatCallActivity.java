package com.shkjs.nim.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.R;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.AVChatProfile;
import com.shkjs.nim.chat.AVChatSoundPlayer;

import java.io.File;

/**
 * Created by xiaohu on 2016/9/14.
 */
public class AVChatCallActivity extends AppCompatActivity implements AVChatStateObserver, View.OnClickListener {

    // constant
    private static final String TAG = "AVChatActivity";
    private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    private static final int SHOW_CALL_VIEW = 1;
    private static final int SHOW_ANSWER_VIEW = 2;
    private static final int SHOW_SURFACE_VIEW = 3;

    /**
     * 来自广播
     */
    public static final int FROM_BROADCASTRECEIVER = 0;
    /**
     * 来自发起方
     */
    public static final int FROM_INTERNAL = 1;
    /**
     * 来自通知栏
     */
    public static final int FROM_NOTIFICATION = 2;
    /**
     * 未知的入口
     */
    public static final int FROM_UNKNOWN = -1;

    // data
    private AVChatData avChatData; // config for connect video server
    private String receiverId; // 对方的account
    private Context context;

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnpause = false; // 是否暂停音视频

    private AVChatVideoRender smallRender;
    private AVChatVideoRender largeRender;
    private String smallAccount;
    private String largeAccount;

    private View callView;
    private View answerView;
    private View surfaceView;
    private RelativeLayout largeRL;
    private RelativeLayout littleRL;
    private ImageView surfaceUserIconIV;
    private ImageView callUserIconIV;
    private ImageView answerUserIconIV;
    private Button surfaceQuitBtn;
    private Button switchCameraBtn;
    private Button switchSpeakerBtn;
    private Button closeSpeakBtn;
    private Button callRefuseBtn;
    private Button answerRefuseBtn;
    private Button answerPickupBtn;
    private TextView callStatusTV;
    private TextView answerStatusTV;

    private AVChatOptionalConfig config;
    private AVChatNotifyOption option;
    private AVChatCallback<AVChatData> callback;

    public static void start(Context context, String account, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_SOURCE, source);//来电或去电
        context.startActivity(intent);
    }

    /**
     * incoming call
     *
     * @param context
     */
    public static void launch(Context context, AVChatData config, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CALL_CONFIG, config);
        intent.putExtra(KEY_IN_CALLING, true);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (needFinish || !checkSource()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_avchat_main);
        context = this;
        mIsInComingCall = getIntent().getBooleanExtra(KEY_IN_CALLING, false);

        findView();
        initData();
        initListener();
        initListener(true);

        if (mIsInComingCall) {
            inComingCalling();
        } else {
            outgoingCalling(receiverId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hasOnpause = true;
        pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasOnpause) {
            resumeVideo();
            hasOnpause = false;
        }
    }

    @Override
    public void finish() {
        isUserFinish = true;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVChatProfile.getInstance().setAVChatting(false);
        needFinish = true;
    }

    private void findView() {
        callView = findViewById(R.id.activity_avchat_call);
        answerView = findViewById(R.id.activity_avchat_answer);
        surfaceView = findViewById(R.id.activity_avchat_surface);

        largeRL = (RelativeLayout) findViewById(R.id.avchat_large_surface_rl);
        littleRL = (RelativeLayout) findViewById(R.id.avchat_large_surface_rl);

        surfaceUserIconIV = (ImageView) findViewById(R.id.avchat_user_icon_iv);
        callUserIconIV = (ImageView) findViewById(R.id.avchat_call_user_icon_iv);
        answerUserIconIV = (ImageView) findViewById(R.id.avchat_answer_user_icon_iv);

        surfaceQuitBtn = (Button) findViewById(R.id.quit_avchat_room_btn);
        switchCameraBtn = (Button) findViewById(R.id.switch_cameras_avchat_room_btn);
        switchSpeakerBtn = (Button) findViewById(R.id.switch_speaker_avchat_room_btn);
        closeSpeakBtn = (Button) findViewById(R.id.close_speak_avchat_room_btn);
        callRefuseBtn = (Button) findViewById(R.id.avchat_call_refuse_btn);
        answerRefuseBtn = (Button) findViewById(R.id.avchat_answer_refuse_btn);
        answerPickupBtn = (Button) findViewById(R.id.avchat_answer_pickup_btn);

        callStatusTV = (TextView) findViewById(R.id.avchat_call_status_tv);
        answerStatusTV = (TextView) findViewById(R.id.avchat_answer_status_tv);

        smallRender = new AVChatVideoRender(this);
        largeRender = new AVChatVideoRender(this);

    }

    private void initData() {

        AVChatProfile.getInstance().setAVChatting(true);
        isCallEstablished = false;

        config = new AVChatOptionalConfig();
        /**
         * 非观众模式，观众模式无法绘出视频图像
         */
        config.enableAudienceRole(false);
        config.enableServerRecordAudio(true);
        config.enableServerRecordVideo(true);

        option = new AVChatNotifyOption();

        callback = new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                Logger.e(avChatData.getAccount() + "\n" + avChatData.getExtra() + "\n" +
                        avChatData.getChatId());
                AVChatCallActivity.this.avChatData = avChatData;
                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailed(int i) {
                Logger.e(getString(R.string.faile) + i);
                DialogMaker.dismissProgressDialog();

                AVChatSoundPlayer.instance(context).stop();

                if (i == ResponseCode.RES_FORBIDDEN) {
                    ToastUtils.showToast(getString(R.string.avchat_no_permission));
                } else {
                    ToastUtils.showToast(getString(R.string.avchat_call_failed));
                }
                finish();
            }

            @Override
            public void onException(Throwable throwable) {
                Logger.e(getString(R.string.faile) + throwable.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.avchat_call_failed));
                finish();
            }
        };
    }

    private void initListener() {
        surfaceQuitBtn.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);
        switchSpeakerBtn.setOnClickListener(this);
        closeSpeakBtn.setOnClickListener(this);
        callRefuseBtn.setOnClickListener(this);
        answerPickupBtn.setOnClickListener(this);
        answerRefuseBtn.setOnClickListener(this);
    }

    private void initListener(boolean register) {
        ackCall(register);

        hangUpCall(register);

        timeOutCall(register);

        autoHangUpCall(register);
    }

    private void autoHangUpCall(boolean register) {
        /** 参数为自动挂断的原因：
         * 1 作为被叫方：网络通话有来电还未接通，此时有本地来电，那么拒绝网络来电
         * 2 作为主叫方：正在发起网络通话时有本地来电，那么挂断网络呼叫
         * 3 双方正在进行网络通话，当有本地来电，用户接听时，挂断网络通话
         * 4 如果发起网络通话，无论是否建立连接，用户又拨打本地电话，那么网络通话挂断
         */
        Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
            @Override
            public void onEvent(Integer integer) {
                // 结束通话
                AVChatSoundPlayer.instance(context).stop();
                finish();
            }
        };
        //监听网络通话发起，接听或正在进行时有本地来电的通知
        AVChatManager.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    private void timeOutCall(boolean register) {
        Observer<AVChatTimeOutEvent> timeoutObserver = new Observer<AVChatTimeOutEvent>() {
            @Override
            public void onEvent(AVChatTimeOutEvent event) {
                // 超时类型
                AVChatSoundPlayer.instance(context).stop();
                finish();
            }
        };
        //监听呼叫或接听超时通知
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
    }

    private void hangUpCall(boolean register) {
        Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
            @Override
            public void onEvent(AVChatCommonEvent hangUpInfo) {
                // 结束通话
                AVChatSoundPlayer.instance(context).stop();
                finish();
            }
        };
        //监听对方挂断（主叫方、被叫方）
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
    }

    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     *
     * @param register true or false
     */
    private void ackCall(boolean register) {
        Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
            @Override
            public void onEvent(AVChatCalleeAckEvent ackInfo) {

                AVChatSoundPlayer.instance(context).stop();

                if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                    // 对方正在忙
                    AVChatSoundPlayer.instance(context).play(AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY);
                    finish();
                } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                    // 对方拒绝接听
                    ToastUtils.showToast("对方拒绝接听");
                    finish();
                } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                    // 对方同意接听
                    if (ackInfo.isDeviceReady()) {
                        // 设备初始化成功，开始通话
                    } else {
                        // 设备初始化失败，无法进行通话
                        ToastUtils.showToast(getString(R.string.avchat_device_no_ready));
                        finish();
                    }
                }
            }
        };
        //监听被叫方回应（主叫方）
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
    }

    /**
     * 拨打视频电话
     */
    private void call() {
        AVChatManager.getInstance().call(receiverId, AVChatType.VIDEO, config, option, callback);
    }

    /**
     * 同意接听视频电话
     */
    private void access() {
        AVChatManager.getInstance().accept(config, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        AVChatSoundPlayer.instance(context).stop();
    }

    /**
     * 拒绝视频电话
     */
    private void hangUp() {
        AVChatManager.getInstance().hangUp(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        AVChatSoundPlayer.instance(context).stop();
        finish();
    }

    /**
     * 切换摄像头（主要用于前置和后置摄像头切换）
     */
    private void switchCamera() {
        AVChatManager.getInstance().switchCamera();
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
        AVChatManager.getInstance().muteRemoteAudio(receiverId, !AVChatManager.getInstance().isRemoteAudioMuted
                (receiverId));
        AVChatManager.getInstance().muteRemoteAudio(AppCache.getAccount(), !AVChatManager.getInstance()
                .isRemoteAudioMuted(AppCache.getAccount()));
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

    /**
     * 判断来电还是去电
     *
     * @return
     */
    private boolean checkSource() {
        switch (getIntent().getIntExtra(KEY_SOURCE, FROM_UNKNOWN)) {
            case FROM_BROADCASTRECEIVER: // incoming call
                parseIncomingIntent();
                return true;
            case FROM_INTERNAL: // outgoing call
                parseOutgoingIntent();
                return true;
            default:
                return false;
        }
    }

    /**
     * 来电参数解析
     */
    private void parseIncomingIntent() {
        avChatData = (AVChatData) getIntent().getSerializableExtra(KEY_CALL_CONFIG);
    }

    /**
     * 去电参数解析
     */
    private void parseOutgoingIntent() {
        receiverId = getIntent().getStringExtra(KEY_ACCOUNT);
    }

    /**
     * 接听
     */
    private void inComingCalling() {

        showView(SHOW_ANSWER_VIEW);

        receiverId = avChatData.getAccount();

        AVChatSoundPlayer.instance(context).play(AVChatSoundPlayer.RingerTypeEnum.RING);
    }

    /**
     * 拨打
     */
    private void outgoingCalling(String account) {
        if (!NetworkUtil.isNetAvailable(AVChatCallActivity.this)) { // 网络不可用
            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showView(SHOW_CALL_VIEW);

        DialogMaker.showProgressDialog(context, null);

        AVChatSoundPlayer.instance(context).play(AVChatSoundPlayer.RingerTypeEnum.CONNECTING);

        this.receiverId = account;

        call();
    }

    /**
     * 添加视频大图像
     *
     * @param surfaceView
     */
    private void addLargeSurfaceView(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null)
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
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
        //        int size = DisplayUtils.dip2px(AVChatCallActivity.this, 60);
        //        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        //        surfaceView.setPadding(10, 10, 10, 10);
        littleRL.addView(surfaceView);
        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);//顶层
    }

    private void showView(int show) {
        switch (show) {
            case SHOW_CALL_VIEW:
                answerView.setVisibility(View.GONE);
                callView.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.GONE);
                break;
            case SHOW_ANSWER_VIEW:
                answerView.setVisibility(View.VISIBLE);
                callView.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                break;
            case SHOW_SURFACE_VIEW:
                answerView.setVisibility(View.GONE);
                callView.setVisibility(View.GONE);
                surfaceView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /****************************** 连接建立处理 ********************/

    /**
     * 处理连接服务器的返回值
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        Logger.i(TAG, "result code->" + auth_result);
        if (auth_result == 200) {
            Logger.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // 连接超时
            finish();
        } else if (auth_result == 401) { // 验证失败
            finish();
        } else if (auth_result == 417) { // 无效的channelId
            finish();
        } else { // 连接服务器错误，直接退出
            finish();
        }
    }

    /**
     * ************************ AVChatStateObserver ****************************
     */

    @Override
    public void onTakeSnapshotResult(String account, boolean success, String file) {

    }

    @Override
    public void onConnectionTypeChanged(int netType) {

    }


    @Override
    public void onLocalRecordEnd(String[] files, int event) {
        if (files != null && files.length > 0) {
            String file = files[0];
            String parent = new File(file).getParent();
            String msg;
            if (event == 0) {
                msg = "录制已结束";
            } else {
                msg = "你的手机内存不足, 录制已结束";
            }

            if (!TextUtils.isEmpty(parent)) {
                msg += ", 录制文件已保存至：" + parent;
            }

            ToastUtils.showToast(msg);
        } else {
            if (event == 1) {
                ToastUtils.showToast("你的手机内存不足, 录制已结束.");
            } else {
                ToastUtils.showToast("录制已结束.");
            }
        }

    }

    @Override
    public void onFirstVideoFrameAvailable(String account) {

    }

    @Override
    public void onVideoFpsReported(String account, int fps) {

    }

    @Override
    public void onJoinedChannel(int code, String audioFile, String videoFile) {
        handleWithConnectServerResult(code);
    }

    @Override
    public void onLeaveChannel() {

    }

    @Override
    public void onUserJoined(String account) {
        Logger.d(TAG, "onUserJoin");
        AVChatManager.getInstance().setupVideoRender(account, largeRender, false, AVChatVideoScalingType
                .SCALE_ASPECT_BALANCED);
        addLargeSurfaceView(largeRender);
    }

    @Override
    public void onUserLeave(String account, int event) {

    }

    @Override
    public void onProtocolIncompatible(int status) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkQuality(String user, int value) {

    }

    @Override
    public void onCallEstablished() {
        Logger.d(TAG, "onCallEstablished");
        showView(SHOW_SURFACE_VIEW);
        AVChatManager.getInstance().setupVideoRender(AppCache.getAccount(), smallRender, false,
                AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addLittleSurfaceView(smallRender);
    }

    @Override
    public void onDeviceEvent(int code, String desc) {

    }

    @Override
    public void onFirstVideoFrameRendered(String user) {

    }

    @Override
    public void onVideoFrameResolutionChanged(String user, int width, int height, int rotate) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.quit_avchat_room_btn) {
            hangUp();
        } else if (i == R.id.switch_cameras_avchat_room_btn) {
            switchCamera();
        } else if (i == R.id.switch_speaker_avchat_room_btn) {
            setSpeaker();
        } else if (i == R.id.close_speak_avchat_room_btn) {
            switchCamera();
        } else if (i == R.id.avchat_call_refuse_btn) {
            hangUp();
        } else if (i == R.id.avchat_answer_refuse_btn) {
            hangUp();
        } else if (i == R.id.avchat_answer_pickup_btn) {
            access();
        }
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
    }
}
