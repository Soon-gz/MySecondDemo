package com.shkjs.doctor.bean.push;

import java.io.Serializable;

/**
 * 会诊创建成功通知医生
 * @author LENOVO
 *
 */
public class GroupDiagnoseCreateSuccessPushDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1702468317138840170L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
