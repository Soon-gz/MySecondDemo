package com.shkjs.patient.util;

import android.content.Context;
import android.content.Intent;

import com.raspberry.library.manager.NotificationManager;
import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.WelcomeActivity;
import com.shkjs.patient.bean.Message;
import com.shkjs.patient.bean.push.DiagnosePushDto;
import com.shkjs.patient.bean.push.ExpenseFamilyPushDto;
import com.shkjs.patient.bean.push.ExpensePushDto;
import com.shkjs.patient.bean.push.FamilyPushDto;
import com.shkjs.patient.bean.push.GroupDiagnosePayPushDto;
import com.shkjs.patient.bean.push.OrderCancelPushDto;
import com.shkjs.patient.bean.push.OrderExpirePushDto;
import com.shkjs.patient.bean.push.RechargePushDto;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.OperateType;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.PushMessageTypeEm;

/**
 * Created by xiaohu on 2016/11/19.
 * <p>
 * 推送消息管理类
 */

public class PushMessageManager {

    private static PushMessageManager manager;
    private NotificationManager notificationManager;

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

    public void putPushMessage(Context context, PushMessageTypeEm em, Message message) {
        String title = "";
        String type = "";
        String content = "";
        Intent intent = new Intent(context, WelcomeActivity.class);
        OrderSource source = null;
        switch (em) {
            case RECHARGE:
                RechargePushDto rechargePushDto = (RechargePushDto) message;
                title = "充值成功";
                content = context.getString(R.string.notifiaction_recharge_success);
                content = String.format(content, rechargePushDto.getDateTime(), SpliceUtils.formatBalance
                        (rechargePushDto.getMoney()));
                break;
            case PAY:
                ExpensePushDto expensePushDto = (ExpensePushDto) message;
                title = "支付成功";
                source = OrderSource.values()[expensePushDto.getOrderSource()];
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
                content = context.getString(R.string.notifiaction_pay_success);
                content = String.format(content, expensePushDto.getDateTime(), type, SpliceUtils.formatBalance
                        (expensePushDto.getMoney()));
                break;
            case FAMILY_PAY:
                ExpenseFamilyPushDto expenseFamilyPushDto = (ExpenseFamilyPushDto) message;
                title = "支付成功";
                source = OrderSource.values()[expenseFamilyPushDto.getOrderSource()];
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
                if (expenseFamilyPushDto.getIsUser() == 1) {//成员
                    content = context.getString(R.string.notifiaction_household_pay_success);
                    content = String.format(content, expenseFamilyPushDto.getDateTime(), type, SpliceUtils
                            .formatBalance(expenseFamilyPushDto.getMoney()));
                } else { //户主
                    content = context.getString(R.string.notifiaction_household_head_pay_success);
                    content = String.format(content, expenseFamilyPushDto.getName(), expenseFamilyPushDto.getDateTime
                            (), type, SpliceUtils.formatBalance(expenseFamilyPushDto.getMoney()));
                }
                break;
            case FAMILY:
                FamilyPushDto familyPushDto = (FamilyPushDto) message;
                title = "家庭组消息";
                OperateType operateType = OperateType.values()[familyPushDto.getOper()];
                switch (operateType) {
                    case ADD:
                        //被添加
                        content = context.getString(R.string.notifiaction_add_member_success);
                        content = String.format(content, familyPushDto.getName(), familyPushDto.getDateTime());
                        break;
                    case DEL:
                        //被删除
                        content = context.getString(R.string.notifiaction_delete_member_success);
                        content = String.format(content, familyPushDto.getName(), familyPushDto.getDateTime());
                        break;
                    case QUIT:
                        //退出
                        content = context.getString(R.string.notifiaction_quit_household_success);
                        content = String.format(content, familyPushDto.getName(), familyPushDto.getDateTime());
                        break;
                    default:
                        break;
                }
                break;
            case ORDER_CANCEL:
                OrderCancelPushDto orderCancelPushDto = (OrderCancelPushDto) message;
                title = "订单取消";
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
                CancelType cancelType = CancelType.values()[orderCancelPushDto.getCancelType()];
                switch (cancelType) {
                    case ADMIN:
                        content = context.getString(R.string.notifiaction_expire_order_success);
                        content = String.format(content, orderCancelPushDto.getName(), type);
                        break;
                    case SYSTEM:
                        break;
                    case DOCTOR:
                        content = context.getString(R.string.notifiaction_cancel_order_success);
                        content = String.format(content, orderCancelPushDto.getName(), orderCancelPushDto.getDateTime
                                (), orderCancelPushDto.getSubscribeTime(), type);
                        break;
                    case USER:
                        break;
                    default:
                        break;
                }
                break;
            case ORDER_OVERTIME:
                OrderExpirePushDto orderExpirePushDto = (OrderExpirePushDto) message;
                title = "订单超时";
                source = OrderSource.values()[orderExpirePushDto.getOrderSource()];
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
                content = context.getString(R.string.notifiaction_expire_order_success);
                content = String.format(content, orderExpirePushDto.getSubscribeTime(), type);
                break;
            case DIAGNOSE:
                DiagnosePushDto diagnosePushDto = (DiagnosePushDto) message;
                break;
            case GROUP_DIAGNOSE:
                GroupDiagnosePayPushDto groupDiagnosePayPushDto = (GroupDiagnosePayPushDto) message;
                title = "视频会诊";
                content = context.getString(R.string.notifiaction_group_sitdiagnose_order_success);
                String doctorName = "";
                for (String name : groupDiagnosePayPushDto.getDoctorNames()) {
                    doctorName = doctorName + name + " ";
                }
                content = String.format(content, groupDiagnosePayPushDto.getCreateName(), doctorName);
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(title)) {
            message.setTitle(title);
            message.setContent(content);
            intent.putExtra(Preference.EXTRA, em);
            intent.putExtra(Preference.DATA, message);
            notificationManager = new NotificationManager.Builder(context).setTitle(message.getTitle()).setContent
                    (message.getContent()).setNotifyId((int) message.getId()).setIntent(intent).setIconId(R.mipmap
                    .ic_launcher).create();
            notificationManager.sendNotify();
        }
    }

    public void dismissNotify(int id) {
        if (null != notificationManager) {
            notificationManager.dismissNotify();
        }
    }

    public void dismissAllNotify() {
        if (null != notificationManager) {
            notificationManager.dismissAllNotify();
        }
    }
}
