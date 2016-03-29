/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.permissionClasses;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.gridsofts.web.taglib.logic.Permission;

/**
 * Permission标签的子标签，用于设置当没有权限时显示的内容
 * 
 * @author Lei
 * 
 */
public class NoPermission extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {

		try {
			if (getParent() != null && getParent() instanceof Permission) {
				Permission parent = (Permission) getParent();

				parent.setNonPermissionContent(bodyContent.getString());
				
				bodyContent.clearBuffer();
			}
		} catch (Throwable e) {
		}

		return BodyTag.SKIP_BODY;
	}
}
