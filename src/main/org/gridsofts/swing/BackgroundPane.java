/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * 支持背景图片及渐变背景色的容器
 * 
 * @author zholey
 * 
 */
public class BackgroundPane extends JPanel {
	private static final long serialVersionUID = 1L;

	// 背景色渐变方式
	public static enum GradientType {
		HORIZONTAL, VERTICAL, ROUND
	}

	private GradientType gradientType;
	// 背景色
	private Color[] backgroundColors;
	// 专用于圆形渐变
	private float[] backgroundBounds;

	// 背景图对齐方式
	public static enum Alignment {
		NONE, CENTER, TILE, STRETCH
	}

	private Alignment align;

	// 原始图像
	private Image backgroundImage;

	public BackgroundPane() {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (backgroundColors != null) {
			paintBackgroundColor(g2);
		}

		if (backgroundImage != null) {
			paintBackgroundImage(g2);
		}
	}

	/**
	 * 画背景
	 * 
	 * @param g
	 */
	private void paintBackgroundColor(Graphics2D g) {

		if (gradientType == GradientType.HORIZONTAL) { // 横向渐变
			g.setPaint(new LinearGradientPaint(0, 0, getWidth(), 0, backgroundBounds, backgroundColors));
		} else if (gradientType == GradientType.ROUND) { // 圆形渐变
			g.setPaint(new RadialGradientPaint(getWidth() / 2, getHeight() / 2, getWidth() / 4 + getHeight() / 4,
					backgroundBounds, backgroundColors));
		} else { // 纵向渐变
			g.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), backgroundBounds, backgroundColors));
		}

		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * 画背景图片
	 * 
	 * @param g
	 */
	private void paintBackgroundImage(Graphics2D g) {

		// 处理各种对齐方式
		if (align == Alignment.CENTER) { // 居中

			int width = backgroundImage.getWidth(this);
			int height = backgroundImage.getHeight(this);

			if (width <= 0 || height <= 0) {
				return;
			}

			g.drawImage(backgroundImage, getWidth() / 2 - width / 2, getHeight() / 2 - height / 2, this);

		} else if (align == Alignment.TILE) { // 平铺

			int width = backgroundImage.getWidth(this);
			int height = backgroundImage.getHeight(this);

			if (width <= 0 || height <= 0) {
				return;
			}

			int w = 0;
			int h = 0;

			while (true) {

				g.drawImage(backgroundImage, w, h, this);

				if (w + width < getWidth()) {
					w += width;
				} else if (h + height < getHeight()) {
					w = 0;
					h += height;
				} else {
					break;
				}
			}

		} else if (align == Alignment.STRETCH) { // 拉伸

			g.drawImage(backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, this);

		} else { // 无

			g.drawImage(backgroundImage, 0, 0, this);
		}
	}

	public Color[] getBackgroundColors() {
		return backgroundColors;
	}

	public void setBackgroundColors(Color[] c, float[] b) {
		setBackgroundColors(GradientType.VERTICAL, c, b);
	}

	public void setBackgroundColors(GradientType g, Color[] c, float[] b) {

		if (c != null && c.length >= 2) {

			backgroundColors = c;
			backgroundBounds = b;

			gradientType = g;
		}
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		setBackgroundImage(backgroundImage, Alignment.CENTER);
	}

	public void setBackgroundImage(Image backgroundImage, Alignment align) {
		this.backgroundImage = backgroundImage;
		this.align = align;
	}
}
