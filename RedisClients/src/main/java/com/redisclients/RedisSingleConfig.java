package com.redisclients;

// redis 配置文件
public class RedisSingleConfig {
	// 单机ip
	private String singleip;
	// 单机端口
	private int singleport;

	// 最大空闲链接
	private int maxidle;

	public int getMaxidle() {
		return maxidle;
	}

	public void setMaxidle(int maxidle) {
		this.maxidle = maxidle;
	}

	public String getSingleip() {
		return singleip;
	}

	public void setSingleip(String singleip) {
		this.singleip = singleip;
	}

	public int getSingleport() {
		return singleport;
	}

	public void setSingleport(int singleport) {
		this.singleport = singleport;
	}

}
