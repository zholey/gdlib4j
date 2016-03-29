/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.dao;

import java.util.Map;

import org.gridsofts.dao.itf.IConnectionFactory;
import org.gridsofts.dao.proxy.BeanFactory;

/**
 * 本类是{@link SqlSupportDAO}的子类，为其父类提供了代理功能扩展。<br/>
 * 通过本类的相关方法返回的JavaBean中包含相应的级联回调拦截方法，可用于JavaBean的自动级联查询。
 * 
 * @author Lei
 */
public class SqlCascadedDAO extends SqlSupportDAO {
	private static final long serialVersionUID = 1L;
	
	protected SqlCascadedDAO() {
	}

	protected SqlCascadedDAO(IConnectionFactory factory) {
		super(factory);
	}

	@Override
	protected <T> T createBean(Class<T> t, Map<String, Object> nameValueMap) {

		try {
			return BeanFactory.createProxyBean(this, t, nameValueMap);
		} catch (Throwable e) {
		}

		return null;
	}
}
