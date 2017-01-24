package com.shkjs.patient.cache;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.patient.Preference;
import com.shkjs.patient.bean.UserInfo;

/**
 * Created by xiaohu on 2016/10/10.
 */

public class DataCache {

    private static DataCache dataCache;
    //当前登录用户信息
    private UserInfo userInfo;
    //系统消息未读条数
    private int unReadNum = 0;
    //是否已登录网易服务器
    private boolean isLoginNim = false;
    //是否有未完成咨询消息
    private boolean imperfectOrder = false;
    //是否有新的健康报告
    private boolean isNewReport = false;
    //是否正在登录
    private boolean isLogining = false;

    private DataCache() {
    }

    public static DataCache getInstance() {
        if (null == dataCache) {
            synchronized (DataCache.class) {
                if (null == dataCache) {
                    dataCache = new DataCache();
                }
            }
        }
        return dataCache;
    }

    /**
     * 得到当前登录用户信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return userInfo == null ? new UserInfo() : userInfo;
    }

    /**
     * 更新当前登录用户信息
     *
     * @param userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 得到未读系统消息条数
     *
     * @return
     */
    public int getUnReadNum() {
        return unReadNum = SharedPreferencesUtils.getInt(Preference.UN_READ_NUM);
    }

    /**
     * 更新未读系统消息条数
     *
     * @param unReadNum
     */
    public void setUnReadNum(int unReadNum) {
        SharedPreferencesUtils.put(Preference.UN_READ_NUM, unReadNum);
        this.unReadNum = unReadNum;
    }

    /**
     * 是否登录网易服务器
     *
     * @return
     */
    public boolean isLoginNim() {
        isLoginNim = true;
        StatusCode statusCode = NIMClient.getStatus();
        if (!statusCode.equals(StatusCode.LOGINED)) {
            isLoginNim = false;
        }
        return isLoginNim;
    }


    public boolean isImperfectOrder() {
        return imperfectOrder;
    }

    public void setImperfectOrder(boolean imperfectOrder) {
        this.imperfectOrder = imperfectOrder;
    }

    public boolean isNewReport() {
        return isNewReport;
    }

    public void setNewReport(boolean newReport) {
        isNewReport = newReport;
    }

    public boolean isLogining() {
        return isLogining;
    }

    public void setLogining(boolean logining) {
        isLogining = logining;
    }

    public void cleanCache() {
        userInfo = null;
        unReadNum = 0;
        isLoginNim = false;
        imperfectOrder = false;
        isNewReport = false;
        isLogining = false;
    }
}
