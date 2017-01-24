package com.shkjs.nim.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.widgets.ToggleListener;
import com.shkjs.nim.chat.widgets.ToggleState;
import com.shkjs.nim.chat.widgets.ToggleView;
import com.shkjs.nim.em.ClientType;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 视频管理器， 视频界面初始化和相关管理
 * Created by hzxuwen on 2015/5/5.
 */
public class AVChatVideo implements View.OnClickListener, ToggleListener {

    // data
    private Context context;
    private View root;
    private AVChatUI manager;
    //顶部控制按钮
    private View topRoot;
    private View switchAudio;
    private CircleImageView video_head_iv;
    private Chronometer time;
    private TextView netUnstableTV;
    //中间控制按钮
    private View middleRoot;
    private HeadImageView headImg;
    private TextView nickNameTV;
    private TextView notifyTV;
    private View refuse_receive;
    private TextView refuseTV;
    private TextView receiveTV;
    //底部控制按钮
    private View bottomRoot;
    ToggleView switchCameraToggle;
    ToggleView closeCameraToggle;
    ToggleView muteToggle;
    ToggleView recordToggle;
    ImageView hangUpImg;
    //医生端结束拨打按钮
    ImageButton doctor_video_finish;

    //record
    private View recordView;
    private View recordTip;
    private View recordWarning;

    private int topRootHeight = 0;
    private int bottomRootHeight = 0;

    private AVChatUIListener listener;

    // state
    private boolean init = false;
    private boolean shouldEnableToggle = false;
    private boolean isInSwitch = false;
    //医生端数据
    private UserInfoBean userInfoBean;
    private ClientType clientType;
    private String orderCode;
    //患者端数据
    private String receiverId;

    public AVChatVideo(Context context, View root, AVChatUIListener listener, AVChatUI manager) {
        this.context = context;
        this.root = root;
        this.listener = listener;
        this.manager = manager;
    }

    public AVChatVideo(Context context, View root, AVChatUIListener listener, AVChatUI manager, ClientType clientType) {
        this.context = context;
        this.root = root;
        this.listener = listener;
        this.manager = manager;
        this.clientType = clientType;
    }


    private void findViews() {
        if (init || root == null)
            return;
        doctor_video_finish = (ImageButton) root.findViewById(R.id.doctor_video_finish);
        doctor_video_finish.setOnClickListener(this);
        topRoot = root.findViewById(R.id.avchat_video_top_control);
        switchAudio = topRoot.findViewById(R.id.avchat_video_switch_audio);
        switchAudio.setOnClickListener(this);
        //头像
        video_head_iv = (CircleImageView) root.findViewById(R.id.video_head_iv);

        video_head_iv.setOnClickListener(this);
        time = (Chronometer) topRoot.findViewById(R.id.avchat_video_time);
        netUnstableTV = (TextView) topRoot.findViewById(R.id.avchat_video_netunstable);

        middleRoot = root.findViewById(R.id.avchat_video_middle_control);
        headImg = (HeadImageView) middleRoot.findViewById(R.id.avchat_video_head);
        nickNameTV = (TextView) middleRoot.findViewById(R.id.avchat_video_nickname);
        notifyTV = (TextView) middleRoot.findViewById(R.id.avchat_video_notify);

        refuse_receive = middleRoot.findViewById(R.id.avchat_video_refuse_receive);
        refuseTV = (TextView) refuse_receive.findViewById(R.id.refuse);
        receiveTV = (TextView) refuse_receive.findViewById(R.id.receive);
        refuseTV.setOnClickListener(this);
        receiveTV.setOnClickListener(this);

        recordView = root.findViewById(R.id.avchat_record_layout);
        recordTip = recordView.findViewById(R.id.avchat_record_tip);
        recordWarning = recordView.findViewById(R.id.avchat_record_warning);

        bottomRoot = root.findViewById(R.id.avchat_video_bottom_control);
        switchCameraToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_switch_camera), ToggleState.DISABLE,
                this);
        closeCameraToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_close_camera), ToggleState.DISABLE,
                this);
        muteToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_video_mute), ToggleState.DISABLE, this);
        recordToggle = new ToggleView(bottomRoot.findViewById(R.id.avchat_video_record), ToggleState.DISABLE, this);
        hangUpImg = (ImageView) bottomRoot.findViewById(R.id.avchat_video_logout);
        hangUpImg.setOnClickListener(this);
        init = true;
    }


    /**
     * 音视频状态变化及界面刷新
     *
     * @param state
     */
    public void onCallStateChange(CallStateEnum state) {
        if (CallStateEnum.isVideoMode(state))
            findViews();
        switch (state) {
            case OUTGOING_VIDEO_CALLING:
                showProfile();//对方的详细信息
                showNotify(R.string.avchat_wait_recieve);
                setRefuseReceive(false);
                shouldEnableToggle = true;
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(false);
                setDoctorVideoFinish(true);
                break;
            case INCOMING_VIDEO_CALLING:
                showProfile();//对方的详细信息
                showNotify(R.string.avchat_video_call_request);
                setRefuseReceive(true);
                receiveTV.setTextColor(ContextCompat.getColor(context, R.color.black_231815));
                receiveTV.setText(R.string.avchat_pickup);
                setTopRoot(false);
                setMiddleRoot(true);
                setBottomRoot(false);
                setDoctorVideoFinish(false);
                break;
            case VIDEO:
                isInSwitch = false;
                enableToggle();
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                setDoctorVideoFinish(false);
                break;
            case VIDEO_CONNECTING:
                showNotify(R.string.avchat_connecting);
                shouldEnableToggle = true;
                setDoctorVideoFinish(false);
                break;
            case OUTGOING_AUDIO_TO_VIDEO:
                isInSwitch = true;
                setTime(true);
                setTopRoot(true);
                setMiddleRoot(false);
                setBottomRoot(true);
                setDoctorVideoFinish(false);
                break;
            default:
                break;
        }
        setRoot(CallStateEnum.isVideoMode(state));
    }

    /********************** 界面显示 **********************************/

    /**
     * 显示个人信息
     */
    private void showProfile() {
        String account = manager.getAccount();
        headImg.loadBuddyAvatar(account);
        nickNameTV.setTextColor(ContextCompat.getColor(context, R.color.black_231815));
        nickNameTV.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
    }

    /**
     * 显示通知
     *
     * @param resId
     */
    private void showNotify(int resId) {
        notifyTV.setTextColor(ContextCompat.getColor(context, R.color.black_231815));
        notifyTV.setText(resId);
        notifyTV.setVisibility(View.VISIBLE);
    }

    /************************
     * 布局显隐设置
     ****************************/

    private void setRoot(boolean visible) {
        root.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setRefuseReceive(boolean visible) {
        refuse_receive.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setTopRoot(boolean visible) {
        topRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (topRootHeight == 0) {
            Rect rect = new Rect();
            topRoot.getGlobalVisibleRect(rect);
            topRootHeight = rect.bottom;
        }
    }

    private void setMiddleRoot(boolean visible) {
        middleRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setDoctorVideoFinish(boolean visible) {
        doctor_video_finish.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setIsFinished(boolean isFinished) {

    }

    public void setBottomRoot(boolean visible) {
        bottomRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (bottomRootHeight == 0) {
            bottomRootHeight = bottomRoot.getHeight();
        }
    }

    private void setTime(boolean visible) {
        time.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            time.setBase(manager.getTimeBase());
            time.start();
        }
    }

    /**
     * 底部控制开关可用
     */
    private void enableToggle() {
        if (shouldEnableToggle) {
            if (manager.canSwitchCamera() && AVChatManager.getInstance().hasMultipleCameras())
                switchCameraToggle.enable();
            closeCameraToggle.enable();
            muteToggle.enable();
            recordToggle.enable();
            shouldEnableToggle = false;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.avchat_video_logout) {
            listener.onHangUp();
            if (clientType != null) {
                switch (clientType.getType()) {
                    case 0:
                        Intent mIntent = new Intent("VIDEO_CONSULT");
                        //发送广播
                        context.sendBroadcast(mIntent);
                        break;
                    case 2:
                        Intent mIntent1 = new Intent("VIDEO_CONSULT_MAIN");
                        //发送广播
                        context.sendBroadcast(mIntent1);
                        break;
                }
            }

        } else if (i == R.id.refuse) {
            listener.onRefuse();

        } else if (i == R.id.receive) {
            if (null != clientType && clientType.equals(ClientType.PATIENT)) {
                final UserInfoProvider.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(receiverId);
                if (null != userInfo) {
                    Glide.with(context).load(userInfo.getAvatar()).placeholder(R.drawable.avatar_def).error(R
                            .drawable.avatar_def).dontAnimate().thumbnail(0.1f).into(video_head_iv);
                }
            }
            listener.onReceive();

        } else if (i == R.id.avchat_video_mute) {
            listener.toggleMute();

        } else if (i == R.id.avchat_video_switch_audio) {
            if (isInSwitch) {
                Toast.makeText(context, R.string.avchat_in_switch, Toast.LENGTH_SHORT).show();
            } else {
                listener.videoSwitchAudio();
            }

        } else if (i == R.id.avchat_switch_camera) {
            listener.switchCamera();

        } else if (i == R.id.avchat_close_camera) {
            listener.closeCamera();

        } else if (i == R.id.avchat_video_record) {
            listener.toggleRecord();

        } else if (i == R.id.doctor_video_finish) {
            listener.onHangUp();
        } else if (i == R.id.video_head_iv) {
            if (clientType != null) {
                switch (clientType.getType()) {
                    case 0:
                        Intent mIntent = new Intent("com.shkjs.doctor.activity.VideoConsultHReport");
                        mIntent.putExtra("userInfo", userInfoBean);
                        mIntent.putExtra("orderCode", orderCode);
                        //发送广播
                        context.sendBroadcast(mIntent);
                        break;
                    case 1:
                        //患者端点击头像的操作
                        Intent intent = new Intent("com.shkjs.patient.doctor_detail");
                        intent.putExtra("doctorUserName", receiverId);
                        context.sendBroadcast(intent);
                        break;
                    case 2:
                        Intent mIntent2 = new Intent("com.shkjs.doctor.activity.VideoConsultHReport");
                        mIntent2.putExtra("userInfo", userInfoBean);
                        mIntent2.putExtra("orderCode", orderCode);
                        //发送广播
                        context.sendBroadcast(mIntent2);
                        break;
                }
            }
        }

    }

    public void showRecordView(boolean show, boolean warning) {
        if (show) {
            recordView.setVisibility(View.VISIBLE);
            recordTip.setVisibility(View.VISIBLE);
            if (warning) {
                recordWarning.setVisibility(View.VISIBLE);
            } else {
                recordWarning.setVisibility(View.GONE);
            }
        } else {
            recordView.setVisibility(View.INVISIBLE);
            recordTip.setVisibility(View.INVISIBLE);
            recordWarning.setVisibility(View.GONE);
        }
    }

    /**
     * 音频切换为视频, 界面控件是否开启显示
     *
     * @param muteOn
     */
    public void onAudioToVideo(boolean muteOn, boolean recordOn, boolean recordWarning) {
        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
        closeCameraToggle.toggle(ToggleState.OFF);
        if (manager.canSwitchCamera()) {
            switchCameraToggle.off(false);
        }
        recordToggle.toggle(recordOn ? ToggleState.ON : ToggleState.OFF);

        showRecordView(recordOn, recordWarning);

    }

    /*******************************
     * 设置userInfo
     *************************/
    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
        if (userInfoBean != null) {
            Glide.with(context).load("http://116.62.7.65:8080/frontWeb/ossfile/" + userInfoBean.getHeadPortrait())
                    .placeholder(R.drawable.avatar_def).dontAnimate().thumbnail(0.1f).into(video_head_iv);
        }
        Log.i("tag00", "AVChatVideo 用户信息：" + userInfoBean.getHeadPortrait());
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
        Log.i("tag00", "AVChatVideo orderCode：" + orderCode);
    }

    /**
     * 设置 receiverId
     *
     * @param receiverId 对方账号用户
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /*******************************
     * toggle listener
     *************************/
    @Override
    public void toggleOn(View v) {
        onClick(v);
    }

    @Override
    public void toggleOff(View v) {
        onClick(v);
    }

    @Override
    public void toggleDisable(View v) {

    }

    public void closeSession(int exitCode) {
        if (init) {
            time.stop();
            switchCameraToggle.disable(false);
            muteToggle.disable(false);
            recordToggle.disable(false);
            closeCameraToggle.disable(false);
            receiveTV.setEnabled(false);
            refuseTV.setEnabled(false);
            hangUpImg.setEnabled(false);
        }
    }
}
