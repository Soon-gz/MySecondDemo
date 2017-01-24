package com.shkjs.nim.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;
import com.raspberry.library.activity.UserInfoBean;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.AVChatExitCode;
import com.shkjs.nim.chat.AVChatNotification;
import com.shkjs.nim.chat.AVChatProfile;
import com.shkjs.nim.chat.AVChatSoundPlayer;
import com.shkjs.nim.chat.AVChatUI;
import com.shkjs.nim.chat.CallStateEnum;

import java.io.File;

/**
 * 音视频界面
 * Created by hzxuwen on 2015/4/21.
 */
public class AVChatActivity extends UI implements AVChatUI.AVChatListener, AVChatStateObserver {
    // constant
    private static final String TAG = "AVChatActivity";
    private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_CALL_TYPE = "KEY_CALL_TYPE";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    private static final String KEY_CLENT_TYPE = "KEY_CLENT_TYPE";
    private static final String KEY_USERINFO = "KEY_USERINFO";
    private static final String KEY_ORDER_CODE = "KEY_ORDER_CODE";
    public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";
    //医生端数据
    private com.shkjs.nim.em.ClientType clientType;
    private UserInfoBean userInfoBean;
    private String orderCode;

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
    private AVChatUI avChatUI; // 音视频总管理器
    private AVChatData avChatData; // config for connect video server
    private int state; // calltype 音频或视频
    private String receiverId; // 对方的account

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnpause = false; // 是否暂停音视频

    // notification
    private AVChatNotification notifier;

    public static void start(Context context, String account, int callType, int source, com.shkjs.nim.em.ClientType
            clientType) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        intent.putExtra(KEY_CLENT_TYPE, clientType);
        context.startActivity(intent);
    }

    public static void start(Context context, String account, int callType, int source, com.shkjs.nim.em.ClientType
            clientType, UserInfoBean userInfoBean,String orderCode) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        intent.putExtra(KEY_USERINFO, userInfoBean);
        intent.putExtra(KEY_CLENT_TYPE, clientType);
        intent.putExtra(KEY_ORDER_CODE, orderCode);
        context.startActivity(intent);
    }

    public static void start(Context context, String account, int callType, int source) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    /**
     * incoming call
     *
     * @param context
     */
    public static void launch(Context context, AVChatData config, int source, com.shkjs.nim.em.ClientType clientType) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_CALL_CONFIG, config);
        intent.putExtra(KEY_IN_CALLING, true);
        intent.putExtra(KEY_SOURCE, source);
        intent.putExtra(KEY_CLENT_TYPE, clientType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needFinish || !checkSource()) {
            finish();
            return;
        }
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        //        View root = LayoutInflater.from(this).inflate(R.layout.activity_avchat, null);
        setContentView(R.layout.activity_avchat);
        View root = findViewById(R.id.parentLayout);
        if (getIntent().getSerializableExtra(KEY_CLENT_TYPE) != null) {
            clientType = (com.shkjs.nim.em.ClientType) getIntent().getSerializableExtra(KEY_CLENT_TYPE);
        }
        if (getIntent().getSerializableExtra(KEY_USERINFO) != null) {
            userInfoBean = (UserInfoBean) getIntent().getSerializableExtra(KEY_USERINFO);
            orderCode = getIntent().getStringExtra(KEY_ORDER_CODE);
            Log.i("tag00", "AVChatActivity 用户信息：" + userInfoBean.getHeadPortrait()+" KEY_ORDER_CODE:"+orderCode);
        }
        mIsInComingCall = getIntent().getBooleanExtra(KEY_IN_CALLING, false);
        if (clientType != null) {
            avChatUI = new AVChatUI(this, root, this, clientType);
        } else {
            avChatUI = new AVChatUI(this, root, this);
        }
        if (!avChatUI.initiation()) {
            this.finish();
            return;
        }

        if (mIsInComingCall) {
            inComingCalling();
        } else {
            outgoingCalling();
        }
        registerNetCallObserver(true);

        notifier = new AVChatNotification(this);
        notifier.init(receiverId != null ? receiverId : avChatData.getAccount());
        isCallEstablished = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        avChatUI.pauseVideo(); // 暂停视频聊天（用于在视频聊天过程中，APP退到后台时必须调用）
        hasOnpause = true;
        activeCallingNotifier();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelCallingNotifier();
        if (hasOnpause) {
            avChatUI.resumeVideo();
            hasOnpause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVChatProfile.getInstance().setAVChatting(false);
        registerNetCallObserver(false);
        cancelCallingNotifier();
        needFinish = true;
    }

    @Override
    public void onBackPressed() {
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
                if (state == AVChatType.VIDEO.getValue() || state == AVChatType.AUDIO.getValue()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 来电参数解析
     */
    private void parseIncomingIntent() {
        avChatData = (AVChatData) getIntent().getSerializableExtra(KEY_CALL_CONFIG);
        state = avChatData.getChatType().getValue();
    }

    /**
     * 去电参数解析
     */
    private void parseOutgoingIntent() {
        receiverId = getIntent().getStringExtra(KEY_ACCOUNT);
        state = getIntent().getIntExtra(KEY_CALL_TYPE, -1);
    }

    /**
     * 注册监听
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        AVChatManager.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    private String getBroadCastStr(com.shkjs.nim.em.ClientType type){
        if (type.getType() == 2){
            Log.i("tag00","主页医生"+"VIDEO_CONSULT_MAIN");
            return "VIDEO_CONSULT_MAIN";
        }else {
            return "VIDEO_CONSULT";
        }
    }

    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     */
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {

            AVChatSoundPlayer.instance(AVChatActivity.this).stop();

            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                Log.i("tag00","CALLEE_ACK_BUSY");
                //设置未接听次数
                sendBroadCastAndSetTimes();
                AVChatSoundPlayer.instance(AVChatActivity.this).play(AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY);
                avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                Log.i("tag00","CALLEE_ACK_REJECT");
                //设置未接听次数
                sendBroadCastAndSetTimes();
                avChatUI.closeSessions(AVChatExitCode.REJECT);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                //显示底部控件
                avChatUI.setBottomRoot(true);
                //隐藏结束按钮
                avChatUI.setDoctorVideoFinish(false);
                //设置用户信息
                avChatUI.setUserInfobean(userInfoBean);
                //设置订单code
                avChatUI.setOrderCode(orderCode);
                if (ackInfo.isDeviceReady()) {
                    avChatUI.isCallEstablish.set(true);
                    avChatUI.canSwitchCamera = true;
                } else {
                    // 设备初始化失败
                    ToastUtils.showToast(getString(R.string.avchat_device_no_ready));
                    avChatUI.closeSessions(AVChatExitCode.OPEN_DEVICE_ERROR);
                }
            }
        }
    };

    Observer<AVChatTimeOutEvent> timeoutObserver = new Observer<AVChatTimeOutEvent>() {
        @Override
        public void onEvent(AVChatTimeOutEvent event) {
            if (event == AVChatTimeOutEvent.NET_BROKEN_TIMEOUT) {
                avChatUI.closeSessions(AVChatExitCode.NET_ERROR);
            } else {
                Log.i("tag00","来电超时，自己未接听 AVChatExitCode.PEER_NO_RESPONSE");
                //设置未接听次数
                sendBroadCastAndSetTimes();
                avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
            }

            // 来电超时，自己未接听
            if (event == AVChatTimeOutEvent.INCOMING_TIMEOUT) {
                Log.i("tag00","来电超时，自己未接听");
                //设置未接听次数
                sendBroadCastAndSetTimes();
                activeMissCallNotifier();
            }

            AVChatSoundPlayer.instance(AVChatActivity.this).stop();
        }
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {

            AVChatSoundPlayer.instance(AVChatActivity.this).stop();
            //设置未接听次数
            sendBroadCastAndSetTimes();

            avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
        }
    };

    //设置未接听次数
    public void sendBroadCastAndSetTimes(){
        if (clientType != null) {
            switch (clientType.getType()) {
                case 0:
                    Intent mIntent1 = new Intent(getBroadCastStr(clientType));
                    mIntent1.putExtra("set_call_times",userInfoBean.getId()+"");
                    //发送广播,结束视频咨询
                    sendBroadcast(mIntent1);
                    break;
                case 1:
                    //患者端对方挂断的操作
                    break;
                case 2:
                    Intent mIntent = new Intent(getBroadCastStr(clientType));
                    mIntent.putExtra("set_call_times",userInfoBean.getId()+"");
                    //发送广播,结束视频咨询
                    sendBroadcast(mIntent);
                    break;
            }
        }
    }

    /**
     * 注册/注销网络通话控制消息（音视频模式切换通知）
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            handleCallControl(netCallControlNotification);
        }
    };

    /**
     * 注册/注销网络通话对方挂断的通知
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {

            AVChatSoundPlayer.instance(AVChatActivity.this).stop();

            avChatUI.closeSessions(AVChatExitCode.HANGUP);
            if (clientType != null) {
                switch (clientType.getType()) {
                    case 0:
                        Intent mIntent = new Intent(getBroadCastStr(clientType));
                        //发送广播,结束视频咨询
                        sendBroadcast(mIntent);
                        break;
                    case 1:
                        //患者端对方挂断的操作
                        //结束医生详情界面
                        Intent intent = new Intent("com.shkjs.patient.finish_doctor");
                        sendBroadcast(intent);
                        break;
                    case 2:
                        Intent mIntent1 = new Intent(getBroadCastStr(clientType));
                        //发送广播,结束视频咨询
                        sendBroadcast(mIntent1);
                        break;
                }
            }
            cancelCallingNotifier();
            // 如果是incoming call主叫方挂断，那么通知栏有通知
            if (mIsInComingCall && !isCallEstablished) {
                activeMissCallNotifier();
            }
        }
    };

    /**
     * 注册/注销同时在线的其他端对主叫方的响应
     */
    Observer<AVChatOnlineAckEvent> onlineAckObserver = new Observer<AVChatOnlineAckEvent>() {
        @Override
        public void onEvent(AVChatOnlineAckEvent ackInfo) {

            AVChatSoundPlayer.instance(AVChatActivity.this).stop();

            if (ackInfo.getClientType() != ClientType.Android) {
                String client = null;
                switch (ackInfo.getClientType()) {
                    case ClientType.Web:
                        client = "Web";
                        break;
                    case ClientType.Windows:
                        client = "Windows";
                        break;
                    default:
                        break;
                }
                if (client != null) {
                    String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "接听！" :
                            "拒绝！";
                    ToastUtils.showToast("通话已在" + client + "端被" + option);
                }
                avChatUI.closeSessions(-1);
            }
        }
    };


    /**
     * 接听
     */
    private void inComingCalling() {
        avChatUI.inComingCalling(avChatData);
    }

    /**
     * 拨打
     */
    private void outgoingCalling() {
        if (!NetworkUtil.isNetAvailable(AVChatActivity.this)) { // 网络不可用
            ToastUtils.showToast(getString(R.string.network_is_not_available));
            finish();
            return;
        }
        switch (clientType){
            case DOCTOR:
                avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state),userInfoBean.getId());
                break;
            case DOCTORMAIN:
                avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state),userInfoBean.getId());
                break;
            case PATIENT:
                avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state));
                break;
        }
    }

    /**
     * *************************** AVChatListener *********************************
     */

    @Override
    public void uiExit() {
        finish();
    }


    /****************************** 连接建立处理 ********************/

    /**
     * 处理连接服务器的返回值
     *
     * @param auth_result
     */
    protected void handleWithConnectServerResult(int auth_result) {
        LogUtil.i(TAG, "result code->" + auth_result);
        if (auth_result == 200) {
            Log.d(TAG, "onConnectServer success");
        } else if (auth_result == 101) { // 连接超时
            avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
        } else if (auth_result == 401) { // 验证失败
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        } else if (auth_result == 417) { // 无效的channelId
            avChatUI.closeSessions(AVChatExitCode.INVALIDE_CHANNELID);
        } else { // 连接服务器错误，直接退出
            avChatUI.closeSessions(AVChatExitCode.CONFIG_ERROR);
        }
    }

    /**************************** 处理音视频切换 *********************************/

    /**
     * 处理音视频切换请求
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        switch (notification.getControlCommand()) {
            case SWITCH_AUDIO_TO_VIDEO:
                avChatUI.incomingAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_AGREE:
                onAudioToVideo();
                break;
            case SWITCH_AUDIO_TO_VIDEO_REJECT:
                avChatUI.onCallStateChange(CallStateEnum.AUDIO);
                ToastUtils.showToast(getString(R.string.avchat_switch_video_reject));
                break;
            case SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case NOTIFY_VIDEO_OFF:
                avChatUI.peerVideoOff();
                break;
            case NOTIFY_VIDEO_ON:
                avChatUI.peerVideoOn();
                break;
            case NOTIFY_RECORD_START:
                ToastUtils.showToast("对方开始了通话录制");
                break;
            case NOTIFY_RECORD_STOP:
                ToastUtils.showToast("对方结束了通话录制");
                break;
            default:
                break;
        }
    }

    /**
     * 音频切换为视频
     */
    private void onAudioToVideo() {
        avChatUI.onAudioToVideo();
        avChatUI.initAllSurfaceView(avChatUI.getVideoAccount());
    }

    /**
     * 视频切换为音频
     */
    private void onVideoToAudio() {
        avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        avChatUI.onVideoToAudio();
    }

    /**
     * 通知栏
     */
    private void activeCallingNotifier() {
        if (notifier != null && !isUserFinish) {
            notifier.activeCallingNotification(true);
        }
    }

    private void cancelCallingNotifier() {
        if (notifier != null) {
            notifier.activeCallingNotification(false);
        }
    }

    private void activeMissCallNotifier() {
        if (notifier != null) {
            notifier.activeMissCallNotification(true);
        }
    }

    @Override
    public void finish() {
        isUserFinish = true;
        super.finish();
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

        if (event == 1) {
            if (avChatUI != null) {
                avChatUI.resetRecordTip();
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
        Log.d(TAG, "onUserJoin");
        avChatUI.setVideoAccount(account);

        avChatUI.initRemoteSurfaceView(avChatUI.getVideoAccount());
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
        Log.d(TAG, "onCallEstablished");
        if (avChatUI.getTimeBase() == 0)
            avChatUI.setTimeBase(SystemClock.elapsedRealtime());

        if (state == AVChatType.AUDIO.getValue()) {
            avChatUI.onCallStateChange(CallStateEnum.AUDIO);
        } else {
            avChatUI.initLocalSurfaceView();
            avChatUI.onCallStateChange(CallStateEnum.VIDEO);
        }
        isCallEstablished = true;
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
}


