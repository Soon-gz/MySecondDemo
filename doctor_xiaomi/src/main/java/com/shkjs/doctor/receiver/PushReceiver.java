package com.shkjs.doctor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.bean.GroupSitDiagnoseRoomInfo;
import com.shkjs.doctor.bean.Message;
import com.shkjs.doctor.bean.push.AuthenticationPushDto;
import com.shkjs.doctor.bean.push.GroupDiagnoseInvitePushDto;
import com.shkjs.doctor.bean.push.MonthClosingPushDto;
import com.shkjs.doctor.bean.push.OrderCancelPushDto;
import com.shkjs.doctor.bean.push.OrderFinishPushDto;
import com.shkjs.doctor.data.PushMessageTypeEm;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.PushMessageManager;


/**
 * Created by LENOVO on 2016/8/18.
 */

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    if (!TextUtils.isEmpty(data)) {
                        Logger.d("push: %s", data);
                        dataFactory(context, data);
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                /**
                 * 第三方应用需要将ClientID上传到第三方服务器，并且将当前用户帐号和ClientID进行关联，
                 * 以便以后通过用户帐号查找ClientID进行消息推送。有些情况下ClientID可能会发生变化，为保证获取最新的ClientID，
                 * 请应用程序在每次获取ClientID广播后，都能进行一次关联绑定
                 */
                // 获取ClientID(CID)
                final String cid = bundle.getString("clientid");
                RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
                    @Override
                    public void onSuccess(ObjectResponse<String> response, int code) {
                        Logger.d("upload cid success\nclient ： " + cid);
                    }
                };

                callback.setMainThread(false);
                HttpProtocol.uploadGetuiCid(cid, callback);
                break;
            case PushConsts.GET_SDKONLINESTATE:
                Logger.d("GET_SDKONLINESTATE ");
                break;
            default:
                break;
        }
    }

    /**
     * 所有推送消息处理
     *
     * @param context
     * @param data    推送消息，json结构
     */
    private void dataFactory(Context context, String data) {
        Log.i("tag00", "通知信息：" + data);
        try {
            JSONObject object = JSON.parseObject(data);
            if (null != object) {
                Message message = null;
                int value = object.getIntValue(Preference.TYPE);
                long id = object.getLongValue(Preference.ID);
                PushMessageTypeEm em = PushMessageTypeEm.values()[value];
                if (value != 1 && value != 0) {

                    object = object.getJSONObject(Preference.DATA);
                    switch (em) {
                        //订单取消通知
                        case ORDER_CANCEL:
                            message = JSON.parseObject(object.toJSONString(), OrderCancelPushDto.class);
                            break;
                        //认证通知
                        case AUTHENTICATION:
                            message = JSON.parseObject(object.toJSONString(), AuthenticationPushDto.class);
                            break;
                        //创建会诊或被人邀请通知
                        case GROUP_DIAGNOSE:
                            message = JSON.parseObject(object.toJSONString(), GroupDiagnoseInvitePushDto.class);
                            break;
                        case GROUP_DIAGNOSE_ROOMINFO:
                            message = JSON.parseObject(object.toJSONString(), GroupSitDiagnoseRoomInfo.class);
                            break;
                        case ORDER_FINISH:
                            message = JSON.parseObject(object.toJSONString(), OrderFinishPushDto.class);
                            break;
                        case MONTH_CLOSING:
                            message = JSON.parseObject(object.toJSONString(), MonthClosingPushDto.class);
                            break;
                        default:
                            message = new Message();
                            break;
                    }
                    message.setId(id);
                    if (SharedPreferencesUtils.getBoolean(Preference.PUSH_MSG)){
                        PushMessageManager.getInstance().putPushMessage(context, em, message);
                    }
                } else if (value != 0) {
                    PushMessageManager.getInstance().putRedDot(context, object.getString(Preference.DATA), id);
                }
            }
        } catch (Exception e) {
            Log.i("tag00", e.toString());
        }
    }
}
