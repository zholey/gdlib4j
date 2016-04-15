/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * 演示性的文件上传解析处理类，本类将HttpServletRequest中的文件数据解析至内存中，<br/>
 * 当文件过大时，有可能会导致内存溢出。
 * 
 * @author Lei
 */
public class FileUploader {

	private ServletInputStream in;

	private Properties parameters;

	private Pattern pName, pFile, pType;
	private String regex = "name=\"[^\"]*\"";
	private String fileRegex = "filename=\"[^\"]*\"";
	private String typeRegex = "Content-Type:.*";

	private byte[] buffer;
	private int sumCount;
	private String separator;

	private int beginIndex = 0;

	public FileUploader() {
		parameters = new Properties();
	}

	/**
	 * 实现接收文件上传功能
	 * 
	 * @param request
	 *            发送的请求; request.contentType = multipart/form-data;
	 *            request.method = POST;
	 * @param property
	 *            用来接收请求中的附加参数
	 * @param fileList
	 *            上传文件数据列表
	 * @throws IOException
	 */
	public Map<String, FileItem> doUpload(HttpServletRequest request)
			throws IOException {
		
		Map<String, FileItem> files = new HashMap<>();

		buffer = new byte[request.getContentLength()]; // 创建数据缓冲区

		String contentType = request.getContentType();
		separator = "boundary=";

		if (contentType.startsWith("multipart/form-data")) { // 判断请求类型，获取分隔符
			separator = contentType.substring(contentType.indexOf(separator) + separator.length());
		} else { // 请求类型错误
			return null;
		}

		in = request.getInputStream();

		int readCount = 0;
		sumCount = 0;
		byte[] temp = new byte[1024];

		while ((readCount = in.read(temp)) > -1) { // 将用户上传的数据暂存于缓冲区内
			System.arraycopy(temp, 0, buffer, sumCount, readCount);
			sumCount += readCount;
		}

		// 创建正则表达式
		pName = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		pFile = Pattern.compile(fileRegex, Pattern.CASE_INSENSITIVE);
		pType = Pattern.compile(typeRegex, Pattern.CASE_INSENSITIVE);

		// 开始解析上传数据
		String tempStr = null;
		byte[] line = null;

		while (beginIndex < sumCount) {
			line = readLine();

			if (line != null && line.length > 2) {
				tempStr = new String(line, 2, line.length - 2);

				if (tempStr.startsWith(separator)) { // 新的数据区
					parse(readLine(), files);
				}
			}
		}
		
		return files;
	}

	// 从已上传数据中读取一行数据
	private byte[] readLine() {
		byte[] line = null;

		if (buffer != null && beginIndex < sumCount) {

			for (int endIndex = beginIndex; endIndex < sumCount; endIndex++) {

				if (buffer[endIndex] == '\n' || endIndex == sumCount - 1) {

					line = new byte[endIndex - beginIndex + 1];

					System.arraycopy(buffer, beginIndex, line, 0, line.length);

					// 移动流指针
					beginIndex = endIndex + 1;
					break;
				}
			}
		}
		return line;
	}

	// 解析数据
	private void parse(byte[] line, Map<String, FileItem> files) {
		if (line == null) {
			return;
		}

		String header = new String(line); // 数据区首行

		Matcher mFile = pFile.matcher(header);
		Matcher mName = pName.matcher(header);

		if (mFile.find()) { // 文件数据

			String fileName = mFile.group();
			fileName = fileName.substring(10, fileName.length() - 1);

			String name = null;
			if (mName.find()) {
				name = mName.group();
				name = name.substring(6, name.length() - 1);
			}

			String contentType = null;
			byte[] tempLine = readLine(); // 读取文件类型

			if (tempLine != null) {
				contentType = new String(tempLine);

				Matcher mType = pType.matcher(contentType);
				if (mType.find()) {
					contentType = contentType.substring(14);
				} else {
					contentType = null;
				}
			}

			readLine(); // 跳过空行

			// 开始读取文件数据
			FileItem file = getFileItem();

			file.setName(name == null ? "undefined" : name);
			file.setFileName(fileName);
			file.setContentType(contentType);

			files.put(file.getName(), file);

		} else if (mName.find()) { // 字符数据

			readLine(); // 跳过空行

			String name = mName.group();
			name = name.substring(6, name.length() - 1);

			// 开始读取参数数据
			parameters.put(name, getParameter()); // 放置参数
		}
	}

	// 获取用户上传的文件数据
	private FileItem getFileItem() {
		FileItem file = new FileItem();

		Vector<byte[]> buffer = new Vector<>();

		int dataSize = 0;
		byte[] line = null;
		String tempStr = null;

		while (beginIndex < sumCount) {
			line = readLine();

			if (line != null) {
				if (line.length > 2) {
					tempStr = new String(line, 2, line.length - 2);

					if (tempStr.startsWith(separator)) { // 本文件结束，将数据指针移回下一数据区开始位置
						beginIndex -= line.length;
						break;
					}
				}

				buffer.add(line);
				dataSize += line.length;
			}
		}

		// 构造文件数据存储区
		file.setData(new byte[dataSize]);
		dataSize = 0;

		for (byte[] cake : buffer) {

			System.arraycopy(cake, 0, file.getData(), dataSize, cake.length);
			dataSize += cake.length;
		}

		return file;
	}

	// 获取用户上传的参数数据
	private String getParameter() {
		StringBuffer buffer = new StringBuffer();

		byte[] line = null;
		String tempStr = null;

		while (beginIndex < sumCount) {
			line = readLine();

			if (line != null) {
				if (line.length > 2) {
					tempStr = new String(line, 2, line.length - 2);

					if (tempStr.startsWith(separator)) { // 已到结尾，将数据指针移回下一数据区开始位置
						beginIndex -= line.length;
						break;
					}
				}

				buffer.append(new String(line));
			}
		}

		return buffer.toString();
	}

	public String getParameter(String name) {
		return parameters.getProperty(name);
	}
}
