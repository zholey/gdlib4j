/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import java.util.regex.Pattern;

public class BooleanTester implements EachTester.IExpTester {

	private final Pattern booleanExp = Pattern.compile("(?i)^\\s*((true)|(false))\\s*$");

	public boolean test(String exp) {

		if (booleanExp.matcher(exp).find()) {
			return "true".equalsIgnoreCase(exp);
		}

		return false;
	}
}
