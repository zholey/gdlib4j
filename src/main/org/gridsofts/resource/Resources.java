/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.resource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 静态资源类
 * 
 * @author Lei
 * 
 */
public class Resources implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 字体资源
	 */
	public static Map<String, java.awt.Font> Font = new HashMap<String, java.awt.Font>();

	static {

		try {
			InputStream fontStream = Resources.class.getResourceAsStream("/org/gridsofts/resource/font/Impact.ttf");
			Font.put("Impact.ttf", java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream));
		} catch (Throwable t) {
		}

		try {
			InputStream fontStream = Resources.class.getResourceAsStream("/org/gridsofts/resource/font/Arial.ttf");
			Font.put("Arial.ttf", java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream));
		} catch (Throwable t) {
		}
	}

	/**
	 * 图片资源
	 */
	public static Map<String, Image> Image = new HashMap<String, Image>();

	static {

		try {
			Image.put("arrow-circle-double.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/arrow-circle-double.png")));
			
			Image.put("banner.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/banner.png")));
			
			Image.put("calendar.gif", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/calendar.gif")));
			
			Image.put("close_0.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/close_0.png")));
			Image.put("close_1.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/close_1.png")));
			Image.put("close_2.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/close_2.png")));

			Image.put("collapse-all.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/collapse-all.png")));

			Image.put("delete.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/delete.png")));
			
			Image.put("document.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/document.png")));
			
			Image.put("expand-all.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/expand-all.png")));
			
			Image.put("folder-open.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/folder-open.png")));
			
			Image.put("folder.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/folder.png")));

			Image.put("list-add.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/list-add.png")));

			Image.put("nex_month.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/nex_month.png")));

			Image.put("nex_year.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/nex_year.png")));

			Image.put("pre_month.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/pre_month.png")));

			Image.put("pre_year.png", Toolkit.getDefaultToolkit().createImage(
					Resources.class.getResource("/org/gridsofts/resource/image/pre_year.png")));
		} catch (Throwable t) {
		}
	}

	/**
	 * 多语资源
	 */
	public static Map<String, Properties> Language = new HashMap<String, Properties>();
	static {

		try {
			Properties lanPACK = new Properties();
			lanPACK.loadFromXML(Resources.class.getResourceAsStream("/org/gridsofts/resource/language/zh_CN.xml"));
			Language.put("zh_CN", lanPACK);

			lanPACK = new Properties();
			lanPACK.loadFromXML(Resources.class.getResourceAsStream("/org/gridsofts/resource/language/en_US.xml"));
			Language.put("en_US", lanPACK);
		} catch (Throwable t) {
		}
	}
}
