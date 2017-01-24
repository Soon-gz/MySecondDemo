package com.shkjs.doctor.bean;


import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.PhoneTerminalType;

/**
 * 
 * @author LENOVO
 *
 */
public class DownloadAddress extends BaseModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1050926956961974486L;

	private Long id;

    private String address;
    
    /**
     * 手机类型
     */
    private PhoneTerminalType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public PhoneTerminalType getType() {
		return type;
	}

	public void setType(PhoneTerminalType type) {
		this.type = type;
	}
}