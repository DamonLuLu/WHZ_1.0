package com.redisclients;

import java.util.ResourceBundle;

//redis 缓存工厂
public class RedisClientFactory {
	private static RedisSingleConfig systemSingleConfig;
	private static RedisClusterConfig systemClusterConfig;

	// 获取单例配置
	private static RedisSingleConfig getSingleConfig() {
		// 加载输入流
		ResourceBundle bundle = ResourceBundle.getBundle("redis_single");
		if (bundle != null) {
			systemSingleConfig = new RedisSingleConfig();
			// 获得配置的各个属性
			// ----单机配置---
			if (bundle.containsKey("redis.ip")) {
				systemSingleConfig.setSingleip(bundle.getString("redis.ip"));// 获取ip
			}
			if (bundle.containsKey("redis.port")) {
				systemSingleConfig.setSingleport(Integer.valueOf(bundle.getString("redis.port")));// 获取端口
			}
			if (bundle.containsKey("redis.maxidle")) {
				systemSingleConfig.setMaxidle(Integer.valueOf(bundle.getString("redis.maxidle")));// 获取最在空闲链接
			}

		}
		return systemSingleConfig;
	}

	// 获取集群配置
	private static RedisClusterConfig getClusterConfig() {

		ResourceBundle bundle = ResourceBundle.getBundle("redis_cluster");
		if (bundle != null) {
			systemClusterConfig = new RedisClusterConfig();
			// 获得配置的各个属性

			// ----集群配置----
			if (bundle.containsKey("redis.clusterip")) {
				systemClusterConfig.setClusterip(bundle.getString("redis.clusterip"));// 获取集群ip
			}
			if (bundle.containsKey("redis.clusterport")) {
				systemClusterConfig.setClusterport(bundle.getString("redis.clusterport"));// 获取集群端口
			}
			if (bundle.containsKey("redis.maxactive")) {
				systemClusterConfig.setMaxactive(Integer.valueOf(bundle.getString("redis.maxactive")));// 获取最大少跃数
			}
			if (bundle.containsKey("redis.timeout")) {
				systemClusterConfig.setTimeout(Integer.valueOf(bundle.getString("redis.timeout")));// 获取超时时间
			}
		}
		return systemClusterConfig;
	}

	public static void setClusterConfig(RedisClusterConfig config) {
		systemClusterConfig = config;
	}

	public static void setSingleConfig(RedisSingleConfig config) {
		systemSingleConfig = config;
	}

	// 重要数据存储,需长期存储
	private static RedisClient redisClient;

	// 单机redis客户端
	public synchronized static RedisClient getRedisClient() {
		if (redisClient == null) {
			if (systemSingleConfig == null) {
				systemSingleConfig = getSingleConfig();
			}
			redisClient = new RedisClient(systemSingleConfig.getSingleip(), systemSingleConfig.getSingleport(), systemSingleConfig.getMaxidle());
		}
		
		System.out.println("redisClient IP:"+systemSingleConfig.getSingleip()+"redisClient PORT"+systemSingleConfig.getSingleport());
		return redisClient;
	}

	// redis 集群连接
	private static RedisClusterClient redisClusterClient;

	// 集群redis客户端
	public synchronized static RedisClusterClient getRedisCluster() {
		if (redisClusterClient == null) {
			if (systemClusterConfig == null) {

				systemClusterConfig = getClusterConfig();
			}
			redisClusterClient = new RedisClusterClient(systemClusterConfig.getClusterip(), systemClusterConfig.getClusterport(), systemClusterConfig.getTimeout(), systemClusterConfig.getMaxactive());
		}
		
		/*System.out.println("------------------redisClusterClient systemClusterConfig:" + "-----getClusterip" +  systemClusterConfig.getClusterip() 
				+ "------getClusterport:" + systemClusterConfig.getClusterport() + "---getTimeout:" + systemClusterConfig.getTimeout() + "----getMaxactive:" + systemClusterConfig.getMaxactive());*/
		
		return redisClusterClient;
	}

}
