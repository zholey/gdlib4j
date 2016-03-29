/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * 提供两种类型的阴影边框，简单和模糊渐变。<br>
 * 使用简单阴影时，只需指定阴影颜色，边框即自动生成（默认在右下方）；<br>
 * 模糊渐变阴影需要指定阴影的颜色和扩散范围。
 * 
 * @author zholey
 * 
 */
public class ShadowBorder implements Border {
	private static final long serialVersionUID = 1L;

	// 阴影样式：简单、模糊渐变
	public static enum Style {
		SIMPLE, BLUR
	}

	private Style style;

	// 阴影扩散范围
	private int bound = 4;

	// 简单阴影颜色，默认为半透明的黑色
	private Color simpleColor = new Color(0x7d000000, true);

	// 模糊渐变颜色，第一个颜色为最内侧颜色，第二个颜色为最外侧颜色
	private Color[] blurColors = new Color[] { new Color(0x4d3f70b2, true), new Color(0x003f70b2, true) };

	public ShadowBorder(Style s) {
		style = s;
	}

	@Override
	public Insets getBorderInsets(Component c) {

		if (style == Style.BLUR) {
			return new Insets(bound, bound, bound, bound);
		} else {
			return new Insets(0, 0, bound, bound);
		}
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (style == Style.BLUR) {
			paintBlurBorder(c, g2, x, y, width, height);
		} else {
			paintSimpleBorder(c, g2, x, y, width, height);
		}
	}

	/**
	 * 画简单阴影
	 * 
	 * @param c
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void paintSimpleBorder(Component c, Graphics2D g, int x, int y, int width, int height) {

		g.setColor(simpleColor);

		g.fillRect(x + 2 * bound, y + height - bound, width - 2 * bound, bound);
		g.fillRect(x + width - bound, y + 2 * bound, bound, height - 3 * bound);
	}

	/**
	 * 画模糊渐变阴影
	 * 
	 * @param c
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void paintBlurBorder(Component c, Graphics2D g, int x, int y, int width, int height) {

		// 画四边
		g.setPaint(new GradientPaint(x, y, blurColors[1], x, y + bound, blurColors[0]));
		g.fillRect(x + bound, y, width - 2 * bound, bound);

		g.setPaint(new GradientPaint(x, y, blurColors[1], x + bound, y, blurColors[0]));
		g.fillRect(x, y + bound, bound, height - 2 * bound);

		g.setPaint(new GradientPaint(x, y + height, blurColors[1], x, y + height - bound, blurColors[0]));
		g.fillRect(x + bound, y + height - bound, width - 2 * bound, bound);

		g.setPaint(new GradientPaint(x + width, y, blurColors[1], x + width - bound, y, blurColors[0]));
		g.fillRect(x + width - bound, y + bound, bound, height - 2 * bound);

		// 画四角
		g.setPaint(new RadialGradientPaint(x + bound, y + bound, bound, new float[] { 0, 1 }, new Color[] { blurColors[0],
				blurColors[1] }));
		g.fillRect(x, y, bound, bound);

		g.setPaint(new RadialGradientPaint(x + bound, y + height - bound, bound, new float[] { 0, 1 }, new Color[] {
				blurColors[0], blurColors[1] }));
		g.fillRect(x, y + height - bound, bound, bound);

		g.setPaint(new RadialGradientPaint(x + width - bound, y + height - bound, bound, new float[] { 0, 1 }, new Color[] {
				blurColors[0], blurColors[1] }));
		g.fillRect(x + width - bound, y + height - bound, bound, bound);

		g.setPaint(new RadialGradientPaint(x + width - bound, y + bound, bound, new float[] { 0, 1 }, new Color[] {
				blurColors[0], blurColors[1] }));
		g.fillRect(x + width - bound, y, bound, bound);
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public Color getSimpleColor() {
		return simpleColor;
	}

	public void setSimpleColor(Color simpleColor) {
		if (simpleColor != null) {
			this.simpleColor = simpleColor;
		}
	}

	public Color[] getBlurColors() {
		return blurColors;
	}

	public void setBlurColors(Color[] blurColors) {
		if (blurColors != null && blurColors.length >= 2) {
			this.blurColors = blurColors;
		}
	}

	public int getBound() {
		return bound;
	}

	public void setBound(int bound) {
		this.bound = bound;
	}
}
