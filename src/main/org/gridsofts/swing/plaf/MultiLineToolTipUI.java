/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.plaf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicToolTipUI;

public class MultiLineToolTipUI extends BasicToolTipUI {
	private static MultiLineToolTipUI sharedInstance;

	private static final int MARGIN = 2;
	private static final String lineSeparator = "\n";

	protected MultiLineToolTipUI() {
		super();
	}

	public static MultiLineToolTipUI createUI(JComponent c) {
		if (sharedInstance == null) {
			sharedInstance = new MultiLineToolTipUI();
		}

		return sharedInstance;
	}

	public void paint(Graphics g, JComponent c) {
		if (c.getComponentCount() > 0) {
			c.paintComponents(g);
			return;
		}
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Font font = c.getFont();
		FontMetrics fontMetrics = c.getFontMetrics(font);
		int fontHeight = fontMetrics.getHeight();
		int fontAscent = fontMetrics.getAscent();

		g2.setColor(c.getBackground());
		Dimension size = c.getSize();
		g2.fillRect(0, 0, size.width, size.height);

		g2.setColor(c.getForeground());
		int y = 2 + fontAscent;
		String tipText = ((JToolTip) c).getTipText();
		StringTokenizer tokenizer = new StringTokenizer(tipText, lineSeparator);
		int numberOfLines = tokenizer.countTokens();

		for (int i = 0; i < numberOfLines; i++) {
			g2.drawString(tokenizer.nextToken(), MARGIN, y);
			y += fontHeight;
		}
	}

	public Dimension getPreferredSize(JComponent c) {
		if (c.getComponentCount() > 0) {
			return c.getLayout().preferredLayoutSize(c);
		}

		Font font = c.getFont();
		FontMetrics fontMetrics = c.getFontMetrics(font);
		int fontHeight = fontMetrics.getHeight();

		String tipText = ((JToolTip) c).getTipText();

		if (tipText == null)
			return new Dimension(2 * MARGIN, 2 * MARGIN);

		StringTokenizer tokenizer = new StringTokenizer(tipText, lineSeparator);
		int numberOfLines = tokenizer.countTokens();

		int height = numberOfLines * fontHeight;

		int width = 0;
		for (int i = 0; i < numberOfLines; i++) {
			int thisWidth = fontMetrics.stringWidth(tokenizer.nextToken());
			width = Math.max(width, thisWidth);
		}

		return (new Dimension(width + 2 * MARGIN, height + 2 * MARGIN));
	}
}
