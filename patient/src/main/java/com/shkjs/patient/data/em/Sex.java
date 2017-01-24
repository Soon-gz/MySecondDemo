package com.shkjs.patient.data.em;

/**
 * 性别
 *
 * @author ZHANGYUKUN
 */
public enum Sex {

    MAN("男"), WOMAN("女"), SECRECY("保密");

    private String mark;

    Sex(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public static Sex getSex(String value) {
        for (Sex sex : values()) {
            if (sex.getMark().equals(value)) {
                return sex;
            }
        }
        return null;
    }

}
