/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.io.Serializable;

import org.gridsofts.util.BeanUtil;

public abstract class AbstractVariableExp implements Serializable {
	private static final long serialVersionUID = 1L;

	// 变量表达式字符串
	protected static final String VariableExp = "\\s*([a-z_A-Z][_\\w]*(\\.[a-z_A-Z][_\\w]*)*)\\s*";

	/**
	 * 检测输入字符串是否与当前表达式格式匹配
	 * 
	 * @param input
	 * @return
	 */
	public abstract boolean match(String input);

	/**
	 * 获取在上次匹配（调用“match”方法）时取得的真实的字段名（可以是含“.”的表达式，但是不包含其它参数）
	 * 
	 * @return
	 */
	public abstract String getFieldName();

	/**
	 * 替换
	 * 
	 * @param htmlContent
	 * @param index
	 * @param valueObj
	 * @return
	 */
	public abstract String replaceAll(String htmlContent, int index, Object valueObj);

	/**
	 * 查找变量值，可以识别'.'表达式
	 * 
	 * @param fldName
	 * @param valueObj
	 * @return
	 */
	public Object getValue(String fldName, Object valueObj, int index) {

		if (valueObj == null) {
			return null;
		}

		// 常量
		if ("__forEach.rownum".equalsIgnoreCase(fldName)) {

			return String.valueOf(index);

		} else if ("__forEach.inturn".equalsIgnoreCase(fldName)) {

			return String.valueOf(index % 2 == 0 ? 0 : 1);

		} else if ("__forEach.value".equalsIgnoreCase(fldName)) {

			return valueObj.toString();
		}

		// Bean字段
		else {
			return BeanUtil.getFieldValue(valueObj, fldName);
		}
	}
}
