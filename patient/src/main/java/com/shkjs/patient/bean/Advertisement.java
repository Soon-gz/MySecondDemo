package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.AdvertisementType;

public class Advertisement extends BaseModel {

	private static final long serialVersionUID = 8740130259496642895L;

	private Long id;

	private AdvertisementType type;

	private String action;

	private String img;
	
	private String marke;

	private Integer sort;


	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action == null ? null : action.trim();
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img == null ? null : img.trim();
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AdvertisementType getType() {
		return type;
	}

	public void setType(AdvertisementType type) {
		this.type = type;
	}

	public String getMarke() {
		return marke;
	}

	public void setMarke(String marke) {
		this.marke = marke;
	}


}