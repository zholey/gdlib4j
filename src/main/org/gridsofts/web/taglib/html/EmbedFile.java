/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.gridsofts.web.util.URLUtil;

public class EmbedFile extends TagSupport {
	private static final long serialVersionUID = 1L;

	public static final Pattern JAVASCRIPT = Pattern.compile("^(javascript)|(js)|(jscript)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern CSS = Pattern.compile("^(css)|(style)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern ICON = Pattern.compile("^icon$", Pattern.CASE_INSENSITIVE);
	public static final Pattern SHORTCUTICON = Pattern.compile("^Shortcut\\s*Icon$", Pattern.CASE_INSENSITIVE);
	public static final Pattern IFRAME = Pattern.compile("^(iframe)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern INLINE = Pattern.compile("^(inline)|(include)$", Pattern.CASE_INSENSITIVE);

	private String file;
	private String type;

	private String extend = "";
	private String encoding = "UTF-8";

	public int doStartTag() {

		HttpURLConnection httpConnection = null;

		try {
			String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

			file = URLUtil.justify(contextPath + "/" + file);

			extend = extend.replaceAll("'", "\"");

			String html = "";

			if (JAVASCRIPT.matcher(type).find()) {

				html = "<script type=\"text/javascript\" src=\"" + file + "\" " + extend + "></script>";
				pageContext.getOut().print(html);

			} else if (CSS.matcher(type).find()) {

				html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + file + "\" " + extend + "/>";
				pageContext.getOut().print(html);

			} else if (ICON.matcher(type).find()) {

				html = "<link rel=\"icon\" href=\"" + file + "\" " + extend + "/>";
				pageContext.getOut().print(html);

			} else if (SHORTCUTICON.matcher(type).find()) {

				html = "<link rel=\"Shortcut Icon\" href=\"" + file + "\" " + extend + "/>";
				pageContext.getOut().print(html);

			} else if (IFRAME.matcher(type).find()) {

				html = "<iframe src=\"" + file + "\" " + extend + "></iframe>";
				pageContext.getOut().print(html);

			} else if (INLINE.matcher(type).find()) {

				String url = URLUtil.getRequestHostURL((HttpServletRequest) pageContext.getRequest()) + file;

				httpConnection = (HttpURLConnection) new URL(url).openConnection();

				httpConnection.setRequestProperty("Cookie", "JSESSIONID=" + pageContext.getSession().getId());

				BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), encoding));

				while ((html = br.readLine()) != null) {
					pageContext.getOut().println(html);
				}
			}
		} catch (Exception ex) {
		} finally {

			if (httpConnection != null) {
				httpConnection.disconnect();
				httpConnection = null;
			}
		}

		return Tag.SKIP_BODY;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
