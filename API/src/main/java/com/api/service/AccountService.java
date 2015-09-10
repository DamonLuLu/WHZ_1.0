package com.api.service;

import java.util.List;

import com.common.model.AccountBookModel;
import com.common.model.AccountRoomModel;
import com.common.model.AccountRoomUserModel;
import com.common.model.ReturnList;

public interface AccountService {
	// 创建房间信息
	public AccountRoomModel saveAccountRoom(AccountRoomModel model);

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomId(String roomId);

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomName(String roomName);

	// 创建用户房间信息
	public int saveAccountRoomUser(AccountRoomUserModel model);

	// 获取用户房间信息
	public AccountRoomUserModel getAccountRoomUserDetail(String roomid, String openid);

	// 获取房间成员列表信息
	public List<AccountRoomUserModel> getAccountRoomUserList(String roomid);

	// 创建用户帐本信息
	public int saveAccountBook(AccountBookModel model);

	// 获取用户帐本列表 信息
	public ReturnList getAccountBookList(String roomid, String openid, int begin, int limit);

}
