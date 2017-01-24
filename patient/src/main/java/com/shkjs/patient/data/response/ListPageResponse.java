package com.shkjs.patient.data.response;

/**
 * Created by xiaohu on 2016/9/28.
 */

public class ListPageResponse<T> extends ListResponse<T> {

    private int pageNum;
    private int pageSize;
    private int totalCount;

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

    @Override
    public String toString() {
        return "PageResponse{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                '}';
    }
}
