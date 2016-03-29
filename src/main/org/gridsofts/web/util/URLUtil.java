/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.gridsofts.util.StringUtil;

/**
 * 与URL处理相关的静态工具方法类
 * 
 * @author Lei
 * 
 */
public class URLUtil {

	/**
	 * 与getRequestHostURL方法相关的静态正则表达式
	 */
	private static final Pattern RequestHostURLExp = Pattern.compile("(?i)^(https?:\\/\\/[^:\\/]+(:\\d+)?)\\/?");

	/**
	 * 从给定的request中获取完整的主机URL。包含端口；
	 * 
	 * 如：http://www.gridsofts.com/
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestHostURL(HttpServletRequest request) {

		String requestHostURL = null;

		if (request != null) {
			String requestURL = request.getRequestURL().toString();

			Matcher mHost = RequestHostURLExp.matcher(requestURL);

			if (mHost != null && mHost.find()) {
				requestHostURL = mHost.group(1);
			}
		}

		return requestHostURL;
	}
	
	/**
	 * 将给定的URL进行整理，使之合法。
	 * 
	 * 具体步骤：
	 * 1、将连续的两个以上的“\”字符变成一个
	 * 2、将URL中首个“?”以后的都变成“&”
	 * 
	 * @param url
	 * @return
	 */
	public static String justify(String url) {
		
		if (!StringUtil.isNull(url)) {
			
			// 1、将连续的两个以上的“/”字符变成一个
			url = url.replaceAll("/{2,}", "/");
			
			Matcher matcherHTTP = Pattern.compile("(?i)^https?:\\/").matcher(url);
			if (matcherHTTP != null && matcherHTTP.find()) {
				url = url.replaceFirst("/", "//");
			}

			// 2、将URL中首个“?”以后的都变成“&”
			Matcher matcherQM = Pattern.compile("\\?").matcher(url);
			if (matcherQM != null && matcherQM.find()) {
				StringBuffer bufferQM = new StringBuffer();
				
				while (matcherQM.find()) {
					matcherQM.appendReplacement(bufferQM, "&");
				}
				matcherQM.appendTail(bufferQM);
				
				url = bufferQM.toString();
			}
		}
		
		return url;
	}
}
