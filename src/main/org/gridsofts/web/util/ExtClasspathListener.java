/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.util;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 容器启动/关闭事件监听器；本类用于在容器启动后添加外部classpath
 * 
 * @author lei
 */
public class ExtClasspathListener implements ServletContextListener {
	private static final String EXT_CLASSPATH_DIR = "extClasspath";

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext currentServletContext = evt.getServletContext();

		String extClasspaths = currentServletContext.getInitParameter(EXT_CLASSPATH_DIR);
		String[] extClasspathAry = extClasspaths.split(",");
		
		if (extClasspathAry != null && extClasspathAry.length > 0) {

			// 顺序添加全部extClassPath
			for (String extClasspath : extClasspathAry) {
				appendExtClassPath(extClasspath);
			}
		}
	}
	
	/**
	 * 添加extClassPath
	 * 
	 * @param extClasspath
	 */
	private void appendExtClassPath(String extClasspath) {
		
		if (extClasspath != null) {
			File extClasspathDir = new File(extClasspath);

			if (extClasspathDir.exists()) {

				// 查找ClassLoader及其addURL方法
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				Method addUrlMethod = null;
				try {
					addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
					if (addUrlMethod != null) {
						addUrlMethod.setAccessible(true);
					}
				} catch (Throwable e) {
				}

				if (addUrlMethod != null) {

					try {
						// 将extClasspathDir加入CLASSPATH
						addUrlMethod.invoke(classLoader, new Object[] { extClasspathDir.toURI().toURL() });

						// 将extClasspathDir下的所有jar文件加入CLASSPATH
						File[] jarFiles = extClasspathDir.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".jar");
							}
						});

						if (jarFiles != null && jarFiles.length > 0) {
							for (File jarFile : jarFiles) {
								addUrlMethod.invoke(classLoader, new Object[] { jarFile.toURI().toURL() });
							}
						}
					} catch (Throwable e) {
					}
				}
			}
		}
	}
}
