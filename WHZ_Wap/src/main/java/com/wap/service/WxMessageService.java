package com.wap.service;

import com.api.service.*;
import com.api.service.impl.WxServiceImpl;
import com.common.model.EventMessage;
import com.common.model.WXMessage;

public class WxMessageService {
	private WxService wxService = new WxServiceImpl();

	// 消息监听
	public WXMessage listenMessage(EventMessage message) {
		if (message == null)
			return null;
		return wxService.listenMessage(message);
	}

	// 创建菜单
	public void createMeum() {
		wxService.createMeum();
	}
}
