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
	 * 建议使用此构造方法时，对话框初始化完成后，调用一次packAndCentrally方法，以自动高速窗口尺寸并居中定位
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public JDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

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

		doCentrally(width, height);
	}

	private void doCentrally(int width, int height) {

		Rectangle ownerRct = getOwner().getBounds();

		int x = ownerRct.x + ownerRct.width / 2 - width / 2;
		int y = ownerRct.y + ownerRct.height / 2 - height / 2;

		setBounds(x, y, width, height);
	}

	protected void packAndCentrally() {
		pack();
		doCentrally(getWidth(), getHeight());
	}
}
