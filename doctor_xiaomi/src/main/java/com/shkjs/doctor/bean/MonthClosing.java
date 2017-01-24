package com.shkjs.doctor.bean;


import com.shkjs.doctor.base.BaseModel;
import com.shkjs.doctor.data.ClosingStatus;

public class MonthClosing extends BaseModel {

	private static final long serialVersionUID = -7534587695093899511L;
	
	private Long id;
	
	private Long userId;

	private Long accountId;

	private int year;

	private int month;

	private Long money;

	private ClosingStatus status;
	
	private String sequenceNumber;

	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public ClosingStatus getStatus() {
		return status;
	}

	public void setStatus(ClosingStatus status) {
		this.status = status;
	}

	public static MonthClosing newDefaultInstance() {
		MonthClosing balance = new MonthClosing();
		balance.setStatus(ClosingStatus.CLOSING);
		return balance;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

}