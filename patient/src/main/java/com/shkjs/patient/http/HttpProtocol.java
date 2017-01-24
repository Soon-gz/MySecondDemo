package com.shkjs.patient.http;

import com.shkjs.patient.Preference;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.data.em.CancelType;
import com.shkjs.patient.data.em.FlowType;
import com.shkjs.patient.data.em.InformationConfidential;
import com.shkjs.patient.data.em.NotificationSwitch;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.OrderType;
import com.shkjs.patient.data.em.PayType;
import com.shkjs.patient.data.em.PhoneTerminalType;
import com.shkjs.patient.data.em.RelationType;
import com.shkjs.patient.data.em.ReportType;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.io.Param;
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
     * 帐号是否能注册
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param userName 用户名
     * @param callback 能否注册结果回调
     */
    public static void canRegister(String userName, HttpCallback callback) {
        String url = HttpBase.CAN_REGISTER_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_NAME, userName);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 用户注册
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userName 用户名
     * @param password 密码
     * @param code     短信验证码
     * @param callback 注册结果回调
     */
    public static void register(String userName, String password, String code, HttpCallback callback) {
        String url = HttpBase.REGISTER_URL;
        String bodyStr = MyApplication.USER_NAME + "=" + userName + "&" + MyApplication.USER_PWD + "=" + password +
                "&" +
                Preference.CODE + "=" + code + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 用户登录
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userName 用户名
     * @param password 密码
     * @param callback 登录结果回调
     */
    public static void login(String userName, String password, HttpCallback callback) {
        String url = HttpBase.LOGIN_URL;
        String bodyStr = MyApplication.USER_NAME + "=" + userName + "&" + MyApplication.USER_PWD + "=" + password;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 用户登出
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 登出结果回调
     */
    public static void logout(HttpCallback callback) {
        String url = HttpBase.LOGOUT_URL;
        Http.postAsync(url, callback, "");
    }

    /**
     * 找回密码
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userName    用户名
     * @param newPassword 新密码
     * @param code        短信验证码
     * @param callback    找回密码结果回调
     */
    public static void findPassword(String userName, String newPassword, String code, HttpCallback callback) {
        String url = HttpBase.FIND_PASSWORD_URL;
        String bodyStr = MyApplication.USER_NAME + "=" + userName + "&" + MyApplication.USER_PWD + "=" + newPassword
                + "&" +
                Preference.CODE + "=" + code;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 修改密码
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     * @param callback 修改密码结果回调
     */
    public static void modifyPassword(String oldPwd, String newPwd, HttpCallback callback) {
        String url = HttpBase.MODIFY_PWD_URL;
        String bodyStr = Preference.OLD_PWD + "=" + oldPwd + "&" + Preference.NEW_PWD + "=" + newPwd;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 帮助信息接口
     * <p>
     * 返回数据对应为ListPageResponse<HelpMesage>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryHelp(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_HELP_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        StrParam bodyStr3 = new StrParam(Preference.TYPE, "USER");
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 获取医院列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Hospital>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getHospitals(Page page, HttpCallback callback) {
        String url = HttpBase.HOSPITAL_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取免费医生医院列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Hospital>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getFreeHospitals(Page page, HttpCallback callback) {
        String url = HttpBase.HOSPITAL_FREE_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取本地医生医院列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Hospital>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getLocalHospitals(Page page, HttpCallback callback) {
        String url = HttpBase.HOSPITAL_LOCAL_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取科室列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Department>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getDepartments(Page page, HttpCallback callback) {
        String url = HttpBase.DEPARTMENT_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取免费科室列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Department>
     *
     * @param hospitalId 医院ID
     * @param page       分页详情
     * @param callback   结果回调
     */
    public static void getFreeDepartments(long hospitalId, Page page, HttpCallback callback) {
        String url = HttpBase.DEPARTMENT_FREE_URL;
        StrParam bodyStr1 = new StrParam(Preference.HOSPITAL_ID, hospitalId);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 获取本地科室列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Department>
     *
     * @param hospitalId 医院ID
     * @param page       分页详情
     * @param callback   结果回调
     */
    public static void getLocalDepartments(long hospitalId, Page page, HttpCallback callback) {
        String url = HttpBase.DEPARTMENT_LOCAL_URL;
        StrParam bodyStr1 = new StrParam(Preference.HOSPITAL_ID, hospitalId);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 获取推荐医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param id       患者ID
     * @param page     分页第几页
     * @param callback 结果回调
     */
    public static void getDoctors(long id, Page page, HttpCallback callback) {
        String url = HttpBase.RECOMMEND_DOCTOR_URL;
        StrParam bodyStr1 = new StrParam(Preference.ID, id);
        //TODO lxh 实际使用时放开注释
        //        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page);
        //        Http.getAsync(url, callback, bodyStr1, bodyStr2);
        Http.getAsync(url, callback, bodyStr1);
    }

    /**
     * 获取推荐医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getDoctors(Page page, HttpCallback callback) {
        String url = HttpBase.DOCTOR_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 搜索医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param searchMsg 搜素内容
     * @param page      分页详情
     * @param callback  结果回调
     */
    public static void searchDoctors(String searchMsg, Page page, HttpCallback callback) {
        String url = HttpBase.SEARCH_DOCTOR_URL;
        StrParam bodyStr1 = new StrParam(Preference.SEARCH_MSG, searchMsg);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 通过科室名称搜索医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param categoryName 搜素内容
     * @param page         分页详情
     * @param callback     结果回调
     */
    public static void searchDoctorsBycategoryName(String categoryName, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.SEARCH_DOCTOR_BY_CATEGORY_NAME_URL, categoryName);
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 搜索免费医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param hospitalName 医院名称
     * @param categoryName 科室名称
     * @param page         分页详情
     * @param callback     结果回调
     */
    public static void searchDoctors(String hospitalName, String categoryName, Page page, HttpCallback callback) {
        String url = HttpBase.SEARCH_FREE_DOCTOR_URL;
        StrParam bodyStr1 = new StrParam("hospitalName", hospitalName);
        StrParam bodyStr2 = new StrParam("categoryName", categoryName);
        StrParam bodyStr3 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr4 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3, bodyStr4);
    }

    /**
     * 搜索本地医生列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param hospitalName 医院名称
     * @param categoryName 科室名称
     * @param page         分页详情
     * @param callback     结果回调
     */
    public static void searchLocalDoctors(String hospitalName, String categoryName, Page page, HttpCallback callback) {
        String url = HttpBase.SEARCH_LOCAL_DOCTOR_URL;
        StrParam bodyStr1 = new StrParam("hospitalName", hospitalName);
        StrParam bodyStr2 = new StrParam("categoryName", categoryName);
        StrParam bodyStr3 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr4 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3, bodyStr4);
    }

    /**
     * 通过userName获取医生信息接口
     * <p>
     * 返回数据对应为ListPageResponse<Doctor>
     *
     * @param userName userName
     * @param tag      tag
     * @param callback 结果回调
     */
    public static void queryDoctorDetailByUserName(String userName, Object tag, HttpCallback callback) {
        String url = HttpBase.QUERY_DOCTOR_DETAIL_BY_USERNAME_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_NAME, userName);
        Http.getAsync(url, tag, callback, bodyStr);
    }

    /**
     * 用户详情接口
     * <p>
     * 返回数据对应为ObjectResponse<UserInfo>
     *
     * @param tag      tag
     * @param callback 结果回调
     */
    public static void getUserDetail(Object tag, HttpCallback callback) {
        String url = HttpBase.USER_DETAIL_URL;
        Http.getAsync(url, tag, callback);
    }

    /**
     * 医生详情接口
     * <p>
     * 返回数据对应为ObjectResponse<Doctor>
     *
     * @param doctorId 医生ID
     * @param tag      tag
     * @param callback 结果回调
     */
    public static void getDoctorDetail(long doctorId, Object tag, HttpCallback callback) {
        String url = String.format(HttpBase.DOCTOR_DETAIL_URL, doctorId);
        Http.getAsync(url, tag, callback);
    }

    /**
     * 获取广告接口
     * <p>
     * 返回数据对应为ListPageResponse<String>
     *
     * @param callback 结果回调
     */
    public static void getADs(HttpCallback callback) {
        String url = HttpBase.AD_URL;
        Http.getAsync(url, callback, (StrParam[]) null);
    }

    /**
     * 单个文件上传
     * <p>
     * key固定为file
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param file     上传的文件
     * @param callback 结果回调
     */
    public static void fileUpload(File file, HttpCallback callback) {
        String url = HttpBase.USER_FILE_UPLOAD_URL;
        Http.uploadAsync(url, "file", file, callback);
    }

    /**
     * 单个文件上传
     * <p>
     * key固定为file
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param file     上传的文件
     * @param tag      tag
     * @param callback 结果回调
     */
    public static void fileUpload(File file, Object tag, HttpCallback callback) {
        String url = HttpBase.USER_FILE_UPLOAD_URL;
        tag = null == tag ? "file" : tag;
        Http.uploadAsync(url, tag.toString(), file, callback);
    }

    /**
     * 多文件上传
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param files    上传的文件
     * @param callback 结果回调
     */
    public static void multiFileUpload(List<File> files, HttpCallback callback) {
        String url = HttpBase.USER_MULTI_FILE_UPLOAD_URL;
        Param[] params = new Param[files.size()];
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            Param param = new Param(file.getName(), file);
            params[i] = param;
        }
        Http.uploadAsync(url, callback, params);
    }

    /**
     * 多文件上传
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param files    上传的文件
     * @param tag      tag
     * @param callback 结果回调
     */
    public static void multiFileUpload(List<File> files, Object tag, HttpCallback callback) {
        String url = HttpBase.USER_MULTI_FILE_UPLOAD_URL;
        Param[] params = new Param[files.size()];
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            Param param = new Param(file.getName(), file);
            params[i] = param;
        }
        Http.uploadAsync(url, tag, callback, params);
    }

    /**
     * 添加用户关系接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param type           关系类型
     * @param relationUserId 关联用户Id
     * @param callback       结果回调
     */
    public static void addRelation(RelationType type, long relationUserId, HttpCallback callback) {
        String url = HttpBase.ADD_RELATION_URL;
        StrParam bodyStr1 = new StrParam(Preference.TYPE, type.name());
        StrParam bodyStr2 = new StrParam(Preference.RELATION_USER_ID, relationUserId);
        Http.postAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 编辑用户关系接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param type           关系类型
     * @param relationUserId 用户Id
     * @param nickName       昵称
     * @param callback       结果回调
     */
    public static void editRelation(RelationType type, String relationUserId, String nickName, HttpCallback callback) {
        String url = String.format(HttpBase.EDIT_RELATION_URL, type.name());
        String bodyStr = Preference.RELATION_TYPE + "=" + type.getMark() + "&" + Preference.RELATION_USER_ID + "=" +
                relationUserId + "&" + Preference.NICK_NAME + "=" + nickName;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 解除用户关系接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param type           关系类型
     * @param relationUserId 用户Id
     * @param callback       结果回调
     */
    public static void removeRelation(RelationType type, long relationUserId, HttpCallback callback) {
        String url = HttpBase.REMOVE_RELATION_URL;
        StrParam bodyStr1 = new StrParam(Preference.TYPE, type.name());
        StrParam bodyStr2 = new StrParam(Preference.RELATION_USER_ID, relationUserId);
        Http.postAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询用户关系接口
     * <p>
     * 返回数据对应为ObjectResponse<UserRelation>
     *
     * @param type           关系类型
     * @param relationUserId 用户Id
     * @param callback       结果回调
     */
    public static void queryRelation(RelationType type, long relationUserId, HttpCallback callback) {
        String url = HttpBase.QUERY_RELATION_URL;
        StrParam bodyStr1 = new StrParam(Preference.TYPE, type.name());
        StrParam bodyStr2 = new StrParam(Preference.RELATION_USER_ID, relationUserId);
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询和当前用户有某种关系的用户列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserRelation>
     * <p>
     * //TODO 待测试，参数还不确定
     *
     * @param type     关系类型
     * @param callback 结果回调
     */
    public static void queryRelationList(RelationType type, Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_RELATION_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.TYPE, type.name());
        StrParam bodyStr2 = new StrParam(Preference.RELATION_TYPE, type.getMark());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr4 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3, bodyStr4);
    }

    /**
     * 获取用户头像接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void getHeadImg(HttpCallback callback) {
        String url = HttpBase.GET_HEADIMG_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 获取用户网易账号接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userId   用户ID
     * @param callback 结果回调
     */
    public static void queryNeteaseUserNameById(String userId, HttpCallback callback) {
        String url = HttpBase.QUERY_NETEASE_USERNAME_BY_ID_URL;
        StrParam bodyStr = new StrParam(Preference.USER_ID, userId);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 修改用户信息接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     * <p>
     *
     * @param userInfo 待修改用户信息
     * @param callback 结果回调
     */
    public static void modifyUserInfo(String userInfo, HttpCallback callback) {
        String url = HttpBase.MODIFICATION_URL;
        //        String bodyStr = Preference.USER_INFO + "=" + userInfo;
        Http.postAsync(url, callback, userInfo);
    }

    /**
     * 修改用户信息保密性
     * <p>
     * 返回数据对应为ObjectResponse<String>
     * <p>
     *
     * @param informationConfidential 用户信息保密性
     * @param callback                结果回调
     */
    public static void modifyPermission(InformationConfidential informationConfidential, HttpCallback callback) {
        String url = HttpBase.MODIFICATION_CONFIDENTIAL_URL;
        String bodyStr = Preference.INFORMATION_CONFIDENTIAL + "=" + informationConfidential.getMark();
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 修改用户头像
     * <p>
     * 返回数据对应为ObjectResponse<String>
     * <p>
     *
     * @param img      用户头像地址
     * @param callback 结果回调
     */
    public static void modifyUserIcon(String img, HttpCallback callback) {
        String url = HttpBase.MODIFICATION_PORTRAIT_URL;
        String bodyStr = Preference.USER_ICON_IMG + "=" + img;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 修改用户消息提示
     * <p>
     * 返回数据对应为ObjectResponse<String>
     * <p>
     *
     * @param notificationSwitch
     * @param callback
     */
    public static void modifyNotificationSwitch(NotificationSwitch notificationSwitch, HttpCallback callback) {
        String url = HttpBase.MODIFY_NOTIFICATION_SWITCH_URL;
        String bodyStr = Preference.NOTIFICATION_SWITCH + "=" + notificationSwitch.getMark();
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 新增报告（病历报告、体检报告）接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param hrwb     新增报告（病历报告、体检报告）
     * @param type     报告类型
     * @param callback 结果回调
     */
    public static void addReport(String hrwb, ReportType type, HttpCallback callback) {
        String url = HttpBase.ADD_REPORT_URL;
        String bodyStr = hrwb + "&" + Preference.TYPE + "=" + type.name() + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 获取健康报告列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserHealthReports>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getReportList(Page page, HttpCallback callback) {
        String url = HttpBase.GET_REPORT_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 通过订单ID查询健康报告ID接口
     * <p>
     * 返回数据对应为ListPageResponse<Long>
     *
     * @param orderCode 订单code
     * @param callback  结果回调
     */
    public static void queryReportId(String orderCode, HttpCallback callback) {
        String url = HttpBase.QUERY_HEALTHREPORTID_BY_ORDERCODE_URL;
        StrParam bodyStr = new StrParam(Preference.CODE, orderCode);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 获取健康报告列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserHealthReports>
     *
     * @param type
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getReportListToType(ReportType type, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.GET_REPORT_LIST_TYPE_URL, type.name());
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取健康报告列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserHealthReports>
     *
     * @param userId   用户ID
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getReportListToUserId(String userId, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.GET_REPORT_LIST_USERID_URL, userId);
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取健康报告列表接口
     * <p>
     * 返回数据对应为ListPageResponse<UserHealthReports>
     *
     * @param type
     * @param userId   用户ID
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getReportList(ReportType type, String userId, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.GET_REPORT_LIST_USERID_AND_TYPE_URL, userId, type.name());
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 更新报告（病历报告、体检报告）接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param hrwb     更新报告（病历报告、体检报告）内容
     * @param callback 结果回调
     */
    public static void updateReport(String hrwb, HttpCallback callback) {
        String url = HttpBase.UPDATE_REPORT_URL;
        String bodyStr = hrwb + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 删除报告（病历报告、体检报告、平台报告）接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param reportId 删除报告的ID
     * @param callback
     */
    public static void deleteReport(long reportId, HttpCallback callback) {
        String url = String.format(HttpBase.DELETE_REPORT_URL, reportId);
        String bodyStr = "_method=put";
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 标记健康报告为已读接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param reportId 已读报告的ID
     * @param callback
     */
    public static void makeReportReaded(long reportId, HttpCallback callback) {
        String url = HttpBase.MAKE_SINGLE_READ_URL;
        Http.postAsync(url, callback);
    }

    /**
     * 标记健康报告为已读接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback
     */
    public static void makeAllReportReaded(HttpCallback callback) {
        String url = HttpBase.MAKE_ALL_READ_URL;
        Http.postAsync(url, callback);
    }

    /**
     * 添加用戶组成员接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userId          userId
     * @param userGroupMember 想添加的人的Id
     * @param callback        结果回调
     */
    public static void addMember(long userId, String userGroupMember, HttpCallback callback) {
        String url = HttpBase.ADD_MEMBER_URL + "?userId=" + userId;
        String bodyStr = Preference.USER_GROUP_MEMBER + "=" + userGroupMember + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 添加用戶组成员接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userName 想添加的人
     * @param callback 结果回调
     */
    public static void addMemberByUserName(String userName, HttpCallback callback) {
        String url = HttpBase.ADD_MEMBER_BY_USER_NAME_URL;
        String bodyStr = MyApplication.USER_NAME + "=" + userName + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 列出用戶组成员接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userGroupMember 想删除的人的Id
     * @param callback        结果回调
     */
    public static void queryGroupMember(String userGroupMember, HttpCallback callback) {
        String url = HttpBase.QUERY_GROUP_MEMBER_URL;
        String bodyStr = Preference.USER_GROUP_MEMBER + "=" + userGroupMember;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 查询用户家庭组信息接口
     * <p>
     * 返回数据对应为ObjectResponse<Map<String,Object>>
     *
     * @param callback 结果回调
     */
    public static void queryGroupInfo(HttpCallback callback) {
        String url = HttpBase.QUERY_GROUP_INFO_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 查询用户是否为家庭组户主接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param callback 结果回调
     */
    public static void queryUserIsGroupManager(HttpCallback callback) {
        String url = HttpBase.QUERY_IS_GROUP_HOLDER_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 得到用戶所在的家庭组的户主信息接口
     * <p>
     * 返回数据对应为ObjectResponse<UserInfo>
     *
     * @param callback 结果回调
     */
    public static void queryGroupManager(HttpCallback callback) {
        String url = HttpBase.QUERY_USER_GROUP_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 得到用戶所在的用户组接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void queryUserGroup(HttpCallback callback) {
        String url = HttpBase.QUERY_USER_GROUP_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 退出用户组接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void quitUserGroup(HttpCallback callback) {
        String url = HttpBase.QUIT_USER_GROUP_URL;
        Http.postAsync(url, callback, "");
    }

    /**
     * 删除用户组接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void removeUserGroup(HttpCallback callback) {
        String url = HttpBase.REMOVE_USER_GROUP_URL;
        Http.postAsync(url, callback);
    }

    /**
     * 删除用戶组成员接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userId   userId
     * @param callback 结果回调
     */
    public static void removeMember(long userId, HttpCallback callback) {
        String url = HttpBase.REMOVE_MEMBER_USER_GROUP_URL;
        StrParam bodyStr = new StrParam("userId", userId);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 新增订单接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param payerId    付款人ID
     * @param receiverId 收款人ID
     * @param money      多少钱
     * @param type       订单类型
     * @param callback   记过回调
     */
    public static void addOrder(String payerId, String receiverId, String money, OrderType type, HttpCallback
            callback) {
        String url = HttpBase.ADD_ORDER_URL;
        String bodyStr = Preference.PAYER_ID + "=" + payerId + "&" + Preference.RECEIVER_ID + "=" + receiverId + "&"
                + Preference.MONEY + "=" + money + "&" + Preference.TYPE + "=" + type.name() + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 取消订单接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderCode 订单编号
     * @param source    订单类型
     * @param callback  记过回调
     */
    public static void cancelOrder(String orderCode, OrderSource source, HttpCallback callback) {
        String url = HttpBase.CANCEL_ORDER_URL;
        StrParam bodyStr1 = new StrParam(Preference.ORDER_CODE, orderCode);
        StrParam bodyStr2 = new StrParam("source", source.name());
        Http.postAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 删除订单接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderId  订单id
     * @param callback 记过回调
     */
    public static void deleteOrder(long orderId, HttpCallback callback) {
        String url = HttpBase.DELETE_ORDER_BY_ID_URL;
        StrParam bodyStr = new StrParam(Preference.ID, orderId);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 删除订单接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderCode 订单编号
     * @param callback  记过回调
     */
    public static void deleteOrder(String orderCode, HttpCallback callback) {
        String url = HttpBase.DELETE_ORDER_BY_CODE_URL;
        StrParam bodyStr = new StrParam(Preference.CODE, orderCode);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 完成订单接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param code      订单Code
     * @param id        订单ID
     * @param payType   订单类型
     * @param payOrigin 支付来源
     * @param callback  记过回调
     */
    public static void completeOrder(String code, String id, String payType, PayType payOrigin, HttpCallback callback) {
        String url = HttpBase.COMPLETE_ORDER_URL + "?code=" + code;
        String bodyStr = Preference.ID + "=" + id + "&" + Preference.PAY_TYPE + "=" + payType + "&" +
                Preference.PAY_ORIGIN + "=" + payOrigin.getMark();
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 获取消费订单列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Order>
     *
     * @param order    订单过滤条件
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getExpenseOrder(String order, Page page, HttpCallback callback) {
        String url = HttpBase.EXPENSE_ORDER_URL;
        StrParam bodyStr1 = new StrParam(Preference.ORDER, order);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 获取用户订单列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Order>
     *
     * @param key      订单过滤条件key
     * @param value    订单过滤条件value
     * @param page     分页详情时
     * @param callback 结果回调
     */
    public static void getUserOrder(String key, String value, Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_USER_ORDER_URL;
        StrParam bodyStr1 = new StrParam(key, value);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 获取收入订单列表接口
     * <p>
     * 返回数据对应为ListPageResponse<Order>
     *
     * @param order    订单过滤条件
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getInComeOrder(String order, Page page, HttpCallback callback) {
        String url = HttpBase.INCOME_ORDER_URL;
        StrParam bodyStr1 = new StrParam(Preference.ORDER, order);
        StrParam bodyStr2 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr3 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 通过code获取订单接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param code     订单号
     * @param callback 结果回调
     */
    public static void getOrderByCode(String code, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_BY_CODE_URL;
        StrParam bodyStr = new StrParam(Preference.CODE, code);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 通过Id获取订单接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param id       订单ID
     * @param callback 结果回调
     */
    public static void getOrderById(long id, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_BY_ID_URL;
        StrParam bodyStr = new StrParam(Preference.ID, id);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 通过code获取子订单接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param code     订单号
     * @param id       父订单code
     * @param callback 结果回调
     */
    public static void getOrderByPCode(String code, String id, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_BY_PCODE_URL + "?code=" + code;
        StrParam bodyStr = new StrParam(Preference.ID, id);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 通过id获取子订单接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param id       父订单id
     * @param callback 结果回调
     */
    public static void getOrderByPId(String id, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_BY_PID_URL;
        StrParam bodyStr = new StrParam(Preference.ID, id);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 通过id获取订单签名接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param id       订单id
     * @param callback 结果回调
     */
    public static void getOrderSignById(String id, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_SIGN_BY_ID_URL;
        StrParam bodyStr = new StrParam(Preference.ID, id);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 通过code获取订单签名接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param code     订单code
     * @param callback 结果回调
     */
    public static void getOrderSignByCode(String code, HttpCallback callback) {
        String url = HttpBase.QUERY_ORDER_SIGN_BY_CODE_URL;
        StrParam bodyStr = new StrParam(Preference.CODE, code);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 通过code获取退款金额接口
     * <p>
     * 返回数据对应为ObjectResponse<RefundOrder>
     *
     * @param code     订单code
     * @param callback 结果回调
     */
    public static void getRefundOrderMoneyByCode(String code, HttpCallback callback) {
        String url = HttpBase.QUERY_REFUND_ORDER_MONEY_BY_CODE_URL;
        StrParam bodyStr1 = new StrParam(Preference.CODE, code);
        StrParam bodyStr2 = new StrParam("cancelType", CancelType.USER.name());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 通过code获取退款订单接口
     * <p>
     * 返回数据对应为ListPageResponse<RefundOrderDetail>
     *
     * @param code     订单code
     * @param callback 结果回调
     */
    public static void getRefundOrderByCode(String code, HttpCallback callback) {
        String url = HttpBase.QUERY_REFUND_ORDER_BY_CODE_URL;
        StrParam bodyStr = new StrParam(Preference.ORDER_CODE, code);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 平台支付接口
     * <p>
     * 返回数据对应为ObjectResponse<AccountPassword>
     *
     * @param orderCode 订单code
     * @param type      支付类型
     * @param password  支付密码
     * @param callback  结果回调
     */
    public static void payByAccount(String orderCode, PayType type, String password, HttpCallback callback) {
        String url = HttpBase.PAY_BY_ACCOUNT_URL;
        StrParam bodyStr1 = new StrParam(Preference.ORDER_CODE, orderCode);
        StrParam bodyStr2 = new StrParam(Preference.TYPE, type.name());
        StrParam bodyStr3 = new StrParam(MyApplication.USER_PWD, password);
        Http.postAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 退款接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderCode 订单code
     * @param callback  结果回调
     */
    public static void refund(String orderCode, HttpCallback callback) {
        String url = HttpBase.REFUND_URL;
        StrParam bodyStr = new StrParam(Preference.REFUND_ORDER_CODE, orderCode);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 支付宝支付成功通知服务器接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param result   result
     * @param callback 结果回调
     */
    public static void aliPaySyncCall(String result, HttpCallback callback) {
        String url = HttpBase.ALIPAY_SYNC_CALLBACK_URL;
        StrParam bodyStr = new StrParam(Preference.RESULT, result);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 绑定银行卡，支付宝、微信等收款方式接口
     *
     * @param bankCard 收款方式card
     * @param callback 结果回调
     */
    public static void bindBankCard(String bankCard, HttpCallback callback) {
        String url = HttpBase.BIND_ACCOUNT_URL;
        String bodyStr = Preference.BANK_CARD + "=" + bankCard;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 解绑银行卡，支付宝、微信等收款方式接口
     *
     * @param bankCard 收款方式card
     * @param callback 结果回调
     */
    public static void unBindBankCard(String bankCard, HttpCallback callback) {
        String url = HttpBase.UNBIND_ACCOUNT_URL;
        String bodyStr = Preference.BANK_CARD + "=" + bankCard;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 充值接口
     *
     * @param money    充值多少钱
     * @param type     用什么支付
     * @param callback 结果回调
     */
    public static void recharge(String money, PayType type, HttpCallback callback) {
        String url = HttpBase.RECHARG_URL;
        String bodyStr = Preference.MONEY + "=" + money + "&" + Preference.TYPE + "=" + type.name();
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 获取账户详情接口
     * <p>
     * 返回数据对应为ObjectResponse<Account>
     *
     * @param callback 结果回调
     */
    public static void getAccountDetail(HttpCallback callback) {
        String url = HttpBase.ACCOUNT_DETAIL_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 获取绑定的银行卡、支付宝。微信等收款方式账号接口
     * <p>
     * 返回数据对应为ListPageResponse<BankCard>
     *
     * @param page     分页详情
     * @param callback 结果回调
     */
    public static void getBindCard(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_BIND_CARD_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 获取家庭组主账户详情接口
     * <p>
     * 返回数据对应为ObjectResponse<Account>
     *
     * @param callback 结果回调
     */
    public static void getGroupMasterDetail(HttpCallback callback) {
        String url = HttpBase.QUERY_GROUP_MASTER_DETAIL_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 查询资金流水接口
     * <p>
     * 返回数据对应为ObjectPageResponse<Account>
     *
     * @param accountId 账户ID
     * @param page      第几页
     * @param callback  结果回调
     */
    public static void getAccountFlowDetail(long accountId, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.ACCOUNT_FLOWDETAIL_URL, accountId);
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询资金流水接口
     * <p>
     * 返回数据对应为ObjectPageResponse<Account>
     *
     * @param accountId 账户ID
     * @param type      用户金额变动类型
     * @param page      第几页
     * @param callback  结果回调
     */
    public static void getAccountFlowDetail(String accountId, FlowType type, Page page, HttpCallback callback) {
        String url = String.format(HttpBase.ACCOUNT_FLOWDETAIL_URL + "/%s", accountId, type.name());
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 同意加入接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userGroupMemberId 请求id
     * @param callback          结果回调
     */
    public static void agreementJoin(String userGroupMemberId, HttpCallback callback) {
        String url = HttpBase.AGREEMENT_JOIN_URL;
        String bodyStr = Preference.USER_GROUP_MEMBER_ID + "=" + userGroupMemberId;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 不同意加入接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userGroupMemberId 请求id
     * @param callback          结果回调
     */
    public static void disagreeJoin(String userGroupMemberId, HttpCallback callback) {
        String url = HttpBase.DISAGREE_JOIN_URL;
        String bodyStr = Preference.USER_GROUP_MEMBER_ID + "=" + userGroupMemberId;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 医生坐诊时间接口
     * <p>
     * 返回数据对应为ObjectResponse<UserInfo>
     *
     * @param doctorId 医生ID
     * @param page     第几页
     * @param callback 结果回调
     */
    public static void queryDoctorTime(long doctorId, Page page, String dateMsg, HttpCallback callback) {
        String url = String.format(HttpBase.QUERY_DOCTOR_TIME_URL, doctorId);
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        StrParam bodyStr3 = new StrParam(Preference.DATE_MSG, dateMsg);
        Http.getAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 预约视频就诊接口
     * <p>
     * 返回数据对应为ObjectResponse<Order>
     *
     * @param sitDiagnoseId 坐诊时间ID
     * @param callback      结果回调
     */
    public static void orderVideo(long sitDiagnoseId, HttpCallback callback) {
        String url = HttpBase.ORDER_VIDEO_URL;
        String bodyStr = Preference.SIT_DIAGNOSE_ID + "=" + sitDiagnoseId + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 预约图文咨询接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param doctorId 医生ID
     * @param callback 结果回调
     */
    public static void orderPicture(long doctorId, HttpCallback callback) {
        String url = HttpBase.ORDER_PICTURE_URL;
        String bodyStr = Preference.DOCTOR_ID + "=" + doctorId + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 完成图文咨询接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param id       咨询ID
     * @param callback 结果回调
     */
    public static void completePicture(long id, HttpCallback callback) {
        String url = HttpBase.COMPLETE_PICTURE_URL;
        String bodyStr = Preference.ID + "=" + id;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 查询图文咨询接口
     * <p>
     * 返回数据对应为ObjectResponse<InquiryReserve>
     *
     * @param id       咨询ID
     * @param callback 结果回调
     */
    public static void queryPictureById(long id, HttpCallback callback) {
        String url = HttpBase.QUERY_PICTURE_BY_ID_URL;
        StrParam bodyStr = new StrParam(Preference.ID, id);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询图文咨询接口
     * <p>
     * 返回数据对应为ListPageResponse<InquiryReserve>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryPictureList(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_PICTURE_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询视频（会诊和坐诊）咨询接口
     * <p>
     * 返回数据对应为ListPageResponse<InquiryReserve>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryMessVideoList(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_MESSVIDEO_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询图文咨询接口
     * <p>
     * 返回数据对应为ListPageResponse<InquiryReserve>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryMessInquiryReserveList(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_MESS_INQUIRY_RESERVE_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询视频坐诊咨询接口
     * <p>
     * 返回数据对应为ListPageResponse<InquiryReserve>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryMessSitDiagnoseReserveList(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_MESS_SITDIAGNOSE_RESERVE_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询视频会诊咨询接口
     * <p>
     * 返回数据对应为ListPageResponse<InquiryReserve>
     *
     * @param page     分页信息
     * @param callback 结果回调
     */
    public static void queryMessGroupSitDiagnoseReserveList(Page page, HttpCallback callback) {
        String url = HttpBase.QUERY_MESS_GROUP_SITDIAGNOSE_RESERVE_LIST_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 对视频诊疗未接听进行计数接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param userId   用户ID
     * @param callback 结果回调
     */
    public static void setVideoDiagnoseTimes(long userId, HttpCallback callback) {
        String url = HttpBase.SET_VIDEO_DIAGNOSE_TIMES_URL;
        StrParam bodyStr = new StrParam(Preference.USER_ID, userId);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 查询未读消息接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void queryUnreadMessage(HttpCallback callback) {
        String url = HttpBase.QUERY_UNREAD_MESSAGE_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 查询是否可以进入视频会诊接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param orderId  订单ID
     * @param callback 结果回调
     */
    public static void queryEnterrRoom(long orderId, HttpCallback callback) {
        String url = HttpBase.QUERY_ENTER_ROOM_URL;
        StrParam bodyStr = new StrParam("orderId", orderId);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询是否可以进入视频会诊接口
     * <p>
     * 返回数据对应为ObjectResponse<GroupSitDiagnoseDoctorsInfo>
     *
     * @param orderId  订单ID
     * @param callback 结果回调
     */
    public static void queryEnterrRoomAndDoctorIds(long orderId, HttpCallback callback) {
        String url = HttpBase.QUERY_ENTER_ROOM_AND_DOCTOR_IDS_URL;
        StrParam bodyStr = new StrParam("orderId", orderId);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询枚举实际内容接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param name     枚举名字
     * @param callback 结果回调
     */
    public static void queryEnum(String name, HttpCallback callback) {
        String url = HttpBase.QUERY_ENUM_URL;
        StrParam bodyStr = new StrParam(Preference.TYPE_NAME, name);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询文件服务器地址接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void queryFileServerUrl(HttpCallback callback) {
        String url = HttpBase.QUERY_FILE_SERVER_URL;
        Http.getAsync(url, callback);
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
        StrParam bodyStr1 = new StrParam(Preference.CID, cid);
        StrParam bodyStr2 = new StrParam("deviceType", "android");
        StrParam bodyStr3 = new StrParam("userType", "USER");
        Http.postAsync(url, callback, bodyStr1, bodyStr2, bodyStr3);
    }

    /**
     * 查询是否设置过支付密码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param callback 结果回调
     */
    public static void queryPayPwdIsExist(HttpCallback callback) {
        String url = HttpBase.ACCOUNT_PWD_ISEXIST_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 查询是否设置过密保接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param callback 结果回调
     */
    public static void querySecurityIsExist(HttpCallback callback) {
        String url = HttpBase.QUERY_SECURITY_QUESTION_ISEXIST_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 初始化（设置）支付密码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param password 支付密码
     * @param callback 结果回调
     */
    public static void initPayPwd(String password, HttpCallback callback) {
        String url = HttpBase.ACCOUNT_INIT_PWD_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_PWD, password);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 校验支付密码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param password 支付密码
     * @param callback 结果回调
     */
    public static void checkPayPwd(String password, HttpCallback callback) {
        String url = HttpBase.ACCOUNT_CHECK_PWD_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_PWD, password);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 修改支付密码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param password    新支付密码
     * @param oldPassword 旧支付密码
     * @param callback    结果回调
     */
    public static void modifyPayPwd(String password, String oldPassword, HttpCallback callback) {
        String url = HttpBase.ACCOUNT_MODIFY_PWD_URL;
        StrParam bodyStr1 = new StrParam(MyApplication.USER_PWD, password);
        StrParam bodyStr2 = new StrParam("oldPassword", oldPassword);
        Http.postAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 找回支付密码接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param password 新支付密码
     * @param callback 结果回调
     */
    public static void retrievePayPwd(String password, HttpCallback callback) {
        String url = HttpBase.ACCOUNT_RETRIEVE_PWD_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_PWD, password);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 查询系统密保问题列表接口
     * <p>
     * 返回数据对应为ListResponse<SecurityQuestion>
     *
     * @param callback 结果回调
     */
    public static void querySecurityQuestions(HttpCallback callback) {
        String url = HttpBase.QUERY_SECURITY_QUESTION_LIST_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 查询自己设置的密保问题列表接口
     * <p>
     * 返回数据对应为ListResponse<SecurityQuestion>
     *
     * @param callback 结果回调
     */
    public static void querySecurityQuestion(HttpCallback callback) {
        String url = HttpBase.QUERY_SECURITY_QUESTION_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 添加密保问题接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param bodyStr  密保问题内容
     * @param callback 结果回调
     */
    public static void addSecurityQuestion(String bodyStr, HttpCallback callback) {
        String url = HttpBase.ADD_SECURITY_QUESTION_URL;
        bodyStr = bodyStr + HttpBase.PUT_METHOD;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 验证密保问题接口
     * <p>
     * 返回数据对应为ObjectResponse<Boolean>
     *
     * @param bodyStr  密保问题内容
     * @param callback 结果回调
     */
    public static void checkSecurityQuestion(String bodyStr, HttpCallback callback) {
        String url = HttpBase.CHECK_SECURITY_QUESTION_URL;
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 上传推送消息已读接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param messageId messageId
     * @param callback  结果回调
     */
    public static void uploadPushMessageReadStatus(long messageId, HttpCallback callback) {
        String url = HttpBase.PUSH_MESSAGE_MARK_READ_URL;
        StrParam bodyStr = new StrParam(Preference.MESSAGE_ID, messageId);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 上传推送消息已读接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param messageIds messageIds
     * @param callback   结果回调
     */
    public static void uploadPushMessageReadStatus(String messageIds, HttpCallback callback) {
        String url = HttpBase.PUSH_MESSAGE_MARKS_READ_URL;
        StrParam bodyStr = new StrParam(Preference.MESSAGE_IDS, messageIds);
        Http.postAsync(url, callback, bodyStr);
    }

    /**
     * 上传用户所有推送消息已读接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param callback 结果回调
     */
    public static void uploadPushMessageReadStatus(HttpCallback callback) {
        String url = HttpBase.PUSH_MESSAGE_MARKS_READ_ALLUSER_URL;
        Http.postAsync(url, callback);
    }

    /**
     * 查询用户所有推送消息接口
     * <p>
     * 返回数据对应为ListPageResponse<PushMessage>
     *
     * @param page     分页参数
     * @param callback 结果回调
     */
    public static void queryUserPushMessage(Page page, HttpCallback callback) {
        String url = HttpBase.PUSH_MESSAGE_USER_MESSAGE_URL;
        StrParam bodyStr1 = new StrParam(Preference.PAGE_NUM, page.getPageNum());
        StrParam bodyStr2 = new StrParam(Preference.PAGE_SIZE, page.getPageSize());
        Http.getAsync(url, callback, bodyStr1, bodyStr2);
    }

    /**
     * 查询用户最优优惠方案接口
     * <p>
     * 返回数据对应为ObjectResponse<PlatformDiscount>
     *
     * @param callback 结果回调
     */
    public static void queryUserDiscount(HttpCallback callback) {
        String url = HttpBase.QUERY_USER_DISCOUNT_URL;
        StrParam bodyStr = new StrParam("totalCount", 0);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询用户最优优惠方案接口
     * <p>
     * 返回数据对应为ObjectResponse<PlatformDiscount>
     *
     * @param orderId  订单ID
     * @param callback 结果回调
     */
    public static void getMaxDiscountByOrderId(long orderId, HttpCallback callback) {
        String url = HttpBase.QUERY_USER_DISCOUNT_BY_ORDERID_URL;
        StrParam bodyStr = new StrParam("orderId", orderId);
        Http.getAsync(url, callback, bodyStr);
    }

    /**
     * 查询扣款规则接口
     * <p>
     * 返回数据对应为ListPageResponse<CutDownSetting>
     *
     * @param callback 结果回调
     */
    public static void queryCutDown(HttpCallback callback) {
        String url = HttpBase.QUERY_CUTDOWN_URL;
        Http.getAsync(url, callback);
    }

    /**
     * 获取短信验证码接口
     * <p>
     * 返回数据对应为ObjectResponse<String>
     *
     * @param phoneNum 电话号码
     * @param callback 结果回调
     */
    public static void getShortMsgCode(String phoneNum, HttpCallback callback) {
        String url = HttpBase.GET_SHORTMSG_CODE_URL;
        StrParam bodyStr = new StrParam(MyApplication.USER_NAME, phoneNum);
        Http.getAsync(url, callback, bodyStr);
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
        StrParam bodyStr = new StrParam(Preference.CODE, code);
        Http.postAsync(url, callback, bodyStr);
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
     * 查询APP下载地址接口
     * <p>
     * 返回数据对应为ObjectResponse<DownloadAddress>
     *
     * @param type     type
     * @param callback 结果回调
     */
    public static void queryDownloadUrl(PhoneTerminalType type, HttpCallback callback) {
        String url = HttpBase.QUERY_UPDATE_APP_BY_TYPE_URL;
        StrParam bodyStr = new StrParam(Preference.TYPE, type.name());
        Http.getAsync(url, callback, bodyStr);
    }
}
