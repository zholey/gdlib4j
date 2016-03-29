/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JLabel;

public class VTitledSeparator extends JLabel {
	private static final long serialVersionUID = 1L;

	private String label;

	public VTitledSeparator(String label) {
		this.label = label;

		setPreferredSize(new Dimension(0, getFontMetrics(getFont()).getHeight()));
	}
	
	@Override
	public Insets getInsets() {
		Insets insets = super.getInsets();
		
		insets.left += 3;
		insets.right += 3;
		
		return insets;
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		FontMetrics fm = g2.getFontMetrics();

		int left = getInsets().left;
		int right = getInsets().right + 1;

		int baseLineY = getInsets().top + fm.getMaxAscent();

		g2.drawString(label, left, baseLineY);

		g2.setColor(new Color(0xd5dfe5));
		g2.drawLine(left + fm.stringWidth(label), baseLineY - 3, getWidth() - right, baseLineY - 3);
		
		g2.setColor(Color.white);
		g2.drawLine(left + fm.stringWidth(label), baseLineY - 2, getWidth() - right, baseLineY - 2);
	}
}
