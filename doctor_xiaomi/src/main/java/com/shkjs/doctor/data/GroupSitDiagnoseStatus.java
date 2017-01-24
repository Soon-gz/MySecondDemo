package com.shkjs.doctor.data;
/**
 * 多人会诊
 * @author ZHANGYUKUN
 *
 */
public enum GroupSitDiagnoseStatus {
	 INITIAL("初始的"),PAID("已支付"),AGREE("同意"),CANCEL("取消"),COMPLETE("完成"),EXPIRE("过期");
	
	private String mark;
	
	GroupSitDiagnoseStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
