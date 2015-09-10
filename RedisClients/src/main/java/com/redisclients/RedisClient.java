package com.redisclients;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

public class RedisClient {
	// redis ip
	private String redisIp;
	// redis 端口
	private int redisPort;
  //最大空闲数 
	private int maxidle;
	// 提供ip和port创建客户端
	public RedisClient(String ip, int port,int idle) {
		this.redisIp = ip;
		this.redisPort = port;
		this.maxidle=idle;
		System.out.println("ip:" + ip + " port:" + port);
	}


	public int DBIndex;
	// 创建连接池
	private JedisPool jedisPool = null;

	private JedisPool getPool() {
		if (jedisPool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			// config.setMaxActive(PropertiesUtils.getRedisInstance().REDIS_MAXACTIVE);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(this.maxidle);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			// config.setMaxWait(PropertiesUtils.getRedisInstance().REDIS_MAXWAIT);//
			// 1000 * 100
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			jedisPool = new JedisPool(config, this.redisIp, this.redisPort,0);
		}
		return jedisPool;
	}

	// 获取连如此redis对象
	public Jedis getJedis() {
		// 连接池
		JedisPool pool = getPool();
		Jedis jedis = null;
		
		try {
			jedis = pool.getResource();
		} catch (JedisException e) {
			
			if (jedis != null) {
				pool.returnBrokenResource(jedis);

			}
			throw e;
		} 
		return jedis;
	}

	// 放回链接池
	public void release(Jedis jedis, boolean isBroken) {
		if (jedis != null) {
			if (isBroken) {
				jedisPool.returnBrokenResource(jedis);
			} else {
				jedisPool.returnResource(jedis);
			}
		}
	}

	// 添加字符串到redis
	public String addStringToJedis(String key, String value) {
		return this.addStringToJedis(key, value, -1, "");
	}

	// 添加字符串到redis
	public String addStringToJedis(String key, String value, String methodName) {
		return this.addStringToJedis(key, value, -1, methodName);
	}

	// 添加字符串到redis
	public String addStringToJedis(String key, String value, int cacheSeconds) {
		return this.addStringToJedis(key, value, cacheSeconds, "");
	}

	public String addStringToJedis(String key, String value, int cacheSeconds,
			String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		String lastVal = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			lastVal = jedis.set(key, value);
			if (cacheSeconds > 0) {
				jedis.expire(key, cacheSeconds);
			}

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return lastVal;
	}

	// 更改key名称
	public String renameToJedis(String key, String oldkey) {
		Jedis jedis = null;
		boolean isBroken = false;
		String lastVal = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			lastVal = jedis.rename(oldkey, key);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return lastVal;
	}

	// 添加字符串到redis
	public void addStringToJedis(Map<String, String> batchData,
			int cacheSeconds, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			Pipeline pipeline = jedis.pipelined();
			for (Map.Entry<String, String> element : batchData.entrySet()) {
				if (cacheSeconds > 0) {
					pipeline.setex(element.getKey(), cacheSeconds,
							element.getValue());
				} else {
					pipeline.set(element.getKey(), element.getValue());
				}
			}
			pipeline.sync();

		} catch (Exception e) {
			isBroken = true;
			e.printStackTrace();
		} finally {
			release(jedis, isBroken);
		}
	}

	// 添加列表到redis
	public void addListToJedis(String key, List<String> list, String methodName) {
		this.addListToJedis(key, list, -1, methodName);
	}

	// 添加列表到redis
	public void addListToJedis(String key, List<String> list, int cacheSeconds,
			String methodName) {
		if (list != null && list.size() > 0) {
			Jedis jedis = null;
			boolean isBroken = false;
			try {
				jedis = this.getJedis();
				jedis.select(DBIndex);
				if (jedis.exists(key)) {
					jedis.del(key);
				}
				for (String aList : list) {
					jedis.rpush(key, aList);
				}
				if (cacheSeconds > 0) {
					jedis.expire(key, cacheSeconds);
				}
			} catch (JedisException e) {
				isBroken = true;
			} catch (Exception e) {
				isBroken = true;
			} finally {
				release(jedis, isBroken);
			}
		}
	}

	// 添加数组到redis
	public void addToSetJedis(String key, String[] value, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			jedis.sadd(key, value);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
	}

	// 从redis中删除数据
	public void removeSetJedis(String key, String value, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
	}

	// 追加数据到redis列表中
	public void pushDataToListJedis(String key, String data, int cacheSeconds,
			String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();

			jedis.select(DBIndex);
			jedis.rpush(key, data);
			if (cacheSeconds > 0) {
				jedis.expire(key, cacheSeconds);
			}
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
	}

	// 追加数据到redis列表中
	public void pushDataToListJedis(String key, List<String> batchData,
			int cacheSeconds, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			jedis.del(key);
			jedis.lpush(key, batchData.toArray(new String[batchData.size()]));
			if (cacheSeconds > 0)
				jedis.expire(key, cacheSeconds);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
	}

	/**
	 * 删除list中的元素
	 * 
	 * @param key
	 * @param values
	 * @param methodName
	 */
	public void deleteDataFromListJedis(String key, List<String> values,
			String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			Pipeline pipeline = jedis.pipelined();
			if (values != null && !values.isEmpty()) {
				for (String val : values) {
					pipeline.lrem(key, 0, val);
				}
			}
			pipeline.sync();
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
	}

	// 添加map到redis 中
	public void addHashMapToJedis(String key, Map<String, String> map,
			String methodName) {
		this.addHashMapToJedis(key, map, -1, methodName);
	}

	public void addHashMapToJedis(String key, Map<String, String> map,
			int cacheSeconds, String methodName) {
		boolean isBroken = false;
		Jedis jedis = null;
		if (map != null && map.size() > 0) {
			try {
				jedis = this.getJedis();
				jedis.select(DBIndex);
				jedis.hmset(key, map);

				if (cacheSeconds >= 0)
					jedis.expire(key, cacheSeconds);
			} catch (Exception e) {
				isBroken = true;
			} finally {
				release(jedis, isBroken);
			}
		}
	}

	// 添加map到redis 中
	public void addHashMapToJedis(String key, String field, String value,
			int cacheSeconds, String methodName) {
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			if (jedis != null) {
				jedis.select(DBIndex);
				jedis.hset(key, field, value);
				jedis.expire(key, cacheSeconds);
			}
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
	}

	// 更新map到redis 中
	public void updateHashMapToJedis(String key, String incrementField,
			long incrementValue, String dateField, String dateValue,
			String methodName) {
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			jedis.hincrBy(key, incrementField, incrementValue);
			jedis.hset(key, dateField, dateValue);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
	}

	// 从redis 中获取字符串
	public String getStringFromJedis(String key, String methodName) {
		String value = null;
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			if (jedis.exists(key)) {
				value = jedis.get(key);

			}
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return value;
	}

	// 从redis 中获取字符串
	public String getStringFromJedis(String key) {
		String value = null;
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			if (jedis.exists(key)) {
				value = jedis.get(key);

			}
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return value;
	}

	// 从redis 中获取字符串
	public List<String> getStringFromJedis(String[] keys, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.mget(keys);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	// 从redis 中获取列表
	public List<String> getListFromJedis(String key, String methodName) {
		List<String> list = null;
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			if (jedis.exists(key)) {
				list = jedis.lrange(key, 0, -1);

			}
		} catch (JedisException e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return list;
	}

	// 从redis 中获取Set列表
	public Set<String> getSetFromJedis(String key, String methodName) {
		Set<String> list = null;
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			if (jedis.exists(key)) {
				list = jedis.smembers(key);
			}
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return list;
	}

	// 从redis 中获取map
	public Map<String, String> getHashMapFromJedis(String key, String methodName) {
		Map<String, String> hashMap = null;
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			hashMap = jedis.hgetAll(key);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return hashMap;
	}

	// 从redis 中获取map
	public String getHashMapValueFromJedis(String key, String field,
			String methodName) {
		String value = null;
		boolean isBroken = false;
		Jedis jedis = null;
		try {
			jedis = this.getJedis();
			if (jedis != null) {
				jedis.select(DBIndex);
				if (jedis.exists(key)) {
					value = jedis.hget(key, field);

				}
			}
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return value;
	}

	// 从redis 中获取id
	public Long getIdentifyId(String identifyName, String methodName) {
		boolean isBroken = false;
		Jedis jedis = null;
		Long identify = null;
		try {
			jedis = this.getJedis();
			if (jedis != null) {
				jedis.select(DBIndex);
				identify = jedis.incr(identifyName);
				if (identify == 0) {
					return jedis.incr(identifyName);
				} else {
					return identify;
				}
			}
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	/**
	 * 删除某db的某个key值
	 * 
	 * @param key
	 * @return
	 */
	public Long delKeyFromJedis(String key) {
		boolean isBroken = false;
		Jedis jedis = null;
		long result = 0;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);

			return jedis.del(key);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return result;
	}

	/**
	 * 根据dbIndex flushDB每个shard
	 * 
	 * @param dbIndex
	 */
	public void flushDBFromJedis(int dbIndex) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(dbIndex);
			jedis.flushDB();

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
	}

	// 从redis 是否存在key
	public boolean existKey(String key, String methodName) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);

			return jedis.exists(key);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return false;
	}

	// 模糊匹配出keys,patternKey 0后面加＊，1前面加＊，2前后 都加＊
	public List<String> GetKeys(String name, int patternKey, int count) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			String pattern = "";
			switch (patternKey) {
			case 0:
				pattern = name + "*";
				break;
			case 1:
				pattern = "*" + name;
				break;
			case 2:
				pattern = "*" + name + "*";
				break;
			default:
				pattern = name + "*";
				break;
			}

			return this.GetKeys(pattern, count);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	public List<String> GetKeys(String pattern, int count) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);

			Set<String> list = this.GetKeys(pattern);
			// 如果count小于 等于0取所在
			List<String> rvtlist = new ArrayList<String>();
			if (list != null && list.size() > 0) {
				int index = 0;
				for (String item : list) {
					if (count > 0) {
						if (index < count) {
							rvtlist.add(item);
						} else {
							break;
						}
					} else {
						rvtlist.add(item);
					}
					index++;
				}
				return rvtlist;
			}
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	public Set<String> GetKeys(String pattern) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);

			return jedis.keys(pattern);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	// 保存排序列表
	public long SaveSortList(String key, String name, double source) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			jedis.zadd(key, source, name);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return -1;
	}

	// 保存排序列表
	public long SaveSortList(String key, Map<String, Double> list, int seconds) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			// 设轩过期时间
			jedis.expire(key, seconds);
			jedis.zadd(key, list);

		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return -1;
	}

	// 获取排序列表
	public Set<String> GetSortList(String key, int start, int end) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return null;
	}
	// remove sort list
	public long RemoveSortList(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.zrem(key, value);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return -1;
	}

	// 获取单个排序列表,按出列方式，取走就删除
	public String PopList(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.rpop(key);
		} catch (Exception e) {
			isBroken = true;
		} finally {
			release(jedis, isBroken);
		}
		return null;
	}

	// 添加单个到列表中
	public long PushList(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.rpush(key, value);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return -1;
	}

	// 添加多个到列表中
	public long PushList(String key, String[] values) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.rpush(key, values);
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return -1;
	}

	// 获取排序列表
	public int GetSortTotal(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = this.getJedis();
			jedis.select(DBIndex);
			return jedis.zrange(key, 0, -1).size();
		} catch (Exception e) {
			isBroken = true;

		} finally {
			release(jedis, isBroken);
		}
		return 0;
	}
}
