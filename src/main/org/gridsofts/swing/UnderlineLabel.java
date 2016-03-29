/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UnderlineLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private Color disabledColor;

	private boolean isUnderline;
	
	private Dimension preferredSize;

	public UnderlineLabel() {
	}

	public UnderlineLabel(String text) {
		super(text);
	}
	
	@Override
	public void setPreferredSize(Dimension size) {
		preferredSize = size;
		
		super.setPreferredSize(size);
	}
	
	@Override
	public Dimension getPreferredSize() {
		
		if (preferredSize != null) {
			return preferredSize;
		}

		int preferredWidth = getFontMetrics(getFont()).stringWidth(getText());
		int preferredHeight = getFontMetrics(getFont()).getHeight() + 1;

		return new Dimension(preferredWidth, preferredHeight);
	}

	@Override
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);

		if (isEnabled()) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (!"".equals(getText())) {

			if (isEnabled()) {
				g2.setColor(getForeground());
			} else {
				g2.setColor(getDisabledColor() == null ? getForeground() : getDisabledColor());
			}

			g2.setFont(getFont());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getFontMetrics(getFont()).stringWidth(getText());
			int h = getFontMetrics(getFont()).getHeight();

			int x = 0;
			int y = getHeight() / 2 - h / 2 + getFontMetrics(getFont()).getMaxAscent();

			switch (getHorizontalAlignment()) {
			case SwingConstants.CENTER:
				x = getWidth() / 2 - w / 2;
				break;
			case SwingConstants.RIGHT:
				x = getWidth() - w;
				break;
			default:
				x = 0;
			}

			g2.drawString(getText(), x, y);

			if (getUnderline()) {
				g.drawLine(1, getHeight() - 1, getWidth() - 2, getHeight() - 1);
			}
		}
	}

	public Color getDisabledColor() {
		return disabledColor;
	}

	public void setDisabledColor(Color color) {
		disabledColor = color;
	}

	public boolean getUnderline() {
		return isUnderline;
	}

	public void setUnderline(boolean isUnderline) {
		this.isUnderline = isUnderline;
	}
}
