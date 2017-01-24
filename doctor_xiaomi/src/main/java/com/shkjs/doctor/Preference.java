package com.shkjs.doctor;

import android.os.Environment;

/**
 * Created by xiaohu on 2016/8/15.
 * <p/>
 * 常量配置
 */

public class Preference {
    public static final String MOB_APPKEY = "172ec723e653c";
    public static final String MOB_APPSECRECT = "cef57885187ee0ae9611caebac15e330";

    public static final String ORDER_CODE = "orderCode";
    public static final String USER_INFO = "userInfo";

    public static final String PUSH_MSG = "push_msg";
    public static final String IS_FROM_WELCOME = "IS_FROM_WELCOME";

    public static final int MYPATINET_REQUEST_CODE = 100;
    public static final int MYPATIENT_RESULT_CODE = 200;


    public static final String CALL_TIMES = "callTimes";

    /**
     * unReadNum
     */
    public static final String UN_READ_NUM = "unReadNum";
    /**
     * 用户NIM用户名后缀
     */
    public static final String NIM_DOCTOR_NAME = "_doctor";
    /**
     * 用户NIM密码后缀
     */
    public static final String NIM_PASS_WORD = "123456";
    /**
     * 用户名
     */
    public static final String USERNAME = "userName";
    public static final String BUGLY_APPID = "dc963af958";//自己建的测试应用

    /**
     * 用户密码
     */
    public static final String PASSWORD = "password";
    /**
     * 更新界面
     */
    public static final String UPDATE_VIEW_ACTION = "update_view_action";
    /**
     * 是否首次运行
     */
    public static final String ISFIRSTRUN = "isFirstRun";
    /**
     * 是否注册之后登录
     */
    public static final String ISREGISTER = "isRegister";
    /**
     * APP版本号
     */
    public static final String VERSION_CODE = "versionCode";
    /**
     * 是否自动登录
     */
    public static final String IS_AUTO_LOGIN = "isAutoLogin";
    /**
     * 设为模板的日期
     */
    public static final String VISITTIME_DATE = "visitdate";
    /**
     * type
     */
    public static final String TYPE = "type";
    /**
     * 患者ID
     */
    public static final String ID = "id";
    /**
     * data
     */
    public static final String DATA = "data";
    /**
     * extra
     */
    public static final String EXTRA = "extra";

    public static final String VISITTIME_TEMPLATE = "visitdatetemplate";

    public static final String BANKSTATUS  = "USE";

    /**
     * 坐诊时间，一天模板的缓存
     */
    public static final String VISIT_TIME_8 = "visittime8";
    public static final String VISIT_TIME_9 = "visittime9";
    public static final String VISIT_TIME_10 = "visittime10";
    public static final String VISIT_TIME_11 = "visittime11";
    public static final String VISIT_TIME_12 = "visittime12";
    public static final String VISIT_TIME_13 = "visittime13";
    public static final String VISIT_TIME_14 = "visittime14";
    public static final String VISIT_TIME_15 = "visittime15";
    public static final String VISIT_TIME_16 = "visittime16";
    public static final String VISIT_TIME_17 = "visittime17";
    public static final String VISIT_TIME_18 = "visittime18";
    public static final String VISIT_TIME_19 = "visittime19";
    public static final String VISIT_TIME_20 = "visittime20";
    public static final String VISIT_TIME_21 = "visittime21";
    public static final String VISIT_TIME_22 = "visittime22";
    public static final String VISIT_TIME_23 = "visittime23";
    /**
     * 医生认证状态
     */
    public static final String AUTHORITY = "AUTHORITY";//权威
    public static final String CERTIFICATION = "CERTIFICATION";//认证
    public static final String NOTPASS = "NOTPASS";//未通过
    public static final String CERTIFICATIONING = "CERTIFICATIONING";//认证中
    public static final String UNCERTIFICATION = "UNCERTIFICATION";//未认证

    public static final String ACTION_NAME = "ACTION_NAME";
    public static final String VIDEO_CONSULT = "VIDEO_CONSULT";
    public static final String VIDEO_CONSULT_GROUP = "VIDEO_CONSULT_GROUP";
    public static final String VIDEO_CONSULT_MAIN = "VIDEO_CONSULT_MAIN";
    /**
     * 支付宝绑定是否成功
     */
    public static final String SUCCESSED_FAIL = "ALIPAY";

    public static final String SUCCESSED = "SUCCESSED";

    public static final String FAIL = "FAIL";

    /**
     * 扫描二维码跳转时携带类型，扫描成功后做相应的处理
     */
    public static final String QRSCAN_INTENT_TYPE = "CREATE_AV_TYPE";

    public static final String QRSCAN_INTENT_ADDPATIENT = "ADDPATIENT";

    public static final String QRSCAN_INTENT_CREATEAV_ADDPATIENT = "CREATE_AV_PATIENT";

    public static final String QRSCAN_INTENT_CREATEAV_ADDDOCTOR = "CREATE_AV_DOCTOR";

    /**
     * 跳转到我的患者携带标识
     */
    public static final String MYPATIENT_TYPE = "MYPATIENT_TYPE";

    public static final String ADD_PATIENT = "ADD_PATIENT";

    /**创建会诊时，Activity的请求码 **/
    public static final int CREATEAV_CONTACTS = 1001;
    public static final int CREATEAV_CONTACTS_REAULT = 1002;
    /**图文完成时的请求码 **/
    public static final int PICTURE_COMPLETE = 123;
    public static final int PICTURE_COMPLETE_RESULT = 1234;
    /**视频完成时的请求码 **/
    public static final int VIDEO_COMPLETE = 456;
    public static final int VIDEO_COMPLETE_RESULT = 4567;
    /**会诊完成时的请求码 **/
    public static final int VIDEO_GROUP_COMPLETE = 10;
    public static final int VIDEO_GROUP_COMPLETE_RESULT = 11;
    public static final int VIDEO_GROUP_CREATE_RESULT = 12;
    public static final int VIDEO_GROUP_CREATE = 13;

    /**
     * 订单完成
     */
    public static final String COMPLETE = "COMPLETE";
    public static final String PAID = "PAID";

    /**
     * 诊金展示
     */
    public static final String VIEW_VIDEO_FEE = "VIEW_VIDEO_FEE";
    public static final String PICTURE_FEE = "PICTURE_FEE";
    /**
     * 分页第几页
     */
    public static final String PAGE_NUM = "pageNum";
    /**
     * 分页每页数据条数
     */
    public static final String PAGE_SIZE = "pageSize";

    public static final String COMPLETE_TYPE = "COMPLETE_TYPE";
    public static final String COMPLETE_PICTURE = "COMPLETE_PICTURE";
    public static final String COMPLETE_SIT_VIDEO_CONSULT = "COMPLETE_SIT_VIDEO_CONSULT";
    public static final String COMPLETE_VIDEO_GROUP_CONSULT = "COMPLETE_VIDEO_GROUP_CONSULT";


    public static final String VIDEO_CONSULT_HEALTH_REPORT = "VIDEO_CONSULT_HEALTH_REPORT";
    public static final String USER_ICON = "usericon.jpg";
    public static final String AUTHENTATIC = "AUTHENTATIC";
    public static final String DOCTORTAG = "doctortag";


    /**
     * 健康报告语音缓存路径
     */
    public static String REPORT_AUDIO_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/com.shkjs" +
            ".doctor/cache/";


    public static String getLevelValue(String level){
        String levelEm = "";
        switch (level){
            case "RESIDENTDOCTOR":
                levelEm = "住院医师";
                break;
            case "VISITINGDOCTOR":
                levelEm = "主治医师";
                break;
            case "ARCHIATERDOCTOR":
                levelEm = "主任医师";
                break;
            case "VICEARCHIATERDOCTOR":
                levelEm = "副主任医师";
                break;
        }
        return levelEm;
    }

}
