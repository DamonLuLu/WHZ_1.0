package com.wap.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.api.service.WxService;
import com.common.model.EventMessage;
import com.common.model.WXMessage;
import com.wap.service.WxMessageService;
import com.wap.utils.WXUtil;
import com.wap.utils.XMLConverUtil;
import com.wx.model.NewsMessage;
import com.wx.model.XMLNewsMessage;
import com.wx.model.XMLNewsMessage.Article;
import com.wx.model.XMLTextMessage;

@Controller
@RequestMapping("/wxMessage")
public class WxMessageController {

	private WxMessageService wxService = new WxMessageService();

	@RequestMapping("callback")
	public void callback(HttpServletRequest request, HttpServletResponse response) {
		try {
			ServletInputStream inputStream = request.getInputStream();
			ServletOutputStream outputStream = response.getOutputStream();
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			System.out.println(String.format("args signature:%s timestamp:%s nonce:%s echostr:%s", signature, timestamp, nonce, echostr));
			// 首次请求申请验证,返回echostr
			if (echostr != null) {
				WXUtil.outputStreamWrite(outputStream, echostr);
				return;
			}

			// 获取消息体信息
			String wxMsgXml = StreamUtils.copyToString(inputStream, Charset.forName("utf-8"));
			System.out.println("wxMsgXml:" + wxMsgXml);
			EventMessage eventMsg = XMLConverUtil.convertToObject(EventMessage.class, wxMsgXml);
			WXMessage msg = wxService.listenMessage(eventMsg);
			// 消息内容
			String content = "";
			if (msg != null) {
				if (msg.getMessagetype() == 0) {// 文本消息
					content = new XMLTextMessage(eventMsg.getFromUserName(), eventMsg.getToUserName(), msg.getMessageContent()).toXML();
				} else {// 图文消息
					List<Article> articles = new ArrayList<Article>();
					List<NewsMessage> messagelist = JSON.parseArray(msg.getMessageContent(), NewsMessage.class);
					for (NewsMessage news : messagelist) {
						Article art = new Article();
						art.setDescription(news.getDes());
						art.setPicurl(news.getPicurl());
						art.setTitle(news.getTitle());
						art.setUrl(news.getUrl());
						articles.add(art);
					}
					content = new XMLNewsMessage(eventMsg.getFromUserName(), eventMsg.getToUserName(), articles).toXML();

				}

			}
			System.out.println("content:" + content);
			WXUtil.outputStreamWrite(outputStream, content);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 创建菜单
	@RequestMapping("createMenu")
	public void createMenu() {
		wxService.createMeum();
	}

}
