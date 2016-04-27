/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * 继承自javax.swing.JDialog，自动居中定位
 * 
 * @author lei
 */
public class JDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @param width
	 *            小于或等于0时，会使用窗口的宽度
	 * @param height
	 *            小于或等于0时，会使用窗口的高度
	 */
	public JDialog(Frame owner, String title, boolean modal, int width, int height) {
		super(owner, title, modal);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		if (width <= 0) {
			width = screen.width;
		}
		if (height <= 0) {
			height = screen.height;
		}

		Rectangle ownerRect = owner.getBounds();
		int x = ownerRect.x + ownerRect.width / 2 - width / 2;
		int y = ownerRect.y + ownerRect.height / 2 - height / 2;

		setBounds(x, y, width, height);
	}
}
