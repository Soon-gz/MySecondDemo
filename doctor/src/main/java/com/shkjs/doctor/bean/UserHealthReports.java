package com.shkjs.doctor.bean;

import java.util.List;

/**
 * 整合问诊表和问诊结果数据
 *
 * @author LENOVO
 */
public class UserHealthReports extends HealthReportsWithBLOBs {

    /**
     *
     */
    private static final long serialVersionUID = -4814973444712094863L;

    private List<DiagnoseCase> diagnoseCases;

    public List<DiagnoseCase> getDiagnoseCases() {
        return diagnoseCases;
    }

    public void setDiagnoseCases(List<DiagnoseCase> diagnoseCases) {
        this.diagnoseCases = diagnoseCases;
    }
}
