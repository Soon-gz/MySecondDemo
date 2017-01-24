package com.shkjs.doctor.data;

/**
 * 用户金额流水类型
 * @author ZHANGYUKUN
 *
 */
public enum FlowType {
	RECHARGE("充值"),DRAWMONEY("提现"),EXPENSE("消费"),INCOME("收入"),PAYREFUND("消费退款"),CHARGEREFUND("收费退款");
	
	private String mark;
	
	FlowType(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
