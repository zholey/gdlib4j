/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gridsofts.dao.exception.DAOException;
import org.gridsofts.dao.itf.IConnectionFactory;
import org.gridsofts.sql.PResultSet;
import org.gridsofts.util.BeanUtil;

/**
 * 提供与自然SQL相关的处理方法的DAO
 * 
 * @author Lei
 */
public abstract class SqlSupportDAO extends JdbcSupportDAO {
	private static final long serialVersionUID = 1L;

	protected SqlSupportDAO() {
	}

	protected SqlSupportDAO(IConnectionFactory factory) {
		super(factory);
	}

	@Override
	public long getUniqueValue(String sql, Object... param) throws DAOException {
		
		PreparedStatement stat = null;
		PResultSet rs = null;

		try {
			// 连接数据库
			requestConnection();

			try {

				stat = conn.prepareStatement(sql.toString());

				if (param != null) {
					for (int i = 0; i < param.length; i++) {
						stat.setObject(i + 1, param[i]);
					}
				}

				// 执行SQL
				rs = new PResultSet(stat.executeQuery());

			} catch (SQLException e) {
				throw new DAOException("执行数据库查询时出现异常，原始信息：" + e.getMessage());
			}

		} finally {
			releaseConnection();
		}

		//
		if (rs != null && rs.next()) {
			return rs.getLong(1);
		}

		return 0;
	}

	@Override
	public synchronized <T> T executeUniqueQuery(Class<T> t, String sql, Object... param) throws DAOException {

		List<T> list = executeQuery(t, -1, -1, sql, param);

		if (list.size() > 1) {
			throw new DAOException("结果不唯一");
		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	@Override
	public synchronized <T> List<T> executeQuery(Class<T> t, String sql, Object... param) throws DAOException {
		return executeQuery(t, -1, -1, sql, param);
	}

	@Override
	public synchronized <T> List<T> executeQuery(Class<T> t, int start, int limit, String sql, Object... param)
			throws DAOException {

		List<T> list = new ArrayList<T>();

		PreparedStatement stat = null;
		PResultSet rs = null;

		requestConnection();

		try {

			// 查找方言
			String dialectSql = sql;

			if (dialect != null) {
				dialectSql = dialect.getPageSQL(dialectSql, start, limit);
			}

			stat = conn.prepareStatement(dialectSql);

			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					stat.setObject(i + 1, param[i]);
				}
			}

			// 执行SQL
			rs = new PResultSet(stat.executeQuery());

		} catch (SQLException e) {
			throw new DAOException("执行数据库查询时出现异常，原始信息：" + e.getMessage());
		} finally {
			releaseConnection();
		}

		// 遍历结果集，构造对象
		while (rs != null && rs.next()) {
			list.add(createBean(t, rs.getRowValueMap()));
		}

		return list;
	}

	@Override
	public synchronized int executeUpdate(String sql, Object... param) throws DAOException {

		int uptRresult = 0;

		PreparedStatement stat = null;

		requestConnection();

		try {

			stat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					stat.setObject(i + 1, param[i]);
				}
			}

			uptRresult = stat.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException("执行数据库查询时出现异常，原始信息：" + e.getMessage());
		} finally {
			releaseConnection();
		}

		return uptRresult;
	}

	@Override
	public synchronized Map<String, Object> executeUniqueQuery(String sql, Object... param) throws DAOException {

		List<Map<String, Object>> list = executeQuery(-1, -1, sql, param);

		if (list.size() > 1) {
			throw new DAOException("结果不唯一");
		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	@Override
	public synchronized List<Map<String, Object>> executeQuery(String sql, Object... param) throws DAOException {
		return executeQuery(-1, -1, sql, param);
	}

	@Override
	public synchronized List<Map<String, Object>> executeQuery(int start, int limit, String sql, Object... param)
			throws DAOException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		PreparedStatement stat = null;
		ResultSet rs = null;

		requestConnection();

		try {

			// 查找方言
			String dialectSql = sql;

			if (dialect != null) {
				dialectSql = dialect.getPageSQL(dialectSql, start, limit);
			}

			stat = conn.prepareStatement(dialectSql);

			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					stat.setObject(i + 1, param[i]);
				}
			}

			// 执行SQL
			rs = stat.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();

			// 遍历结果集，构造Map
			while (rs != null && rs.next()) {
				list.add(BeanUtil.getEntityMap(rs, rsmd));
			}

		} catch (SQLException e) {
			throw new DAOException("执行数据库查询时出现异常，原始信息：" + e.getMessage());
		} finally {
			releaseConnection();
		}

		return list;
	}
}
