package com.api.service.impl;

import com.api.dao.UserDao;
import com.api.service.UserService;
import com.common.model.UserModel;

//用户逻辑
public class UserServiceImpl implements UserService {
	// 用户数据
	UserDao userDao = new UserDao();

	public UserModel getUserByOpenid(String openid) {
		return userDao.getUserByOpenid(openid);
	}

	// 保存用户信息
	public int saveUserInfo(UserModel model) {
		if (model == null)
			return -1;
		return userDao.saveUserInfo(model);
	}

}
