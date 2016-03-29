/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class HintablePasswordField extends JPasswordField {
	private static final long serialVersionUID = 1L;

	private String hint;

	private boolean isPaintHint = true;

	public HintablePasswordField() {
		this("");
	}

	public HintablePasswordField(String hint) {
		this.hint = hint;

		addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				isPaintHint = false;
				repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				isPaintHint = true;
				repaint();
			}
		});
	}

	public void setHintText(String hint) {
		this.hint = hint;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();

		if (isPaintHint && getPassword().length == 0) {

			g2.setColor(Color.gray);

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Rectangle rect = getBounds();
			FontMetrics fm = g2.getFontMetrics();
			
			// 默认是左对齐
			int x = 2;
			
			if (SwingConstants.RIGHT == getHorizontalAlignment()) {
				x = rect.width - fm.stringWidth(hint) - 2;
			} else if (SwingConstants.CENTER == getHorizontalAlignment()) {
				x = rect.width / 2 - fm.stringWidth(hint) / 2;
			}
			
			g2.drawString(hint, x, rect.height / 2 - fm.getHeight() / 2 + fm.getMaxAscent());
		}
	}
}
