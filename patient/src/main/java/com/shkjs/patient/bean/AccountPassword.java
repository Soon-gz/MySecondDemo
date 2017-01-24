package com.shkjs.patient.bean;

import java.io.Serializable;

/**
 * Created by xiaohu on 2016/11/30.
 */

public class AccountPassword implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -868177205084252805L;
    /**
     * 密码为空
     */
    private boolean empty;
    /**
     * 密码错误
     */
    private boolean passwordError;
    /**
     * 错误次数
     */
    private int errorTimes;
    /**
     * 剩余次数
     */
    private int remainTimes;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isPasswordError() {
        return passwordError;
    }

    public void setPasswordError(boolean passwordError) {
        this.passwordError = passwordError;
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int getRemainTimes() {
        return remainTimes;
    }

    public void setRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
    }
}
