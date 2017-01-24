package com.shkjs.doctor.http;

/**
 * Created by xiaohu on 2016/8/15.
 */

public class HttpBase {
    /**
     * 服务器地址
     */
    //      public static final String BASE_URL = "http://192.168.2.90:8080/frontWeb/";
    //      public static final String BASE_URL = "http://116.62.7.65:8080/frontWeb/";
    //      public static final String BASE_URL = "http://server.120yxh.com:8080/frontWeb/";
    public static final String BASE_URL = "https://server.120yxh.com:8443/frontWeb/";


    public static final String PUT_METHOD = "&_method=put";
    public static final int SUCCESS = 200;

    /**
     * 注册接口地址
     */
    public static final String REGISTER_URL = BASE_URL + "index/register";
    /**
     * 是否可以注册接口地址
     */
    public static final String CAN_REGISTER_URL = BASE_URL + "index/canRegister";
    /**
     * 找回密码接口地址
     */
    public static final String FINDPASSWORD_URL = BASE_URL + "index/findPassword";
    /**
     * 登录接口地址
     */
    public static final String LOGIN_URL = BASE_URL + "index/login";
    /**
     * 登出接口地址
     */
    public static final String LOGOUT_URL = BASE_URL + "system/logout";
    /**
     * 帮助信息接口地址
     */
    public static final String QUERYHELP_URL = BASE_URL + "pushMessage/messageDoctor";


    /**
     * 获取用户信息接口地址
     */
    public static final String DETAIL = BASE_URL + "doctor/detailWithString";

    /**
     * 上传认证资料
     */
    public static final String AUTHENTICATION = BASE_URL + "doctor/authenticationWithString";

    /**
     * 单文件上传接口
     */
    public static final String UPLOADONEFILE = BASE_URL + "file/doctorFileUpload";

    /**
     * 图片加载地址
     */
    public static final String IMGURL = BASE_URL + "ossfile/";

    /**
     * 设置坐诊时间
     */
    public static final String VISIT_TIME_SETTINGS = BASE_URL + "sitDiagnose/setting";

    /**
     * 获取坐诊时间设置
     */
    public static final String GET_VISIT_TIME_SETTINGS = BASE_URL + "sitDiagnose/getting";

    /**
     * 获取我的钱包详细信息
     */
    public static final String GET_ACCOUNT_DETAIL = BASE_URL + "account/detail";

    /**
     * 获取账户绑定的支付宝、微信
     */
    public static final String GET_BANDCARD = BASE_URL + "account/getBindCard";

    /**
     * 绑定银行卡 支付宝
     */
    public static final String UPLOAD_BANDCARD = BASE_URL + "account/bind";

    /**
     * 获取应用的枚举变量
     */
    public static final String OPTION = BASE_URL + "option/query";

    /**
     * 获取科室信息
     */
    public static final String SEARCH = BASE_URL + "medicalCategory/detail";

    /**
     * 获取我的病人信息
     */
    public static final String MYPATIENT = BASE_URL + "doctor/patientList";

    /**
     * 修改用户密码
     */
    public static final String MODEIFYPW = BASE_URL + "userInfo/modifyPwd";

    /**
     * 获取历史订单
     */
    public static final String HISTORY_ORDER = BASE_URL + "order/doctorOrderList";
    /**
     * 获取完成历史订单
     */
    public static final String HISTORY_ORDER_FINISH = BASE_URL + "order/doctorFinishOrderList";


    /**
     * 取消未完成订单
     */
    public static final String CANCEL_ORDER = BASE_URL + "order/doctorCancel";


    /**
     * 添加患者至我的患者
     */
    public static final String ADD_MYPATIENTS = BASE_URL + "userInfo/addRelation";

    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST_USERID_AND_TYPE_URL = BASE_URL + "healthReport/%s/report";
    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST = BASE_URL + "healthReport/listSelfGenerateByDoctor";

    /**
     * 解除我的患者
     */
    public static final String DELETE_MYPATIENTS = BASE_URL + "userInfo/removeRelation";


    /**
     * 通过用户id直接拿到用户名
     */
    public static final String GET_USERNAME_BYID = BASE_URL + "userInfo/queryNameById";

    /**
     * 获取坐诊时间
     */
    public static final String GET_DIGNOSE_TIME = BASE_URL + "sitDiagnose/query";

    /**
     * 通过
     */
    public static final String GET_DOCTOR_FROM_NAME = BASE_URL + "doctor/infoByUserNameWithString";

    /**
     * 获取未完成图文
     */
    public static final String GET_PICTURE_CONSULT = BASE_URL + "messdiagnose/messDoctorInquiryReserve";

    /**
     * 获取会诊信息
     */
    public static final String GET_VIDEO_GROUP_CONSULT = BASE_URL + "messdiagnose/messDoctorGroupSitDiagnoseReserve";

    /**
     * 获取坐诊信息
     */
    public static final String GET_VIDEO_CONSULT = BASE_URL + "messdiagnose/messDoctorSitDiagnoseReserve";

    /**
     * 创建会诊
     */
    public static final String CREATE_GROUP_AV = BASE_URL + "groupSitDiagnose/add";

    /**
     * 完成图文资讯
     */
    public static final String COMPLETE_PICTURE_CONSULT = BASE_URL + "inquiryReserve/completeDiagnoseCaseByOrderId";
    /**
     * 完成坐诊咨询
     */
    public static final String COMPLETE_SIT_VIDEO_CONSULT = BASE_URL +
            "sitDiagnoseReserve/completeDiagnoseCaseByOrderId";

    /**
     * 完成会诊咨询
     */
    public static final String COMPLETE_VIDEO_GROUP_CONSULT = BASE_URL +
            "groupSitDiagnoseDetail/completeDiagnoseCaseByOrderId";

    /**
     * 完成图文咨询所需数据
     */
    public static final String MEDICAL_REPORT = BASE_URL + "healthReport/queryHealthReportByOrderCode";
    /**
     * 资金流动
     */
    public static final String ACCOUNT_FLOW = BASE_URL + "account/%s/doctorFlowDetail";

    /**
     * 创建会诊搜索患者
     */
    public static final String GET_PATIENT_BY_USERNAME = BASE_URL + "userInfo/queryUserInfoByUserName";
    /**
     * 上传个推cid接口地址
     */
    public static final String UPLOAD_GETUI_CID_URL = BASE_URL + "thirdRegister/getui";

    /**
     * 判断患者是否可以拨打视频电话
     */
    public static final String CALLABLE_USER = BASE_URL + "messdiagnose/callable";

    /**
     * 设置患者拨打视频的次数
     */
    public static final String SET_CALL_TIMES = BASE_URL + "messdiagnose/setVideoDiagnoseTimes";
    /**
     * 查询是否可以进入视频会诊接口地址
     */
    public static final String QUERY_ENTER_ROOM_URL = BASE_URL + "groupSitDiagnose/enterRoom";

    /**
     * 同意会诊的邀请
     */
    public static final String AGREE_GROUP_DIANOSE = BASE_URL + "groupSitDiagnoseDetail/agreeBySubOrderId";
    /**
     * 拒绝会诊的邀请
     */
    public static final String DIS_AGREE_GROUP_DIANOSE = BASE_URL + "groupSitDiagnoseDetail/disAgreeBySubOrderId";
    /**
     * 设置消息为已读
     */
    public static final String MARK_READ_MESSAGE = BASE_URL + "pushMessage/markRead";

    /**
     * 主页的最早的视频
     */
    public static final String EARLIEST_VIDEO = BASE_URL + "messdiagnose/earliestVideo";
    /**
     * 首页红点
     */
    public static final String RED_DOT = BASE_URL + "messdiagnose/doctorQueryTheRedDot";
    /**
     * 设置是否能主动完成视频咨询和会诊
     */
    public static final String SET_CANFINISH_VIDEO = BASE_URL + "messdiagnose/setDoctorCanFinishFlag";
    /**
     * 平台扣钱
     */
    public static final String INCOME_MONEY = BASE_URL + "income/show";
    /**
     * 帮助文档
     */
    public static final String HELP = BASE_URL + "system/queryHelp";
    /**
     * 获得结算总金额
     */
    public static final String CLOSING_MONTH_MONEY = BASE_URL + "monthClosing/closingMoney";
    /**
     * 获得结算总金额
     */
    public static final String CLOSING_MONTH_MONEYS = BASE_URL + "monthClosing/listUserClosingMoney";


    /**
     * 获取短信验证码地址
     */
    public static final String GET_SHORTMSG_CODE_URL = BASE_URL + "index/getShortMsgCode";
    /**
     * 校验短信验证码地址
     */
    public static final String VERYFY_MSG_CODE_URL = BASE_URL + "account/veryfyMsgCode";

    /**
     * 查询APP下载地址地址
     */
    public static final String QUERY_UPDATE_APP_URL = BASE_URL + "downloadAddress/list";
    /**
     * 查询APP更新版本地址
     */
    public static final String QUERY_UPDATE_VERSION_URL = BASE_URL + "index/queryVersion";
    /**
     * 查询配置信息地址
     */
    public static final String QUERY_PROPERTY_URL = BASE_URL + "property/list";
}
