/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 静态资源类
 * 
 * @author Lei
 * 
 */
public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 多语资源
	 */
	public static Map<String, Properties> Language = new HashMap<>();

	static {

		try {
			Language.put("zh_CN", loadLanguage("zh_CN"));
			Language.put("en_US", loadLanguage("en_US"));
		} catch (Throwable t) {
		}
	}

	private static Properties loadLanguage(String localName) {

		Properties lanPACK = new Properties();
		try {
			String lanName = "/org/gridsofts/resource/language/" + localName + ".xml";
			lanPACK.loadFromXML(Resource.class.getResourceAsStream(lanName));
		} catch (IOException e) {
		}

		return lanPACK;
	}
}
