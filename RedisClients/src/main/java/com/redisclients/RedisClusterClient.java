package com.redisclients;

import java.util.*;


import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.SortingParams;

public class RedisClusterClient {
	// redis ip
	private String redisIp;
	// redis 端口
	private String redisPort;
	// 集群
	private static JedisCluster jedis;
	// 超时时间
	private int timeout;
	// 最大链接数
	private int maxactive;

	
	// 提供ip和port创建客户端
	public RedisClusterClient(String ip, String port, int timeout, int active) {
		this.redisIp = ip;
		this.redisPort = port;
		this.timeout = timeout;
		this.maxactive = active;
		System.out.println("init ip:" + ip + "init port:" + port);
		jedis = this.getCluster();
	}

	// 获取集群连接
	private JedisCluster getCluster() {
		try {
			if (jedis == null) {
				String[] iplist = this.redisIp.trim().split(",");
				String[] portlist = this.redisPort.trim().split(",");
				Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
				// Jedis Cluster will attempt to discover cluster nodes
				// automatically
				for (int i = 0; i < iplist.length; i++) {
					String ipStr = iplist[i];
					String portStr = portlist[i];
					System.out.println("ip:" + ipStr + "  port:" + portStr);
					if (null != ipStr && !ipStr.trim().equals("") && null != portStr && !portStr.trim().equals("")) {
						int port = Integer.parseInt(portStr.trim());
						// 添加集群地址
						jedisClusterNodes.add(new HostAndPort(ipStr.trim(), port));
					}
				}

				jedis = new JedisCluster(jedisClusterNodes, this.timeout, this.maxactive);
			}
			return jedis;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// 把字符串数据添加到缓存
	public String setStringToRedis(String key, String value) {
		return this.setStringToRedis(key, value, 0);
	}

	// 把字符串数据添加到缓存
	public String setStringToRedis(String key, String value, int seconds) {
		try {
			jedis = this.getCluster();

			String rtv = jedis.set(key, value);
			if (seconds > 0) {
				jedis.expire(key, seconds);
			}
			return rtv;
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 从redis 获取字符串信息
	public String getStringFromRedis(String key) {
		try {
			jedis = this.getCluster();
			return jedis.get(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 删除指定key值
	public Long delKeyFromRedis(String key) {
		try {
			jedis = this.getCluster();
			return jedis.del(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1l;
	}

	// 从redis 是否存在key
	public boolean existKey(String key) {
		try {
			jedis = this.getCluster();
			return jedis.exists(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return false;
	}

	/***************** Set相关 ******************/
	// 添加数组到redis
	public Long addToSetRedis(String key, String[] value) {

		try {
			jedis = this.getCluster();
			return jedis.sadd(key, value);
		} catch (Exception ex) {
			this.error(ex);
		}
		return 0l;
	}

	public void zadd(String key, double weight, String member) {
		this.getCluster().zadd(key, weight, member);
	}

	public void zrem(String key, String member) {
		this.getCluster().zrem(key, member);
	}

	public long zcount(String key, double min, double max) {
		return this.getCluster().zcount(key, min, max);
	}

	// 从redis 中获取Set列表
	public Set<String> getSetFromRedis(String key) {
		Set<String> list = null;

		try {
			jedis = this.getCluster();
			if (jedis.exists(key)) {
				list = jedis.smembers(key);
			}
		} catch (Exception ex) {
			this.error(ex);
		}
		return list;
	}

	/******************** HashMap相关 **********************/
	// 添加map到redis 中
	public void addHashMapToRedis(String key, Map<String, String> map) {
		this.addHashMapToRedis(key, map, -1);
	}

	// 带过期时间map添加到redis中
	public void addHashMapToRedis(String key, Map<String, String> map, int cacheSeconds) {
		if (map != null && map.size() > 0) {
			try {
				jedis = this.getCluster();
				jedis.hmset(key, map);
				if (cacheSeconds >= 0)
					jedis.expire(key, cacheSeconds);
			} catch (Exception ex) {
				this.error(ex);
			}
		}
	}

	// 添加map到redis 中
	public void delHashMapFromRedis(String key, String field) {
		try {
			jedis = this.getCluster();
			if (jedis != null) {
				jedis.hdel(key, field);
			}
		} catch (Exception ex) {
			this.error(ex);
		}
	}

	// 添加map到redis 中
	public void addHashMapToRedis(String key, String field, String value) {
		this.addHashMapToRedis(key, field, value, 0);
	}

	// 添加map到redis 中
	public void addHashMapToRedis(String key, String field, String value, int cacheSeconds) {
		try {
			jedis = this.getCluster();
			if (jedis != null) {
				jedis.hset(key, field, value);
				if (cacheSeconds > 0) {
					jedis.expire(key, cacheSeconds);
				}
			}
		} catch (Exception ex) {
			this.error(ex);
		}
	}

	// 从map中删除信息
	public void delHashMapToRedis(String key, String... fields) {
		try {
			jedis = this.getCluster();
			if (jedis != null) {
				jedis.hdel(key, fields);
			}
		} catch (Exception ex) {
			this.error(ex);
		}
	}

	// 根据多个key获取map值
	public Map<String, String> getHashMapFromRedis(String key) {
		try {
			jedis = this.getCluster();
			return jedis.hgetAll(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 获取key列表
	public Set<String> getHashMapKeysFromRedis(String key) {
		try {
			jedis = this.getCluster();
			return jedis.hkeys(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 根据多个key获取map值
	public List<String> getHashMapFromFields(String key, String... fields) {
		try {
			jedis = this.getCluster();
			return jedis.hmget(key, fields);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 根据多个key获取map值
	public String getHashMapFromFieldByOne(String key, String field) {
		List<String> list = this.getHashMapFromFields(key, field);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	// 更新map到redis 中
	public void updateHashMapToRedis(String key, String field, String value) {

		try {
			jedis = this.getCluster();
			jedis.hset(key, field, value);

		} catch (Exception ex) {
			this.error(ex);
		}
	}

	// 增量更新map到redis 中
	public void incrementHashMapToRedis(String key, String field, long value) {

		try {
			jedis = this.getCluster();
			jedis.hincrBy(key, field, value);
		} catch (Exception ex) {
			this.error(ex);
		}
	}

	/***************** 列表相关 *****************/
	// 保存排序列表
	public long saveSortList(String key, String value, double source) {
		try {
			jedis = this.getCluster();
			return jedis.zadd(key, source, value);

		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 保存排序列表
	public long saveSortList(String key, Map<String, Double> list, int seconds) {
		try {
			jedis = this.getCluster();
			// 设轩过期时间
			jedis.expire(key, seconds);
			return jedis.zadd(key, list);
			
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 保存排序列表
	public long saveSortList(String key, Map<String, Double> list) {
		try {
			jedis = this.getCluster();
			return	jedis.zadd(key, list);
			
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 获取单个值的权重
	public Double getSortScore(String key, String member) {
		try {
			jedis = this.getCluster();
			return jedis.zscore(key, member);

		} catch (Exception ex) {
			this.error(ex);
		}
		return -100000000.0;
	}

	// 获取列表总数
	public long getListTotal(String key) {
		JedisCluster jedis = this.getCluster();
		try {

			return jedis.llen(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return 0;
	}

	// 获取排序列表倒序
	public Set<String> getSortList(String key, int start, int end) {
		try {
			jedis = this.getCluster();
			return jedis.zrevrange(key, start, end);
		} catch (Exception ex) {
			this.error(ex);

		}
		return null;
	}

	// 添加map到redis 中,不添加重复的值
	public void addHashMapNxToRedis(String key, String field, String value) {
		this.addHashMapNxToRedis(key, field, value, 0);
	}

	// 添加map到redis 中,不添加重复的值
	public void addHashMapNxToRedis(String key, String field, String value, int cacheSeconds) {
		JedisCluster jedis = this.getCluster();
		try {
			if (jedis != null) {
				if (jedis.hexists(key, field)) {
					jedis.hdel(key, field);
				}
				jedis.hset(key, field, value);
				if (cacheSeconds > 0) {
					jedis.expire(key, cacheSeconds);
				}
			}
		} catch (Exception ex) {
			this.error(ex);
		}
	}

	// remove sort list
	public long removeSortList(String key, String value) {
		try {
			jedis = this.getCluster();
			return jedis.zrem(key, value);
		} catch (Exception ex) {

			this.error(ex);
		}
		return -1;
	}

	// 获取单个排序列表,按出列方式，取走就删除
	public String popList(String key) {
		try {
			jedis = this.getCluster();
			return jedis.lpop(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 添加单个到列表中
	public long pushList(String key, String value) {
		try {
			jedis = this.getCluster();
			return jedis.rpush(key, value);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 添加多个到列表中
	public long pushList(String key, String[] values) {
		try {
			jedis = this.getCluster();
			return jedis.rpush(key, values);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 获取sortlist中的排名信息,倒序
	public long getSortRank(String key, String member) {
		try {
			jedis = this.getCluster();
			return jedis.zrevrank(key, member);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 获取排序列表
	public int getSortTotal(String key) {

		try {
			jedis = this.getCluster();
			return jedis.zrange(key, 0, -1).size();
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 获取自增
	public long getIncr(String key) {
		try {
			jedis = this.getCluster();
			return jedis.incr(key);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// 保存排序列表值
	public List<String> getSort(String key, int begin, int limit) {
		try {
			SortingParams sortingParameters = new SortingParams();
			sortingParameters.desc();
			sortingParameters.limit(begin, limit);
			jedis = this.getCluster();
			return jedis.sort(key, sortingParameters);
		} catch (Exception ex) {
			this.error(ex);
		}
		return null;
	}

	// 根据权重值获取排序列表值
	public Set<String> getSortedSetByScore(String key, double max, double min) {

		try {
			jedis = this.getCluster();
			return jedis.zrangeByScore(key, max, min);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Collections.emptySet();
	}

	// 错误消息处理
	public void error(Exception ex) {
		ex.printStackTrace();
	}

	// REDIS加锁
	public long setnx(String key, String locked) {
		try {
			jedis = this.getCluster();
			return jedis.setnx(key, locked);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

	// REDIS设置过期时间
	public long expire(String key, int expireTime) {
		try {
			jedis = this.getCluster();
			return jedis.expire(key, expireTime);
		} catch (Exception ex) {
			this.error(ex);
		}
		return -1;
	}

}
