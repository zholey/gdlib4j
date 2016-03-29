/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import java.util.ArrayList;
import java.util.List;

public class EachTester {

	private static final List<IExpTester> testerAry;
	static {
		testerAry = new ArrayList<IExpTester>();

		testerAry.add(new BooleanTester());
		testerAry.add(new EqualTester());
		testerAry.add(new UnEqualTester());
		testerAry.add(new LargerTester());
		testerAry.add(new SmallerTester());
		testerAry.add(new RemainderTester());
	}

	public static boolean test(String exp) {

		if (exp != null) {

			for (IExpTester tester : testerAry) {
				if (tester.test(exp)) {
					return true;
				}
			}
		}

		return false;
	}

	public static interface IExpTester {
		boolean test(String exp);
	}
}
