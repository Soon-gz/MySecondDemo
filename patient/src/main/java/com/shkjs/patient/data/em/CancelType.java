package com.shkjs.patient.data.em;

/**
 * 取消类型
 *
 * @author ZHANGYUKUN
 */
public enum CancelType {
    ADMIN("管理员取消"), DOCTOR("医生取消"), USER("用户取消"), SYSTEM("系统取消");

    private String mark;

    CancelType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    /**
     * 应为前面不小心用了两，所以提供一个转换方法
     *
     * @param roleType
     * @return
     * @author ZHANGYUKUN
     */
    public static CancelType fromRolesEm(RolesEm roleType) {
        if (RolesEm.USER.equals(roleType)) {
            return USER;
        } else if (RolesEm.DOCTOR.equals(roleType)) {
            return DOCTOR;
        } else if (RolesEm.ADMIN.equals(roleType)) {
            return ADMIN;
        } else if (null == roleType) {
            return SYSTEM;
        }
        return USER;
    }
}
