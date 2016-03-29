/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 提供查找类的静态方法
 *
 * @author lei
 */
public class ClassScanUtil {

	/**
	 * 获取指定类的静态变量值
	 * 
	 * @param klass
	 * @param fldName
	 * @return
	 */
	public static Object getStaticFldValue(Class<?> klass, String fldName) {

		try {
			Field fld = klass.getDeclaredField(fldName);
			if (fld != null) {
				return fld.get(null);
			}
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 查找给定包下的所有指定类的子类
	 * 
	 * @param superClass
	 * @param basePackage
	 * @return
	 */
	public static <T> List<Class<? extends T>> findClasses(Class<T> superClass, String basePackage) {
		return findClasses(Thread.currentThread().getContextClassLoader(), superClass, basePackage, false);
	}

	/**
	 * 查找给定包下的所有指定类的子类
	 * 
	 * @param superClass
	 * @param basePackage
	 * @param recursive
	 * @return
	 */
	public static <T> List<Class<? extends T>> findClasses(Class<T> superClass, String basePackage, boolean recursive) {
		return findClasses(Thread.currentThread().getContextClassLoader(), superClass, basePackage, recursive);
	}

	/**
	 * 查找给定包下的所有指定类的子类
	 * 
	 * @param classLoader
	 * @param superClass
	 * @param basePackage
	 * @param recursive
	 * @return
	 */
	public static <T> List<Class<? extends T>> findClasses(ClassLoader classLoader, Class<T> superClass,
			String basePackage, boolean recursive) {
		List<Class<? extends T>> classList = new ArrayList<Class<? extends T>>();

		String packageDirName = basePackage.replaceAll("\\.+", "/");

		Enumeration<URL> dirResourceEnum;
		try {
			dirResourceEnum = classLoader.getResources(packageDirName);

			// 迭代所有的路径资源
			while (dirResourceEnum.hasMoreElements()) {

				URL dirUrl = dirResourceEnum.nextElement();

				// 得到协议的名称
				String protocol = dirUrl.getProtocol();

				// 如果是以文件的形式保存在服务器上
				if ("file".equalsIgnoreCase(protocol)) {

					// 获取包的物理路径
					String filePath = URLDecoder.decode(dirUrl.getFile(), "UTF-8");

					// 以文件的方式扫描整个包下的文件 并添加到集合中
					loadClassesInDir(classLoader, classList, superClass, basePackage, filePath, recursive);

				} else if ("jar".equalsIgnoreCase(protocol)) {

					loadClassesInJar(classLoader, classList, superClass, basePackage, dirUrl, null, recursive);
				}
			}
		} catch (IOException e) {
		}

		return classList;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param classLoader
	 * @param classList
	 * @param superClass
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 */
	public static <T> void loadClassesInDir(ClassLoader classLoader, List<Class<? extends T>> classList,
			Class<T> superClass, String packageName, String dirPath, final boolean recursive) {

		// 获取此包的目录 建立一个File
		File dir = new File(dirPath);

		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {

			// 自定义过滤规则 如果可以循环(包含子目录) 或者是以.class结尾的文件
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});

		// 循环所有文件
		for (File file : dirfiles) {

			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				loadClassesInDir(classLoader, classList, superClass, packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive);
			} else {

				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().replaceFirst("\\.class$", "");
				try {
					Class<?> klass = classLoader.loadClass(packageName + "." + className);
					if (superClass.isAssignableFrom(klass)) {
						classList.add(klass.asSubclass(superClass));
					}
				} catch (ClassNotFoundException e) {
				}
			}
		}
	}

	/**
	 * 从一个JAR文件的包里获取指定类的所有子类
	 * 
	 * @param classLoader
	 * @param classList
	 * @param superClass
	 * @param packageName
	 * @param jarUrl
	 * @param jarFile
	 * @param recursive
	 */
	public static <T> void loadClassesInJar(ClassLoader classLoader, List<Class<? extends T>> classList,
			Class<T> superClass, String packageName, URL jarUrl, File jarFile, final boolean recursive) {

		JarFile jar = null;
		try {
			// 获取jar
			if (jarUrl != null) {
				URLConnection urlConn = jarUrl.openConnection();

				if (urlConn != null && JarURLConnection.class.isAssignableFrom(urlConn.getClass())) {
					jar = ((JarURLConnection) jarUrl.openConnection()).getJarFile();
				}
			} else if (jarFile != null) {
				jar = new JarFile(jarFile);
			}

			if (jar == null) {
				throw new NullPointerException("jarUrl/jarFile is null.");
			}

			// 从此jar包得到一个枚举类
			Enumeration<JarEntry> entries = jar.entries();
			// 同样的进行循环迭代
			while (entries.hasMoreElements()) {

				// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
				JarEntry entry = entries.nextElement();

				String entryName = entry.getName().replaceFirst("^\\/+", "").replaceAll("\\/+", ".")
						.replaceFirst("\\.+$", "");

				// 如果直接在该包下找到类，或者在允许递归的情况下找到子包下的类
				if (Pattern.matches("^" + packageName + "\\.[^\\.]+\\.class$", entryName)
						|| (recursive && Pattern.matches("^" + packageName + "\\..+\\.class$", entryName))) {

					try {
						Class<?> klass = classLoader.loadClass(entryName.replaceFirst("\\.class$", ""));
						if (superClass.isAssignableFrom(klass)) {
							classList.add(klass.asSubclass(superClass));
						}
					} catch (Throwable e) {
					}
				}
			}
		} catch (IOException e) {
		}
	}
}
