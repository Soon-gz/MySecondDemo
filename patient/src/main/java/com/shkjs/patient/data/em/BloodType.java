package com.shkjs.patient.data.em;

/**
 * 血型类型
 *
 * @author ZHANGYUKUN
 */
public enum BloodType {
    A("A型"), B("B型"), O("O型"), AB("AB型"), UNKONW("未知");

    private String mark;

    BloodType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public static BloodType getBloodType(String value) {
        for (BloodType bloodType : values()) {
            if (bloodType.getMark().equals(value)) {
                return bloodType;
            }
        }
        return null;
    }
}
