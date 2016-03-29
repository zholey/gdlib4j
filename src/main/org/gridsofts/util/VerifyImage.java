/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * 验证码图片生成器
 * 
 * @author Lei
 */
public class VerifyImage implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String VerifyCode = "org.gridsofts.VerifyImage.VerifyCode";

	private int maxFactor = 50;
	private int level = 2;

	private String fontFamily = "Microsoft YaHei";
	private int fontStyle = Font.PLAIN;
	private int fontSize = 16;

	private int color = 0x080808;
	private int backgroundColor = 0xf0f0f0;

	private int jamColor = 0x8f8f8f;
	private int jamCount = 5;

	private int width;
	private int height;

	public float write(OutputStream out) {

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setColor(new Color(backgroundColor));
		g2.fillRect(0, 0, width, height);

		g2.setColor(new Color(color));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setFont(new Font(fontFamily, fontStyle, fontSize));

		Random random = new Random();

		int a = random.nextInt(maxFactor);
		int b = random.nextInt(maxFactor);

		int s = random.nextInt(level);

		float c = 0;

		switch (s) {
		case 0:
			c = a + b;
			break;
		case 1:
			c = a - b;
			break;
		case 2:
			c = a * b;
			break;
		case 3:
			c = a / b;
		}

		String symbol = s == 0 ? "加" : s == 1 ? "减" : s == 2 ? "乘" : "除";
		String str = (a >= b ? a : b) + symbol + (a < b ? a : b) + "等于几";

		int strWidth = g2.getFontMetrics().stringWidth(str);
		int strHeight = g2.getFontMetrics().getHeight();
		int ascent = g2.getFontMetrics().getMaxAscent();
		
		int offsetX = width > strWidth + 4 ? (width - strWidth - 4) : 0;
		float posX = offsetX <= 0 ? 0 : random.nextInt(offsetX);
		float posY = height / 2 - strHeight / 2 + ascent;

		g2.drawString(str, posX + 5, posY);

		// 涂果酱
		paintingJams(g2);

		// 生成图片
		try {
			ImageIO.write(img, "JPEG", out);

			return Math.abs(c);
		} catch (Throwable e) {
		}

		return -1;
	}

	/**
	 * 绘制干扰像素
	 * 
	 * @param g2
	 */
	private void paintingJams(Graphics2D g2) {

		g2.setColor(new Color(jamColor));

		Random random = new Random();

		// 线条
		for (int i = 0; i < jamCount; i++) {
			int originX = random.nextInt(width);
			int originY = random.nextInt(height);

			int terminalX = random.nextInt(width);
			int terminalY = random.nextInt(height);

			g2.drawLine(originX, originY, terminalX, terminalY);
		}

		// 点
		for (int i = 0; i < 3 * jamCount; i++) {
			int originX = random.nextInt(width);
			int originY = random.nextInt(height);
			
			int w = random.nextInt(3);

			g2.fillRoundRect(originX, originY, w, w, w / 2, w / 2);
		}
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

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setJamColor(int jamColor) {
		this.jamColor = jamColor;
	}

	public void setJamCount(int jamCount) {
		this.jamCount = jamCount;
	}
}
