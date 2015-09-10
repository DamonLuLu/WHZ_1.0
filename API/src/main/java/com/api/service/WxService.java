package com.api.service;

import com.common.model.EventMessage;
import com.common.model.WXMessage;

public interface WxService {

	// 消息监听
	public WXMessage listenMessage(EventMessage message);
	//创建菜单 
	public void createMeum();
}
