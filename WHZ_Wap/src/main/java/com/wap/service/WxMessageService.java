package com.wap.service;

import com.api.service.*;
import com.api.service.impl.WxServiceImpl;
import com.common.model.EventMessage;
import com.common.model.WXMessage;

public class WxMessageService {
	private WxService wxService = new WxServiceImpl();

	// ��Ϣ����
	public WXMessage listenMessage(EventMessage message) {
		if (message == null)
			return null;
		return wxService.listenMessage(message);
	}

	// �����˵�
	public void createMeum() {
		wxService.createMeum();
	}
}
