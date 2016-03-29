/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gridsofts.util.StringUtil;

public class FixedLenVariableExp extends AbstractVariableExp {
	private static final long serialVersionUID = 1L;

	// 定长Text型表达式
	private static final Pattern VariableRegExp = Pattern.compile("#FIXED\\[" + VariableExp + ",\\s*(\\d+)\\s*\\]");
	
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

		// 去掉#FIXED[]和空白字符
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
			
			// 长度
			int length = Integer.valueOf(matcher.group(3));

			// 真实值
			Object fldValue = getValue(fldName, valueObj, index);

			if (fldValue == null) {
				fldValue = "";
			}
			// 过滤非法值
			else {

				// 过滤Html标签、定长
				fldValue = StringUtil.getFixedLength(fldValue.toString(), length);
			}

			// 用真实值替换变量出现的位置
			htmlContent = matcher.replaceFirst(fldValue.toString());

			// 重新查找变量
			matcher = VariableRegExp.matcher(htmlContent);
		}

		return htmlContent;
	}
}
