package com.shkjs.doctor.data;
/**
 * 取消类型
 * @author ZHANGYUKUN
 *
 */
public enum CancelType {
	ADMIN("管理员取消"),DOCTOR("医生取消"),USER("用户取消"),SYSTEM("系统取消");
	
	private String mark;
	
	CancelType(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
