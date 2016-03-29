/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class ContextPath extends TagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {

		try {
			String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
			
			if (contextPath != null) {
				pageContext.getOut().print(contextPath);
			}
		} catch (IOException e) {
		}

		return Tag.SKIP_BODY;
	}
}
