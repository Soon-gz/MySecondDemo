package com.shkjs.nim.chat.session.action;

import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.activity.AVChatActivity;
import com.shkjs.nim.em.ClientType;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class AVChatAction extends BaseAction {
    private AVChatType avChatType;

    public AVChatAction(AVChatType avChatType) {
        super(avChatType == AVChatType.AUDIO ? R.drawable.message_plus_audio_chat_selector : R.drawable
                .message_plus_video_chat_selector, avChatType == AVChatType.AUDIO ? R.string.input_panel_audio_call :
                R.string.input_panel_video_call);
        this.avChatType = avChatType;
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            startAudioVideoCall(avChatType);
        } else {
            ToastUtils.showToast(getActivity().getString(R.string.network_is_not_available));
        }
    }

    /************************
     * 音视频通话
     ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
        //这里不使用，所以携带的客户端没有意义
        AVChatActivity.start(getActivity(), getAccount(), avChatType.getValue(), AVChatActivity.FROM_INTERNAL, ClientType.DOCTOR);
    }
}