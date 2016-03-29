/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.gridsofts.util.BeanUtil;
import org.gridsofts.util.DateTime;
import org.gridsofts.util.StringUtil;

public class HttpServletRequestUtil implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 利用HttpServletRequest中的参数，填充bean的字段
	 * 
	 * @param <T>
	 * @param bean
	 * @param request
	 */
	public static <T> void fillField(T bean, HttpServletRequest request) {

		// 取所有参数
		Enumeration<String> paramNames = (Enumeration<String>) request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String param = paramNames.nextElement();
			String value = request.getParameter(param);

			try {
				Field field = bean.getClass().getDeclaredField(param);

				if (field != null) {

					// 自动转换数据为预期的类型

					// 字符串(此处是为了提高程序运行速度)
					if (field.getType().equals(String.class)) {
						BeanUtil.setFieldValue(bean, param, String.class, value);
					}
					
					// 整型
					else if (field.getType().equals(int.class)) {
						BeanUtil.setFieldValue(bean, param, int.class, Integer.valueOf(value));
					}
					// 整型
					else if (field.getType().equals(Integer.class)) {
						BeanUtil.setFieldValue(bean, param, Integer.class, Integer.valueOf(value));
					}
					
					// 浮点
					else if (field.getType().equals(float.class)) {
						BeanUtil.setFieldValue(bean, param, float.class, Float.valueOf(value));
					}
					// 浮点
					else if (field.getType().equals(Float.class)) {
						BeanUtil.setFieldValue(bean, param, Float.class, Float.valueOf(value));
					}
					
					// 双精度浮点
					else if (field.getType().equals(double.class)) {
						BeanUtil.setFieldValue(bean, param, double.class, Double.valueOf(value));
					}
					// 双精度浮点
					else if (field.getType().equals(Double.class)) {
						BeanUtil.setFieldValue(bean, param, Double.class, Double.valueOf(value));
					}
					
					// 布尔
					else if (field.getType().equals(boolean.class)) {

						boolean v = false;

						if (StringUtil.isNumber(value)) {
							v = Double.valueOf(value) != 0;
						} else {
							v = StringUtil.isTrue(value);
						}

						BeanUtil.setFieldValue(bean, param, boolean.class, v);
					}
					// 布尔
					else if (field.getType().equals(Boolean.class)) {

						boolean v = false;

						if (StringUtil.isNumber(value)) {
							v = Double.valueOf(value) != 0;
						} else {
							v = StringUtil.isTrue(value);
						}

						BeanUtil.setFieldValue(bean, param, Boolean.class, v);
					}
					
					// Timestamp
					else if (field.getType().equals(Timestamp.class)) {

						Timestamp t = null;

						Pattern dateExp = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}(\\s+\\d{2}:\\d{2}(:\\d{2})?)?$");

						if (dateExp.matcher(value).find()) {
							String[] dateAry = value.split("\\s+");

							DateTime datetime = null;

							if (dateAry != null && dateAry.length == 2) {
								String[] date = dateAry[0].split("-");
								String[] time = dateAry[1].split(":");

								if (time.length == 3) {
									datetime = new DateTime(Integer.valueOf(date[0]), Integer.valueOf(date[1]),
											Integer.valueOf(date[2]), Integer.valueOf(time[0]),
											Integer.valueOf(time[1]), Integer.valueOf(time[2]));
								} else {
									datetime = new DateTime(Integer.valueOf(date[0]), Integer.valueOf(date[1]),
											Integer.valueOf(date[2]), Integer.valueOf(time[0]),
											Integer.valueOf(time[1]));
								}
							} else if (dateAry != null && dateAry.length == 1) {
								String[] date = dateAry[0].split("-");

								datetime = new DateTime(Integer.valueOf(date[0]), Integer.valueOf(date[1]),
										Integer.valueOf(date[2]));
							}

							if (datetime != null) {

								t = new Timestamp(datetime.getTimeInMillis());
							}
						}

						BeanUtil.setFieldValue(bean, param, Timestamp.class, t);
					}

					// 默认为字符串
					else {
						BeanUtil.setFieldValue(bean, param, String.class, value);
					}
				}
			} catch (Throwable t) {
			}
		}
	}
}
