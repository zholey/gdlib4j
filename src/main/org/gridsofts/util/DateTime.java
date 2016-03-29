/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间类。为操作日期、时间等提供便捷的方法。
 * 
 * @author Lei
 * @version 2.0
 */
public class DateTime implements Serializable {
	private static final long serialVersionUID = 1L;

	private Calendar curTime;

	/**
	 * 静态方法。返回一个日期时间类对象。
	 * 
	 * @return 系统当前时间
	 */
	public static DateTime getCurrentTime() {
		return new DateTime();
	}

	/**
	 * 静态方法。返回一个日期时间类对象。
	 * 
	 * @param millis
	 *            毫秒值
	 * @return 指定时间
	 */
	public static DateTime getDateTime(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);

		return new DateTime(cal);
	}

	/**
	 * 以系统当前时间构造。
	 */
	public DateTime() {
		curTime = Calendar.getInstance();
	}

	/**
	 * 根据用户提供的Calendar 类对象构造此类
	 * 
	 * @param cal
	 *            用户提供的Calendar对象
	 */
	public DateTime(Calendar cal) {
		curTime = cal;
	}

	/**
	 * 尝试根据用户提供的时间字符串解析构造，如果不成功则相当于调用无参数构造函数
	 * 
	 * @param timeStr
	 *            用户提供的时间字符串
	 */
	public DateTime(String timeStr) {
		curTime = Calendar.getInstance();

		SimpleDateFormat format = new SimpleDateFormat();

		try {
			Date date = format.parse(timeStr);
			curTime.setTimeInMillis(date.getTime());
		} catch (Throwable t) {
		}
	}

	/**
	 * 以用户指定的年、月、日，构造此类
	 * 
	 * @param year
	 *            指定的年
	 * @param month
	 *            指定的月
	 * @param date
	 *            指定的日
	 */
	public DateTime(int year, int month, int date) {
		curTime = Calendar.getInstance();
		curTime.setTimeInMillis(0);

		curTime.set(year, month - 1, date);
	}

	/**
	 * 以用户指定的年、月、日、时、分，构造此类
	 * 
	 * @param year
	 *            指定的年
	 * @param month
	 *            指定的月
	 * @param date
	 *            指定的日
	 * @param hour
	 *            指定的时
	 * @param minute
	 *            指定的分
	 */
	public DateTime(int year, int month, int date, int hour, int minute) {
		curTime = Calendar.getInstance();
		curTime.setTimeInMillis(0);

		curTime.set(year, month - 1, date, hour, minute);
	}

	/**
	 * 以用户指定的年、月、日、时、分、秒，构造此类
	 * 
	 * @param year
	 *            指定的年
	 * @param month
	 *            指定的月
	 * @param date
	 *            指定的日
	 * @param hour
	 *            指定的时
	 * @param minute
	 *            指定的分
	 * @param second
	 *            指定的秒
	 */
	public DateTime(int year, int month, int date, int hour, int minute, int second) {
		curTime = Calendar.getInstance();
		curTime.setTimeInMillis(0);

		curTime.set(year, month - 1, date, hour, minute, second);
	}

	/**
	 * 返回系统当前年份。
	 * 
	 * @return 当前年份
	 */
	public int getYear() {
		return curTime.get(Calendar.YEAR);
	}

	/**
	 * 返回系统当前月份。
	 * 
	 * @return 当前月份
	 */
	public int getMonth() {
		return curTime.get(Calendar.MONTH) + 1;
	}

	/**
	 * 返回系统当前日期。
	 * 
	 * @return 当前日期
	 */
	public int getDayOfMonth() {
		return curTime.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 返回一个星期中的某天
	 * 
	 * @return 星期*
	 */
	public int getDayOfWeek() {
		return curTime.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 返回小时
	 * 
	 * @return 小时
	 */
	public int getHour() {
		return curTime.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 返回分钟
	 * 
	 * @return 分钟
	 */
	public int getMinute() {
		return curTime.get(Calendar.MINUTE);
	}

	/**
	 * 返回此 DateTime 的时间值，以毫秒为单位
	 * 
	 * @return 当前时间，以从历元至现在所经过的 UTC 毫秒数形式。
	 */
	public long getTimeInMillis() {
		return curTime.getTimeInMillis();
	}

	/**
	 * 比较两个 DateTime 对象表示的时间值（从历元至现在的毫秒偏移量）。
	 * 
	 * @param time
	 *            要比较的 DateTime。
	 * @return 如果参数表示的时间等于此 DateTime 表示的时间，则返回 0 值；如果此 DateTime
	 *         的时间在参数表示的时间之前，则返回小于 0 的值；如果此 DateTime 的时间在参数表示的时间之后，则返回大于 0 的值。
	 */
	public int compareTo(DateTime time) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTimeInMillis());

		return curTime.compareTo(cal);
	}

	/**
	 * 比较指定的年、月、日
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public int compareTo(int year, int month, int dayOfMonth) {

		Calendar thisCal = Calendar.getInstance();
		Calendar siteCal = Calendar.getInstance();

		thisCal.setTimeInMillis(getTimeInMillis());
		siteCal.setTimeInMillis(getTimeInMillis());

		siteCal.set(Calendar.YEAR, year);
		siteCal.set(Calendar.MONTH, month - 1);
		siteCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		return thisCal.compareTo(siteCal);
	}

	/**
	 * 返回默认格式的时间字符串。
	 * 
	 * @return 当前时间字符串。格式：“yyyy-mm-dd hh:mi:ss”
	 */
	public String toString() {
		return toString(null);
	}

	/**
	 * 返回指定格式的时间字符串。
	 * 
	 * @param format
	 *            指定的时间格式
	 * @return 当前时间字符串
	 */
	public String toString(String format) {

		if (format == null) {
			format = "yyyy-mm-dd hh:mi:ss";
		}

		format = format.replaceAll("[yY]{4}", String.valueOf(curTime.get(Calendar.YEAR)));
		format = format.replaceAll("[yY]{2}", String.valueOf(curTime.get(Calendar.YEAR)).substring(2));

		format = format.replaceAll("[mM]{2}", setLengthTo2(curTime.get(Calendar.MONTH) + 1));
		format = format.replaceAll("[dD]{2}", setLengthTo2(curTime.get(Calendar.DAY_OF_MONTH)));

		format = format.replaceAll("[hH]{2}", setLengthTo2(curTime.get(Calendar.HOUR_OF_DAY)));
		format = format.replaceAll("[mM][iI]", setLengthTo2(curTime.get(Calendar.MINUTE)));
		format = format.replaceAll("[sS]{2}", setLengthTo2(curTime.get(Calendar.SECOND)));

		return format;
	}

	private String setLengthTo2(int value) {

		if (value < 10) {
			return "0" + value;
		}

		return String.valueOf(value);
	}

	/**
	 * 返回指定年、月的天数。
	 * 
	 * @param year
	 *            指定年份
	 * @param month
	 *            指定月份
	 * @return 天数
	 */
	public static int getMaxDaysOfMonth(int year, int month) {

		switch (month) {
		case 2:
			return (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) ? 29 : 28;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		default:
			return 31;
		}
	}
}
