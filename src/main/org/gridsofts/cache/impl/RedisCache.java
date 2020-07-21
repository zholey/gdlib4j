/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.gridsofts.cache.ICache;
import org.gridsofts.json.JacksonHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis的缓存实现类
 * 
 * @author lei
 */
public class RedisCache implements ICache {
	private static JedisPool jedisPool;

	private JacksonHelper jackson = JacksonHelper.newInstance();

	private int maxTotal = 500;
	private int maxIdle = 5;
	private long maxWaitMillis = 1000 * 100;

	/** Redis 主机地址 */
	private String redisHost;

	/** Redis 端口号 */
	private int redisPort;

	/** Key namespace */
	private String namespace;

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public RedisCache() {
	}

	public void connect() {

		if (jedisPool == null) {
			JedisPoolConfig config = new JedisPoolConfig();

			config.setMaxTotal(maxTotal);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWaitMillis);
			config.setTestOnBorrow(true);

			jedisPool = new JedisPool(config, redisHost, redisPort);
		}
	}

	public void disconnect() {
		
		if (jedisPool != null) {
			jedisPool.destroy();
			jedisPool = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gridsofts.cache.ICache#keys(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<String> keys(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			return jedis.keys(obtainKey(scope, key));
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gridsofts.cache.ICache#values(java.lang.String,
	 * java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> Collection<T> values(String scope, String key, Class<T> objClass) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> keys = jedis.keys(obtainKey(scope, key));

			Collection<T> values = null;
			if (keys != null) {
				values = new ArrayList<>();

				for (String k : keys) {

					try {
						String objJsonStr = jedis.get(obtainKey(scope, k));
						values.add(jackson.getObject(objJsonStr, objClass));
					} catch (Throwable e) {
					}
				}
			}

			return values;
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			return jedis.exists(obtainKey(scope, key));
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#get(java.lang.String)
	 */
	@Override
	public String get(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			return jedis.get(obtainKey(scope, key));
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> T getObject(String scope, String key, Class<T> objClass) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (key != null && objClass != null && jedis.exists(obtainKey(scope, key))) {
				String jsonVal = jedis.get(obtainKey(scope, key));

				return jackson.getObject(jsonVal, objClass);
			}

			return null;
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> T getObject(String scope, String key, Class<T> objClass, Class<?> actualTypeClass) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (key != null && objClass != null && jedis.exists(obtainKey(scope, key))) {
				String jsonVal = jedis.get(obtainKey(scope, key));

				return jackson.getObject(jsonVal, objClass, actualTypeClass);
			}

			return null;
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key) {
		put(scope, key, null, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, int expired) {
		put(scope, key, null, expired);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value) {
		put(scope, key, value, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value, int expired) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (key != null) {
				String jsonVal = ((value == null) ? ""
						: (value instanceof String) ? value.toString() : jackson.toJson(value));

				if (expired > 0) {
					jedis.setex(obtainKey(scope, key), expired, jsonVal);
				} else {
					jedis.set(obtainKey(scope, key), jsonVal);
				}
			}
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#remove(java.lang.String)
	 */
	@Override
	public void remove(String scope, String key) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			jedis.del(obtainKey(scope, key));
		} finally {
			jedis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.common.cache.ICache#setExpired(java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public void setExpire(String scope, String key, int expired) {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			jedis.expire(obtainKey(scope, key), expired);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 构造缓存Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	private String obtainKey(String scope, String key) {
		return namespace + "." + scope + "." + key;
	}
}
