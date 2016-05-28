/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * 提供涉及文件及IO操作的常用方法
 * 
 * @author zholey
 * 
 */
public class FileIO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 将文本文件内容读入至内存
	 * 
	 * @param file
	 * @return
	 */
	public static String readString(File file) {

		if (file != null && file.exists() && file.isFile()) {

			StringBuffer strBuffer = new StringBuffer();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String readStr = null;

				while ((readStr = reader.readLine()) != null) {
					strBuffer.append(readStr + "\r\n");
				}
			} catch (Throwable e) {
			}

			return strBuffer.toString();
		}

		return null;
	}

	/**
	 * 将二进制文件内容读入至内存
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] read(File file) {

		if (file != null && file.exists() && file.isFile()) {

			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			try(BufferedInputStream bufInstream = new BufferedInputStream(new FileInputStream(file))) {

				byte[] readBuf = new byte[4096];
				int readLen = 0;

				while ((readLen = bufInstream.read(readBuf)) > 0) {
					byteOutStream.write(readBuf, 0, readLen);
				}

				byteOutStream.flush();
				bufInstream.close();

				return byteOutStream.toByteArray();
			} catch (Throwable e) {
			}
		}

		return null;
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {

		if (file != null && file.exists()) {

			if (file.isDirectory()) {

				for (File f : file.listFiles()) {
					delete(f);
				}
			}

			file.delete();
		}
	}

	/**
	 * 从输入流向输出流拷贝数据，从输入流的当前位置，直至流末尾
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param bufSize
	 *            缓冲大小
	 * @param isNowFlush
	 *            是否立即刷新输出流
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, Integer bufSize, Boolean isNowFlush) throws IOException {
		copy(in, out, bufSize, isNowFlush, null);
	}

	/**
	 * 从输入流向输出流拷贝数据，从输入流的当前位置，直至流末尾。支持对数据进行过滤处理
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param bufSize
	 *            缓冲大小
	 * @param isNowFlush
	 *            是否立即刷新输出流
	 * @param filter
	 *            数据过滤器
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, Integer bufSize, Boolean isNowFlush, DataFilter filter)
			throws IOException {

		byte[] buf = new byte[bufSize];

		for (int len = 0; (len = in.read(buf)) > -1;) {

			if (filter != null) {
				out.write(filter.doFilter(buf, len));
			} else {
				out.write(buf, 0, len);
			}

			if (isNowFlush) {
				out.flush();
			}
		}
	}

	/**
	 * 从输入流向输出流拷贝数据，从输入流的当前位置，直至流末尾
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param isNowFlush
	 *            是否立即刷新输出流
	 * @throws IOException
	 */
	public static void copyText(InputStream in, OutputStream out, Boolean isNowFlush) throws IOException {
		copyText(in, out, isNowFlush, null);
	}

	/**
	 * 从输入流向输出流拷贝数据，从输入流的当前位置，直至流末尾。支持对数据进行过滤处理
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param isNowFlush
	 *            是否立即刷新输出流
	 * @param filter
	 *            数据过滤器
	 * @throws IOException
	 */
	public static void copyText(InputStream in, OutputStream out, Boolean isNowFlush, TextFilter filter)
			throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

		for (String temp = null; (temp = reader.readLine()) != null;) {

			if (filter != null) {
				writer.write(filter.doFilter(temp + "\r\n"));
			} else {
				writer.write(temp + "\r\n");
			}

			if (isNowFlush) {
				writer.flush();
			}
		}
	}

	/**
	 * 文本过滤器
	 * 
	 * @author Lei
	 * 
	 */
	public static interface TextFilter {
		public String doFilter(String text);
	}

	/**
	 * 数据过滤器
	 * 
	 * @author Lei
	 * 
	 */
	public static interface DataFilter {
		public byte[] doFilter(byte[] data, int len);
	}
}
