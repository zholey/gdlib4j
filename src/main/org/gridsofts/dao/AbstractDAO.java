/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gridsofts.dao.exception.ConnectionException;
import org.gridsofts.dao.exception.DAOException;
import org.gridsofts.dao.itf.IConnectionFactory;
import org.gridsofts.dao.itf.IDialect;
import org.gridsofts.dao.itf.ITypeConverter;
import org.gridsofts.util.StringUtil;

/**
 * DAO基类，用于实现一些与 Connection & transaction 有关的方法<br>
 * <br>
 * 
 * 注意：<br>
 * 手动事务控制时，事务之间不能互相嵌套。<br>
 * 
 * 对连接池的实现在外部完成
 * 
 * @author Lei
 */
abstract class AbstractDAO implements Serializable {
	private static final long serialVersionUID = 1L;

	protected IDialect dialect = null;
	protected List<ITypeConverter> typeConverters = null;

	// 标识事务状态（True表示已经手动开启，False表示未手动开启）
	protected boolean transactionOpenState = false;

	protected IConnectionFactory factory;
	protected Connection conn;

	protected AbstractDAO() {
		this(null);
	}

	protected AbstractDAO(IConnectionFactory factory) {
		this.factory = factory;

		// 加载配置信息（如果有）
		Properties property = new Properties();
		try {
			property.load(AbstractDAO.class.getResourceAsStream("/etc/GDlib4j.dao.properties"));

			if (property.containsKey("dialect")) {
				Class<?> dialectCls = Class.forName(property.getProperty("dialect"));

				dialect = (IDialect) dialectCls.newInstance();
				dialect.setProperties(property);
			}

			if (property.containsKey("typeConverter")
					&& !StringUtil.isNull(property.getProperty("typeConverter"))) {

				String[] converterTypes = property.getProperty("typeConverter").trim()
						.split("(?m)\\s*,\\s*");

				if (converterTypes != null && converterTypes.length > 0) {

					for (String type : converterTypes) {
						Class<?> cls = Class.forName(type.trim());

						if (cls != null && ITypeConverter.class.isAssignableFrom(cls)) {
							addTypeConverter(ITypeConverter.class.cast(cls.newInstance()));
						}
					}
				}
			}
		} catch (Throwable e) {
		}
	}
	
	/**
	 * 获取注册于该DAO的所有类型转换器
	 * 
	 * @return
	 */
	public List<ITypeConverter> getTypeConverters() {
		return typeConverters;
	}
	
	/**
	 * 重置该DAO的类型转换器
	 */
	public void setTypeConverters(List<ITypeConverter> typeConverters) {
		this.typeConverters = typeConverters;
	}
	
	/**
	 * 向该DAO注册类型转换器
	 */
	public void addTypeConverter(ITypeConverter typeConverter) {
		
		if (getTypeConverters() == null) {
			setTypeConverters(new ArrayList<ITypeConverter>());
		}
		
		getTypeConverters().add(typeConverter);
	}

	/**
	 * 允许注入不同的Factory
	 * 
	 * @param factory
	 */
	public synchronized void setFactory(IConnectionFactory factory) {
		this.factory = factory;
	}

	/**
	 * 手动开启事务
	 * 
	 * @throws DAOException
	 */
	public synchronized void beginTransaction() throws DAOException {

		requestConnection();

		if (isConnectionValid() && !transactionOpenState) {

			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DAOException("无法将该连接的自动提交模式设置为False，原始信息：" + e.getMessage());
			}

			transactionOpenState = true;
		} else {
			throw new ConnectionException("无法获取数据库连接，或事务已经开启！");
		}
	}

	/**
	 * 手动关闭事务（提交）
	 * 
	 * @throws DAOException
	 */
	public synchronized void endTransaction() throws DAOException {

		try {
			if (isConnectionValid() && transactionOpenState) {

				try {
					conn.commit();
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					throw new DAOException("手动关闭事务时出现异常，原始信息：" + e.getMessage());
				}
			} else {
				throw new ConnectionException("无法获取数据库连接，或事务已经关闭！");
			}
		} finally {
			cleanTransaction();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @throws DAOException
	 */
	public synchronized void rollBackTransaction() throws DAOException {

		try {
			if (isConnectionValid() && transactionOpenState) {

				try {
					conn.rollback();
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					throw new DAOException("回滚事务时出现异常，原始信息：" + e.getMessage());
				}
			} else {
				throw new ConnectionException("无法获取数据库连接，或事务已经关闭！");
			}
		} finally {
			cleanTransaction();
		}
	}

	/**
	 * 关闭事务，并关闭数据库连接
	 */
	protected synchronized void cleanTransaction() {

		transactionOpenState = false;

		if (isConnectionValid()) {
			factory.release(conn);
		}
	}

	/**
	 * 请求连接
	 * 
	 * @throws ConnectionException
	 */
	protected synchronized void requestConnection() throws ConnectionException {

		// 当连接无效，或未手动开启事务时请求连接
		if (!isConnectionValid() || !transactionOpenState) {
			conn = factory.getConnection();
		}

		if (conn == null) {
			throw new ConnectionException();
		}
	}

	/**
	 * 关闭连接
	 */
	protected synchronized void releaseConnection() {

		// 当连接有效，并且未手动开启事务时关闭连接
		if (isConnectionValid() && !transactionOpenState) {
			factory.release(conn);
		}
	}

	/**
	 * 测试连接的有效性
	 * 
	 * @return
	 */
	protected synchronized boolean isConnectionValid() {

		try {
			return conn != null && !conn.isClosed();
		} catch (Throwable e) {
		}

		return false;
	}
}
