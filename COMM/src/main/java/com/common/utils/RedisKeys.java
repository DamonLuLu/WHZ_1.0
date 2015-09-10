package com.common.utils;

public class RedisKeys {
	// 用户信息
	public static String UserKey = "UserKey";
	// 房间信息
	public static String AccountRoomKey = "AccountRoomKey";
	// 房间信息
	public static String AccountRoomNameKey = "AccountRoomNameKey";
	// 房间用户信息 roomid
	public static String AccountRoomUserKey = "AccountRoomUserKey:%s";
	// 帐本信息 roomid+openid
	public static String AccountBookKey = "AccountBookKey:%s:%s";

}
