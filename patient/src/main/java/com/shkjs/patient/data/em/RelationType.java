package com.shkjs.patient.data.em;

/**
 * 用户关系枚举
 *
 * @author ZHANGYUKUN
 */
public enum RelationType {
    DD("医医关系"), DU("医患关系"), UD("患医关系"), UU("患患关系");

    private String mark;

    RelationType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
