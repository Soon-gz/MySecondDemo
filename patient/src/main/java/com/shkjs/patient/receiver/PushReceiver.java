package com.shkjs.patient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.bean.Message;
import com.shkjs.patient.bean.push.DiagnosePushDto;
import com.shkjs.patient.bean.push.ExpenseFamilyPushDto;
import com.shkjs.patient.bean.push.ExpensePushDto;
import com.shkjs.patient.bean.push.FamilyPushDto;
import com.shkjs.patient.bean.push.GroupDiagnosePayPushDto;
import com.shkjs.patient.bean.push.OrderCancelPushDto;
import com.shkjs.patient.bean.push.OrderExpirePushDto;
import com.shkjs.patient.bean.push.RechargePushDto;
import com.shkjs.patient.bean.push.RedDotPushDto;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.PushMessageTypeEm;
import com.shkjs.patient.data.em.RedDotType;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.PushMessageManager;


/**
 * Created by LENOVO on 2016/8/18.
 * <p>
 * 个推透传消息处理
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
        JSONObject object = null;
        try {
            Logger.json(data);
            object = JSON.parseObject(data);

            if (null != object) {
                Message message = null;
                int value = object.getIntValue(Preference.TYPE);
                long id = object.getLongValue(Preference.ID);
                data = object.getString(Preference.DATA);
                PushMessageTypeEm em = PushMessageTypeEm.values()[value];
                switch (em) {
                    case RED_DOT:
                        message = JSON.parseObject(data, RedDotPushDto.class);
                        break;
                    case RECHARGE:
                        message = JSON.parseObject(data, RechargePushDto.class);
                        break;
                    case PAY:
                        message = JSON.parseObject(data, ExpensePushDto.class);
                        break;
                    case FAMILY_PAY:
                        message = JSON.parseObject(data, ExpenseFamilyPushDto.class);
                        break;
                    case FAMILY:
                        message = JSON.parseObject(data, FamilyPushDto.class);
                        break;
                    case ORDER_CANCEL:
                        message = JSON.parseObject(data, OrderCancelPushDto.class);
                        break;
                    case ORDER_OVERTIME:
                        message = JSON.parseObject(data, OrderExpirePushDto.class);
                        break;
                    case DIAGNOSE:
                        message = JSON.parseObject(data, DiagnosePushDto.class);
                        break;
                    case GROUP_DIAGNOSE:
                        message = JSON.parseObject(data, GroupDiagnosePayPushDto.class);
                        break;
                    default:
                        break;
                }
                if (null != message) {
                    if (message instanceof RedDotPushDto) {
                        RedDotType redDotType = RedDotType.values()[((RedDotPushDto) message).getRedDotType()];
                        switch (redDotType) {
                            case SYSTEM_MESSAGE:
                                break;
                            case INQUIRY_RESERVE:
                            case SIT_DIAGNOSE:
                            case GROUP_SIT_DIAGNOSE:
                                break;
                            case HEALTH_REPORT:
                                DataCache.getInstance().setNewReport(true);
                                break;
                            default:
                                break;
                        }
                    } else if (SharedPreferencesUtils.getBoolean(MyApplication.VOICE_SETTING)) {
                        message.setId(id);
                        PushMessageManager.getInstance().putPushMessage(context, em, message);
                        DataCache.getInstance().setUnReadNum(DataCache.getInstance().getUnReadNum() + 1);
                    }
                }
                Intent intent = new Intent(Preference.UPDATE_VIEW_ACTION);
                context.sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("推送数据为非JSON结构");
        }
    }

}
