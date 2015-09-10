package com.common.utils;

//系统配置
public class SystemConfig {
	// 应用ID
	public static final String appid = "wxfdf2c2264b1b87ee";
	// 密钥
	public  static final String appSecret = "31bb0bc300f5d034473841a8e6fe472b";
	//获取accesstoken
	public static final String reg_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	//获取用户信息
	public static final String user_url="https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
	//创建菜单
	public static final String menu_url="https://api.weixin.qq.com/cgi-bin/cgi-bin/menu/create?access_token=%s";
	//删除菜单
	public static final String menu_delete_url="https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";
    //消息发送
	public static final String msg_url="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
}
