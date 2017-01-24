package com.shkjs.doctor.data;
/**
 * 多人会诊子项
 * @author ZHANGYUKUN
 *
 */
public enum GroupSitDiagnoseDetailStatus {
	INITIAL("初始的"),PAID("已支付"),AGREE("同意"),CANCEL("取消"),COMPLETE("完成");
	
	private String mark;
	
	GroupSitDiagnoseDetailStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
