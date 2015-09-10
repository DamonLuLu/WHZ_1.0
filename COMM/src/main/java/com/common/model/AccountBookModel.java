package com.common.model;

import java.math.BigDecimal;
import java.util.Date;

//帐本信息
public class AccountBookModel {
	// 房间号
	private String roomid;
	private String openid;
	// 已付金额
	private BigDecimal payMoney;
	// 备注
	private String remark;
	// 创建时时间
	private Date createtime;


	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

}
