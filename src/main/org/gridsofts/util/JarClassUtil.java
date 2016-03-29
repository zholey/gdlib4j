/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 自定义的类加载器
 *
 * @author lei
 */
public class JarClassUtil {

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
	public static <T> void loadClassesInJar(List<Class<? extends T>> classList, Class<T> superClass, String packageName,
			File jarFile, final boolean recursive) {

		loadClassesInJar(new JarClassLoader(jarFile), classList, superClass, packageName, null, jarFile, recursive);
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
	private static <T> void loadClassesInJar(ClassLoader classLoader, List<Class<? extends T>> classList,
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

	public static class JarClassLoader extends java.lang.ClassLoader {

		private HashSet<String> dynaclazns;

		public JarClassLoader(URL url) {
			super(null);
			dynaclazns = new HashSet<String>();

			// 获取jar file
			JarFile jarFile = null;

			try {
				if (url != null) {
					URLConnection urlConn = url.openConnection();

					if (urlConn != null && JarURLConnection.class.isAssignableFrom(urlConn.getClass())) {
						jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
					}
				}
			} catch (IOException e) {
			}

			if (jarFile == null) {
				throw new NullPointerException("jarUrl is null.");
			}

			load(jarFile);
		}

		public JarClassLoader(File file) {
			super(null);
			dynaclazns = new HashSet<String>();

			// 获取jar file
			JarFile jarFile = null;

			try {
				if (file != null) {
					jarFile = new JarFile(file);
				}
			} catch (IOException e) {
			}

			if (jarFile == null) {
				throw new NullPointerException("jarFile is null.");
			}

			load(jarFile);
		}

		private void load(JarFile jarFile) {

			// 从此jar包得到一个枚举类
			Enumeration<JarEntry> entries = jarFile.entries();
			// 同样的进行循环迭代
			while (entries.hasMoreElements()) {

				// 获取jar里的实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
				JarEntry entry = entries.nextElement();

				String entryName = entry.getName().replaceFirst("^\\/+", "").replaceAll("\\/+", ".")
						.replaceFirst("\\.+$", "");

				// 加载包内所有的类
				if (Pattern.matches(".+\\.[^\\.]+\\.class$", entryName)) {
					String qualifiedName = entryName.replaceFirst("\\.class$", "");

					try {
						InputStream inStream = jarFile.getInputStream(entry);
						byte[] classBytes = new byte[(int) entry.getSize()];

						inStream.read(classBytes);
						inStream.close();

						defineClass(qualifiedName, classBytes, 0, classBytes.length);

						dynaclazns.add(qualifiedName);
					} catch (Throwable e) {
					}
				}
			}
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			Class<?> cls = findLoadedClass(name);

			if (!this.dynaclazns.contains(name) && cls == null) {
				cls = Thread.currentThread().getContextClassLoader().loadClass(name);
			}

			if (cls == null) {
				throw new ClassNotFoundException(name);
			}

			if (resolve) {
				resolveClass(cls);
			}

			return cls;
		}
	}
}
