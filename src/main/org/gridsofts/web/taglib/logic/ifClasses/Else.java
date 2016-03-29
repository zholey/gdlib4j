/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.ifClasses;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.gridsofts.web.taglib.logic.If;

public class Else extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {

		if (getParent() != null && getParent() instanceof If) {
			If parent = (If) getParent();

			if (!parent.getTest()) {
				return BodyTag.EVAL_BODY_BUFFERED;
			}
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException {

		try {
			If parent = (If) getParent();

			parent.setElseContent(bodyContent.getString());

			bodyContent.clearBuffer();
		} catch (Throwable e) {
		}

		return BodyTag.SKIP_BODY;
	}
}
