/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import java.util.regex.Pattern;

public class RemainderTester implements EachTester.IExpTester {

	private final Pattern compareExp1 = Pattern
			.compile("^\\s*-?(\\d+\\.)?\\d+\\s*%\\s*-?(\\d+\\.)?\\d+\\s*==\\s*-?(\\d+\\.)?\\d+\\s*$");
	private final Pattern compareExp2 = Pattern
			.compile("^\\s*-?(\\d+\\.)?\\d+\\s*%\\s*-?(\\d+\\.)?\\d+\\s*!=\\s*-?(\\d+\\.)?\\d+\\s*$");

	public boolean test(String exp) {

		if (compareExp1.matcher(exp).find()) {

			String[] expAry = exp.split("==");
			String[] operAry = expAry[0].split("%");

			return Double.valueOf(operAry[0]).doubleValue() % Double.valueOf(operAry[1]).doubleValue() == Double
					.valueOf(expAry[1]).doubleValue();
		
		} else if (compareExp2.matcher(exp).find()) {

			String[] expAry = exp.split("!=");
			String[] operAry = expAry[0].split("%");

			return Double.valueOf(operAry[0]).doubleValue() % Double.valueOf(operAry[1]).doubleValue() != Double
					.valueOf(expAry[1]).doubleValue();
		}

		return false;
	}
}
