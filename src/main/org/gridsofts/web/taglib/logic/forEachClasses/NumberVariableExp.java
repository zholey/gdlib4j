/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gridsofts.util.Formatter;

public class NumberVariableExp extends AbstractVariableExp {
	private static final long serialVersionUID = 1L;

	// 数值型表达式
	private static final Pattern VariableRegExp = Pattern.compile("#\\.(#{0,4})\\[" + VariableExp + "\\]");

	// 用于保存在调用“match”方法时所取得的字段名字
	private String matchedFieldName;

	@Override
	public boolean match(String input) {

		matchedFieldName = null;

		boolean find = false;

		Matcher matcher = VariableRegExp.matcher(input);

		if (matcher.find()) {
			find = true;

			if (matcher.groupCount() >= 2) {
				matchedFieldName = matcher.group(2);
			}
		}

		return find;
	}

	@Override
	public String getFieldName() {

		// 去掉#.#+[]和空白字符
		return matchedFieldName;
	}

	/**
	 * 替换htmlContent中出现的所有变量表达式
	 * 
	 * @param htmlContent
	 * @param valueObj
	 * @return
	 */
	@Override
	public String replaceAll(String htmlContent, int index, Object valueObj) {

		// 查找变量
		Matcher matcher = VariableRegExp.matcher(htmlContent);

		// 如果找到 ...
		while (matcher.find()) {

			// 变量名
			String fldName = matcher.group(2);

			// 保留精度
			int digits = 0;

			if (matcher.group(1) != null) {
				digits = matcher.group(1).length();
			}

			// 真实值
			Object fldValue = getValue(fldName, valueObj, index);

			if (fldValue == null) {
				fldValue = "0";
			}

			// 将结果强制转化为Double型，并保留指定的精度
			try {
				fldValue = Formatter.decimalFormat(Double.valueOf(fldValue.toString()), digits);
			} catch (Exception ex) {
				fldValue = Formatter.decimalFormat(0d, digits);
			}

			// 用真实值替换变量出现的位置
			htmlContent = matcher.replaceFirst(fldValue.toString());

			// 重新查找变量
			matcher = VariableRegExp.matcher(htmlContent);
		}

		return htmlContent;
	}
}
