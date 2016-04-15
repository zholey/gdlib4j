/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

/**
 * Web应用程序上下文容器
 * 
 * @author Lei
 */
public class WebContext implements Serializable {
	private static final long serialVersionUID = 1L;

	// HttpServletRequest Key
	protected static final String HTTP_REQ = "HTTP_REQ";

	// HttpServletResponse Key
	protected static final String HTTP_RES = "HTTP_RES";

	// HttpSession Key
	protected static final String HTTP_SESSION = "HTTP_SESSION";

	private static ThreadLocal<Map<String, Object>> localMap = new ThreadLocal<>();
	
	private static ServletContext servletContext;
	private static ApplicationContext applicationContext;

	protected static Map<String, Object> getLocalMap() {

		Map<String, Object> map = localMap.get();

		if (map == null) {
			localMap.set(Collections.synchronizedMap(new HashMap<String, Object>()));
		}

		return localMap.get();
	}

	/**
	 * @return the applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param applicationContext the applicationContext to set
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		WebContext.applicationContext = applicationContext;
	}

	/**
	 * ServletContext getter method
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * ServletContext setter method
	 * 
	 * @param sc
	 */
	public static void setServletContext(ServletContext sc) {
		servletContext = sc;
	}

	/**
	 * ServletContext clean method
	 * 
	 * @param sc
	 */
	public static void cleanServletContext() {
		servletContext = null;
	}

	/**
	 * HttpServletRequest getter method
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getLocalMap().get(HTTP_REQ);
	}

	/**
	 * HttpServletRequest setter method
	 * 
	 * @param req
	 */
	public static void setRequest(HttpServletRequest req) {
		getLocalMap().put(HTTP_REQ, req);

		setHttpSession(req.getSession(true));
	}

	/**
	 * HttpServletRequest clean method
	 * 
	 * @param req
	 */
	public static void cleanRequest() {
		getLocalMap().remove(HTTP_REQ);
	}

	/**
	 * HttpServletResponse getter method
	 * 
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getLocalMap().get(HTTP_RES);
	}

	/**
	 * HttpServletResponse setter method
	 * 
	 * @param res
	 */
	public static void setResponse(HttpServletResponse res) {
		getLocalMap().put(HTTP_RES, res);
	}

	/**
	 * HttpServletResponse clean method
	 * 
	 * @param res
	 */
	public static void cleanResponse() {
		getLocalMap().remove(HTTP_RES);
	}

	/**
	 * HttpSession getter method
	 * 
	 * @return
	 */
	public static HttpSession getHttpSession() {
		return (HttpSession) getLocalMap().get(HTTP_SESSION);
	}

	/**
	 * HttpSession setter method
	 * 
	 * @param session
	 */
	public static void setHttpSession(HttpSession session) {
		getLocalMap().put(HTTP_SESSION, session);
	}

	/**
	 * HttpSession clean method
	 * 
	 * @param session
	 */
	public static void cleanHttpSession() {
		getLocalMap().remove(HTTP_SESSION);
	}
}
