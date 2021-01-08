/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
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

				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("User-Agent", "Gridsofts.taglib.EmbedFile");
				httpGet.setHeader("Cookie", "JSESSIONID=" + pageContext.getSession().getId());

				try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
					if (response.getStatusLine().getStatusCode() == 200) {
						String content = EntityUtils.toString(response.getEntity());

						Charset charset = Charset.forName(encoding);
						pageContext.getOut().println(new String(content.getBytes(charset), charset));
					}
				} catch (Throwable e) {
				}
			}
		} catch (Exception ex) {
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
