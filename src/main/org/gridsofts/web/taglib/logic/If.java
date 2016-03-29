/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.gridsofts.web.taglib.logic.ifClasses.EachTester;

public class If extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private boolean test = false;
	private String testEach;

	/**
	 * 子标签Then包含的内容（如果有）
	 */
	private String thenContent;

	/**
	 * 子标签Else包含的内容（如果有）
	 */
	private String elseContent;

	@Override
	public int doStartTag() throws JspException {

		// 为支持If标签嵌套，向上递归查找ForEach
		ForEach forEachTag = ForEach.findForEach(this);

		// 以下条件成立则显示内容，否则不显示内容（如果存在子标签Else，则显示子标签的内容）：<br>
		if (testEach != null && forEachTag != null) {
			test = EachTester.test(forEachTag.getHtmlString(testEach));
		}

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {

		try {

			if (test) {
				bodyContent.getEnclosingWriter().print(bodyContent.getString());

				if (thenContent != null) {
					bodyContent.getEnclosingWriter().print(thenContent);
				}
			} else {
				if (elseContent != null) {
					bodyContent.getEnclosingWriter().print(elseContent);
				}
			}
		} catch (Throwable ex) {
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		test = false;

		setThenContent(null);
		setElseContent(null);

		return BodyTag.EVAL_PAGE;
	}

	public boolean getTest() {
		return this.test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public void setTestEach(String testEach) {
		this.testEach = testEach;
	}

	public void setThenContent(String thenContent) {
		this.thenContent = thenContent;
	}

	public void setElseContent(String elseContent) {
		this.elseContent = elseContent;
	}
}
