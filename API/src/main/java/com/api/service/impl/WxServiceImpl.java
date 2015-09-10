package com.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.api.service.AccountService;
import com.api.service.UserService;
import com.api.service.WxService;
import com.api.utils.WXHepler;
import com.common.enums.WXEventEnum.WXEventType;
import com.common.model.AccountBookModel;
import com.common.model.AccountRoomModel;
import com.common.model.AccountRoomUserModel;
import com.common.model.EventMessage;
import com.common.model.ReturnList;
import com.common.model.UserModel;
import com.common.model.WXMessage;
import com.common.utils.ModelUtils;
import com.wx.model.MenuButtons;
import com.wx.model.MenuButtons.Button;

public class WxServiceImpl implements WxService {
	// 用户信息
	private UserService userService = new UserServiceImpl();
	// 账户信息
	private AccountService accountService = new AccountServiceImpl();
	private WXHepler wxHepler = new WXHepler();

	@Override
	public WXMessage listenMessage(EventMessage msg) {
		if (msg == null)
			return null;

		// 消息key
		String eventKey = msg.getEventKey();
		// 消息类型
		// 转换类型
		WXEventType msgtype = null;
		if (msg.getMsgType().equals("event")) {
			msgtype = WXEventType.valueOf(msg.getEvent().toLowerCase().trim());
		} else {
			msgtype = WXEventType.valueOf(msg.getMsgType().toLowerCase().trim());
		}
		if (msgtype == null) {
			//System.out.println("内容不符合。");
			return null;
		}
		//System.out.println("msgtype:" + msgtype);
		// 返回内容
		StringBuilder content = new StringBuilder();
		// 推送类型
		int sendType = 0;
		if (msgtype != null) {
			String openid = msg.getFromUserName();
			UserModel user = userService.getUserByOpenid(openid);
			;// 获取用户信息
			switch (msgtype) {
			/*** 需要回应的事件 ****/
			// 关注事件
			case subscribe:
				// 获取用户信息
				content = this.processSubscribe(user, msg);
				break;
			case click:// 点击事件

				break;
			case text: // 文本信息
				content = this.processText(user, msg);
				break;
			case unsubscribe:// 取消关注
				// 获取用户信息
				user = userService.getUserByOpenid(openid);
				if (user != null) {
					user.setStatus(-1);
					userService.saveUserInfo(user);
				}
				break;
			case templatesendjobfinish:
				break;
			case scan:// 扫描事件

				break;
			default:
				break;
			}
		}
		// 返回消息
		WXMessage rtvmsg = new WXMessage();
		rtvmsg.setMessageContent(content.toString());
		rtvmsg.setMessagetype(sendType);
		return rtvmsg;
	}

	// 处理订阅事件
	private StringBuilder processSubscribe(UserModel user, EventMessage msg) {
		StringBuilder content = new StringBuilder();
		String openid = msg.getFromUserName();
		if (user != null) {
			user.setStatus(1);
			userService.saveUserInfo(user);
		} else {
			user = wxHepler.getUserFanInfo(openid);
			if (user != null && !ModelUtils.isNullOrEmpty(user.getOpenid())) {
				userService.saveUserInfo(user);
			} else {
				user = new UserModel();
				user.setOpenid(openid);
				//System.out.println("UserModel:" + JSON.toJSONString(user));
				userService.saveUserInfo(user);
			}
		}
		if (user != null) {
			content.append("欢迎来到微合账，使用以下关键字：\n");
			content.append("1、［申请账本:XXX］即可申请一本账本~\n");
			content.append("2、［账本:XXX］即可加入账本~\n");
			content.append("3、［账本］即可查看你的账本~\n");
			content.append("4、［明细］即可查看你的账本明细~\n");
			content.append("还等什么呢，快去获取账本吧~\n");
		} else {
			content.append("账户创建失败〜\n");
		}
		return content;
	}

	// 处理记帐信息
	private StringBuilder processText(UserModel user, EventMessage msg) {
		StringBuilder content = new StringBuilder();
		String msgConent = msg.getContent().trim();// obvwrs5nY5BoCO_w_sTwckqhEkl4
		if (user != null) {
			if (msgConent.startsWith("申请账本")) {
				content = this.applyAccountBook(user, msg);
			} else if (msgConent.equals("账本")) {
				content = this.getAccountBook(user);
			} else if (msgConent.startsWith("账本")) {
				content = this.joinAccountBook(user, msg);
			} else if (msgConent.equals("明细")) {
				content = this.getAccountBookDeatil(user);
			} else {

				// 记帐
				if (!ModelUtils.isNullOrEmpty(user.getCurrRoomid())) {
					// 处理计算
					BigDecimal money = this.calculateAccount(user, msg.getContent());
					if (money.floatValue() >= 0) {
						AccountRoomUserModel roomuser = accountService.getAccountRoomUserDetail(user.getCurrRoomid(), user.getOpenid());
						if (roomuser != null) {

							if (roomuser.getNeedMoney().floatValue() >= 0) {
								content.append(String.format("记账%s元,总共支付%s元,结余%s元〜", ModelUtils.getFloatBit(money.doubleValue()), ModelUtils.getFloatBit(roomuser.getPayMoney().doubleValue()), ModelUtils.getFloatBit(roomuser.getNeedMoney().doubleValue())));
							} else {
								content.append(String.format("记账%s元,总共支付%s元,还需支付%s元〜", ModelUtils.getFloatBit(money.doubleValue()), ModelUtils.getFloatBit(roomuser.getPayMoney().doubleValue()), ModelUtils.getFloatBit(roomuser.getNeedMoney().doubleValue()).replaceAll("-", "")));
							}

						} else {
							content.append("记账失败〜");
						}
					} else {
						content.append("记账失败〜");
					}

				} else {
					content.append("你还没账本信息，使用以下关键字获取账本〜\n");
					content.append("1、［申请账本:XXX］即可申请一本账本~\n");
					content.append("2、［账本:XXX］即可加入账本~\n");
					content.append("3、［账本］即可查看你的账本~\n");
					content.append("4、［明细］即可查看你的账本明细~\n");
					content.append("还等什么呢，快去获取账本吧~\n");
				}

			}
		} else {
			content.append("账户信息不存在〜");
		}

		return content;
	}

	// 计算账户信息
	private BigDecimal calculateAccount(UserModel user, String content) {
		if (user == null)
			return ModelUtils.getDecimal("-1");
		// 获取金额
		BigDecimal money = ModelUtils.getTextInDecimal(content);
		//System.out.println("money:" + money);
		// 获取房间信息
		AccountRoomModel room = accountService.getAccountRoomByRoomId(user.getCurrRoomid());
		if (room == null)
			return ModelUtils.getDecimal("-1");
		// 获取用户所在房间信息
		AccountRoomUserModel roomuser = accountService.getAccountRoomUserDetail(user.getCurrRoomid(), user.getOpenid());
		if (roomuser == null)
			return ModelUtils.getDecimal("-1");
		// 总消息额度
		if (room.getTotalMoney() == null) {
			room.setTotalMoney(new BigDecimal(0));
		}
		room.setTotalMoney(room.getTotalMoney().add(money));
		accountService.saveAccountRoom(room);
		// 添加消息记录
		AccountBookModel book = new AccountBookModel();
		book.setOpenid(user.getOpenid());
		book.setPayMoney(money);
		book.setRemark(content);
		book.setRoomid(user.getCurrRoomid());
		accountService.saveAccountBook(book);

		// 获取房间用户列表,均分钱物
		if (money.floatValue() > 0) {

			List<AccountRoomUserModel> userlist = accountService.getAccountRoomUserList(user.getCurrRoomid());
			if (userlist != null && userlist.size() > 0) {
				int total = userlist.size();
				// 每个人扣除金额
				BigDecimal subMoney = money.divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
				for (AccountRoomUserModel item : userlist) {
					// 自身不处理
					if (item.getOpenid().equals(user.getOpenid()))
						continue;
					AccountRoomUserModel model = item;
					// 扣除金额
					if (model.getNeedMoney() == null) {
						model.setNeedMoney(ModelUtils.getDecimal("0"));
					}

					model.setNeedMoney(model.getNeedMoney().subtract(subMoney));
					// 保存账本
					accountService.saveAccountRoomUser(model);

				}

				// 处理当前用户金额
				// 已付金额
				if (roomuser.getPayMoney() == null) {
					roomuser.setPayMoney(new BigDecimal(0));
				}
				roomuser.setPayMoney(roomuser.getPayMoney().add(money));
				// 需付金额
				if (roomuser.getNeedMoney() == null) {
					roomuser.setNeedMoney(ModelUtils.getDecimal("0"));
				}
				roomuser.setNeedMoney(roomuser.getNeedMoney().add(money.subtract(subMoney)));
				accountService.saveAccountRoomUser(roomuser);
			}

		}
		return money;

	}

	// 申请账本
	private StringBuilder applyAccountBook(UserModel user, EventMessage msg) {
		StringBuilder content = new StringBuilder();
		String openid = user.getOpenid();
		String bookName = wxHepler.getAccountBook(msg.getContent());
		AccountRoomModel room = new AccountRoomModel();
		room.setRoomName(bookName);
		room.setRoomid(UUID.randomUUID().toString());
		room.setOpenid(openid);
		room = accountService.saveAccountRoom(room);
		// 把当前用户添加到房间中
		AccountRoomUserModel roomuser = new AccountRoomUserModel();
		roomuser.setOpenid(openid);
		roomuser.setRoomid(room.getRoomid());
		roomuser.setNeedMoney(ModelUtils.getDecimal("0"));
		roomuser.setPayMoney(ModelUtils.getDecimal("0"));
		roomuser.setCreatetime(ModelUtils.getCurrTime());
		accountService.saveAccountRoomUser(roomuser);
		// 切换账户
		user.setCurrRoomid(room.getRoomid());
		userService.saveUserInfo(user);
		content.append("申请账本［" + bookName + "］成功！");
		content.append("快分享给小伙伴们来加入吧〜");
		return content;
	}

	// 加入帐本
	private StringBuilder joinAccountBook(UserModel user, EventMessage msg) {
		StringBuilder content = new StringBuilder();
		String bookName = wxHepler.getAccountBook(msg.getContent());
		// 获取账本信息
		AccountRoomModel room = accountService.getAccountRoomByRoomName(bookName);
		if (room != null) {
			if (room.getRoomid().equals(user.getCurrRoomid())) {
				content.append("你已加入本账本，赶紧记账吧〜");

			} else {

				// 切换账户
				user.setCurrRoomid(room.getRoomid());
				userService.saveUserInfo(user);
				// 创建帐本
				// 把当前用户添加到房间中
				AccountRoomUserModel roomuser = new AccountRoomUserModel();
				roomuser.setOpenid(user.getOpenid());
				roomuser.setRoomid(room.getRoomid());
				roomuser.setNeedMoney(ModelUtils.getDecimal("0"));
				roomuser.setPayMoney(ModelUtils.getDecimal("0"));
				roomuser.setCreatetime(ModelUtils.getCurrTime());
				accountService.saveAccountRoomUser(roomuser);

				content.append("切换账户成功〜");
			}

		} else {
			content.append("账本不存在〜");
		}
		return content;
	}

	// 获取帐本信息
	private StringBuilder getAccountBook(UserModel user) {
		StringBuilder content = new StringBuilder();
		String openid = user.getOpenid();
		if (!ModelUtils.isNullOrEmpty(user.getCurrRoomid())) {
			// 获取账本信息
			AccountRoomModel room = accountService.getAccountRoomByRoomId(user.getCurrRoomid());
			if (room != null) {
				// 获取账本详情
				List<AccountRoomUserModel> userlist = accountService.getAccountRoomUserList(room.getRoomid());
				if (userlist != null) {
					content.append("记账明细如下：\n");
					AccountRoomUserModel myself = new AccountRoomUserModel();
					StringBuilder friendcontent = new StringBuilder();
					for (AccountRoomUserModel item : userlist) {
						if (item.getOpenid().equals(openid)) {
							myself = item;
						} else {
							String name = item.getOpenid().substring(item.getOpenid().length() - 5, item.getOpenid().length() - 1);
							friendcontent.append(name + "的支出：" + ModelUtils.getFloatBit(item.getPayMoney().doubleValue()) + "元\n");
							if (item.getNeedMoney().floatValue() >= 0) {
								friendcontent.append("结余：" + ModelUtils.getFloatBit(item.getNeedMoney().doubleValue()) + "元.\n");
							} else {
								friendcontent.append("还需支付：" + ModelUtils.getFloatBit(item.getNeedMoney().doubleValue()).replaceAll("-", "") + "元.\n");
							}
						}
					}
					if (myself != null) {
						content.append("我的支出：" + ModelUtils.getFloatBit(myself.getPayMoney().doubleValue()) + "元\n");
						if (myself.getNeedMoney().floatValue() >= 0) {
							content.append("结余：" + ModelUtils.getFloatBit(myself.getNeedMoney().doubleValue()) + "元.\n");
						} else {
							content.append("还需支付：" + ModelUtils.getFloatBit(myself.getNeedMoney().doubleValue()).replaceAll("-", "") + "元.\n\n");
						}
						content.append(friendcontent);
					} else {
						content.append("账本不存在〜");
					}

				}
			} else {
				content.append("账本不存在〜");
			}

		} else {
			content.append("你还没有加入过任何账本，赶紧去加入吧〜");
		}
		return content;
	}

	// 获取帐本明细
	private StringBuilder getAccountBookDeatil(UserModel user) {

		StringBuilder content = new StringBuilder();
		String openid = user.getOpenid();
		if (!ModelUtils.isNullOrEmpty(user.getCurrRoomid())) {
			// 获取账本信息
			AccountRoomModel room = accountService.getAccountRoomByRoomId(user.getCurrRoomid());
			if (room != null) {
				ReturnList rtvlist = accountService.getAccountBookList(user.getCurrRoomid(), openid, 0, 10);
				if (rtvlist != null && rtvlist.getTotal() > 0) {
					List<AccountBookModel> list = (List<AccountBookModel>) rtvlist.getList();
					content.append("总共" + rtvlist.getTotal() + "条记账明细：\n");
					if (list != null) {
						for (AccountBookModel item : list) {
							content.append(ModelUtils.getDateToString(item.getCreatetime()) + "\n");
							content.append(item.getRemark() + "\n");
						}
						if (rtvlist.getTotal() > 10) {
							content.append("<a href=\"\">查看更多账本明细</a>");
						}
					}

				} else {
					content.append("还没有任何账本明细〜");
				}
			} else {
				content.append("账本不存在〜");
			}
		} else {
			content.append("你还没有加入过任何账本，赶紧去加入吧〜");
		}
		return content;
	}

	// 创建菜单
	@Override
	public void createMeum() {

		Button stockRank = new Button();
		stockRank.setName("创建账本");
		stockRank.setType("click");
		stockRank.setKey("room");

		Button stockjia = new Button();
		stockjia.setName("我的账本");
		List<Button> sub_buttons = new ArrayList<Button>();

		Button hero = new Button();
		hero.setName("明细");
		hero.setType("view");
		hero.setUrl("http://stock.ttync.com/whz/");
		sub_buttons.add(hero);
		stockjia.setSub_button(sub_buttons);

		Button[] btns = new Button[] { stockRank, stockjia };
		MenuButtons menuBtn = new MenuButtons();
		menuBtn.setButton(btns);
		wxHepler.menuDelete();
		String rst = wxHepler.menuCreate(menuBtn);
		System.err.println(rst);

	}
}
