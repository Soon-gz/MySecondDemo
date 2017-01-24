package com.shkjs.doctor.data;
/**
 * 图文质询状态
 * @author ZHANGYUKUN
 *
 */
public enum InquiryStatus {
	 INITIAL("初始的"),PAID("已支付"),COMPLETE("完成"),CANCEL("取消"),;
	
	
	
	
	private String mark;
	
	InquiryStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
