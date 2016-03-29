/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import java.util.regex.Pattern;

import org.gridsofts.util.StringUtil;

public class EqualTester implements EachTester.IExpTester {

	private final Pattern compareExp = Pattern.compile("^[^<=>!]*==[^<=>!]*$");

	public boolean test(String exp) {

		if (compareExp.matcher(exp).find()) {

			String[] expAry = exp.split("==");
			
			if (expAry == null || expAry.length == 0) {
				return false;
			}

			// ‘空’判断的固定格式为，左边是待检验的内容，右边为空
			if (expAry.length < 2 || StringUtil.isNull(expAry[1])) {
				
				return StringUtil.isNull(expAry[0]);
				
			} else {

				if (StringUtil.isNumber(expAry[0]) && StringUtil.isNumber(expAry[1])) {
					return Double.valueOf(expAry[0]).doubleValue() == Double.valueOf(expAry[1]).doubleValue();
				} else {
					return expAry[0].equals(expAry[1]);
				}
			}
		}

		return false;
	}
}
