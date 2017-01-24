package com.shkjs.patient.data.em;

/**
 * 支付类型
 *
 * @author ZHANGYUKUN
 */
public enum PhoneTerminalType {

    ANDROID_USER("android_user"), IOS_USER("ios_user"), ANDROID_DOCTOR("android_doctor"), IOS_DOCTOR("ios_doctor");

    private String mark;

    PhoneTerminalType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

}
