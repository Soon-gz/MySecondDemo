package com.shkjs.doctor.data;

/**
 * 支付类型
 * @author ZHANGYUKUN
 *
 */
public enum PayType {
	
	BALANCE("余额"),FAMILYBALANCE("家庭组余额"),ALIPAY("支付宝"),WECHAT("微信"),BANKCARD("银行卡");
	
	private String mark;
	
	PayType(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}

}
