/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gridsofts.util.DateTime;

public class DTVariableExp extends AbstractVariableExp {
	private static final long serialVersionUID = 1L;

	// 日期、时间型表达式
	private static final Pattern VariableRegExp = Pattern.compile("#DT\\[" + VariableExp
			+ "(,\\s*'([^'\\[\\]]+)'\\s*)?\\]");

	// 用于保存在调用“match”方法时所取得的字段名字
	private String matchedFieldName;

	@Override
	public boolean match(String input) {

		matchedFieldName = null;

		boolean find = false;

		Matcher matcher = VariableRegExp.matcher(input);

		if (matcher.find()) {
			find = true;

			if (matcher.groupCount() >= 1) {
				matchedFieldName = matcher.group(1);
			}
		}

		return find;
	}

	@Override
	public String getFieldName() {

		// 去掉#DT[]和空白字符
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
			String fldName = matcher.group(1);

			// 是否包含参数
			String argument = null;

			if (matcher.groupCount() >= 4) {

				// 参数字符串
				argument = matcher.group(4);
			}

			// 真实值
			Object fldValue = getValue(fldName, valueObj, index);

			if (fldValue != null) {

				// 利用指定的格式字符串格式化日期/时间值
				try {
					if (fldValue instanceof Timestamp) {

						fldValue = DateTime.getDateTime(((Timestamp) fldValue).getTime()).toString(argument);

					} else if (fldValue instanceof DateTime) {

						fldValue = ((DateTime) fldValue).toString(argument);
					}
				} catch (Exception ex) {
					fldValue = "";
				}
			}

			if (fldValue == null) {
				fldValue = "";
			}

			// 用真实值替换变量出现的位置
			htmlContent = matcher.replaceFirst(fldValue.toString());

			// 重新查找变量
			matcher = VariableRegExp.matcher(htmlContent);
		}

		return htmlContent;
	}
}
