package com.shkjs.doctor.bean.push;


import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.CancelType;
import com.shkjs.doctor.data.GroupSitDiagnoseDetailStatus;

public class GroupSitDiagnoseDetail extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8554767670640500845L;

	private Long id;

	private Long groupSitDiagnoseId;

	private Long doctorId;

	private GroupSitDiagnoseDetailStatus status;

	private Long money;

	private Long orderId;

	private CancelType cancelType;

	private Long cancelUserId;

	public Long getCancelUserId() {
		return cancelUserId;
	}

	public void setCancelUserId(Long cancelUserId) {
		this.cancelUserId = cancelUserId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public GroupSitDiagnoseDetailStatus getStatus() {
		return status;
	}

	public void setStatus(GroupSitDiagnoseDetailStatus status) {
		this.status = status;
	}

	public static GroupSitDiagnoseDetail newInstance() {
		GroupSitDiagnoseDetail groupSitDiagnoseDetail = new GroupSitDiagnoseDetail();
		groupSitDiagnoseDetail.setStatus(GroupSitDiagnoseDetailStatus.INITIAL);
		return groupSitDiagnoseDetail;
	}

	public Long getGroupSitDiagnoseId() {
		return groupSitDiagnoseId;
	}

	public void setGroupSitDiagnoseId(Long groupSitDiagnoseId) {
		this.groupSitDiagnoseId = groupSitDiagnoseId;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public CancelType getCancelType() {
		return cancelType;
	}

	public void setCancelType(CancelType cancelType) {
		this.cancelType = cancelType;
	}

}