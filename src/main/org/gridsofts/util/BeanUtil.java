/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供与Bean相关的方法集合
 * 
 * @author Lei
 * 
 */
public class BeanUtil {

	/**
	 * 将对象转换成字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> byte[] convertToBytes(T obj) {

		try {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream);

			objOutStream.writeObject(obj);
			objOutStream.flush();
			objOutStream.close();

			return byteOutStream.toByteArray();
		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * 将字节数组转换成对象
	 * 
	 * @param bytesOfObj
	 * @param objClass
	 * @return
	 */
	public static <T> T convertToObject(byte[] bytesOfObj, Class<T> objClass) {

		try {
			ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytesOfObj);
			ObjectInputStream objInStream = new ObjectInputStream(byteInStream);

			Object obj = objInStream.readObject();
			objInStream.close();

			if (obj != null && objClass.isAssignableFrom(obj.getClass())) {
				return objClass.cast(obj);
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		return null;
	}

	// 用于从Bean中取值时的级联表达式
	private static final Pattern ChainExp = Pattern.compile("((\\w+)\\.)");

	public static <T> T newInstance(Class<T> t) throws Exception {

		T bean = null;

		try {
			bean = t.newInstance();
		} catch (Exception e) {
			throw new Exception("该类无法实例化，原始信息：" + e.getMessage());
		}

		if (bean == null) {
			throw new NullPointerException();
		}

		return bean;
	}

	public static String getGetterMethodName(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	public static String getSetterMethodName(String fieldName) {
		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	/**
	 * 在给定的类中查找所有具有某种标注类型的属性
	 * 
	 * @param beanClass
	 * @param annotationClass
	 * @return
	 */
	public static Field[] getFieldByAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationClass) {
		List<Field> fldList = new ArrayList<>();

		if (beanClass != null) {
			Field[] fldAry = getDeclaredFields(beanClass, true);
			for (int i = 0; fldAry != null && i < fldAry.length; i++) {

				if (fldAry[i].isAnnotationPresent(annotationClass)) {
					fldList.add(fldAry[i]);
				}
			}
		}

		return fldList.toArray(new Field[0]);
	}

	/**
	 * 在给定的类中查找所有属性，可以根据需要向上查找所有的父类
	 * 
	 * @param beanClass
	 * @param includeSuperClass
	 * @return
	 */
	public static Field[] getDeclaredFields(Class<?> beanClass, boolean includeSuperClass) {
		List<Field> fldList = new ArrayList<>();

		if (beanClass != null) {
			Field[] fldAry = beanClass.getDeclaredFields();
			for (int i = 0; fldAry != null && i < fldAry.length; i++) {
				fldList.add(fldAry[i]);
			}

			if (includeSuperClass && beanClass.getSuperclass() != null) {
				fldAry = getDeclaredFields(beanClass.getSuperclass(), includeSuperClass);
				for (int i = 0; fldAry != null && i < fldAry.length; i++) {
					fldList.add(fldAry[i]);
				}
			}
		}

		return fldList.toArray(new Field[0]);
	}

	/**
	 * 从给定的Bean获取属性值；
	 * 
	 * @param bean    给定的Bean，可以是Map
	 * @param fldName 属性名称，支持级联表达式“.”
	 * @return
	 */
	public static <T> Object getFieldValue(T bean, String fldName) {

		try {
			if (bean != null) {

				// 级联表达式
				Matcher chainMatcher = ChainExp.matcher(fldName);
				if (chainMatcher.find()) {

					String firstName = chainMatcher.group(2);
					fldName = chainMatcher.replaceFirst("");

					return getFieldValue(getFieldValue(bean, firstName), fldName);
				} else {

					if (bean instanceof Map) {
						return Map.class.cast(bean).get(fldName);
					}

					Class<?> beanCls = bean.getClass();
					String getterMethodName = getGetterMethodName(fldName);

					return beanCls.getMethod(getterMethodName).invoke(bean);
				}
			}
		} catch (Throwable t) {
		}

		return null;
	}

	/**
	 * 设置单个属性
	 * 
	 * @param bean
	 * @param fieldName
	 * @param fieldType
	 * @param fieldValue
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static <T> void setFieldValue(T bean, String fieldName, Class<?> fieldType, Object fieldValue)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String fieldSetterName = BeanUtil.getSetterMethodName(fieldName);

		Method fieldSetterMethod = bean.getClass().getMethod(fieldSetterName, fieldType);

		if (fieldSetterMethod != null) {
			fieldSetterMethod.invoke(bean, fieldValue);
		}
	}

	/**
	 * 判断给定的字段是否是常量（包含静态）
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isConstField(Field field) {
		return Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
	}

	/**
	 * 根据结果集信息，构造结果Map。对于此结果集的遍历操作，应该在此方法的宿主内进行。
	 * 
	 * @param rs   结果集
	 * @param rsmd 结果集元数据
	 * @return
	 */
	public static Map<String, Object> getEntityMap(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {

		Map<String, Object> entity = new HashMap<>();

		int colCount = rsmd.getColumnCount();

		for (int i = 0; i < colCount; i++) {
			String colName = rsmd.getColumnLabel(i + 1).toUpperCase();
			Object colValue = rs.getObject(colName);

			entity.put(colName, colValue);
		}

		return entity;
	}

	/**
	 * 拷贝全部属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 */
	public static <T> T copyProperties(T fromBean, T toBean) {
		return copyProperties(fromBean, toBean, null, null);
	}

	/**
	 * 拷贝部分属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 * @param ignoreFields
	 */
	public static <T> T copyProperties(T fromBean, T toBean, String[] ignoreFields) {
		return copyProperties(fromBean, toBean, ignoreFields, null);
	}

	/**
	 * 拷贝部分属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 * @param ignoreAnnotations
	 */
	public static <T> T copyProperties(T fromBean, T toBean, Annotation[] ignoreAnnotations) {
		
		if (fromBean == null || toBean == null) {
			throw new NullPointerException();
		}
		
		if (ignoreAnnotations == null) {
			return copyProperties(fromBean, toBean);
		}
		
		// 需要忽略的注解列表
		final List<Annotation> ignoreAnnotationList = Arrays.asList(ignoreAnnotations);

		// 找出所有需要忽略的属性名称
		List<String> ignoreFieldNames = new ArrayList<>();
		Field[] fields = toBean.getClass().getDeclaredFields();

		if (fields == null || fields.length == 0) {
			return toBean;
		}

		// 为bean的各字段赋值
		fieldLoop: for (int i = 0, fieldCount = fields.length; i < fieldCount; i++) {
			Field toFld = fields[i];

			// 跳过静态、常量字段
			if (isConstField(toFld)) {
				continue fieldLoop;
			}

			String fieldName = toFld.getName();

			// 忽略的字段
			Annotation[] fldAnnotations = toFld.getAnnotations();
			if (fldAnnotations != null && fldAnnotations.length > 0) {
				Arrays.stream(fldAnnotations).filter(annotation -> {
					return ignoreAnnotationList.contains(annotation);
				}).findAny().ifPresent(annotaion -> {
					ignoreFieldNames.add(fieldName);
				});
			}
		}
		
		return copyProperties(fromBean, toBean, ignoreFieldNames.toArray(new String[0]));
	}

	/**
	 * 拷贝属性
	 * 
	 * @param <T>
	 * @param fromObj
	 * @param toObj
	 * @param ignoreFields
	 * @param limitFields
	 */
	public static <T> T copyProperties(T fromObj, T toObj, String[] ignoreFields, String[] limitFields) {

		if (fromObj == null || toObj == null) {
			throw new NullPointerException();
		}

		Field[] fields = toObj.getClass().getDeclaredFields();

		if (fields == null || fields.length == 0) {
			return toObj;
		}

		// 为bean的各字段赋值
		fieldLoop: for (int i = 0, fieldCount = fields.length; i < fieldCount; i++) {
			Field toFld = fields[i];

			// 跳过静态、常量字段
			if (isConstField(toFld)) {
				continue fieldLoop;
			}

			String fieldName = toFld.getName();

			// 忽略的字段
			if (ignoreFields != null) {

				for (String fld : ignoreFields) {

					if (fieldName.equals(fld)) {
						continue fieldLoop;
					}
				}
			}

			// 限制的字段
			if (limitFields != null) {

				boolean finded = false;
				for (String fld : limitFields) {

					if (fieldName.equals(fld)) {
						finded = true;
						break;
					}
				}

				if (!finded) {
					continue fieldLoop;
				}
			}

			Object fieldValue = null;

			// getter
			Method getterMethod = null;
			try {
				getterMethod = fromObj.getClass().getMethod(getGetterMethodName(fieldName));

				// 优先使用 getter 方法来获取属性值
				if (getterMethod != null) {
					fieldValue = getterMethod.invoke(fromObj);
				} else {
					Field fromFld = fromObj.getClass().getField(fieldName);
					if (fromFld != null) {
						fromFld.setAccessible(true);
						fieldValue = fromFld.get(fromObj);
					}
				}
			} catch (Throwable e) {
			}

			// setter
			Method setterMethod = null;
			try {
				setterMethod = toObj.getClass().getMethod(getSetterMethodName(fieldName), toFld.getType());

				// 优先使用 setter 方法来为属性赋值
				if (setterMethod != null) {
					setterMethod.invoke(toObj, fieldValue);
				} else {
					toFld.setAccessible(true);
					toFld.set(toObj, fieldValue);
				}
			} catch (Throwable e) {
			}
		}

		return toObj;
	}
}
