/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * 单边边框，可以任意指定需要四边中的哪一边。同时，可以指定线的颜色及粗细。
 * 
 * @author zholey
 * 
 */
public class ScatterLineBorder implements Border {
	private static final long serialVersionUID = 1L;

	public static final int TOP = 0x01;
	public static final int LEFT = 0x02;
	public static final int BOTTOM = 0x04;
	public static final int RIGHT = 0x08;

	private Color color;
	private int thick;
	private int position;

	public ScatterLineBorder() {
		this(TOP | LEFT | BOTTOM | RIGHT, Color.black, 1);
	}

	public ScatterLineBorder(int position) {
		this(position, Color.black, 1);
	}

	public ScatterLineBorder(int position, Color color) {
		this(position, color, 1);
	}

	public ScatterLineBorder(int position, Color color, int thick) {
		this.position = position;
		this.color = color;
		this.thick = thick;
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		Graphics2D g2 = (Graphics2D) g.create();
		
		Object antialiasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color oldColor = g2.getColor();
		g2.setColor(color);

		if ((position & TOP) > 0) {
			g2.fillRect(x, y, x + width, y + thick);
		}
		if ((position & LEFT) > 0) {
			g2.fillRect(x, y, x + thick, y + height);
		}
		if ((position & BOTTOM) > 0) {
			g2.fillRect(x, y + height - thick, x + width, y + height);
		}
		if ((position & RIGHT) > 0) {
			g2.fillRect(x + width - thick, y, x + width, y + height);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing);
		g2.setColor(oldColor);
	}

	@Override
	public Insets getBorderInsets(Component c) {

		Insets insets = new Insets(0, 0, 0, 0);

		if ((position & TOP) > 0) {
			insets.top = thick;
		}
		if ((position & LEFT) > 0) {
			insets.left = thick;
		}
		if ((position & BOTTOM) > 0) {
			insets.bottom = thick;
		}
		if ((position & RIGHT) > 0) {
			insets.right = thick;
		}

		return insets;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getThick() {
		return thick;
	}

	public void setThick(int thick) {
		this.thick = thick;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
