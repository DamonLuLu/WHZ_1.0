package com.redisclients;

public class RedisClusterConfig {
	// 集群ip
	private String clusterip;
	// 集群端口
	private String clusterport;
	// 超时时间
	private int timeout;
	// 最大连接数
	private int maxactive;

	public String getClusterip() {
		return clusterip;
	}

	public void setClusterip(String clusterip) {
		this.clusterip = clusterip;
	}

	public String getClusterport() {
		return clusterport;
	}

	public void setClusterport(String clusterport) {
		this.clusterport = clusterport;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxactive() {
		return maxactive;
	}

	public void setMaxactive(int maxactive) {
		this.maxactive = maxactive;
	}

}
