/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * 用于描述从FileUploader类中解析出的文件数据。请使用 open 方法返回该文本的输入流
 * 
 * @author Lei
 */
public class FileItem implements Serializable {
	private static final long serialVersionUID = 1L;

	// 文件在请求中的名字
	private String name;

	// 文件真实名字
	private String fileName;

	// 文件类型
	private String contentType;

	// 文件数据
	private byte[] data;

	public InputStream open() {
		return new ByteArrayInputStream(data);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
