package com.api.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.model.UserModel;
import com.common.utils.HttpsUtil;
import com.common.utils.ModelUtils;
import com.common.utils.SystemConfig;
import com.wx.model.MenuButtons;

public class WXHepler {
	// 获取带参二唯码
	public String runQrCode(long pid) {

		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + getAccessToken();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action_name", "QR_LIMIT_SCENE");
		Map<String, Object> action_info = new HashMap<String, Object>();
		Map<String, Object> scene = new HashMap<String, Object>();
		scene.put("scene_id", pid);
		action_info.put("scene", scene);
		params.put("action_info", action_info);
		String rtv = HttpsUtil.doPost(url, params);
		if (!ModelUtils.isNullOrEmpty(rtv)) {
			JSONObject obj = JSONObject.parseObject(rtv);
			return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(obj.getString("ticket"));
		}
		return "";
	}

	// 获取accesstoken
	public String getAccessToken() {
		String url = String.format(SystemConfig.reg_token_url, SystemConfig.appid, SystemConfig.appSecret);
		String rtv = HttpsUtil.doGet(url);
		if (!ModelUtils.isNullOrEmpty(rtv)) {
			JSONObject obj = JSONObject.parseObject(rtv);
			return obj.getString("access_token");
		}
		return "";
	}

	// 获取用户信息
	public UserModel getUserFanInfo(String openid) {
		try {
			String url = String.format(SystemConfig.user_url, getAccessToken(), openid);
			String rtv = HttpsUtil.doGet(url);
			System.out.println("rtv:" + rtv);
			if (!ModelUtils.isNullOrEmpty(rtv)) {
				return JSONObject.parseObject(rtv, UserModel.class);

			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	// 创建菜单
	public String menuCreate(MenuButtons menuButtons) {

		try {
			String url = String.format(SystemConfig.menu_url, getAccessToken());
			String rtv = HttpsUtil.doPost(url, menuButtons);
			System.out.println(rtv);
			return rtv;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 删除菜单
	public String menuDelete() {
		String url = String.format(SystemConfig.menu_delete_url, getAccessToken());
		String rtv = HttpsUtil.doGet(url);
		System.out.println(rtv);
		return rtv;

	}

	// 发送图文消息
	public void PushNewsMessage(String appid, String toUsers, String picurl, String title, String url, String description) {
		try {
			JSONObject param = new JSONObject();
			param.put("touser", toUsers);
			param.put("msgtype", "news");

			JSONObject data = new JSONObject();
			data.put("title", title);
			data.put("description", description);
			data.put("url", url);
			data.put("picurl", picurl);

			JSONArray art = new JSONArray(1);
			art.add(data);

			JSONObject newsJson = new JSONObject(1);
			newsJson.put("articles", art);

			param.put("news", newsJson);

			System.out.println(param.toJSONString());
			this.sendClientMsg(param.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送文本消息
	public void PushTextMessage(String toUsers, String content) {
		JSONObject jsonObject = new JSONObject(3);
		jsonObject.put("touser", toUsers);
		jsonObject.put("msgtype", "text");
		JSONObject textJsonObject = new JSONObject(1);
		textJsonObject.put("content", content);
		jsonObject.put("text", textJsonObject);

		String value = jsonObject.toJSONString();

		System.out.println(value);
		this.sendClientMsg(value);
	}

	// 消息客服发送
	private String sendClientMsg(String msg) {
		String url = String.format(SystemConfig.menu_delete_url, getAccessToken());
		String rtv = HttpsUtil.doPost(url, msg);
		System.out.println(rtv);
		return rtv;
	}

	// 获取帐本名称
	public String getAccountBook(String value) {
		if (ModelUtils.isNullOrEmpty(value))
			return null;
		String[] strArr = value.replace("：", ":").split(":");
		if (strArr.length > 1) {
			return strArr[1];
		}
		return value;

	}

}
