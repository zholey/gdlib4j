/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

/**
 * 提供生成UUID的便捷方法
 * 
 * @author lei
 */
public class UUID {

	public static String randomUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static String randomUUID32() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}
}
