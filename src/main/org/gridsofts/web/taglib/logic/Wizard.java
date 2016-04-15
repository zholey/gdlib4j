/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.gridsofts.util.BeanUtil;
import org.gridsofts.util.Encrypt;
import org.gridsofts.util.StringUtil;
import org.gridsofts.web.util.HttpServletRequestUtil;

public class Wizard extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String id;
	
	/**
	 * 用于在Session保存配置信息的属性名
	 */
	private String name;

	/**
	 * 用于存值的Bean ClassName
	 */
	private String bean;

	/**
	 * 子标签Step包含的内容
	 */
	private List<String> stepContent;

	private String direction;
	private Integer stepIndex = 0;
	
	private String gotoStepIndex;
	
	/**
	 * 动态令牌（防止页面重复提交）
	 */
	private String pageToken;

	@Override
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = pageContext.getSession();
		
		if (StringUtil.isEmpty(name)) {
			name = Wizard.class.getName().replaceAll("\\.", "_");
		}

		stepContent = new ArrayList<>();

		// 获取向导的走行方向
		direction = request.getParameter("direction");
		// 如果 direction == goto，则此参数值为要跳转到的步骤索引
		gotoStepIndex = request.getParameter("stepIndex");
		
		// 获取页面令牌
		pageToken = request.getParameter("token");
		

		// 获取当前步骤编号
		if (session.getAttribute(name + ".stepIndex") == null) {
			session.setAttribute(name + ".stepIndex", 0);
		}
		stepIndex = (Integer) session.getAttribute(name + ".stepIndex");

		// 初始化向导值域对象
		if (session.getAttribute(name) == null) {

			try {
				session.setAttribute(name, BeanUtil.newInstance(Class.forName(bean)));
			} catch (Exception e) {
			}
		}

		// 用Request中的值，填充向导值域对象
		Object bean = session.getAttribute(name);
		HttpServletRequestUtil.fillField(bean, request);

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {

		try {
			HttpSession session = pageContext.getSession();
			JspWriter bodyWriter = bodyContent.getEnclosingWriter();

			// 生成Form标签头
			bodyWriter.println("<form id=\"" + id + "\" name=\"" + id + "\" action=\"\" method=\"post\">");

			if (stepContent != null && stepContent.size() > 0) {

				// 验证动态令牌，
				if (session.getAttribute(name + ".token") == null
						|| session.getAttribute(name + ".token").equals(pageToken)) {

					if ("forward".equalsIgnoreCase(direction)) {
						stepIndex++;
					} else if ("backward".equalsIgnoreCase(direction)) {
						stepIndex--;
					} else if ("goto".equalsIgnoreCase(direction) && !StringUtil.isNull(gotoStepIndex)) {
						
						stepIndex = Integer.valueOf(gotoStepIndex);
					}
				}

				if (stepIndex < 0) {
					
					stepIndex = 0;
					
				} else if (stepIndex >= stepContent.size()) {
					
					stepIndex = stepContent.size() - 1;
				}

				bodyWriter.println(stepContent.get(stepIndex));
			}

			// 更新步骤编号
			session.setAttribute(name + ".stepIndex", stepIndex);
			
			
			// 生成新的动态令牌
			String token = Encrypt.md5(name + System.currentTimeMillis() + Math.random());
			
			// 更新验证令牌
			session.setAttribute(name + ".token", token);
			
			bodyWriter.println("<input type=\"hidden\" name=\"token\" value=\"" + token + "\"/>");
			bodyWriter.println("<input type=\"hidden\" name=\"direction\" value=\"forward\"/>");
			bodyWriter.println("<input type=\"hidden\" name=\"stepIndex\" value=\"" + stepIndex + "\"/>");

			// 生成Form结束标签
			bodyWriter.println("</form>");
			
		} catch (Throwable ex) {
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		stepContent.clear();
		stepContent = null;

		return BodyTag.EVAL_PAGE;
	}

	public void addStepContent(String content) {
		stepContent.add(content);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}
}
