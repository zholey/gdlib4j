/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;

/**
 * @author lei
 */
public class Copyright extends JComponent {
	private static final long serialVersionUID = 1L;

	private Alignment align;
	private String text;

	public Copyright() {
		this(Alignment.CENTER);
	}

	public Copyright(Alignment align) {
		this(align, TXT);
	}

	public Copyright(String text) {
		this(Alignment.CENTER, text);
	}

	public Copyright(Alignment align, String text) {
		this.align = align;
		this.text = text;
	}

	@Override
	public Insets getInsets() {
		return new Insets(10, 5, 10, 5);
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		Dimension minimumSize = getMinimumSize();

		return new Dimension((int) minimumSize.getWidth() + insets.left + insets.right,
				(int) minimumSize.getHeight() + insets.top + insets.bottom);
	}

	@Override
	public Dimension getMinimumSize() {
		FontMetrics fm = getFontMetrics(getFont());
		return new Dimension(fm.stringWidth(text), fm.getHeight());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Color oldColor = g.getColor();

		Insets insets = getInsets();
		int w = getWidth(), h = getHeight();
		int ascent = g.getFontMetrics().getAscent();
		int textH = g.getFontMetrics().getHeight();
		int textW = g.getFontMetrics().stringWidth(text);

		int x = 0;
		switch (align) {
		case LEFT:
			x = insets.left;
			break;
		case CENTER:
			x = w / 2 - textW / 2;
			break;
		case RIGHT:
			x = w - textW - insets.right;
		}
		int y = h / 2 - textH / 2 + ascent;

		g.setColor(new Color(0xdddddd));
		g.drawLine(insets.left, 0, w - insets.right, 0);

		g.setColor(new Color(0xafafaf));
		g.drawString(text, x, y);

		g.setColor(oldColor);
	}

	public static enum Alignment {
		LEFT, CENTER, RIGHT
	}

	private static final String TXT = "\u6280\u672F\u652F\u6301\uFF1A\u683C\u70B9"
			+ "\u8F6F\u4EF6\uFF08\u5317\u4EAC\uFF09\u6709\u9650\u516C\u53F8";
}
