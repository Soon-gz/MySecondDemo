package com.shkjs.patient.bean;

/**
 * Created by xiaohu on 2016/10/14.
 */

public class Page {

    private int pageNum = 1;
    private int pageSize = 10;
    private int totalCount = 0;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
