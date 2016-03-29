/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.html;

import java.awt.Font;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.gridsofts.util.VerifyImage;

public class VerifyImageTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	private int maxFactor = 50;
	private int level = 2;

	private String fontFamily = "Microsoft YaHei";
	private int fontStyle = Font.PLAIN;
	private int fontSize = 14;

	private int color = 0x808080;
	private int backgroundColor = 0xf0f0f0;
	
	private int jamColor = 0x8f8f8f;
	private int jamCount = 50;

	private int width;
	private int height;

	@Override
	public int doStartTag() throws JspException {

		VerifyImage vertifyImg = new VerifyImage();
		
		vertifyImg.setMaxFactor(maxFactor);
		vertifyImg.setLevel(level);
		vertifyImg.setFontFamily(fontFamily);
		vertifyImg.setFontStyle(fontStyle);
		vertifyImg.setFontSize(fontSize);
		vertifyImg.setColor(color);
		vertifyImg.setBackgroundColor(backgroundColor);
		vertifyImg.setJamColor(jamColor);
		vertifyImg.setJamCount(jamCount);
		vertifyImg.setWidth(width);
		vertifyImg.setHeight(height);

		try {
			pageContext.getResponse().setContentType("image/JPEG");
			
			float c = vertifyImg.write(pageContext.getResponse().getOutputStream());

			if (c >= 0) {

				// 存储校验值
				pageContext.getSession().setAttribute(VerifyImage.VerifyCode, c);
			}
		} catch (IOException e) {
		}

		return Tag.SKIP_BODY;
	}

	public void setMaxFactor(int maxFactor) {
		this.maxFactor = maxFactor;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setJamColor(int jamColor) {
		this.jamColor = jamColor;
	}

	public void setJamCount(int jamCount) {
		this.jamCount = jamCount;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
