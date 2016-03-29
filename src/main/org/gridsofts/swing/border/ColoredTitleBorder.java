/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * 可以认为是可以指定边框线颜色的TitledBorder。
 * 
 * @author zholey
 * 
 */
public class ColoredTitleBorder implements Border {
	private static final long serialVersionUID = 1L;

	public static final int LEFT_TOP = 1;
	public static final int RIGHT_TOP = 2;
	public static final int LEFT_BOTTOM = 3;
	public static final int RIGHT_BOTTOM = 4;

	private String title;

	private Color lineColor = new Color(0xd0d0bf);
	private Color titleColor = new Color(0x0046d5);

	private int position = LEFT_TOP;
	private int cornerRadius = 3;
	private float titleAscent = 0.4f;

	public ColoredTitleBorder() {
	}

	public ColoredTitleBorder(String title) {
		this.title = title;
	}

	public ColoredTitleBorder(Color lineColor) {
		this.lineColor = lineColor;
	}

	public ColoredTitleBorder(String title, Color lineColor) {
		this.title = title;
		this.lineColor = lineColor;
	}

	public ColoredTitleBorder(String title, Color lineColor, Color titleColor, int position) {
		this.title = title;
		this.lineColor = lineColor;
		this.titleColor = titleColor;
		this.position = position;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		FontMetrics fm = g2.getFontMetrics(g2.getFont());
		Color oldColor = g2.getColor();

		Rectangle lineRect = new Rectangle(0, 0, width - 1, height - 1);
		Rectangle titleRect = new Rectangle(0, 0, 0, 0);

		// 如果有标题
		if (title != null && !title.trim().equals("")) {

			// 计算边框位置
			if (position == LEFT_TOP || position == RIGHT_TOP) {
				lineRect.y = (int) Math.floor(fm.getHeight() * titleAscent);
			}
			lineRect.height -= (int) Math.floor(fm.getHeight() * titleAscent);

			titleRect.width = fm.stringWidth(title) + 2;
			titleRect.height = fm.getHeight();

			// 计算标题区域
			switch (position) {
			case LEFT_TOP:

				titleRect.x = lineRect.x + 8;
				titleRect.y = lineRect.y - ((int) Math.floor(fm.getHeight() * titleAscent));
				break;
			case RIGHT_TOP:

				titleRect.x = lineRect.x + lineRect.width - fm.stringWidth(title) - 8;
				titleRect.y = lineRect.y - ((int) Math.floor(fm.getHeight() * titleAscent));
				break;
			case LEFT_BOTTOM:

				titleRect.x = lineRect.x + 8;
				titleRect.y = lineRect.y + lineRect.height - ((int) Math.floor(fm.getHeight() * (1 - titleAscent)));
				break;
			case RIGHT_BOTTOM:
			default:

				titleRect.x = lineRect.x + lineRect.width - fm.stringWidth(title) - 8;
				titleRect.y = lineRect.y + lineRect.height - ((int) Math.floor(fm.getHeight() * (1 - titleAscent)));
			}
		}

		// 画边框
		paintLineBorder(g2, lineRect, titleRect);
		paintTitle(g2, titleRect);

		g2.setColor(oldColor);
	}

	private void paintLineBorder(Graphics2D g, Rectangle lineRect, Rectangle titleRect) {

		g.setColor(lineColor);

		// 如果有标题
		if (title != null && !title.trim().equals("")) {

			// corners
			g.drawArc(lineRect.x, lineRect.y, cornerRadius * 2, cornerRadius * 2, 90, 90);
			g.drawArc(lineRect.x + lineRect.width - cornerRadius * 2, lineRect.y, cornerRadius * 2, cornerRadius * 2, 0, 90);
			g.drawArc(lineRect.x, lineRect.y + lineRect.height - cornerRadius * 2, cornerRadius * 2, cornerRadius * 2, 180, 90);
			g.drawArc(lineRect.x + lineRect.width - cornerRadius * 2, lineRect.y + lineRect.height - cornerRadius * 2,
					cornerRadius * 2, cornerRadius * 2, 270, 90);

			// line (l, r)
			g.drawLine(lineRect.x, lineRect.y + cornerRadius, lineRect.x, lineRect.y + lineRect.height - cornerRadius);
			g.drawLine(lineRect.x + lineRect.width, lineRect.y + cornerRadius, lineRect.x + lineRect.width, lineRect.y
					+ lineRect.height - cornerRadius);

			// line (t, b)
			switch (position) {

			case LEFT_TOP:
			case RIGHT_TOP:

				g.drawLine(lineRect.x + cornerRadius, lineRect.y, titleRect.x, lineRect.y);
				g.drawLine(titleRect.x + titleRect.width, lineRect.y, lineRect.x + lineRect.width - cornerRadius, lineRect.y);

				g.drawLine(lineRect.x + cornerRadius, lineRect.y + lineRect.height, lineRect.x + lineRect.width - cornerRadius,
						lineRect.y + lineRect.height);
				break;
			case LEFT_BOTTOM:
			case RIGHT_BOTTOM:
			default:

				g.drawLine(lineRect.x + cornerRadius, lineRect.y, lineRect.x + lineRect.width - cornerRadius, lineRect.y);

				g.drawLine(lineRect.x + cornerRadius, lineRect.y + lineRect.height, titleRect.x, lineRect.y + lineRect.height);
				g.drawLine(titleRect.x + titleRect.width, lineRect.y + lineRect.height, lineRect.x + lineRect.width
						- cornerRadius, lineRect.y + lineRect.height);
			}

		} else {
			g.drawRoundRect(lineRect.x, lineRect.y, lineRect.width, lineRect.height, cornerRadius * 2, cornerRadius * 2);
		}
	}

	private void paintTitle(Graphics2D g, Rectangle titleRect) {

		FontMetrics fm = g.getFontMetrics();

		// 如果有标题
		if (title != null && !title.trim().equals("")) {

			g.setColor(titleColor);
			g.drawString(title, titleRect.x + 2, titleRect.y + fm.getMaxAscent());
		}
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public Insets getBorderInsets(Component c) {

		Insets insets = new Insets(0, 0, 0, 0);

		insets.left = insets.right = 1 + cornerRadius;

		// 如果有标题
		if (title != null && !title.trim().equals("")) {

			FontMetrics fm = c.getGraphics().getFontMetrics();

			if (position == LEFT_TOP || position == RIGHT_TOP) {

				insets.top = fm.getHeight() + cornerRadius;
				insets.bottom = 1 + cornerRadius;
			} else {

				insets.top = 1 + cornerRadius;
				insets.bottom = fm.getHeight() + cornerRadius;
			}
		} else {
			insets.top = insets.bottom = 1 + cornerRadius;
		}

		return insets;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public float getTitleAscent() {
		return titleAscent;
	}

	public void setTitleAscent(float titleAscent) {
		this.titleAscent = titleAscent;
	}
}
