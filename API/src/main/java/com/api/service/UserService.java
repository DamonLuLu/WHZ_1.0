package com.api.service;

import com.common.model.UserModel;

public interface UserService {
	// 获取用户信息
	public UserModel getUserByOpenid(String openid);

	// 保存用户信息
	public int saveUserInfo(UserModel model);

}
