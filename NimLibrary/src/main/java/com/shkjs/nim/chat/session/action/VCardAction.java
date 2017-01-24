package com.shkjs.nim.chat.session.action;

import android.content.Intent;

import com.netease.nim.uikit.session.actions.BaseAction;
import com.shkjs.nim.R;

/**
 * Created by xiaohu on 2016/9/8.
 */
public class VCardAction extends BaseAction {

    /**
     * 构造函数
     */
    public VCardAction() {
        super(R.drawable.im_upload_bussiness_sel, R.string.vcard);
    }

    @Override
    public void onClick() {

        Intent mIntent = new Intent("com.shkjs.doctor.activity.SearchDoctorActivity");
        mIntent.putExtra("Account",getAccount());
        //发送广播
        getActivity().sendBroadcast(mIntent);
        getActivity().finish();
        //TODO 测试使用
//        VCardAttachment attachment = new VCardAttachment(102l, getAccount(), "https://ss0.bdstatic" +
//                ".com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1477892196&di" +
//                "=d081aacc4f3b39db05ae8a30a3ff788d&src=http://file27.mafengwo" +
//                ".net/M00/B2/12/wKgB6lO0ahWAMhL8AAV1yBFJDJw20.jpeg");
//
//        IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment);
//        sendMessage(message);
    }
}
