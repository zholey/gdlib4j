/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.gridsofts.util.PagingList;

/**
 * 生成连续的行号。
 * 
 * @author Lei
 */
public class ContinuousNum extends TagSupport {
	private static final long serialVersionUID = 1L;

	private PagingList<?> items;

	private int offset;

	@Override
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		try {
			if (items != null) {
				pageContext.getOut().print(
						(items.getPageNum() - 1) * items.getPageCapacity() + offset);
			} else {
				pageContext.getOut().print(offset);
			}
		} catch (IOException ex) {
		}

		return Tag.EVAL_PAGE;
	}

	public PagingList<?> getItems() {
		return items;
	}

	public void setItems(PagingList<?> items) {
		this.items = items;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
