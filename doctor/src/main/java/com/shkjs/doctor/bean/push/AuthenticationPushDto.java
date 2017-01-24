package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.bean.Message;
import com.shkjs.doctor.data.DoctorPlatformLevel;

import java.io.Serializable;


/**
 * 认证结果
 * @author LENOVO
 *
 */
public class AuthenticationPushDto extends Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5571306869458419515L;
	
	private DoctorPlatformLevel level;  //处理结果
	private String  content; //结果直接为字符串
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public DoctorPlatformLevel getLevel() {
		return level;
	}
	public void setLevel(DoctorPlatformLevel level) {
		this.level = level;
	}
}
