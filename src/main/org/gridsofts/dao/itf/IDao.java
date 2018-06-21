/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.dao.itf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.gridsofts.dao.exception.DAOException;

/**
 * DAO对象的抽象接口
 * 
 * @author Lei
 */
public interface IDao extends Serializable {

	/**
	 * 获取注册于该DAO的所有类型转换器
	 * 
	 * @return 类型转换器列表
	 */
	public List<ITypeConverter> getTypeConverters();

	/**
	 * 重置该DAO的类型转换器
	 * 
	 * @param typeConverters
	 *            类型转换器列表
	 */
	public void setTypeConverters(List<ITypeConverter> typeConverters);

	/**
	 * 向该DAO注册类型转换器
	 * 
	 * @param typeConverter
	 *            准备注册的类型转换器
	 */
	public void addTypeConverter(ITypeConverter typeConverter);

	/**
	 * 根据主键，查询指定的Bean
	 * 
	 * @param <T>
	 * @param t
	 *            与准备要查询的表相映射的Bean的class对象
	 * @param key
	 *            任意数量的主键（至少一个）；如果有多个，需要与Table注解中指定的PrimaryKeys中的顺序保持一致
	 * @return 实际找到的Bean，如果未找到则返回null
	 * @throws DAOException
	 */
	public <T> T find(Class<T> t, Object... key) throws DAOException;

	/**
	 * 获取总记录数； 该方法只是为用户在使用count查询时提供一点点便利，实际运行时自动在表名前添加 了select count(0)语句
	 * 
	 * @param <T>
	 * @param t
	 *            与准备要查询的表相映射的Bean的class对象
	 * @param condition
	 *            具体的查询条件，需要自行添加“Where”、“And”等关键词。<br>
	 *            根据实际需要还可以拼接其它合适的语句，如 order子句
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return 该SQL的返回结果中，第一行第一列的值
	 * @throws DAOException
	 */
	public <T> long getTotalQuantity(Class<T> t, String condition, Object... param)
			throws DAOException;

	/**
	 * 获取指定的Bean所映射表的所有记录列表；
	 * 
	 * @param <T>
	 * @param t
	 *            List内将存放的Bean的class对象
	 * 
	 * @return 查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象； 注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名
	 *         ，如果未指定别名，则使用原始列名； 而Bean中的属性名称如果标注有Column注解
	 *         ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> List<T> list(Class<T> t) throws DAOException;

	/**
	 * 获取符合条件的Bean列表；
	 * 
	 * @param <T>
	 * @param t
	 *            List内将存放的Bean的class对象
	 * @param condition
	 *            具体的查询条件，需要自行添加“Where”、“And”等关键词。<br>
	 *            根据实际需要还可以拼接其它合适的语句，如 order子句
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return 查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象； 注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名
	 *         ，如果未指定别名，则使用原始列名； 而Bean中的属性名称如果标注有Column注解
	 *         ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> List<T> list(Class<T> t, String condition, Object... param) throws DAOException;

	/**
	 * 获取符合条件的Bean列表；支持分页查询
	 * 
	 * @param <T>
	 * @param t
	 *            List内将存放的Bean的class对象
	 * @param start
	 *            分页查询记录起始行数
	 * @param limit
	 *            分页查询记录数
	 * @param condition
	 *            具体的查询条件，需要自行添加“Where”、“And”等关键词。<br>
	 *            根据实际需要还可以拼接其它合适的语句，如 order子句
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return 查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象； 注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名
	 *         ，如果未指定别名，则使用原始列名； 而Bean中的属性名称如果标注有Column注解
	 *         ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> List<T> list(Class<T> t, int start, int limit, String condition, Object... param)
			throws DAOException;

	/**
	 * 保存指定Bean(新增)
	 * 
	 * @param <T>
	 * @param t
	 *            准备要保存的Bean的class对象
	 * @param bean
	 *            准备要保存的Bean
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> T save(Class<T> t, T bean) throws DAOException;

	/**
	 * 批量保存指定的Bean(新增)；注意：在需要同时保存多个同类型的Bean时，此方法的执行效率理论上要高于多次调用save方法。
	 * 
	 * @param <T>
	 * @param t
	 *            准备要保存的Bean的class对象
	 * @param bean
	 *            准备要保存的Bean，可以任意多个，但至少也要有一个
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> int batchSave(Class<T> t, T... bean) throws DAOException;

	/**
	 * 更新给定的Bean，更新过程不修改主键值。
	 * 
	 * @param <T>
	 * @param bean
	 *            准备要保存的Bean
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> int update(T bean) throws DAOException;

	/**
	 * 保存给定的Bean。如果已经存在则更新，否则新增
	 * 
	 * @param <T>
	 * @param t
	 *            准备要保存的Bean的class对象
	 * @param bean
	 *            准备要保存的Bean
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> int saveOrUpdate(Class<T> t, T bean) throws DAOException;

	/**
	 * 删除指定的Bean
	 * 
	 * @param <T>
	 * @param bean
	 *            准备删除的Bean
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> int delete(T bean) throws DAOException;

	/**
	 * 删除该类型所映射到的表内的全部记录
	 * 
	 * @param <T>
	 * @param t
	 *            与准备要清空的表相映射的Bean的class对象
	 * @return 实际影响的行记录数
	 * @throws DAOException
	 */
	public <T> int deleteAll(Class<T> t) throws DAOException;

	/*************************************************************************************************/
	/**************** 以下方法为SQL支持 ***************************************************************/
	/*************************************************************************************************/

	/**
	 * 获取包含聚合函数的SQL返回值； 该方法只是为用户在使用count/max/min等聚合函数查询时提供一点点便利，
	 * 实际上executeUniqueQuery方法也可以达到类似的目的。
	 * 
	 * @param sql
	 *            准备执行查询的包含聚合函数的SQL，可以包含用“?”代表的参数；
	 *            注意：该SQL的返回结果中，只有第一行的第一列被当作本方法的返回值，其它行 /列均忽略
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return 该SQL的返回结果中，第一行第一列的值
	 * @throws DAOException
	 */
	public long getUniqueValue(String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果对象；此方法期望给定的SQL只返回一行记录，如果结果不唯一则抛出异常
	 * 
	 * @param <T>
	 * @param t
	 *            期望返回的Bean的class对象
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * 
	 * @return SQL查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象；
	 *         注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名 ，如果未指定别名，则使用原始列名；
	 *         而Bean中的属性名称如果标注有Column注解 ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> T executeUniqueQuery(Class<T> t, String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果列表；
	 * 
	 * @param <T>
	 * @param t
	 *            List内将存放的Bean的class对象
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * 
	 * @return SQL查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象；
	 *         注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名 ，如果未指定别名，则使用原始列名；
	 *         而Bean中的属性名称如果标注有Column注解 ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> List<T> executeQuery(Class<T> t, String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果列表；支持分页查询
	 * 
	 * @param <T>
	 * @param t
	 *            List内将存放的Bean的class对象
	 * @param start
	 *            分页查询记录起始行数
	 * @param limit
	 *            分页查询记录数
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * 
	 * @return SQL查询结果；根据SQL查询结果将每行记录创建为一个指定类型的对象；
	 *         注意：在与Bean属性对应时，使用的是SQL中AS子句指定的别名 ，如果未指定别名，则使用原始列名；
	 *         而Bean中的属性名称如果标注有Column注解 ，则使用该注解中声明的列名，否则使用Bean的属性名
	 * @throws DAOException
	 */
	public <T> List<T> executeQuery(Class<T> t, int start, int limit, String sql, Object... param)
			throws DAOException;

	/**
	 * 执行SQL修改操作，返回实际修改的行记录数
	 * 
	 * @param sql
	 *            准备执行修改操作的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return 修改操作（INSERT/UPDATE/DELETE）实际影响的行记录数
	 * @throws DAOException
	 */
	public int executeUpdate(String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果。此方法期望给定的SQL只返回一行记录，如果结果不唯一则抛出异常
	 * 
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return SQL查询结果； 其中Map的键值是SQL中AS子句指定的别名（全大写）， 如果未使用AS子句指定别名，则使用原始列名 （全大写）
	 * @throws DAOException
	 */
	public Map<String, Object> executeUniqueQuery(String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果列表
	 * 
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return SQL查询结果，以Map来保存每行记录； 其中Map的键值是SQL中AS子句指定的别名（全大写），
	 *         如果未使用AS子句指定别名，则使用原始列名 （全大写）
	 * @throws DAOException
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object... param) throws DAOException;

	/**
	 * 执行SQL查询，返回结果列表；支持分页查询
	 * 
	 * @param start
	 *            分页查询记录起始行数
	 * @param limit
	 *            分页查询记录数
	 * @param sql
	 *            准备执行查询的SQL，可以包含用“?”代表的参数
	 * @param param
	 *            任意数量的参数（如果有）
	 * @return SQL查询结果，以Map来保存每行记录； 其中Map的键值是SQL中AS子句指定的别名（全大写），
	 *         如果未使用AS子句指定别名，则使用原始列名 （全大写）
	 * @throws DAOException
	 */
	public List<Map<String, Object>> executeQuery(int start, int limit, String sql, Object... param)
			throws DAOException;
}
