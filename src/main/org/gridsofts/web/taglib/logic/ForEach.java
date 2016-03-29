/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.gridsofts.web.taglib.logic.forEachClasses.AbstractVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.DTVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.FixedLenVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.HtmlVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.IEachable;
import org.gridsofts.web.taglib.logic.forEachClasses.NumberVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.StringVariableExp;
import org.gridsofts.web.taglib.logic.forEachClasses.TextVariableExp;
import org.gridsofts.util.StringUtil;

/**
 * 分页结果集迭代器<br>
 * 支持在迭代语句中使用表达式，这样可以将结果集中的值替换表达式出现的位置。<br/>
 * 通过设置属性“scope”的值，可以控制此标记从何处获取结果集<br/>
 * “scope”可能的值有：request,session,parent。其中：parent指的是父标记，这个父标记必须是实现了IEachable接口的。
 * 
 * @author Lei
 * 
 */
public class ForEach extends BodyTagSupport implements IEachable {
	private static final long serialVersionUID = 1L;

	private static final List<AbstractVariableExp> variableExpAry;
	static {
		variableExpAry = new ArrayList<AbstractVariableExp>();

		variableExpAry.add(new NumberVariableExp());
		variableExpAry.add(new DTVariableExp());
		variableExpAry.add(new TextVariableExp());
		variableExpAry.add(new HtmlVariableExp());
		variableExpAry.add(new StringVariableExp());
		variableExpAry.add(new FixedLenVariableExp());
	}

	private String scope;
	private String field;

	private List<?> items;

	private int index;

	/**
	 * 向上递归查找ForEach的通用方法
	 * 
	 * @return
	 */
	public static ForEach findForEach(Tag tag) {

		Tag parent = tag.getParent();

		if (parent != null && parent instanceof ForEach) {

			return (ForEach) parent;

		} else if (parent != null) {

			return findForEach(parent);
		}

		return null;
	}

	@Override
	public int doStartTag() {

		// 默认从父标签中取结果集
		if (items == null) {
			
			if (StringUtil.isNull(scope)) {
	
				Tag parentTag = getParent();
	
				if (parentTag != null && parentTag instanceof IEachable) {
					items = ((IEachable) parentTag).getEachList(field);
				}
	
			} else if ("request".equalsIgnoreCase(scope)) {
	
				items = (List<?>) pageContext.getRequest().getAttribute(field);
	
			} else if ("session".equalsIgnoreCase(scope)) {
	
				items = (List<?>) pageContext.getSession().getAttribute(field);
			}
		}

		if (items != null && items.size() > 0) {
			index = 0;

			return BodyTag.EVAL_BODY_BUFFERED;
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doAfterBody() {

		try {
			String htmlContent = getBodyContent().getString();
			getBodyContent().clearBody();

			if (htmlContent == null || htmlContent.trim().equals("")) {
				return BodyTag.SKIP_BODY;
			}

			if (index < items.size()) {

				String html = getHtmlString(htmlContent);

				index++;

				getBodyContent().getEnclosingWriter().println(html);

				return BodyTag.EVAL_BODY_AGAIN;
			}

		} catch (java.io.IOException ioe) {
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		field = null;
		items = null;
		index = 0;

		return BodyTag.EVAL_PAGE;
	}

	/**
	 * 实现对ForEach标签的嵌套调用。<br/>
	 * 如： <br/>
	 * ----<logic:forEach ... ...<br/>
	 * --------... ...<br/>
	 * --------<logic:forEach field="#[...]"><br/>
	 * --------</logic:forEach><br/>
	 * ----</logic:forEach>
	 */
	public List<?> getEachList(String name) {

		Object valueObj = getObject();

		if (valueObj != null) {

			// 匹配
			for (AbstractVariableExp exp : variableExpAry) {

				if (exp.match(name)) {
					return (List<?>) exp.getValue(exp.getFieldName(), valueObj, index);
				}
			}
		}

		return null;
	}

	/**
	 * 获取当前正在进行迭代的对象
	 * 
	 * @return
	 */
	public Object getObject() {

		if (index < items.size()) {
			return items.get(index);
		}

		return null;
	}

	/**
	 * 为子标签获取当前Html内容提供接口
	 * 
	 * @param htmlContent
	 * @return
	 */
	public String getHtmlString(String htmlContent) {

		if (htmlContent != null && index < items.size()) {

			Object valueObj = getObject();

			if (valueObj != null) {

				// 匹配、替换变量表达式
				for (AbstractVariableExp exp : variableExpAry) {
					htmlContent = exp.replaceAll(htmlContent, index + 1, valueObj);
				}
			}
		}

		return htmlContent;
	}

	public void setItems(List<?> items) {
		this.items = items;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setField(String field) {
		this.field = field;
	}
}
