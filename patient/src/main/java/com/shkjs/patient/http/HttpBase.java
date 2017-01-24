package com.shkjs.patient.http;

/**
 * Created by xiaohu on 2016/8/15.
 */

public class HttpBase {
    /**
     * 服务器地址
     */
    public static final String BASE_URL = "http://192.168.2.101:8080/frontWeb/";
    //    public static final String BASE_URL = "https://server.120yxh.com:8443/frontWeb/";

    public static final String PUT_METHOD = "&_method=put";
    public static final int SUCCESS = 200;

    public static final String BASE_OSS_URL = BASE_URL + "ossfile/";

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
    public static final String FIND_PASSWORD_URL = BASE_URL + "index/findPassword";
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
    public static final String QUERY_HELP_URL = BASE_URL + "system/queryHelp";


    /**
     * 比对通讯录接口地址
     */
    public static final String ADD_RESSLIST_URL = BASE_URL + "userInfo/addressList";
    /**
     * 修改用户基本信息接口地址
     */
    public static final String MODIFICATION_URL = BASE_URL + "userInfo/modification";
    /**
     * 修改当前用户的信息保密性接口地址
     */
    public static final String MODIFICATION_CONFIDENTIAL_URL = BASE_URL + "userInfo/modificationConfidential";
    /**
     * 修改用户头像接口地址
     */
    public static final String MODIFICATION_PORTRAIT_URL = BASE_URL + "userInfo/modificationPortrait";
    /**
     * 修改消息提示接口地址
     */
    public static final String MODIFY_NOTIFICATION_SWITCH_URL = BASE_URL + "userInfo/modifyNotificationSwitch";
    /**
     * 获取病历列表接口地址
     */
    public static final String PATIENTCASE_URL = BASE_URL + "userInfo/%s/patientCase";
    /**
     * 获取用户信息接口地址
     */
    public static final String DETAIL_URL = BASE_URL + "userInfo/%s/detail";
    /**
     * 获取医院列表接口地址
     */
    public static final String HOSPITAL_URL = BASE_URL + "hospital/listHospital";
    /**
     * 获取免费医生医院列表接口地址
     */
    public static final String HOSPITAL_FREE_URL = BASE_URL + "hospital/listFeeHospital";
    /**
     * 获取本地医生医院列表接口地址
     */
    public static final String HOSPITAL_LOCAL_URL = BASE_URL + "hospital/listPromotionHospital";
    /**
     * 获取科室列表接口地址
     */
    public static final String DEPARTMENT_URL = BASE_URL + "medicalCategory/detail";
    /**
     * 获取免费医生科室列表接口地址
     */
    public static final String DEPARTMENT_FREE_URL = BASE_URL + "medicalCategory/listFeeMedicalCategory";
    /**
     * 获取本地医生科室列表接口地址
     */
    public static final String DEPARTMENT_LOCAL_URL = BASE_URL + "medicalCategory/listPromotionMedicalCategory";
    /**
     * 获取推荐医生列表接口地址
     */
    public static final String RECOMMEND_DOCTOR_URL = BASE_URL + "doctor/recommend";
    /**
     * 获取我的医生列表接口地址
     */
    public static final String DOCTOR_URL = BASE_URL + "doctor/listDoctor";
    /**
     * 搜索医生列表接口地址
     */
    public static final String SEARCH_DOCTOR_URL = BASE_URL + "doctor/search";
    /**
     * 通过科室搜索医生列表接口地址
     */
    public static final String SEARCH_DOCTOR_BY_CATEGORY_NAME_URL = BASE_URL + "doctor/%s/categoryByName";
    /**
     * 搜索免费医生列表接口地址
     */
    public static final String SEARCH_FREE_DOCTOR_URL = BASE_URL + "doctor/listDoctorByHctFree";
    /**
     * 搜索本地医生列表接口地址
     */
    public static final String SEARCH_LOCAL_DOCTOR_URL = BASE_URL + "doctor/listDoctorByHctPromotion";
    /**
     * 通过帐号查询医生信息接口地址
     */
    public static final String QUERY_DOCTOR_DETAIL_BY_USERNAME_URL = BASE_URL + "doctor/infoByUserNameWithString";
    /**
     * 用户详情接口地址
     */
    public static final String USER_DETAIL_URL = BASE_URL + "userInfo/detail";
    /**
     * 医生详情接口地址
     */
    public static final String DOCTOR_DETAIL_URL = BASE_URL + "userInfo/%s/doctorInfo";
    /**
     * 获取广告接口地址
     */
    public static final String AD_URL = BASE_URL + "advertisement/listAdvertisement";
    /**
     * 修改用户密码接口地址
     */
    public static final String MODIFY_PWD_URL = BASE_URL + "userInfo/modifyPwd";
    /**
     * 用户文件上传接口地址
     */
    public static final String USER_FILE_UPLOAD_URL = BASE_URL + "file/userFileUpload";
    /**
     * 用户多文件文件上传接口地址
     */
    public static final String USER_MULTI_FILE_UPLOAD_URL = BASE_URL + "file/userMultiFileUpload";
    /**
     * 添加用户关系接口地址
     */
    public static final String ADD_RELATION_URL = BASE_URL + "userInfo/addRelation";
    /**
     * 编辑用户关系接口地址
     */
    public static final String EDIT_RELATION_URL = BASE_URL + "userInfo/editRelation?type=%s";
    /**
     * 查询用户关系接口地址
     */
    public static final String QUERY_RELATION_URL = BASE_URL + "userInfo/queryRelation";
    /**
     * 查询和当前用户有某种关系的用户列表接口地址
     */
    public static final String QUERY_RELATION_LIST_URL = BASE_URL + "userInfo/queryRelationList";
    /**
     * 查询和当前用户有某种关系的用户列表接口地址
     */
    public static final String REMOVE_RELATION_URL = BASE_URL + "userInfo/removeRelation";
    /**
     * 获取用户头像接口地址
     */
    public static final String GET_HEADIMG_URL = BASE_URL + "userInfo/headImg";
    /**
     * 查询用户网易账号接口地址
     */
    public static final String QUERY_NETEASE_USERNAME_BY_ID_URL = BASE_URL + "userInfo/queryNeteaseUserNameById";
    /**
     * 获取账户详情接口地址
     */
    public static final String ACCOUNT_DETAIL_URL = BASE_URL + "account/detail";
    /**
     * 资金变动流水接口地址
     */
    public static final String ACCOUNT_FLOWDETAIL_URL = BASE_URL + "account/%s/userFlowDetail";
    /**
     * 是否设置过支付密码接口地址
     */
    public static final String ACCOUNT_PWD_ISEXIST_URL = BASE_URL + "account/passwordIsExist";
    /**
     * 设置支付密码接口地址
     */
    public static final String ACCOUNT_INIT_PWD_URL = BASE_URL + "account/initPassword";
    /**
     * 校验支付密码接口地址
     */
    public static final String ACCOUNT_CHECK_PWD_URL = BASE_URL + "account/checkPassword";
    /**
     * 修改支付密码接口地址
     */
    public static final String ACCOUNT_MODIFY_PWD_URL = BASE_URL + "account/changePassword";
    /**
     * 找回支付密码接口地址
     */
    public static final String ACCOUNT_RETRIEVE_PWD_URL = BASE_URL + "account/findPassword";
    /**
     * 查询密保问题列表接口地址
     */
    public static final String QUERY_SECURITY_QUESTION_LIST_URL = BASE_URL + "securityQuestion/list";
    /**
     * 设置密保接口地址
     */
    public static final String ADD_SECURITY_QUESTION_URL = BASE_URL + "accountSecurityQuestion/add";
    /**
     * 查询自己设置的密保问题接口地址
     */
    public static final String QUERY_SECURITY_QUESTION_URL = BASE_URL + "accountSecurityQuestion/query";
    /**
     * 查询自己是否设置过密保问题接口地址
     */
    public static final String QUERY_SECURITY_QUESTION_ISEXIST_URL = BASE_URL + "accountSecurityQuestion/queryIsExist";
    /**
     * 校验密保接口地址
     */
    public static final String CHECK_SECURITY_QUESTION_URL = BASE_URL + "accountSecurityQuestion/check";
    /**
     * 新增报告接口地址
     */
    public static final String ADD_REPORT_URL = BASE_URL + "healthReport/add";
    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST_URL = BASE_URL + "healthReport/report";
    /**
     * 通过订单号查询健康报告ID接口地址
     */
    public static final String QUERY_HEALTHREPORTID_BY_ORDERCODE_URL = BASE_URL +
            "healthReport/queryHealthReportIdByOrderCode";
    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST_TYPE_URL = BASE_URL + "healthReport/%/category/report";
    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST_USERID_URL = BASE_URL + "healthReport/%s/report";
    /**
     * 获取健康报告列表接口地址
     */
    public static final String GET_REPORT_LIST_USERID_AND_TYPE_URL = BASE_URL + "healthReport/%s/%s/report";
    /**
     * 更新健康报告接口地址
     */
    public static final String UPDATE_REPORT_URL = BASE_URL + "healthReport/updateReport";
    /**
     * 删除健康报告接口地址
     */
    public static final String DELETE_REPORT_URL = BASE_URL + "healthReport/%s/delReport";
    /**
     * 标记健康档案为已读接口地址
     */
    public static final String MAKE_SINGLE_READ_URL = BASE_URL + "healthReport/makeSingleRead";
    /**
     * 标记健康档案为已读接口地址
     */
    public static final String MAKE_ALL_READ_URL = BASE_URL + "healthReport/makeAllRead";
    /**
     * 添加用户组成员接口地址
     */
    public static final String ADD_MEMBER_URL = BASE_URL + "userGroup/addMember";
    /**
     * 添加用户组成员接口地址
     */
    public static final String ADD_MEMBER_BY_USER_NAME_URL = BASE_URL + "userGroup/addMemberByUserName";
    /**
     * 查询是否是家庭组户主接口地址
     */
    public static final String QUERY_IS_GROUP_HOLDER_URL = BASE_URL + "userGroup/isHouseholder";
    /**
     * 查询家庭组户主接口地址
     */
    public static final String QUERY_GROUP_HOLDER_URL = BASE_URL + "userGroup/queryHouseholder";
    /**
     * 列出用户组成员接口地址
     */
    public static final String QUERY_GROUP_MEMBER_URL = BASE_URL + "userGroup/listMember";
    /**
     * 查询家庭组信息接口地址
     */
    public static final String QUERY_GROUP_INFO_URL = BASE_URL + "userGroup/listMemberOrHouseholder";
    /**
     * 得到当前用户所在的用户组接口地址
     */
    public static final String QUERY_USER_GROUP_URL = BASE_URL + "userGroup/queryUserGroup";
    /**
     * 退出用户组接口地址
     */
    public static final String QUIT_USER_GROUP_URL = BASE_URL + "userGroup/quit";
    /**
     * 刪除用户组接口地址
     */
    public static final String REMOVE_USER_GROUP_URL = BASE_URL + "userGroup/remove";
    /**
     * 刪除用户组成员接口地址
     */
    public static final String REMOVE_MEMBER_USER_GROUP_URL = BASE_URL + "userGroup/removeMember";
    /**
     * 新增订单接口地址
     */
    public static final String ADD_ORDER_URL = BASE_URL + "order/add";
    /**
     * 取消订单接口地址
     */
    public static final String CANCEL_ORDER_URL = BASE_URL + "order/userCancel";
    /**
     * 删除订单接口地址
     */
    public static final String DELETE_ORDER_BY_ID_URL = BASE_URL + "order/delById";
    /**
     * 删除订单接口地址
     */
    public static final String DELETE_ORDER_BY_CODE_URL = BASE_URL + "order/delByCode";
    /**
     * 完成订单接口地址
     */
    public static final String COMPLETE_ORDER_URL = BASE_URL + "order/completeOrder";
    /**
     * 订单接口地址
     */
    public static final String QUERY_USER_ORDER_URL = BASE_URL + "order/userOrderList";
    /**
     * 消费订单接口地址
     */
    public static final String EXPENSE_ORDER_URL = BASE_URL + "order/expenseOrderList";
    /**
     * 收入订单接口地址
     */
    public static final String INCOME_ORDER_URL = BASE_URL + "order/incomeOrderList";
    /**
     * 通过code查询订单接口地址
     */
    public static final String QUERY_ORDER_BY_CODE_URL = BASE_URL + "order/queryByCode";
    /**
     * 通过ID查询订单接口地址
     */
    public static final String QUERY_ORDER_BY_ID_URL = BASE_URL + "order/queryById";
    /**
     * 通过code查询子订单接口地址
     */
    public static final String QUERY_ORDER_BY_PCODE_URL = BASE_URL + "order/queryByPCode";
    /**
     * 通过ID查询子订单接口地址
     */
    public static final String QUERY_ORDER_BY_PID_URL = BASE_URL + "order/queryByPId";
    /**
     * 通过ID查询订单签名接口地址
     */
    public static final String QUERY_ORDER_SIGN_BY_ID_URL = BASE_URL + "order/querySignById";
    /**
     * 通过Code查询订单签名接口地址
     */
    public static final String QUERY_ORDER_SIGN_BY_CODE_URL = BASE_URL + "order/querySignByCode";
    /**
     * 通过Code查询退款金额接口地址
     */
    public static final String QUERY_REFUND_ORDER_MONEY_BY_CODE_URL = BASE_URL + "order/calculateUserRefundMony";
    /**
     * 通过Code查询退款订单接口地址
     */
    public static final String QUERY_REFUND_ORDER_BY_CODE_URL = BASE_URL + "refundOrder/queryByOrderCode";
    /**
     * 平台支付接口地址
     */
    public static final String PAY_BY_ACCOUNT_URL = BASE_URL + "payment/payByAccount";
    /**
     * 退款接口地址
     */
    public static final String REFUND_URL = BASE_URL + "payment/refund";
    /**
     * 支付宝支付成功通知服务器地址
     */
    public static final String ALIPAY_SYNC_CALLBACK_URL = BASE_URL + "alipay/aliSyncCallback";
    /**
     * 绑定银行卡，支付宝、微信等收款接口地址
     */
    public static final String BIND_ACCOUNT_URL = BASE_URL + "account/bind";
    /**
     * 解绑银行卡，支付宝、微信等收款接口地址
     */
    public static final String UNBIND_ACCOUNT_URL = BASE_URL + "account/unbind";
    /**
     * 获取绑定的银行卡、支付宝、微信等收款账户接口地址
     */
    public static final String QUERY_BIND_CARD_URL = BASE_URL + "account/getBindCard";
    /**
     * 获取家庭组账户信息详情接口地址
     */
    public static final String QUERY_GROUP_MASTER_DETAIL_URL = BASE_URL + "account/groupMasterDetail";
    /**
     * 充值接口地址
     */
    public static final String RECHARG_URL = BASE_URL + "account/recharge";
    /**
     * 同意加入接口地址
     */
    public static final String AGREEMENT_JOIN_URL = BASE_URL + "userGroupMember/agreementJoin";
    /**
     * 不同意加入接口地址
     */
    public static final String DISAGREE_JOIN_URL = BASE_URL + "userGroupMember/disagreeJoin";
    /**
     * 查询医生坐诊时间接口地址
     */
    public static final String QUERY_DOCTOR_TIME_URL = BASE_URL + "sitDiagnose/%s/getting";
    /**
     * 预约视频就诊接口地址
     */
    public static final String ORDER_VIDEO_URL = BASE_URL + "sitDiagnoseReserve/add";
    /**
     * 查询是否可以进入视频会诊接口地址
     */
    public static final String QUERY_ENTER_ROOM_URL = BASE_URL + "groupSitDiagnose/enterRoom";
    /**
     * 查询是否可以进入视频会诊接口地址
     */
    public static final String QUERY_ENTER_ROOM_AND_DOCTOR_IDS_URL = BASE_URL +
            "groupSitDiagnose/enterRoomAndDoctorIds";
    /**
     * 预约图文咨询接口地址
     */
    public static final String ORDER_PICTURE_URL = BASE_URL + "inquiryReserve/add";
    /**
     * 查询单个图文咨询接口地址
     */
    public static final String QUERY_PICTURE_BY_ID_URL = BASE_URL + "inquiryReserve/queryById";
    /**
     * 查询自己图文咨询列表接口地址
     */
    public static final String QUERY_PICTURE_LIST_URL = BASE_URL + "inquiryReserve/list";
    /**
     * 完成图文咨询接口地址
     */
    public static final String COMPLETE_PICTURE_URL = BASE_URL + "inquiryReserve/complete";
    /**
     * 查询自己视频（会诊和视频诊疗）咨询列表接口地址
     */
    public static final String QUERY_MESSVIDEO_LIST_URL = BASE_URL + "messdiagnose/messVideo";
    /**
     * 查询自己图文咨询咨询列表接口地址
     */
    public static final String QUERY_MESS_INQUIRY_RESERVE_LIST_URL = BASE_URL + "messdiagnose/messInquiryReserve";
    /**
     * 查询自己视频咨询列表接口地址
     */
    public static final String QUERY_MESS_SITDIAGNOSE_RESERVE_LIST_URL = BASE_URL +
            "messdiagnose/messSitDiagnoseReserve";
    /**
     * 查询自己视频会诊咨询列表接口地址
     */
    public static final String QUERY_MESS_GROUP_SITDIAGNOSE_RESERVE_LIST_URL = BASE_URL +
            "messdiagnose/messGroupSitDiagnoseReserve";
    /**
     * 对视频诊疗未接听进行计数接口地址
     */
    public static final String SET_VIDEO_DIAGNOSE_TIMES_URL = BASE_URL + "messdiagnose/setVideoDiagnoseTimes";
    /**
     * 查询未读消息（红点）接口地址
     */
    public static final String QUERY_UNREAD_MESSAGE_URL = BASE_URL + "messdiagnose/userQueryTheRedDot";
    /**
     * 查询枚举内容接口地址
     */
    public static final String QUERY_ENUM_URL = BASE_URL + "option/query";
    /**
     * 查询文件服务器地址接口地址
     */
    public static final String QUERY_FILE_SERVER_URL = BASE_URL + "file/queryFilePath";
    /**
     * 上传个推cid接口地址
     */
    public static final String UPLOAD_GETUI_CID_URL = BASE_URL + "thirdRegister/getui";
    /**
     * 上传推送消息已读状态地址
     */
    public static final String PUSH_MESSAGE_MARK_READ_URL = BASE_URL + "pushMessage/markRead";
    /**
     * 上传推送消息已读状态地址
     */
    public static final String PUSH_MESSAGE_MARKS_READ_URL = BASE_URL + "pushMessage/marksRead";
    /**
     * 上传用户所有推送消息已读状态地址
     */
    public static final String PUSH_MESSAGE_MARKS_READ_ALLUSER_URL = BASE_URL + "pushMessage/marksReadAllUser";
    /**
     * 查询用户所有推送消息地址
     */
    public static final String PUSH_MESSAGE_USER_MESSAGE_URL = BASE_URL + "pushMessage/messageUser";
    /**
     * 查询用户最优优惠地址
     */
    public static final String QUERY_USER_DISCOUNT_URL = BASE_URL + "platformDiscount/getMaxDiscount";
    /**
     * 查询用户最优优惠地址
     */
    public static final String QUERY_USER_DISCOUNT_BY_ORDERID_URL = BASE_URL +
            "platformDiscount/getMaxDiscountByOrderId";
    /**
     * 查询扣款规则地址
     */
    public static final String QUERY_CUTDOWN_URL = BASE_URL + "cutDown/list";
    /**
     * 获取短信验证码地址
     */
    public static final String GET_SHORTMSG_CODE_URL = BASE_URL + "index/getShortMsgCode";
    /**
     * 校验短信验证码地址
     */
    public static final String VERYFY_MSG_CODE_URL = BASE_URL + "account/veryfyMsgCode";
    /**
     * 查询APP更新版本地址
     */
    public static final String QUERY_UPDATE_VERSION_URL = BASE_URL + "index/queryVersion";
    /**
     * 查询APP下载地址地址
     */
    public static final String QUERY_UPDATE_APP_URL = BASE_URL + "downloadAddress/list";
    /**
     * 查询APP下载地址地址
     */
    public static final String QUERY_UPDATE_APP_BY_TYPE_URL = BASE_URL + "downloadAddress/listByType";
    /**
     * 查询APP下载地址地址
     */
    public static final String APP_DOWNLOAD_URL = BASE_URL + "index/download";

}
