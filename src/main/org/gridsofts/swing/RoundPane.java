/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class RoundPane extends JPanel {
	private static final long serialVersionUID = 1L;

	private int radius;
	private int alpha;

	public RoundPane(int r, int a) {
		radius = r;
		alpha = a;
	}

	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color c = getBackground();

		g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
	}

	@Override
	protected void paintBorder(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	@Override
	public Insets getInsets() {
		return new Insets(radius, radius, radius, radius);
	}
}
