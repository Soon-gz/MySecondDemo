package com.shkjs.doctor.bean;

/**
 * 分页参数类,默认10条每页
 * @author ZHANGYUKUN
 *
 *
 */
public class Page {
	private int limit;
	private int anInt;
	private int page;
	public Page(){
		limit = 10;
	}
	
	public Integer getPageNum() {
		return this.page;
	}
	public void setPageNum(Integer pageNum) {
		this.page = pageNum;
	}

	public Integer getPageSize() {
		return this.limit;
	}

	public void setPageSize(Integer pageSize) {
		this.limit = pageSize;
	}


}
