/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.gridsofts.web.taglib.logic.forEachClasses.IEachable;
import org.gridsofts.util.Resource;
import org.gridsofts.util.PagingList;
import org.gridsofts.util.StringUtil;
import org.gridsofts.web.util.URLUtil;

/**
 * 翻页采用静态链接的形式，这样有利于搜索引擎优化。
 * 
 * @author Lei
 * 
 */
public class SimpleTurnPager extends TagSupport {
	protected static final long serialVersionUID = 1L;
	
	private static final Pattern ContextURLExp = Pattern.compile("^\\/");

	protected String scope;
	protected String field;

	protected String style = "default";

	// 翻页控制栏Div的ID
	protected String id;

	// 用于接收翻页动作的URL
	protected String url;

	protected PagingList<?> items;

	// 需要支持多语的环境可以将需要的语言传入，也可以通过继承此标签来实现
	protected String lan = "zh_CN";

	@Override
	public int doStartTag() throws JspException {

		if (items == null) {
			
			// 默认从父标签中取结果集
			if (StringUtil.isNull(scope)) {
	
				Tag parentTag = getParent();
	
				if (parentTag != null && parentTag instanceof IEachable) {
					items = (PagingList<?>) ((IEachable) parentTag).getEachList(field);
				}
	
			} else if ("request".equalsIgnoreCase(scope)) {
	
				items = (PagingList<?>) pageContext.getRequest().getAttribute(field);
	
			} else if ("session".equalsIgnoreCase(scope)) {
	
				items = (PagingList<?>) pageContext.getSession().getAttribute(field);
	
			}
		}
		
		if (StringUtil.isNull(id)) {
			id = "SimpleTurnPager";
		}
		
		// 默认显示简体中文
		if (StringUtil.isNull(lan)) {
			lan = "zh_CN";
		}

		return Tag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		if (items != null) {

			String htmlContent = null;

			if ("default".equalsIgnoreCase(style)) {
				htmlContent = getDefaultStyle();
			}

			// direct
			else if ("direct".equalsIgnoreCase(style)) {
				htmlContent = getDirectStyle();
			}

			try {
				pageContext.getOut().print(htmlContent);
			} catch (IOException ex) {
			}
		}

		items = null;

		return Tag.EVAL_PAGE;
	}

	/**
	 * 构造简单链接样式翻页
	 * 
	 * @return
	 */
	private String getDirectStyle() {

		String attachUrl = url.replaceAll("(?i)\\#\\[TF\\]", (field == null ? "" : field));

		// 在当前页面两侧填充的页面数
		int fill = items.getPageCount() >= 100 ? 10 : 5;

		int start = items.getPageNum() - fill;
		int end = items.getPageNum() + fill;

		if (start < 1) {
			start = 1;
		}
		if (end > items.getPageCount()) {
			end = items.getPageCount();
		}

		StringBuffer htmlContent = new StringBuffer();

		// 如需精细控制翻页条的样式，则必须指定ID
		htmlContent.append("<div id=\"" + id + "\" class=\"SimpleTurnPager\">");

		// previous
		if (items.getPageNum() > 1) {
			htmlContent.append("<span id=\"" + id + "_prev\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageNum() - 1)) + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.prevPage") + "</a></span>");
		}

		// first
		if (start > 1) {
			htmlContent.append("<span id=\"" + id + "_first\">");
			htmlContent.append("<a href=\"" + attachUrl.replaceAll("(?i)\\#\\[TP\\]", "1") + "\">1 ...</a></span>");
		}

		// left side
		for (int i = start; i < items.getPageNum(); i++) {
			htmlContent.append("<span id=\"" + id + "_" + i + "\">");
			htmlContent.append("<a href=\"" + attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(i)) + "\">" + i
					+ "</a></span>");
		}

		// current
		htmlContent.append("<span id=\"" + id + "_f\" class=\"current\">" + items.getPageNum() + "</span>");

		// right side
		for (int i = items.getPageNum() + 1; i <= end; i++) {
			htmlContent.append("<span id=\"" + id + "_" + i + "\">");
			htmlContent.append("<a href=\"" + attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(i)) + "\">" + i
					+ "</a></span>");
		}

		// last
		if (end < items.getPageCount()) {
			htmlContent.append("<span id=\"" + id + "_last\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageCount())) + "\">... "
					+ items.getPageCount() + "</a></span>");
		}

		// next
		if (items.getPageNum() < items.getPageCount()) {
			htmlContent.append("<span id=\"" + id + "_next\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageNum() + 1)) + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.nextPage") + "</a></span>");
		}

		htmlContent.append("</div>");

		return htmlContent.toString();
	}

	/**
	 * 构造默认样式翻页
	 * 
	 * @return
	 */
	private String getDefaultStyle() {

		String attachUrl = url.replaceAll("(?i)\\#\\[TF\\]", (field == null ? "" : field));

		StringBuffer htmlContent = new StringBuffer();

		// 如需精细控制翻页条的样式，则必须指定ID
		htmlContent.append("<div id=\"" + id + "\" class=\"SimpleTurnPager\">");

		htmlContent.append("<span id=\"" + id + "_0\">"
				+ Resource.Language.get(lan).getProperty("SimpleTurnPager.total") + " " + items.getRealSize() + " "
				+ Resource.Language.get(lan).getProperty("SimpleTurnPager.record") + "</span>");
		htmlContent.append("<span id=\"" + id + "_1\">" + items.getPageNum() + "/" + items.getPageCount() + ""
				+ Resource.Language.get(lan).getProperty("SimpleTurnPager.page") + "</span>");

		if (items.getPageNum() > 1) {
			htmlContent.append("<span id=\"" + id + "_2\">");
			htmlContent.append("<a href=\"" + attachUrl.replaceAll("(?i)\\#\\[TP\\]", "1") + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.firstPage") + "</a></span>");
		} else {
			htmlContent.append("<span id=\"" + id + "_2\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.firstPage") + "</span>");
		}

		if (items.getPageNum() > 1) {
			htmlContent.append("<span id=\"" + id + "_3\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageNum() - 1)) + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.prevPage") + "</a></span>");
		} else {
			htmlContent.append("<span id=\"" + id + "_3\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.prevPage") + "</span>");
		}

		if (items.getPageNum() < items.getPageCount()) {
			htmlContent.append("<span id=\"" + id + "_4\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageNum() + 1)) + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.nextPage") + "</a></span>");
		} else {
			htmlContent.append("<span id=\"" + id + "_4\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.nextPage") + "</span>");
		}

		if (items.getPageNum() < items.getPageCount()) {
			htmlContent.append("<span id=\"" + id + "_5\">");
			htmlContent.append("<a href=\""
					+ attachUrl.replaceAll("(?i)\\#\\[TP\\]", String.valueOf(items.getPageCount())) + "\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.lastPage") + "</a></span>");
		} else {
			htmlContent.append("<span id=\"" + id + "_5\">"
					+ Resource.Language.get(lan).getProperty("SimpleTurnPager.lastPage") + "</span>");
		}

		htmlContent.append("</div>");

		return htmlContent.toString();
	}

	public void setItems(PagingList<?> items) {
		this.items = items;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
		
		String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
		
		if (!StringUtil.isEmpty(url) && ContextURLExp.matcher(url).find()) {
			this.url = URLUtil.justify(contextPath + "/" + url);
		}
	}

	public void setLan(String lan) {
		this.lan = lan;
	}
}
