package com.api.dao;

import com.redisclients.RedisClientFactory;
import com.redisclients.RedisClusterClient;

public class BaseDao {
	// redis服务
		public RedisClusterClient redis = RedisClientFactory.getRedisCluster();

}
