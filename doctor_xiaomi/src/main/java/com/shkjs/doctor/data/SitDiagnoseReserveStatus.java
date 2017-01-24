package com.shkjs.doctor.data;
/**
 * 视屏预定状态
 * @author ZHANGYUKUN
 *
 */
public enum SitDiagnoseReserveStatus {
	INITIAL("初始的"),PAID("已支付"),COMPLETE("完成"),CANCEL("取消"),;
	
	private String mark;
	
	SitDiagnoseReserveStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
