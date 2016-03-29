/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gridsofts.util.StringUtil;

public class HtmlVariableExp extends AbstractVariableExp {
	private static final long serialVersionUID = 1L;

	// Html型表达式
	private static final Pattern VariableRegExp = Pattern.compile("#HTML\\[" + VariableExp + "\\]");

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

		// 去掉#HTML[]和空白字符
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

			// 真实值
			Object fldValue = getValue(fldName, valueObj, index);

			if (fldValue == null) {
				fldValue = "";
			}

			// 用真实值替换变量出现的位置
			// 同时过滤非法值；防"蛀"
			htmlContent = matcher.replaceFirst(filter(fldValue.toString()));

			// 重新查找变量
			matcher = VariableRegExp.matcher(htmlContent);
		}

		return htmlContent;
	}
	
	/**
	 * 过滤非法值；防"蛀"
	 * @param text
	 * @return
	 */
	public static String filter(String text) {
		
		if (StringUtil.isNull(text)) {
			return text;
		}
		
		// iframe
		text = text.replaceAll("(?i)<iframe\\s", "&lt;iframe&nbsp;");
		text = text.replaceAll("(?i)<\\/iframe>\\s", "&lt;/iframe&gt;");
		
		return text;
	}
}
