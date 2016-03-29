/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JLabel;

public class IconButton extends JLabel implements MouseListener {
	private static final long serialVersionUID = 1L;

	private Icon[] icon;

	public IconButton() {

		initialization();
	}

	public IconButton(String label) {
		super(label);

		initialization();
	}

	public IconButton(Icon... icon) {

		this.icon = icon;

		initialization();
	}

	public IconButton(String label, Icon... icon) {
		super(label);

		this.icon = icon;

		initialization();
	}

	public void setIcon(Icon... icon) {

		this.icon = icon;

		initialization();
	}

	private void initialization() {

		setCursor(new Cursor(Cursor.HAND_CURSOR));

		if (icon != null && icon.length > 0) {
			super.setIcon(icon[0]);
			addMouseListener(this);
		}
	}

	@Override
	protected void processMouseEvent(MouseEvent event) {

		if (isEnabled()) {
			super.processMouseEvent(event);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {

		if (icon.length > 1) {
			super.setIcon(icon[1]);
		} else {
			super.setIcon(icon[0]);
		}
	}

	@Override
	public void mouseExited(MouseEvent event) {
		super.setIcon(icon[0]);
	}

	@Override
	public void mousePressed(MouseEvent event) {

		if (icon.length > 2) {
			super.setIcon(icon[2]);
		} else if (icon.length > 1) {
			super.setIcon(icon[0]);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {

		if (contains(event.getPoint()) && icon.length > 1) {
			super.setIcon(icon[1]);
		} else {
			super.setIcon(icon[0]);
		}
	}
}
