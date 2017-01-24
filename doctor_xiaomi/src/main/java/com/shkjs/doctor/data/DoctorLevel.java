package com.shkjs.doctor.data;
/**
 * 医生等级
 * @author ZHANGYUKUN
 *
 */
public enum DoctorLevel {
	RESIDENTDOCTOR("住院医师"),VISITINGDOCTOR("主治医师"),ARCHIATERDOCTOR("主任医师"),VICEARCHIATERDOCTOR("副主任医师");
	
	private String mark;
	
	DoctorLevel(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
