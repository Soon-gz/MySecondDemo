package com.shkjs.doctor.data;
/**
 * 医生平台等级
 * @author ZHANGYUKUN
 *
 */
public enum DoctorPlatformLevel {
	
	AUTHORITY("权威"),CERTIFICATION("认证"),NOTPASS("未通过"),CERTIFICATIONING("认证中"),UNCERTIFICATION("未认证");
	
	private String mark;
	
	DoctorPlatformLevel(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
