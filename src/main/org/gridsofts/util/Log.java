/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;


/**
 * 日志类，用来维护系统用户的日志信息。
 * 
 * @author zholey
 * @version 1.0
 */

public class Log implements Serializable {
	private static final long serialVersionUID = 1L;

	private FileWriter fw;

	private PrintWriter pw;

	private File file;

	private String LogPath;

	private String fileName;

	/**
	 * 通过指定日志文件的存放路径构造日志类。
	 * 
	 * @param LogPath
	 *            日志文件的存放路径
	 */
	public Log(String LogPath) {
		this.LogPath = LogPath;
		this.file = new File(this.LogPath);
		if (!this.file.exists()) {
			this.file.mkdirs();
		}
		this.fileName = DateTime.getCurrentTime().toString("yyyy-mm-dd")
				+ ".log";
		this.LogPath += this.fileName;
	}

	/**
	 * 以“当前路径/logs”为日志文件的存放路径，构造日志类。
	 */
	public Log() {
		this.LogPath = "./logs/";
		this.file = new File(this.LogPath);
		if (!this.file.exists()) {
			this.file.mkdirs();
		}
		this.fileName = DateTime.getCurrentTime().toString("yyyy-mm-dd")
				+ ".log";
		this.LogPath += this.fileName;
	}

	/**
	 * 向日志文件中添加日志信息，日志信息被添加至文件尾。
	 * 
	 * @param msg
	 *            需要添加的日志信息
	 */
	public void append(String msg) {
		try {
			this.fw = new FileWriter(this.LogPath, true);
			this.pw = new PrintWriter(this.fw, true);
			this.pw.println(DateTime.getCurrentTime().toString(
					"yyyy-mm-dd hh:mi:ss")
					+ "\t" + msg);
			this.pw.println();
			this.pw.close();
			this.fw.close();
		} catch (IOException ioe) {
			System.out.println("日志文件打开失败。");
			ioe.printStackTrace();
		}
	}

	/**
	 * 向日志文件中添加日志信息，及生成此日志对象的类名信息。
	 * 
	 * @param obj
	 *            生成此日志对象的对象
	 * @param msg
	 *            日志信息
	 */
	public void append(Object obj, String msg) {
		try {
			this.fw = new FileWriter(this.LogPath, true);
			this.pw = new PrintWriter(this.fw, true);
			this.pw.println(DateTime.getCurrentTime().toString(
					"yyyy-mm-dd hh:mi:ss")
					+ "\t" + obj.getClass().getName());
			this.pw.println("\t" + msg);
			this.pw.println();
			this.pw.close();
			this.fw.close();
		} catch (IOException ioe) {
			System.out.println("日志文件打开失败。");
			ioe.printStackTrace();
		}
	}

	/**
	 * 向日志文件中添加异常信息。
	 * 
	 * @param e
	 *            异常信息
	 */
	public void append(Exception e) {
		try {
			this.fw = new FileWriter(this.LogPath, true);
			this.pw = new PrintWriter(this.fw, true);
			this.pw.println(DateTime.getCurrentTime().toString(
					"yyyy-mm-dd hh:mi:ss")
					+ "\t" + e.toString());
			this.pw.println("详细信息:");
			e.printStackTrace(this.pw);
			this.pw.println();
			this.pw.close();
			this.fw.close();
		} catch (IOException ioe) {
			System.out.println("日志文件打开失败。");
			ioe.printStackTrace();
		}
	}
}
