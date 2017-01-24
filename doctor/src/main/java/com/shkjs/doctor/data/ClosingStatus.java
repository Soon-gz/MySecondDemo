package com.shkjs.doctor.data;

/**
 * 结算状态
 * @author ZHANGYUKUN
 *
 */
public enum ClosingStatus {
	CLOSING("结算"),TRANSFERING("转账中"),TRANSFERSUCCEED("转账成功"),TRANSFERFAIL("转账失败");
	
	private String mark;
	
	ClosingStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
