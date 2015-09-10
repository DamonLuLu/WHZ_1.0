package com.common.enums;

public class WXEventEnum {
	public enum WXEventType {

		// 关注
		subscribe(0),
		// 取消关注
		unsubscribe(1),
		// 菜单点击
		click(2),
		// 菜单浏览
		view(3),
		// 文字
		text(4),
		// 图片
		image(5),
		// 声音
		voice(6),
		// 视频
		video(7),
		// 链接
		link(8),
		// 扫描二维码
		scan(9),
		// 地理位置
		location(10),
		// 模板发送完成
		templatesendjobfinish(11);

		private int value = 0;

		private WXEventType(int value) { // 必须是private的，否则编译错误
			this.value = value;
		}

		public static WXEventType valueOf(int value) {
			switch (value) {
			case 0:
				return subscribe;
			case 1:
				return unsubscribe;
			case 2:
				return click;
			case 3:
				return view;
			case 4:
				return text;
			case 5:
				return image;
			case 6:
				return voice;
			case 7:
				return video;
			case 8:
				return link;
			case 9:
				return scan;
			case 10:
				return location;
			case 11:
				return templatesendjobfinish;

			default:
				return null;
			}
		}

		public int value() {
			return this.value;
		}
	}

}
