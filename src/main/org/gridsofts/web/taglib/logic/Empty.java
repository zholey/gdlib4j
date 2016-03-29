/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.gridsofts.web.taglib.logic.forEachClasses.IEachable;
import org.gridsofts.util.StringUtil;

/**
 * 用于当结果为空时所要显示的内容
 * @author Lei
 *
 */
public class Empty extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	
	private String scope;
	private String field;

	private List<?> list;

	@Override
	public int doStartTag() throws JspException {

		// 默认从父标签中取结果集
		if (StringUtil.isNull(scope)) {

			Tag parentTag = getParent();

			if (parentTag != null && parentTag instanceof IEachable) {
				list = ((IEachable) parentTag).getEachList(field);
			}

		} else if ("request".equalsIgnoreCase(scope)) {

			list = (List<?>) pageContext.getRequest().getAttribute(field);

		} else if ("session".equalsIgnoreCase(scope)) {

			list = (List<?>) pageContext.getSession().getAttribute(field);

		}

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {
		
		try {
			String htmlContent = getBodyContent().getString();
			getBodyContent().clearBody();

			if (!StringUtil.isEmpty(htmlContent) && (list == null || list.size() == 0)) {
				getBodyContent().getEnclosingWriter().println(htmlContent);
			}

		} catch (java.io.IOException ioe) {
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		list = null;

		return BodyTag.EVAL_PAGE;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}
