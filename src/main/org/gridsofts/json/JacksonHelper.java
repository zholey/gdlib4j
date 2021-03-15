/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.json;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具集(By jackson)
 * 
 * @author lei
 */
public class JacksonHelper {

	private ObjectMapper objectMapper;

	private JacksonHelper() {
		objectMapper = new ObjectMapper();

		// 注册自定义的解析模块
		objectMapper.registerModule(new CustomJacksonModule());
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static JacksonHelper newInstance() {
		return new JacksonHelper();
	}

	/**
	 * 将JavaBean转换成JSON字符串
	 * 
	 * @param obj      将要进行序列化的对象
	 * @param isPretty 是否进行美化
	 * @return
	 */
	public String toJson(Object obj, boolean isPretty) {

		try {
			return isPretty ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
					: objectMapper.writeValueAsString(obj);
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 将JavaBean转换成JSON字符串
	 * 
	 * @param obj 将要进行序列化的对象
	 * @return
	 */
	public String toJson(Object obj) {
		return toJson(obj, false);
	}

	/**
	 * （常用）从给定的JSON字符串中读取出指定类型的JavaBean；
	 * 
	 * @param content   将要进行反序列化的JSON字符串
	 * @param typeClass 与JSON内容等价的JavaBean类型
	 * @return
	 */
	public <T> T getObject(String content, Class<T> typeClass) {

		try {
			return objectMapper.readValue(content, typeClass);
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 从给定的JSON字符串中读取出指定类型的JavaBean；<br/>
	 * 这是 {@link public <T> T getObject(String content, Class<T> typeClass)}
	 * 的另一种写法<br/>
	 * 
	 * @param content       将要进行反序列化的JSON字符串
	 * @param typeReference 用这个类的实例化对象可以包装一个泛型；<br/>
	 *                      如：TypeReference ref = new TypeReference<List<Integer>>()
	 *                      { };<br/>
	 *                      这将让本方法知道你期望返回的类型是 List<Integer>
	 * @return
	 */
	public <T> T getObject(String content, TypeReference<T> typeReference) {

		try {
			return objectMapper.readValue(content, typeReference);
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 从给定的JSON字符串中读取出指定类型的JavaBean；可以明确指定泛型的类型，支持多层嵌套；<br/>
	 * 用法：ArrayList&lt;List&lt;String>> obj = getObject(content, ArrayList.class,
	 * List.class, String.class)
	 * 
	 * @param content          将要进行反序列化的JSON字符串
	 * @param parametrized     最外层的对象类型，它本身可能是泛型化的
	 * @param parameterClasses 按照参数顺序，指代前一个类型的泛型类型
	 * @return
	 */
	public <T> T getObject(String content, Class<?> parametrized, Class<?>... parameterClasses) {

		try {
			return objectMapper.readValue(content,
					objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * （常用）从给定的JSON字符串中读取出指定元素类型的List对象；<br/>
	 * 这个方法是 List&lt;String> obj = getObject(content, List.class, String.class)
	 * 的简写形式
	 * 
	 * @param content     将要进行反序列化的JSON字符串，JSON内容应为数组
	 * @param elementType 与JSON数组内单个对象等价的JavaBean类型，即List内的元素对象类型
	 * @return
	 */
	public <T> List<T> getList(String content, Class<T> elementType) {

		try {
			return objectMapper.readValue(content,
					objectMapper.getTypeFactory().constructParametricType(List.class, elementType));
		} catch (Throwable e) {
		}

		return null;
	}
}
