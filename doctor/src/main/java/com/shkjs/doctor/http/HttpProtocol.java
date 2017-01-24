package com.shkjs.doctor.http;


import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.NetworkUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.CertificationBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.Page;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.ResponseStatusEnum;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.io.StrParam;

import java.io.File;
import java.util.List;

/**
 * Created by xiaohu on 2016/9/18.
 * <p/>
 * 所有协议实现
 */
public class HttpProtocol {

    /**
     * 认证通过后，自动登录
     */
    public static void loginAutoBackgroud() {
        if (null != DataCache.getInstance().getUserInfo()) {
            String platformLevel = DataCache.getInstance().getUserInfo().getPlatformLevel() != null ? DataCache
                    .getInstance().getUserInfo().getPlatformLevel() : "";
            Log.i("tag00", "医生：" + platformLevel);
            if (platformLevel.equals(Preference.AUTHORITY) || platformLevel.equals(Preference.CERTIFICATION)) {
                RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response, int code) {
                        ToastUtils.showToast("恭喜您，已通过医生认证。可以使用更丰富的功能~");
                    }
                };
                Log.i("tag00", "用户：" + SharedPreferencesUtils.getString(Preference.USERNAME) + "密码：" + MD5Utils
                        .encodeMD52(SharedPreferencesUtils.getString(Preference.PASSWORD)));
                HttpProtocol.login(SharedPreferencesUtils.getString(Preference.USERNAME), MD5Utils.encodeMD52
                        (SharedPreferencesUtils.getString(Preference.PASSWORD)), callback);
            }
        }
    }

    /**
     * 未通过后自动登录
     */
    //    public static void loginBackgroudNotpass(){
    //        if (null != DataCache.getInstance().getUserInfo()){
    //            String platformLevel = DataCache.getInstance().getUserInfo().getPlatformLevel() != null?DataCache
    // .getInstance().getUserInfo().getPlatformLevel():"";
    //            if (platformLevel.equals(Preference.NOTPASS)){
    //                RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
    //                    @Override
    //                    public void onSuccess(BaseResponse response, int code) {
    //                    }
    //                };
    //                Log.i("tag00","用户："+SharedPreferencesUtils.getString(Preference.USERNAME) + "密码："+MD5Utils
    // .encodeMD52(SharedPreferencesUtils.getString(Preference.PASSWORD)));
    //                HttpProtocol.login(SharedPreferencesUtils.getString(Preference.USERNAME),  MD5Utils.encodeMD52
    // (SharedPreferencesUtils.getString(Preference.PASSWORD)), callback);
    //            }
    //        }
    //    }
    public static boolean checkStatus(BaseResponse response, int code) {
        if (code == 200 && response != null) {
            if (response.getStatus().equals(ResponseStatusEnum.FAIL.getValue())) {
                return false;
            } else if (response.getStatus().equals(ResponseStatusEnum.EXCEPTION.getValue())) {
                return false;
            } else if (response.getStatus().equals(ResponseStatusEnum.UNLOGIN.getValue())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 账户是否能注册
     *
     * @param userName 用户名
     * @param callback 能否注册结果回调
     */
    public static void canRegister(String userName, HttpCallback callback) {
        String url = HttpBase.CAN_REGISTER_URL;
        StrParam bodyStr = new StrParam(Preference.USERNAME, userName);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 上传个推cid接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param cid      clentid
     * @param callback 结果回调
     */
    public static void uploadGetuiCid(String cid, HttpCallback callback) {
        String url = HttpBase.UPLOAD_GETUI_CID_URL;
        String bodyStr = "cid=" + cid + "&deviceType=android&userType=DOCTOR";
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 用户注册
     *
     * @param userName 用户名
     * @param password 密码
     * @param code     短信验证码
     * @param callback 注册结果回调
     */
    public static void register(String userName, String password, String code, final HttpCallback callback, Activity activity) {
        final String url = HttpBase.REGISTER_URL;
        final String bodyStr = Preference.USERNAME + "=" + userName + "&" + Preference.PASSWORD + "=" + password + "&code="
                + code + HttpBase.PUT_METHOD;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 用户登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param callback 登录结果回调
     */
    public static void login(String userName, String password, HttpCallback callback) {
        String url = HttpBase.LOGIN_URL;
        String bodyStr = Preference.USERNAME + "=" + userName + "&" + Preference.PASSWORD + "=" + password;
        Http.postAsync(url, callback, bodyStr);

    }

    private interface NetworkStatus{
        void connected();
    }

    private static void checkNetwork(final Activity activity, NetworkStatus networkStatus){
        if (NetworkUtils.isNetworkAvailable(activity)){
            networkStatus.connected();
        }else {
            CustomAlertDialog.dialogExSureCancel("网络连接异常，是否去设置网络？", activity, new CustomAlertDialog.OnDialogClickListener() {
                @Override
                public void doSomeThings() {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    activity.startActivity(intent);
                }
            });
        }
    }

    /**
     * 用户登出
     *
     * @param callback 登出结果回调
     */
    public static void logout(HttpCallback callback) {
        String url = HttpBase.LOGOUT_URL;
        Http.postAsync(url, callback, "");
    }

    /**
     * 找回密码
     *
     * @param userName    用户名
     * @param newPassword 新密码
     * @param code        短信验证码
     * @param callback    找回密码结果回调
     */
    public static void findPassword(String userName, String newPassword, String code, final HttpCallback callback, Activity activity) {
        final String url = HttpBase.FINDPASSWORD_URL;
        final String bodyStr = Preference.USERNAME + "=" + userName + "&" + Preference.PASSWORD + "=" + newPassword +
                "&code=" + code;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 帮助信息接口
     *
     * @param callback 结果返回
     */
    public static void queryHelp(HttpCallback callback, int page) {
        String url = HttpBase.QUERYHELP_URL + "?pageSize=" + 20 + "&pageNum=" + page;
        Http.getAsync(url, callback);
    }

    /**
     * 获取医生详细信息接口
     *
     * @param callback
     */
    public static void detail(HttpCallback callback) {
        String url = HttpBase.DETAIL;
        Http.getAsync(url, callback);
    }

    /**
     * 医生认证资料上传
     *
     * @param callback
     */
    public static void authentication(final HttpCallback callback, CertificationBean bean, String headPortrait, String
            identityPermitFront, String identityPermitReverse, String workPermit, String doctorPermit, Activity activity) {
        final String url = HttpBase.AUTHENTICATION;
        final String bodyStr = "name=" + bean.getCertificateName() + "&introduce=" + bean.getCertificateJianjie() +
                "&hospitalName=" +
                bean.getCertificateHospital() + "&skilled=" + bean.getCertificateShanchang() + "&categoryName=" +
                bean.getCertificateSubject() +
                "&level=" + bean.getCertificateZhicheng() + "&headPortrait=" + headPortrait + "&identityPermitFront="
                + identityPermitFront +
                "&identityPermitReverse=" + identityPermitReverse + "&workPermit=" + workPermit + "&doctorPermit=" +
                doctorPermit;
        Log.i("tag00", bodyStr);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 单文件上传
     *
     * @param callback
     * @param file
     */
    public static void uploadOneFile(final HttpCallback callback, final File file, Activity activity) {
        final String url = HttpBase.UPLOADONEFILE;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.uploadAsync(url, "file", file, callback);
            }
        });
    }

    /**
     * 坐诊时间设置
     *
     * @param callback
     * @param body
     */
    public static void visitTimeSetting(final HttpCallback callback, String body, Activity activity) {
        final String url = HttpBase.VISIT_TIME_SETTINGS;
        final String bodyStr = body + HttpBase.PUT_METHOD;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 获取坐诊时间设置
     *
     * @param callback
     * @param date
     */
    public static void getVisitTimeSettings(HttpCallback callback, Long date, int pageSize) {
        String url = HttpBase.GET_VISIT_TIME_SETTINGS + "?pageSize=" + pageSize + "&dateMsg=" + date;
        Http.getAsync(url, callback);
    }


    /**
     * 获取我的钱包详细信息
     *
     * @param callback
     */
    public static void getAccountDeatil(HttpCallback callback) {
        Http.getAsync(HttpBase.GET_ACCOUNT_DETAIL, callback);
    }


    /**
     * 获取账户已绑的银行，支付宝，微信
     *
     * @param callback
     */
    public static void getBandCard(HttpCallback callback) {
        Http.getAsync(HttpBase.GET_BANDCARD, callback);
    }

    /**
     * 绑定支付宝
     *
     * @param callback   接口回调
     * @param bankCard   银行卡、支付宝
     * @param defaultUse 真实用户名
     */
    public static void bindCard(final HttpCallback callback, String bankCard, String defaultUse, Activity activity) {
        final String bodyStr = "bankCard=" + bankCard + "&defaultUse=" + defaultUse;
        Log.i("tag00", "绑定支付宝:" + HttpBase.UPLOAD_BANDCARD + "post数据:" + bodyStr);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(HttpBase.UPLOAD_BANDCARD, callback, bodyStr);
            }
        });
    }

    /**
     * 获取应用枚举变量
     *
     * @param callback
     */
    public static void getOptions(HttpCallback callback, String optiontype) {
        String url = HttpBase.OPTION + "?typeName=" + optiontype;
        Http.getAsync(url, callback);
    }

    /**
     * 获取科室信息
     *
     * @param callback
     */
    public static void getMedical(HttpCallback callback) {
        String url = HttpBase.SEARCH + "?pageSize=" + Integer.MAX_VALUE;
        Http.getAsync(url, callback);
    }

    /**
     * 获取我的病人信息
     */
    public static void getMypatients(HttpCallback callback, int page) {
        String url = HttpBase.MYPATIENT + "?pageSize=" + 20 + "&pageNum=" + page;
        Http.getAsync(url, callback);
    }

    /**
     * 修改用户的密码
     *
     * @param callback
     * @param oldPw
     * @param newPw
     */
    public static void modifyUserPW(HttpCallback callback, String oldPw, String newPw) {
        String url = HttpBase.MODEIFYPW;
        String strBody = "oldPwd=" + oldPw + "&newPwd=" + newPw;
        Http.postAsync(url, callback, strBody);
    }


    /**
     * 获取历史订单
     *
     * @param callback
     */
    public static void getHistoryOrder(HttpCallback callback, String type, int page) {
        String urlStr = HttpBase.HISTORY_ORDER + "?status=" + type + "&pageSize=" + 20 + "&pageNum=" + page;
        Log.i("tag00", "获取历史订单:" + urlStr);
        Http.getAsync(urlStr, callback);
    }

    /**
     * 获取历史订单
     *
     * @param callback
     */
    public static void getHistoryOrderFinish(HttpCallback callback, int page) {
        String urlStr = HttpBase.HISTORY_ORDER_FINISH + "?pageSize=" + 20 + "&pageNum=" + page;
        Log.i("tag00", "获取已结束历史订单:" + urlStr);
        Http.getAsync(urlStr, callback);
    }

    /**
     * 取消未完成订单
     *
     * @param callback
     * @param orderCode
     * @param source
     */
    public static void postCancelOrder(final HttpCallback callback, String orderCode, String source, Activity activity) {
        final String url = HttpBase.CANCEL_ORDER;
        final String bodyStr = "orderCode=" + orderCode + "&source=" + source;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 添加至我的患者
     *
     * @param callback
     * @param uerId
     */
    public static void postAddMypatients(final HttpCallback callback, String uerId, Activity activity) {
        final String url = HttpBase.ADD_MYPATIENTS;
        final String bodyStr = "type=" + "DU" + "&relationUserId=" + uerId;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 获取健康报告列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserHealthReports>
     *
     * @param userId   用户ID
     * @param callback 结果回调
     */
    public static void getReportList(int userId, HttpCallback callback, int page) {
        String url = String.format(HttpBase.GET_REPORT_LIST_USERID_AND_TYPE_URL, userId);
        String bodyStr = url + "?" + Preference.PAGE_SIZE + "=" + 20 + "&pageNum=" + page;
        Http.getAsync(bodyStr, callback);
    }

    /**
     * 获取健康报告
     *
     * @param userId
     * @param callback
     * @param page
     */
    public static void getReportRightList(String userId, HttpCallback callback, int page) {
        String url = HttpBase.GET_REPORT_LIST;
        String bodyStr = url + "?userId=" + userId + "&" + Preference.PAGE_SIZE + "=" + 20 + "&pageNum=" + page;
        Http.getAsync(bodyStr, callback);
    }

    /**
     * 解除我的患者
     *
     * @param callback
     * @param userId
     */
    public static void postDeleteMyPatients(final HttpCallback callback, String userId, Activity activity) {
        final String url = HttpBase.DELETE_MYPATIENTS;
        final String bodyStr = "type=" + "DU" + "&relationUserId=" + userId;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 通过用户id直接拿到用户名
     *
     * @param callback
     * @param userId
     */
    public static void getUserNameById(HttpCallback callback, String userId) {
        String url = HttpBase.GET_USERNAME_BYID;
        String bodyStr = url + "?userId=" + userId;
        Log.i("tag00", "通过用户id直接拿到用户名:" + bodyStr);
        Http.getAsync(bodyStr, callback);
    }

    /**
     * 获取坐诊时间
     *
     * @param callback
     * @param dignoseId
     */
    public static void getDignoseTime(HttpCallback callback, String dignoseId) {
        String url = HttpBase.GET_DIGNOSE_TIME + "?id=" + dignoseId;
        Http.getAsync(url, callback);
    }

    /**
     * 通过医生的电话获取医生信息
     *
     * @param callback
     * @param doctorName
     */
    public static void getDoctorInfoFromName(HttpCallback callback, String doctorName) {
        String url = HttpBase.GET_DOCTOR_FROM_NAME + "?userName=" + doctorName;
        Http.getAsync(url, callback);
    }

    /**
     * 获取未完成的图文资讯
     *
     * @param callback
     */
    public static void getPictureConsult(HttpCallback callback) {
        String url = HttpBase.GET_PICTURE_CONSULT + "?pageSize=" + Integer.MAX_VALUE + "&pageNum=" + 1;
        Http.getAsync(url, callback);
    }

    /**
     * 获取会诊
     *
     * @param callback
     */
    public static void getVideoGroupConsult(HttpCallback callback) {
        Http.getAsync(HttpBase.GET_VIDEO_GROUP_CONSULT, callback);
    }

    /**
     * 获取坐诊
     *
     * @param callback
     */
    public static void getVideoConsult(HttpCallback callback, int pageNum) {
        Log.i("tag00", "获取未完成会诊:" + HttpBase.GET_VIDEO_CONSULT + "?pageSize=" + 20 + "&pageNum=" + pageNum);
        Http.getAsync(HttpBase.GET_VIDEO_CONSULT + "?pageSize=" + 20 + "&pageNum=" + pageNum, callback);
    }


    /**
     * 创建会诊
     *
     * @param callback
     * @param confirmDate
     * @param startDate
     * @param userId
     * @param doctorIds
     */
    public static void putCreateGroupAv(final HttpCallback callback, String confirmDate, String startDate, String userId,
                                        List<DoctorBean> doctorIds, Activity activity) {
        final String url = HttpBase.CREATE_GROUP_AV;
        final String strBody = "confirmDate=" + confirmDate + "&startDate=" + startDate + "&userId=" + userId +
                getDoctorIds(doctorIds) + HttpBase.PUT_METHOD;
        Log.i("tag00", "会诊：" + strBody);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, strBody);
            }
        });
    }

    public static String getDoctorIds(List<DoctorBean> doctorIds) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < doctorIds.size(); index++) {
            builder.append("&doctorIds=" + doctorIds.get(index).getId());
        }
        return builder.toString();
    }


    /**
     * 完成图文咨询
     *
     * @param callback
     * @param orderId
     * @param exhort
     * @param content
     * @param sex
     * @param age
     * @param name
     */
    public static void completePictureConsult(final HttpCallback callback, String orderId, String exhort, String content,
                                              String sex, String age, String name, Activity activity) {
        final String url = HttpBase.COMPLETE_PICTURE_CONSULT;
        final String bodyStr = "orderId=" + orderId + "&name=" + name + "&sex=" + sex + "&age=" + age + "&exhort=" + exhort
                + "&content=" + content;
        Log.i("tag00", "完成图文咨询：" + bodyStr);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 完成坐诊咨询
     *
     * @param callback
     * @param orderId
     * @param exhort
     * @param content
     * @param sex
     * @param age
     * @param name
     */
    public static void completeSitVideoConsult(final HttpCallback callback, String orderId, String exhort, String content,
                                               String sex, String age, String name, Activity activity) {
        final String url = HttpBase.COMPLETE_SIT_VIDEO_CONSULT;
        final String bodyStr = "orderId=" + orderId + "&name=" + name + "&sex=" + sex + "&age=" + age + "&exhort=" + exhort
                + "&content=" + content;
        Log.i("tag00", "完成坐诊咨询：" + bodyStr);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 完成会诊咨询
     *
     * @param callback
     * @param orderId
     * @param exhort
     * @param content
     * @param sex
     * @param age
     * @param name
     */
    public static void completeVideoGroupConsult(final HttpCallback callback, String orderId, String exhort, String
            content, String sex, String age, String name, Activity activity) {
        final String url = HttpBase.COMPLETE_VIDEO_GROUP_CONSULT;
        final String bodyStr = "orderId=" + orderId + "&name=" + name + "&sex=" + sex + "&age=" + age + "&exhort=" + exhort
                + "&content=" + content;
        Log.i("tag00","完成会诊："+bodyStr);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 获取完成图文咨询界面所需数据
     *
     * @param callback
     * @param ordercode
     */
    public static void medicalReport(HttpCallback callback, String ordercode) {
        String url = HttpBase.MEDICAL_REPORT;
        String bodyStr = url + "?code=" + ordercode;
        Log.i("tag00", "获取完成图文咨询界面所需数据:" + bodyStr);
        Http.getAsync(bodyStr, callback);
    }

    /**
     * 获取详细的资金变动
     *
     * @param callback
     * @param account
     */
    public static void getAccountFlow(HttpCallback callback, String account) {
        String url = String.format(HttpBase.ACCOUNT_FLOW, account);
        String urlStr = url + "?pageSize=" + Integer.MAX_VALUE;
        Http.getAsync(urlStr, callback);
    }

    /**
     * 搜索患者
     *
     * @param callback
     * @param userName
     */
    public static void getPatientByUserName(HttpCallback callback, String userName) {
        String url = HttpBase.GET_PATIENT_BY_USERNAME + "?userName=" + userName;
        Http.getAsync(url, callback);
    }

    /**
     * 判断患者是否可拨打电话
     *
     * @param callback
     * @param userId
     */
    public static void isCallableUser(final HttpCallback callback, int userId, int orderId, Activity activity) {
        final String url = HttpBase.CALLABLE_USER;
        final String bodyStr = "userId=" + userId + "&orderId=" + orderId + "&pageSize=" + Integer.MAX_VALUE;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 设置患者拨打电话次数
     *
     * @param callback
     * @param userId
     */
    public static void setCallTimes(HttpCallback callback, String userId, String orderId) {
        String url = HttpBase.SET_CALL_TIMES;
        String bodyStr = "userId=" + userId + "&orderId=" + orderId + "&pageSize=" + Integer.MAX_VALUE;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 查询是否可以进入视频会诊接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderId  订单ID
     * @param callback 结果回调
     */
    public static void queryEnterrRoom(long orderId, final HttpCallback callback, Activity activity) {
        final String url = HttpBase.QUERY_ENTER_ROOM_URL;
        final StrParam bodyStr = new StrParam("orderId", orderId);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.getAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 同意会诊邀请
     *
     * @param callback
     * @param groupSitDiagnoseDetailId
     */
    public static void agreeGroupDiagnose(final HttpCallback callback, String groupSitDiagnoseDetailId, Activity activity) {
        final String url = HttpBase.AGREE_GROUP_DIANOSE;
        final String bodyStr = "subOrderId=" + groupSitDiagnoseDetailId;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 拒绝会诊邀请
     *
     * @param callback
     * @param groupSitDiagnoseDetailId
     */
    public static void disAgreeGroupDiagnose(final HttpCallback callback, String groupSitDiagnoseDetailId, Activity activity) {
        final String url = HttpBase.DIS_AGREE_GROUP_DIANOSE;
        final String bodyStr = "subOrderId=" + groupSitDiagnoseDetailId;
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.postAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 设置消息为已读
     *
     * @param callback
     * @param messageId
     */
    public static void markMessageReded(HttpCallback callback, String messageId) {
        String bodyStr = "messageId=" + messageId;
        Http.postAsync(HttpBase.MARK_READ_MESSAGE, callback, bodyStr);

    }

    /**
     * 首页的最早的视频咨询
     *
     * @param callback
     */
    public static void earliestVideo(HttpCallback callback) {
        Http.getAsync(HttpBase.EARLIEST_VIDEO, callback);
    }

    /**
     * 医生首页红点
     *
     * @param callback
     */
    public static void redDot(HttpCallback callback) {
        Http.getAsync(HttpBase.RED_DOT, callback);
    }

    /**
     * 设置是否能主动完成
     *
     * @param callback
     * @param orderId
     */
    public static void setCanfinishVideo(HttpCallback callback, String orderId) {
        String url = HttpBase.SET_CANFINISH_VIDEO;
        String bodyStr = "orderId=" + orderId;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 获取平台扣钱
     *
     * @param callback
     */
    public static void getMoneyDelay(HttpCallback callback) {
        Http.getAsync(HttpBase.INCOME_MONEY, callback);
    }

    /**
     * 获取帮助
     */
    public static void getHelp(HttpCallback callback) {
        String url = HttpBase.HELP + "?type=DOCTOR&pageSize=" + Integer.MAX_VALUE;
        Http.getAsync(url, callback);
    }

    /**
     * 获取结算该账户总金额
     *
     * @param callback
     */
    public static void getMonthClosingMoney(HttpCallback callback) {
        String url = HttpBase.CLOSING_MONTH_MONEY;
        Http.getAsync(url, callback);
    }

    /**
     * 获取账户金额列表
     *
     * @param callback
     */
    public static void getMonthsMoney(HttpCallback callback) {
        Http.getAsync(HttpBase.CLOSING_MONTH_MONEYS, callback);
    }


    /**
     * 获取短信验证码接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param phoneNum 电话号码
     * @param callback 结果回调
     */
    public static void getShortMsgCode(String phoneNum, final HttpCallback callback, Activity activity) {
        final String url = HttpBase.GET_SHORTMSG_CODE_URL;
        final StrParam bodyStr = new StrParam("userName", phoneNum);
        checkNetwork(activity, new NetworkStatus() {
            @Override
            public void connected() {
                Http.getAsync(url, callback, bodyStr);
            }
        });
    }

    /**
     * 校验短信验证码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param code     验证码
     * @param callback 结果回调
     */
    public static void veryfyMsgCode(String code, HttpCallback callback) {
        String url = HttpBase.VERYFY_MSG_CODE_URL;
        StrParam bodyStr = new StrParam("code", code);
        Http.postAsync(url, callback, bodyStr);
    }


    /**
     * 查询APP下载地址接口
     * <p>
     * 返回数据对应为ListPageResponse<DownloadAddress>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryDownloadUrls(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_UPDATE_APP_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询APP是否有新版本接口
     * <p>
     * 返回数据对应为ObjectResponse<PatientClient>
     *
     * @param callback 结果回调
     */
    public static void queryVersion(HttpCallback callback) {
        String url = HttpBase.QUERY_UPDATE_VERSION_URL;
        Http.getAsync(url, callback);
    }
    /**
     * 查询配置信息接口
     * <p>
     * 返回数据对应为ObjectResponse<PropertyDto >
     *
     * @param callback 结果回调
     */
    public static void queryProperty(HttpCallback callback) {
        String url = HttpBase.QUERY_PROPERTY_URL;
        Http.getAsync(url, callback);
    }
}
