/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.gridsofts.web.taglib.logic.permissionClasses.IPermission;
import org.gridsofts.util.StringUtil;

/**
 * 权限校验标签。校验通过时显示内容，否则不显示。如果包含有子标签NonPermission，则不通过时显示子标签的内容。
 * 
 * @author Lei
 * 
 */
public class Permission extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	// 用于保存权限校验对象的缓存
	private static final Map<String, IPermission> permissionObjCache;
	static {
		permissionObjCache = new HashMap<String, IPermission>();
	}

	// 重定向代码
	private static final String REDIRECT_CODE = "<meta http-equiv=\"Refresh\" content=\"0;url=${redirectURI}\">";

	/**
	 * 输入参数：权限校验实现类
	 */
	private String implement;
	
	private String redirectURI;

	private String[] operations;

	private Class<?> permissionClass;
	private IPermission permissionObject;

	private boolean hasPermission = false;

	/**
	 * 子标签NonPermission包含的内容（如果有）
	 */
	private String nonPermissionContent;

	/**
	 * 输入参数：权限标识列表（以“,”分隔）
	 */
	public void setOperation(String operation) {

		if (operation != null) {
			operations = operation.split("\\s*,\\s*");
		}
	}

	public void setImplement(String implement) {
		this.implement = implement;
	}

	public void setNonPermissionContent(String nonPermissionContent) {
		this.nonPermissionContent = nonPermissionContent;
	}
	
	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}

	@Override
	public int doStartTag() throws JspException {

		setNonPermissionContent(null);

		if (StringUtil.isNull(redirectURI)) {
			redirectURI = REDIRECT_CODE.replace("${redirectURI}", pageContext.getServletContext().getContextPath() + "/");
		} else {
			redirectURI = REDIRECT_CODE.replace("${redirectURI}", redirectURI);
		}

		// 查找并实例化权限验证类
		try {

			if (permissionObjCache.containsKey(implement)) {
				permissionObject = permissionObjCache.get(implement);
			} else {

				permissionClass = Class.forName(implement);
				Method getInstanceMethod = null;

				try {
					getInstanceMethod = permissionClass.getDeclaredMethod("getInstance");
				} catch (Throwable tx) {
				}

				if (getInstanceMethod != null) {
					permissionObject = IPermission.class.cast(getInstanceMethod.invoke(permissionClass));
				} else {
					permissionObject = IPermission.class.cast(permissionClass.newInstance());
				}

				// 添加至缓存
				permissionObjCache.put(implement, permissionObject);
			}
		} catch (Throwable ex) {
		}

		// 验证权限
		try {

			if (permissionObject == null) {

				writeToBody("无法实例化指定的权限类 [" + implement + "]");

				return BodyTag.SKIP_BODY;

			} else {

				hasPermission = permissionObject.check(pageContext.getRequest(), operations);

				return BodyTag.EVAL_BODY_BUFFERED;
			}
		} catch (Throwable ex) {
		}

		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException {

		try {

			/**
			 * 以下条件成立则显示内容，其它情况不显示内容（如果存在子标签NonPermission，则显示子标签的内容）：<br>
			 * 
			 * A：权限校验为True；
			 */
			if (bodyContent != null) {
				
				if (hasPermission) {

					writeToBody(bodyContent.getString());

				} else if (nonPermissionContent != null) {

					writeToBody(nonPermissionContent);
				}
			}
		} catch (Throwable ex) {
		}

		return BodyTag.SKIP_BODY;
	}

	/**
	 * 向标签体内的页面区域写入内容
	 * 
	 * @param text
	 * @throws IOException
	 */
	private void writeToBody(String text) throws IOException {

		if (bodyContent != null) {
			bodyContent.getEnclosingWriter().print(text);
		}
	}

	@Override
	public int doEndTag() throws JspException {

		/**
		 * 当标签内容为空时，以下条件有一条成立则停止加载网页，其它情况继续加载网页：<br>
		 * 
		 * A：权限判断对象为空；<br>
		 * B：权限校验为False；
		 */
		try {
			if (bodyContent == null && !hasPermission) {

				try {
					pageContext.getOut().println(redirectURI);
				} catch (IOException e) {
				}

				return BodyTag.SKIP_PAGE;
			}

			return Tag.EVAL_PAGE;
		} finally {
			hasPermission = false;
			permissionClass = null;
			permissionObject = null;
		}
	}

	/**
	 * 此方法是为其它标签验证权限提供的应用扩展。<br/>
	 * 其它想要验证权限的标签，可以作为本标签的子标签，继而调用本方法来验证权限。<br/>
	 * 
	 * @return
	 */
	public boolean hasPermission() {
		return hasPermission;
	}
}
