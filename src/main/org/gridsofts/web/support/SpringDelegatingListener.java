/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 在使用Spring框架时，用于配置Servlet容器监听代理
 * 
 * @author lei
 */
public class SpringDelegatingListener implements ServletContextListener {

	private WebApplicationContext webApplicationContext;

	private String targetBeanName;

	private ServletContextListener targetListener;

	@Override
	public void contextDestroyed(ServletContextEvent scEvent) {
		ServletContext servletContext = scEvent.getServletContext();

		if (targetListener == null) {
			initTargetListener(servletContext);
		}

		targetListener.contextInitialized(scEvent);
	}

	@Override
	public void contextInitialized(ServletContextEvent scEvent) {
		ServletContext servletContext = scEvent.getServletContext();

		if (targetListener == null) {
			initTargetListener(servletContext);
		}

		targetListener.contextInitialized(scEvent);
	}

	protected void initTargetListener(ServletContext servletContext) {

		// get target bean name
		if (this.targetBeanName == null) {
			this.targetBeanName = getTargetBeanName(servletContext);
		}

		findWebApplicationContext(servletContext);

		Object targetBeanObj = webApplicationContext.getBean(targetBeanName);
		if (targetBeanObj != null && ServletContextListener.class.isAssignableFrom(targetBeanObj.getClass())) {
			targetListener = (ServletContextListener) targetBeanObj;
		}
	}

	protected String getTargetBeanName(ServletContext servletContext) {
		return String.valueOf(servletContext.getAttribute(getClass().getName() + ".targetBeanName"));
	}

	protected WebApplicationContext findWebApplicationContext(ServletContext servletContext) {

		if (this.webApplicationContext != null) {
			// The user has injected a context at construction time -> use it...
			if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
				ConfigurableApplicationContext cac = (ConfigurableApplicationContext) this.webApplicationContext;
				if (!cac.isActive()) {
					// The context has not yet been refreshed -> do so before
					// returning it...
					cac.refresh();
				}
			}
		} else {
			webApplicationContext = WebApplicationContextUtils.findWebApplicationContext(servletContext);
		}

		return this.webApplicationContext;
	}
}
