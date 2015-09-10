package com.common.model;

public class WXMessage {
//0文本，1图文
private int messagetype;
//消息内容,文本消息直接输出；图文消息则为List
private String messageContent; 


public int getMessagetype() {
	return messagetype;
}

public void setMessagetype(int messagetype) {
	this.messagetype = messagetype;
}

public String getMessageContent() {
	return messageContent;
}

public void setMessageContent(String messageContent) {
	this.messageContent = messageContent;
}

}
