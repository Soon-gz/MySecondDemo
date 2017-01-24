package com.shkjs.doctor.data;

public enum DoctorTag {
	NORMAL("默认"),PROMOTION("推广"),FREE("免费");
	
	private String mark;
	
	DoctorTag(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
