package com.shkjs.doctor.data;

/**
 * 报告类型
 *
 * @author ZHANGYUKUN
 */
public enum ReportType {
    PLATFORM("平台报告"), CASEHISTORY("病历报告"), PHYSICALEXAM("体检报告");

    private String mark;

    ReportType(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }
}
