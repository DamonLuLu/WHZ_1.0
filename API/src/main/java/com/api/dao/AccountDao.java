package com.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.common.model.AccountBookModel;
import com.common.model.AccountRoomModel;
import com.common.model.AccountRoomUserModel;
import com.common.model.ReturnList;
import com.common.utils.ModelUtils;
import com.common.utils.RedisKeys;

public class AccountDao extends BaseDao {
	// 创建房间信息
	public int saveAccountRoom(AccountRoomModel model) {
		if (model == null)
			return -1;
		try {
			String key = RedisKeys.AccountRoomKey;
			redis.addHashMapToRedis(key, model.getRoomid(), JSON.toJSONString(model));
			// 建立映射
			key = RedisKeys.AccountRoomNameKey;
			redis.addHashMapToRedis(key, model.getRoomName(), model.getRoomid());
			return 1;
		} catch (Exception ex) {
			return -1;
		}
	}

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomId(String roomId) {
		if (ModelUtils.isNullOrEmpty(roomId))
			return null;
		String key = RedisKeys.AccountRoomKey;
		String room = redis.getHashMapFromFieldByOne(key, roomId);
		if (!ModelUtils.isNullOrEmpty(room)) {
			return JSON.parseObject(room, AccountRoomModel.class);
		}
		return null;
	}

	// 获取房间信息
	public AccountRoomModel getAccountRoomByRoomName(String roomName) {
		if (ModelUtils.isNullOrEmpty(roomName))
			return null;
		String key = RedisKeys.AccountRoomNameKey;
		String roomId = redis.getHashMapFromFieldByOne(key, roomName);
		if (!ModelUtils.isNullOrEmpty(roomId)) {
			return this.getAccountRoomByRoomId(roomId);
		}
		return null;
	}

	// 创建用户房间信息
	public int saveAccountRoomUser(AccountRoomUserModel model) {
		if (model == null)
			return -1;
		try {
			String key = String.format(RedisKeys.AccountRoomUserKey, model.getRoomid());
			redis.addHashMapToRedis(key, model.getOpenid(), JSON.toJSONString(model));
			return 1;
		} catch (Exception ex) {
			return -1;
		}
	}

	// 获取用户房间信息
	public AccountRoomUserModel getAccountRoomUserDetail(String roomid, String openid) {
		String key = String.format(RedisKeys.AccountRoomUserKey, roomid);
		String roomuser = redis.getHashMapFromFieldByOne(key, openid);
		if (!ModelUtils.isNullOrEmpty(roomuser)) {
			return JSON.parseObject(roomuser, AccountRoomUserModel.class);
		}
		return null;
	}

	// 获取房间成员列表信息
	public List<AccountRoomUserModel> getAccountRoomUserList(String roomid) {
		String key = String.format(RedisKeys.AccountRoomUserKey, roomid);
		Map<String, String> userMap = redis.getHashMapFromRedis(key);
		if (userMap != null) {
			List<AccountRoomUserModel> list = new ArrayList<AccountRoomUserModel>();
			for (Entry<String, String> item : userMap.entrySet()) {
				AccountRoomUserModel model = JSON.parseObject(item.getValue(), AccountRoomUserModel.class);
				if (model != null) {
					list.add(model);
				}
			}
			return list;
		}
		return null;
	}

	// 创建用户帐本信息
	public int saveAccountBook(AccountBookModel model) {
		if (model == null)
			return -1;
		try {
			String key = String.format(RedisKeys.AccountBookKey, model.getRoomid(), model.getOpenid());
			return (int) redis.saveSortList(key, JSON.toJSONString(model), ModelUtils.getCurrTimeDouble());

		} catch (Exception ex) {
			return -1;
		}
	}

	// 获取用户帐本列表 信息
	public ReturnList getAccountBookList(String roomid, String openid, int begin, int limit) {
		String key = String.format(RedisKeys.AccountBookKey, roomid, openid);
		Set<String> list = redis.getSortList(key, begin, limit);

		List<AccountBookModel> rtvlist = new ArrayList<AccountBookModel>();
		if (list != null && list.size() > 0) {
			for (String item : list) {
				AccountBookModel model = JSON.parseObject(item, AccountBookModel.class);
				if (model != null) {
					rtvlist.add(model);
				}
			}

		}
		// 获取总数
		int total = redis.getSortTotal(key);
		ReturnList rtv = new ReturnList();
		rtv.setList(rtvlist);
		rtv.setTotal(total);
		return rtv;
	}
	
}
