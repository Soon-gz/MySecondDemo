package com.shkjs.doctor.data;

/**
 * 版本等级
 * @author ZHANGYUKUN
 *
 */
public enum VersionType {
	
	SUGGEST("建议升级"),FORCE("强制升级");
	
	private String mark;
	
	VersionType(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
}
