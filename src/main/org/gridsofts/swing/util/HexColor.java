/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.util;

import java.awt.Color;

/**
 * 构造颜色的方法
 * 
 * @author zholey
 */
public class HexColor {

	public static Color convert(Integer hexColor) {
		return new Color((hexColor & 0xff0000) >> 16, (hexColor & 0x00ff00) >> 8, hexColor & 0x0000ff);
	}

	public static Color convert(Integer hexColor, Integer alpha) {
		return new Color((hexColor & 0xff0000) >> 16, (hexColor & 0x00ff00) >> 8, hexColor & 0x0000ff, alpha);
	}
}
