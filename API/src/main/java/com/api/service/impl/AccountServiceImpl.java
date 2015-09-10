package com.api.service.impl;

import java.util.List;

import com.api.dao.AccountDao;
import com.api.service.AccountService;
import com.api.service.UserService;
import com.api.utils.WXHepler;
import com.common.enums.WXEventEnum.WXEventType;
import com.common.model.AccountBookModel;
import com.common.model.AccountRoomModel;
import com.common.model.AccountRoomUserModel;
import com.common.model.EventMessage;
import com.common.model.ReturnList;
import com.common.model.WXMessage;
import com.common.utils.ModelUtils;

public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao = new AccountDao();
	private WXHepler wxHepler = new WXHepler();

	// 保存房间信息
	public AccountRoomModel saveAccountRoom(AccountRoomModel model) {
		// // 房间号
		// int roomid = 100;
		// // 获取二维码
		// String randomCode = wxHepler.runQrCode(roomid);
		// if (!ModelUtils.isNullOrEmpty(randomCode))
		// return null;
		// model.setRandomCode(randomCode);
		// model.setRoomid(roomid);
		model.setCreatetime(ModelUtils.getCurrTime());
		int rtv = accountDao.saveAccountRoom(model);
		if (rtv >= 0) {
			return model;
		}
		return null;
	}

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomId(String roomId) {

		return accountDao.getAccountRoomByRoomId(roomId);
	}

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomName(String roomName) {
		return accountDao.getAccountRoomByRoomName(roomName);
	}

	// 保存用户房间信息
	public int saveAccountRoomUser(AccountRoomUserModel model) {
		if (model == null)
			return -1;
		return accountDao.saveAccountRoomUser(model);
	}

	// 获取用户房间信息
	public AccountRoomUserModel getAccountRoomUserDetail(String roomid, String openid) {

		return accountDao.getAccountRoomUserDetail(roomid, openid);
	}

	// 获取用户房间信息列表
	public List<AccountRoomUserModel> getAccountRoomUserList(String roomid) {

		return accountDao.getAccountRoomUserList(roomid);
	}

	// 保存帐本信息
	public int saveAccountBook(AccountBookModel model) {
		if (model == null)
			return -1;
		model.setCreatetime(ModelUtils.getCurrTime());
		return accountDao.saveAccountBook(model);
	}

	// 获取帐本列表
	@Override
	public ReturnList getAccountBookList(String roomid, String openid, int begin, int limit) {
		return accountDao.getAccountBookList(roomid, openid, begin, limit);
	}

}
