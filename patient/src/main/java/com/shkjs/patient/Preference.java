package com.shkjs.patient;

import android.os.Environment;

/**
 * Created by xiaohu on 2016/8/15.
 * <p>
 * 常量配置
 */

public class Preference {

    //    public static final String BUGLY_APPID = "210bf4247b";//自己建的测试应用
    public static final String BUGLY_APPID = "2c7c38c487";//正式应用

    /**
     * 健康报告语音缓存路径
     */
    public static String REPORT_AUDIO_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/com.shkjs" +
            ".patient/cache/";

    /**
     * 用户id
     */
    public static final String USER_ID = "userId";

    /**
     * 用户头像名称
     */
    public static final String USER_ICON = "usericon.jpg";
    /**
     * 旧密码
     */
    public static final String OLD_PWD = "oldPwd";
    /**
     * 新密码
     */
    public static final String NEW_PWD = "newPwd";

    /**
     * 更新界面
     */
    public static final String UPDATE_VIEW_ACTION = "update_view_action";

    /**
     * 是否注册之后登录
     */
    public static final String IS_REGISTER = "isRegister";
    /**
     * APP版本号
     */
    public static final String VERSION_CODE = "versionCode";
    /**
     * 分页第几页
     */
    public static final String PAGE_NUM = "pageNum";
    /**
     * 分页每页数据条数
     */
    public static final String PAGE_SIZE = "pageSize";
    /**
     * 用户ID
     */
    public static final String ID = "id";
    /**
     * 搜索医生
     */
    public static final String SEARCH_MSG = "searchMsg";
    /**
     * 搜索医生
     */
    public static final String HOSPITAL_NAME = "hospitalName";
    /**
     * 搜索医生
     */
    public static final String CATEGORY_NAME = "categoryName";

    /**
     * 医生详情
     */
    public static final String DOCTOR_DETAIL = "doctor_detail";
    /**
     * 医生ID
     */
    public static final String DOCTOR_ID = "doctorId";
    /**
     * 关联用户ID
     */
    public static final String RELATION_USER_ID = "relationUserId";
    /**
     * 昵称
     */
    public static final String NICK_NAME = "nickName";
    /**
     * 用户基本信息
     */
    public static final String USER_INFO = "userInfo";
    /**
     * 用户的信息保密性
     */
    public static final String INFORMATION_CONFIDENTIAL = "informationConfidential";
    /**
     * 用户头像地址
     */
    public static final String USER_ICON_IMG = "img";
    /**
     * 消息提示
     */
    public static final String NOTIFICATION_SWITCH = "notificationSwitch";
    /**
     * type
     */
    public static final String TYPE = "type";
    /**
     * 关系类型
     */
    public static final String RELATION_TYPE = "RelationType";
    /**
     * 健康报告
     */
    public static final String HEALTH_REPORTS_WITH_BLOBS = "HealthReportsWithBLOBs";
    /**
     * 健康报告类型
     */
    public static final String HEALTH_REPORTS_WITH_BLOBS_TYPE = "hrwb";
    /**
     * 想添加的用户的id（用户组）
     */
    public static final String USER_GROUP_MEMBER = "userGroupMember";
    /**
     * 付款人ID
     */
    public static final String PAYER_ID = "payerId";
    /**
     * 收款人ID
     */
    public static final String RECEIVER_ID = "receiverId";
    /**
     * 多少钱
     */
    public static final String MONEY = "money";
    /**
     * 订单类型
     */
    public static final String PAY_TYPE = "payType";
    /**
     * 支付来源
     */
    public static final String PAY_ORIGIN = "payOrigin";
    /**
     * 订单参数
     */
    public static final String ORDER = "order";
    /**
     * 订单code
     */
    public static final String CODE = "code";
    /**
     * 订单code
     */
    public static final String ORDER_CODE = "orderCode";
    /**
     * 订单code
     */
    public static final String REFUND_ORDER_CODE = "refundOrderCode";
    /**
     * 收款方式card
     */
    public static final String BANK_CARD = "bankCard";
    /**
     * 请求家庭组id
     */
    public static final String USER_GROUP_MEMBER_ID = "userGroupMemberId";
    /**
     * 日期信息转换为1970年经过的毫秒信息
     */
    public static final String DATE_MSG = "dateMsg";
    /**
     * 坐诊时间ID
     */
    public static final String SIT_DIAGNOSE_ID = "sitDiagnoseId";
    /**
     * 枚举名字
     */
    public static final String TYPE_NAME = "typeName";
    /**
     * 个推cid
     */
    public static final String CID = "cid";
    /**
     * uuid
     */
    public static final String UUID = "uuid";
    /**
     * position
     */
    public static final String POSITION = "position";
    /**
     * result
     */
    public static final String RESULT = "result";
    /**
     * sign_type
     */
    public static final String SIGN_TYPE = "sign_type";
    /**
     * sign_type
     */
    public static final String SIGNTYPE = "signType";
    /**
     * sign
     */
    public static final String SIGN = "sign";
    /**
     * content
     */
    public static final String CONTENT = "content";
    /**
     * alipay_trade_app_pay_response
     */
    public static final String ALIPAY_TRADE_APP_PAY_RESPONSE = "alipay_trade_app_pay_response";
    /**
     * data
     */
    public static final String DATA = "data";
    /**
     * extra
     */
    public static final String EXTRA = "extra";
    /**
     * messageId
     */
    public static final String MESSAGE_ID = "messageId";
    /**
     * messageIds
     */
    public static final String MESSAGE_IDS = "messageIds";
    /**
     * recode
     */
    public static final String RECODE = "recode";
    /**
     * hospitalId
     */
    public static final String HOSPITAL_ID = "hospitalId";

    /**
     * unReadNum
     */
    public static final String UN_READ_NUM = "unReadNum";
}
