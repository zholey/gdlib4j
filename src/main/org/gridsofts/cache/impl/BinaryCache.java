/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.cache.impl;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.gridsofts.cache.ICache;
import org.gridsofts.util.BeanUtil;
import org.gridsofts.util.StringUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis的缓存实现类；采用字节数组存储缓存数据，加快存取速度
 * 
 * @author lei
 */
public class BinaryCache implements ICache {
	private static JedisPool jedisPool;

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

	public BinaryCache() {
	}
	
	public void connect() {
		connect(false);
	}

	public void connect(boolean force) {
		
		if (force) {
			disconnect();
		}

		if (jedisPool == null && !StringUtil.isNull(redisHost) && redisPort > 0) {
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
			Collection<String> kList = null;

			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (jedis != null) {
					Set<byte[]> keys = jedis.keys(obtainKey(scope, key));

					if (keys != null && !keys.isEmpty()) {

						return keys.parallelStream().map(k -> {
							return obtainPrimitiveKey(scope, toString(k));
						}).collect(Collectors.toSet());
					}
				}
			}

			return kList;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gridsofts.cache.ICache#values(java.lang.String, java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> Collection<T> values(String scope, String key, Class<T> objClass) {

		Jedis jedisP = null;
		try {
			Collection<T> values = null;

			if (jedisPool != null) {
				final Jedis jedis = jedisPool.getResource();
				
				jedisP = jedis;

				if (jedis != null) {
					Set<byte[]> keys = jedis.keys(obtainKey(scope, key));

					if (keys != null && !keys.isEmpty()) {

						return keys.parallelStream().map(k -> {
							try {
								return BeanUtil.convertToObject(jedis.get(k), objClass);
							} catch (Throwable e) {
								return null;
							}
						}).collect(Collectors.toSet());
					}
				}
			}

			return values;
		} finally {
			if (jedisP != null) {
				jedisP.close();
			}
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
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				return jedis != null && jedis.exists(obtainKey(scope, key));
			}

			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
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
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (jedis != null) {
					byte[] bytesVal = jedis.get(obtainKey(scope, key));

					try {
						return new String(bytesVal, "UTF-8");
					} catch (Throwable e) {
						return new String(bytesVal);
					}
				}
			}

			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
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
		return getObject(scope, key, objClass, null);
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
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (key != null && objClass != null && jedis != null && jedis.exists(obtainKey(scope, key))) {
					byte[] bytesVal = jedis.get(obtainKey(scope, key));

					return BeanUtil.convertToObject(bytesVal, objClass);
				}
			}

			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String, java.lang.String)
	 */
	@Override
	public void put(String scope, String key) {
		put(scope, key, null, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String, java.lang.String)
	 */
	@Override
	public void put(String scope, String key, int expired) {
		put(scope, key, null, expired);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String, java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value) {
		put(scope, key, value, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uptech.homer.cache.ICache#put(java.lang.String, java.lang.String)
	 */
	@Override
	public void put(String scope, String key, Object value, int expired) {

		Jedis jedis = null;
		try {
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (key != null && jedis != null) {
					byte[] bytesVal = BeanUtil.convertToBytes(value == null ? "" : value);

					if (expired > 0) {
						jedis.setex(obtainKey(scope, key), expired, bytesVal);
					} else {
						jedis.set(obtainKey(scope, key), bytesVal);
					}
				}
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
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
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (jedis != null) {
					jedis.del(obtainKey(scope, key));
				}
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
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
			if (jedisPool != null) {
				jedis = jedisPool.getResource();

				if (jedis != null) {
					jedis.expire(obtainKey(scope, key), expired);
				}
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 构造缓存Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	private byte[] obtainKey(String scope, String key) {
		String k = namespace + "." + scope + "." + key;

		try {
			return k.getBytes("UTF-8");
		} catch (Throwable e) {
			return k.getBytes();
		}
	}

	/**
	 * 将字节数组转换为字符串
	 * 
	 * @param keyBytes
	 * @return
	 */
	private String toString(byte[] keyBytes) {

		try {
			return new String(keyBytes, "UTF-8");
		} catch (Throwable e) {
			return new String(keyBytes);
		}
	}

	/**
	 * 获取原始Key（去掉前缀）
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	private String obtainPrimitiveKey(String scope, String key) {
		String prefix = namespace + "." + scope + ".";

		if (key.startsWith(prefix)) {
			return key.replaceFirst(prefix, "");
		}

		return key;
	}
}
