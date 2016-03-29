/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class HtmlFormatter extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() throws JspException {
		
		String content = getBodyContent().getString();
		getBodyContent().clearBody();

		if (content == null || content.trim().equals("")) {
			return BodyTag.SKIP_BODY;
		}
		
		try {
			content = content.replaceAll("\\n", "<br>");
			
			getBodyContent().getEnclosingWriter().println(content);
		} catch (IOException e) {
		}

		return BodyTag.SKIP_BODY;
	}
}
