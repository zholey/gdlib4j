/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 应用配置
 * 
 * @author Lei
 */
public class Configure extends Properties {
	private static final long serialVersionUID = 1L;

	private File cfgFile;

	private Configure(String cfgFilePath) {
		cfgFile = new File(cfgFilePath);
		
		load();
	}
	
	private static class SingletonHolder {
		private static Map<String, Configure> instanceMap = new HashMap<>();
	}

	public static Configure getInstance(String cfgFilePath) {
		if (!SingletonHolder.instanceMap.containsKey(cfgFilePath)) {
			SingletonHolder.instanceMap.put(cfgFilePath, new Configure(cfgFilePath));
		}
		
		return SingletonHolder.instanceMap.get(cfgFilePath);
	}

	public void load() {

		try {
			load(new FileInputStream(cfgFile));
		} catch (Throwable e) {
		}
	}

	public void storage() {

		try {
			store(new FileOutputStream(cfgFile), null);
		} catch (Throwable e) {
		}
	}

	public String getString(String key) {
		return getProperty(key);
	}

	public String getString(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	public int getInt(String key) {

		try {
			return Integer.valueOf(getProperty(key));
		} catch (Throwable e) {
		}

		return 0;
	}

	public int getInt(String key, int defaultValue) {

		try {
			return Integer.valueOf(getProperty(key, String.valueOf(defaultValue)));
		} catch (Throwable e) {
		}

		return 0;
	}
}
