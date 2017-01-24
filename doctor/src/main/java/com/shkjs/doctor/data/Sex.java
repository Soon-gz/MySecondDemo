package com.shkjs.doctor.data;

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

    public static String  getSexCalue(String sex){
        String em = "";
        switch (sex){
            case "MAN":
                em = "男";
                break;
            case "WOMAN":
                em = "女";
                break;
            case "SECRECY":
                em = "保密";
                break;
            default:
                em = "保密";
                break;
        }
        return em;
    }

    public static String getSexEm(String sex){
        String em = "";
        switch (sex){
            case "男":
                em = "MAN";
                break;
            case "女":
                em = "WOMAN";
                break;
            case "保密":
                em = "SECRECY";
                break;
            default:
                em = "SECRECY";
                break;
        }
        return em;
    }

}
