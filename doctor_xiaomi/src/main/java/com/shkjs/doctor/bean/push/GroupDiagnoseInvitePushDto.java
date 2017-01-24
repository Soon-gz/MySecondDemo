package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.bean.Message;

import java.io.Serializable;
/**
 * 邀请医生
 * @author LENOVO
 *
 */
public class GroupDiagnoseInvitePushDto extends Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7350019195544033212L;
	// 创建者名称
	private String createName;
	// 用户名称
	private String name;
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
