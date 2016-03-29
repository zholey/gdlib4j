/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.cache;

import java.util.Collection;

/**
 * 分布式缓存服务接口(Distributed)
 * 
 * @author lei
 */
public interface ICache {

	/**
	 * 模糊查找所有的Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	public Collection<String> keys(String scope, String key);

	/**
	 * 模糊查找所有的Value
	 * 
	 * @param scope
	 * @param key
	 * @param objClass
	 * @return
	 */
	public <T> Collection<T> values(String scope, String key, Class<T> objClass);

	/**
	 * 测试缓存中是否包含指定的Key
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	public boolean containsKey(String scope, String key);

	/**
	 * 从缓存中获取指定Key对应的原始值
	 * 
	 * @param scope
	 * @param key
	 * @return
	 */
	public String get(String scope, String key);

	/**
	 * 从缓存中获取指定Key对应的对象
	 * 
	 * @param scope
	 * @param key
	 * @param objClass
	 * @return
	 */
	public <T> T getObject(String scope, String key, Class<T> objClass);

	/**
	 * 从缓存中获取指定Key对应的对象
	 * 
	 * @param scope
	 * @param key
	 * @param objClass
	 * @param actualTypeClass
	 * @return
	 */
	public <T> T getObject(String scope, String key, Class<T> objClass, Class<?> actualTypeClass);

	/**
	 * 向缓存中设置一个Key用作标记
	 * 
	 * @param scope
	 * @param key
	 */
	public void put(String scope, String key);

	/**
	 * 向缓存中设置一个Key用作标记
	 * 
	 * @param scope
	 * @param key
	 * @param expired
	 *            过期时间(S)
	 */
	public void put(String scope, String key, int expired);

	/**
	 * 向缓存中设置值
	 * 
	 * @param scope
	 * @param key
	 * @param value
	 */
	public void put(String scope, String key, Object value);

	/**
	 * 向缓存中设置值
	 * 
	 * @param scope
	 * @param key
	 * @param value
	 * @param expired
	 *            过期时间(S)
	 */
	public void put(String scope, String key, Object value, int expired);

	/**
	 * 删除指定的缓存
	 * 
	 * @param scope
	 * @param key
	 */
	public void remove(String scope, String key);

	/**
	 * 更新缓存中某个Key的过期时间
	 * 
	 * @param scope
	 * @param key
	 * @param expired
	 *            新的过期时间(S)
	 */
	public void setExpire(String scope, String key, int expired);
}
