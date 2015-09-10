package com.api.dao;


import com.alibaba.fastjson.JSON;
import com.common.model.UserModel;
import com.common.utils.ModelUtils;
import com.common.utils.RedisKeys;

public class UserDao extends BaseDao {

	// 获取用户信息
	public UserModel getUserByOpenid(String openid) {
		String key = RedisKeys.UserKey;
		String user = redis.getHashMapFromFieldByOne(key, openid);
		if (!ModelUtils.isNullOrEmpty(user)) {
			return  JSON.parseObject(user, UserModel.class);
		}
		return null;
	}

	// 保存用户信息
	public int saveUserInfo(UserModel model) {
		if (model == null)
			return -1;
		try {
			String key = RedisKeys.UserKey;
			redis.addHashMapToRedis(key, model.getOpenid(), JSON.toJSONString(model));
			return 1;
		} catch (Exception ex) {
			return -1;
		}
	}
}
