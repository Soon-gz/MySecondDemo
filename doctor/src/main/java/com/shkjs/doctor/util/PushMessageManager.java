package com.shkjs.doctor.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raspberry.library.manager.NotificationManager;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.TextUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.activity.MessageActivity;
import com.shkjs.doctor.bean.GroupSitDiagnoseRoomInfo;
import com.shkjs.doctor.bean.Message;
import com.shkjs.doctor.bean.push.AuthenticationPushDto;
import com.shkjs.doctor.bean.push.GroupDiagnoseInvitePushDto;
import com.shkjs.doctor.bean.push.MonthClosingPushDto;
import com.shkjs.doctor.bean.push.OrderCancelPushDto;
import com.shkjs.doctor.bean.push.OrderFinishPushDto;
import com.shkjs.doctor.data.CancelType;
import com.shkjs.doctor.data.OrderSource;
import com.shkjs.doctor.data.PushMessageTypeEm;

/**
 * Created by xiaohu on 2016/11/19.
 * <p>
 * 推送消息管理类
 */

public class PushMessageManager {

    private static PushMessageManager manager;

    private PushMessageManager() {
    }

    /**
     * 单例
     *
     * @return PushMessageManager
     */
    public static PushMessageManager getInstance() {
        if (null == manager) {
            synchronized (PushMessageManager.class) {
                if (null == manager) {
                    manager = new PushMessageManager();
                }
            }
        }
        return manager;
    }

    public void putRedDot(Context context,String contentStr,long id){
        Intent intent = new Intent(context, MessageActivity.class) ;
        String title = "订单消息";
        String content = contentStr;
        if (!TextUtils.isEmpty(title)) {
            Log.i("tag00","透传消息，自定义notification~");
            NotificationManager manager = new NotificationManager.Builder(context).setTitle(title)
                    .setContent(content).setNotifyId((int)id).setIntent(intent).setIconId
                            (R.mipmap.ic_launcher).create();
            manager.sendNotify();
        }
    }

    public void putPushMessage(Context context, PushMessageTypeEm em, Message message) {
        String title = "";
        String type = "";
        String content = "";
        Log.i("tag00","弹窗通知传入context:"+context.getClass().getSimpleName());
        Intent intent = new Intent(context, MessageActivity.class) ;
        OrderSource source = null;
        boolean hasIntent = true;
        switch (em) {
            case RED_DOT:
                break;
            case ORDER_CANCEL:
                OrderCancelPushDto orderCancelPushDto = (OrderCancelPushDto) message;
                title = "订单取消";
//                String cancelType = "";
                CancelType cancelType1 = CancelType.values()[orderCancelPushDto.getCancelType()];
//                switch (cancelType1){
//                    case ADMIN:
//                        cancelType = "管理员";
//                        break;
//                    case DOCTOR:
//                        cancelType = "医生";
//                        break;
//                    case SYSTEM:
//                        cancelType = "系统";
//                        break;
//                    case USER:
//                        cancelType = "用户";
//                        break;
//                    default:
//                        break;
//                }

                source = OrderSource.values()[orderCancelPushDto.getOrderSource()];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                content = String.format("尊敬的医生您好，%s于%s取消了%s时间段的“%s”的订单，请合理安排该时间段的订单", orderCancelPushDto.getName(), orderCancelPushDto.getDateTime(),
                        orderCancelPushDto.getSubscribeTime(), type);
                break;
            case GROUP_DIAGNOSE:
                GroupDiagnoseInvitePushDto diagnoseInvitePushDto = (GroupDiagnoseInvitePushDto) message;
                title = "视频会诊邀请";
                content = "尊敬的医生,您好,"+diagnoseInvitePushDto.getCreateName()+"医生邀请了您加入"+diagnoseInvitePushDto.getName()+"患者的视频会诊,请尽快确认。";
                break;
            case AUTHENTICATION:
                AuthenticationPushDto authenticationPushDto = (AuthenticationPushDto) message;
                title = "医生认证";
                if (!Preference.NOTPASS.equals(authenticationPushDto.getLevel().name())){
                    content = String.format("尊敬的医生,您好,恭喜您已成功通过医星汇的 “%s”认证。", authenticationPushDto.getContent());
                }else {
                    content = String.format("尊敬的医生,您好,您在医星汇的医师认证审核未通过，具体原因是：%s。",authenticationPushDto.getContent() );
                }
                break;
            case GROUP_DIAGNOSE_ROOMINFO:
                hasIntent = false;
                GroupSitDiagnoseRoomInfo roomInfo = (GroupSitDiagnoseRoomInfo)message;
                title = "会诊房间号";
                content = "您好，会诊的房间号"+roomInfo.getRoomId();
                SharedPreferencesUtils.put(roomInfo.getOrderCode(),roomInfo.getRoomId());
                break;
            case ORDER_FINISH:
                OrderFinishPushDto orderFinishPushDto = (OrderFinishPushDto) message;
                title = "订单完成";
                source = OrderSource.values()[orderFinishPushDto.getOrderSource()];
                switch (source) {
                    case INQUIRY_RESERVE:
                        type = "图文咨询";
                        break;
                    case SIT_DIAGNOSE_RESERVE:
                        type = "视频咨询";
                        break;
                    case GROUP_SIT_DIAGNOSE:
                        type = "视频会诊";
                        break;
                    default:
                        break;
                }
                content = "尊敬的医生,您已完成"+orderFinishPushDto.getUserName()+"的"+type+",收到诊金"+BalanceUtils.formatBalance2(orderFinishPushDto.getMoney())+"元。";
                break;
            case MONTH_CLOSING:
                //尊敬的%s医生，医星汇已于%s将您%s的诊金%s结算至您绑定的支付宝账号，请注意查看。
                MonthClosingPushDto monthClosingPushDto = (MonthClosingPushDto) message;
                title = "结算";
                content = "尊敬的医生，医星汇已于"+monthClosingPushDto.getDateTime()+"将您"+monthClosingPushDto.getYearAndMonth()+"的诊金"+BalanceUtils.formatBalance2(monthClosingPushDto.getMoney())+"结算至您绑定的支付宝账号，请注意查看。";
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(title) && hasIntent) {
            Log.i("tag00","透传消息，自定义notification~");
            message.setTitle(title);
            message.setContent(content);
            intent.putExtra(Preference.EXTRA, em);
            intent.putExtra(Preference.DATA, message);
            NotificationManager manager = new NotificationManager.Builder(context).setTitle(message.getTitle())
                    .setContent(message.getContent()).setNotifyId((int) message.getId()).setIntent(intent).setIconId
                            (R.mipmap.ic_launcher).create();
            manager.sendNotify();
        }else {
            message.setTitle(title);
            message.setContent(content);
            intent.putExtra(Preference.EXTRA, em);
            intent.putExtra(Preference.DATA, message);
            NotificationManager manager = new NotificationManager.Builder(context).setTitle(message.getTitle())
                    .setContent(message.getContent()).setNotifyId((int) message.getId()).setIconId
                            (R.mipmap.ic_launcher).create();
            manager.sendNotify();
        }
    }
}
