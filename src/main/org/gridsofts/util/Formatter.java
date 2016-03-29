/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串格式化类。
 * 
 * @author zholey
 * @version 2.0
 */

public abstract class Formatter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 返回指定格式的时间字符串。
	 * 
	 * @param format
	 *            指定的时间格式
	 * @return 当前时间字符串
	 */
	public static String dateTimeFormat(Date dateTime, String pattern) {
		
		if (dateTime == null) {
			return "";
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		
		return formatter.format(dateTime);
	}

	/**
	 * 对指定的数值进行四舍五入，保留两位精度。
	 * 
	 * @param data
	 *            需进行四舍五入运算的数值
	 * @return 四舍五入后的字符串
	 */
	public static String decimalFormat(Double data) {
		return new DecimalFormat("0.00").format(data);
	}

	/**
	 * 以指定的精度位，对指定的数值进行四舍五入。
	 * 
	 * @param data
	 *            需进行四舍五入运算的数值
	 * @param digits
	 *            指定的精度位
	 * @return 四舍五入后的字符串
	 */
	public static String decimalFormat(Double data, Integer digits) {
		
		if (data == null) {
			data = 0d;
		}

		String format = "0";

		if (digits > 0) {
			format += ".";
		}

		for (int i = 0; i < digits; i++) {
			format += "0";
		}

		DecimalFormat dFormat = new DecimalFormat(format);
		return dFormat.format(data);
	}

	/**
	 * 将单位为Ms的时间数据转换为合适的单位
	 * 
	 * @param time
	 * @return
	 */
	public static String convertTimeString(Double time) {

		String str = "";

		if (time >= 1 * 60 * 60 * 1000f) {

			str = Formatter.decimalFormat(time / (1 * 60 * 60 * 1000f)) + " H";

		} else if (time >= 1 * 60 * 1000f) {

			str = Formatter.decimalFormat(time / (1 * 60 * 1000f)) + " M";

		} else if (time >= 1 * 1000f) {

			str = Formatter.decimalFormat(time / 1000f) + " S";

		} else {
			str = Formatter.decimalFormat(time) + " MS";
		}

		return str;
	}

	/**
	 * 将单位为Byte的数据转换为合适的单位
	 * 
	 * @param size
	 * @return
	 */
	public static String convertSizeString(Double size) {

		String str = "";

		if (size >= 1 * 1024 * 1024 * 1024 * 1024f) {

			str = Formatter.decimalFormat(size / (1 * 1024 * 1024 * 1024 * 1024f)) + " TB";

		} else if (size >= 1 * 1024 * 1024 * 1024f) {

			str = Formatter.decimalFormat(size / (1 * 1024 * 1024 * 1024f)) + " GB";

		} else if (size >= 1 * 1024 * 1024f) {

			str = Formatter.decimalFormat(size / (1 * 1024 * 1024f)) + " MB";

		} else if (size >= 1 * 1024f) {

			str = Formatter.decimalFormat(size / (1 * 1024f)) + " KB";

		} else {
			str = Formatter.decimalFormat(size) + " B";
		}

		return str;
	}

	/**
	 * 将速率单位为B/S的数据转换为合适的数据格式，秒保持不变
	 * 
	 * @param rate
	 * @return
	 */
	public static String convertByteRateString(Double rate) {
		return convertSizeString(rate) + "/S";
	}

	/**
	 * 将数据转换为百分比的形式
	 * 
	 * @param data
	 * @return
	 */
	public static String convertPercentString(Double data) {
		return decimalFormat(data * 100) + " %";
	}

	/**
	 * 格式化逗号表达式。主要用于SQL in(not in)语句
	 * 
	 * @param str
	 * @return
	 */
	public static String convertCommaExp(String str, boolean isCharacter) {

		if (str != null) {

			String[] elements = str.split(",");

			StringBuffer buf = new StringBuffer();

			for (String element : elements) {
				buf.append((isCharacter ? "'" : "") + element + (isCharacter ? "'," : ","));
			}

			str = buf.toString();
			
			str = str.replaceAll("\\,$", "");
			str = str.replaceAll("'{2,}", "'");
		}

		return str;
	}
}
