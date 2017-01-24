package com.shkjs.doctor.bean.push;

import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.CancelType;
import com.shkjs.doctor.data.GroupSitDiagnoseStatus;

import java.util.Date;
import java.util.List;


public class GroupSitDiagnose extends BaseModel {
	private static final long serialVersionUID = -4323576907206127595L;

	private Long id;

	private Long doctorId;

	private Long userId;

	private GroupSitDiagnoseStatus status;

	private Long money;

	private Long orderId;

	private CancelType cancelType;

	private Long cancelUserId;
	
	private Long healthReportId;
	
	private Date confirmDate;
	
	private Date startDate;
	
	private String roomName;

	public Long getCancelUserId() {
		return cancelUserId;
	}

	public void setCancelUserId(Long cancelUserId) {
		this.cancelUserId = cancelUserId;
	}

	private List<GroupSitDiagnoseDetail> groupSitDiagnoseDetail;

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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public GroupSitDiagnoseStatus getStatus() {
		return status;
	}

	public void setStatus(GroupSitDiagnoseStatus status) {
		this.status = status;
	}

	public List<GroupSitDiagnoseDetail> getGroupSitDiagnoseDetail() {
		return groupSitDiagnoseDetail;
	}

	public void setGroupSitDiagnoseDetail(List<GroupSitDiagnoseDetail> groupSitDiagnoseDetail) {
		this.groupSitDiagnoseDetail = groupSitDiagnoseDetail;
	}

	public CancelType getCancelType() {
		return cancelType;
	}

	public void setCancelType(CancelType cancelType) {
		this.cancelType = cancelType;
	}

	public Long getHealthReportId() {
		return healthReportId;
	}

	public void setHealthReportId(Long healthReportId) {
		this.healthReportId = healthReportId;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


}