/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import java.util.regex.Pattern;

import org.gridsofts.util.StringUtil;

public class LargerTester implements EachTester.IExpTester {

	private final Pattern compareExp1 = Pattern.compile("^[^<=>!]+>[^<=>!]+$");
	private final Pattern compareExp2 = Pattern.compile("^[^<=>!]+>=[^<=>!]+$");

	public boolean test(String exp) {

		if (compareExp1.matcher(exp).find()) {

			String[] expAry = exp.split(">");

			if (expAry == null || expAry.length == 0) {
				return false;
			}

			// 只有左运算数而没有右运算数时，数组长度为1
			// 左运算数不为空时则认为其大于右运算数
			if (expAry.length == 1) {

				return !StringUtil.isEmpty(expAry[0]);

			} else {

				if (StringUtil.isNumber(expAry[0]) && StringUtil.isNumber(expAry[1])) {
					return Double.valueOf(expAry[0]).doubleValue() > Double.valueOf(expAry[1]).doubleValue();
				} else {
					return expAry[0].compareTo(expAry[1]) > 0;
				}
			}

		} else if (compareExp2.matcher(exp).find()) {

			String[] expAry = exp.split(">=");

			if (expAry == null || expAry.length == 0) {
				return false;
			}

			// 只有左运算数而没有右运算数时，数组长度为1
			// 左运算数不为空时则认为其大于右运算数
			if (expAry.length == 1) {

				return !StringUtil.isEmpty(expAry[0]);

			} else {

				if (StringUtil.isNumber(expAry[0]) && StringUtil.isNumber(expAry[1])) {
					return Double.valueOf(expAry[0]).doubleValue() >= Double.valueOf(expAry[1]).doubleValue();
				} else {
					return expAry[0].compareTo(expAry[1]) >= 0;
				}
			}
		}

		return false;
	}
}
