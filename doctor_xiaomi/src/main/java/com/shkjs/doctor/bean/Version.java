package com.shkjs.doctor.bean;

import com.shkjs.doctor.data.VersionType;

/**
 * 版本信息的类
 * @author ZHANGYUKUN
 */
public class Version {
	
	/**
	 * 版本等级
	 */
	private VersionType versionType;
	
	/**
	 * 版本号
	 */
	private String code;
	
	/**
	 * 描述
	 */
	private String describe;
	
	
	public VersionType getVersionType() {
		return versionType;
	}
	public void setVersionType(VersionType versionType) {
		this.versionType = versionType;
	}
	
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
