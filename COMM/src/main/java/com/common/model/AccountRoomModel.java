package com.common.model;

import java.math.BigDecimal;
import java.util.Date;

//帐户房间信息
public class AccountRoomModel {

	// 房间名
	private String roomName;
	// 房间二维码
	private String randomCode;
	// 消费总金额
	private BigDecimal totalMoney;
	// 创建时时间
	private Date createtime;
	// 创建人
	private String openid;
	// 房间id
	private String roomid;

	public String getOpenid() {
		return openid;
	}

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRandomCode() {
		return randomCode;
	}

	public void setRandomCode(String randomCode) {
		this.randomCode = randomCode;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

}
