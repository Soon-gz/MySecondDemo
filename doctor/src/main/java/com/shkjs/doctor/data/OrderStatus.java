package com.shkjs.doctor.data;
/**
 * 订单状态
 * @author ZHANGYUKUN
 *
 */
public enum OrderStatus {
	 INITIAL("待支付"),EXPIRE("过期"), AGREE("同意"),PAID("已支付"),COMPLETE("完成"),CANCEL("取消"),REFUND("退款");
	
	
	
	private String mark;
	
	OrderStatus(String mark){
		this.mark = mark;
	}

	public String getMark() {
		return mark;
	}
}
