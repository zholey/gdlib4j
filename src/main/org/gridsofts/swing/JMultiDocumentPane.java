/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Component;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * 多文档容器，继承自JDesktopPane，提供了一些子容器排列的方法
 * 
 * @author lei
 */
public class JMultiDocumentPane extends JDesktopPane {
	private static final long serialVersionUID = 1L;

	private static final int AdjustOffset = 30;

	@Override
	public Component add(Component comp) {

		JInternalFrame thisFrame = null;

		if (JInternalFrame.class.isAssignableFrom(comp.getClass())) {
			thisFrame = (JInternalFrame) comp;
		} else {
			thisFrame = new JInternalFrame();
			thisFrame.getContentPane().add(comp);
			thisFrame.setSize(comp.getPreferredSize());

			thisFrame.setResizable(true);
			thisFrame.setMaximizable(true);
			thisFrame.setIconifiable(true);
			thisFrame.setClosable(true);

			thisFrame.addInternalFrameListener(new InternalFrameAdapter() {

				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {
					JInternalFrame theFrame = e.getInternalFrame();

					try {
						if (theFrame.isMaximum()) {
							theFrame.setMaximum(false);
						}
					} catch (PropertyVetoException e1) {
					}

					// 更新窗口标题(Ubuntu下发现的一个Bug：恢复后窗口标题没了)
					theFrame.setTitle(comp.getName());
				}
			});
		}

		if (thisFrame != null) {
			int tabIndex = getIndexOf(thisFrame);
			if (tabIndex < 0) {
				super.add(thisFrame);

				// 定位
				int offset = (getAllFrames().length % 10 - 1) * AdjustOffset;
				thisFrame.setLocation(offset, offset);

				// 设置窗口图标&显示
				thisFrame.setVisible(true);
			}

			// 更新窗口标题
			thisFrame.setTitle(comp.getName());

			// 选中
			try {
				thisFrame.setSelected(true);
			} catch (PropertyVetoException e) {
			}
		}

		return comp;
	}

	/**
	 * 将所有图标化的窗口恢复
	 */
	private void deIconifiedAllFrames() {
		
		JInternalFrame[] children = getAllFrames();
		if (children != null && children.length > 0) {
			
			for (int i = 0; i < children.length; i++) {
				JInternalFrame theFrame = children[i];

				try {
					if (theFrame.isIcon()) {
						theFrame.setIcon(false);
					}
				} catch (PropertyVetoException e1) {
				}
			}
		}
	}

	/**
	 * 横向排列
	 */
	public void splitHorizontal() {
		
		deIconifiedAllFrames();
		
		JInternalFrame[] children = getAllFrames();
		if (children != null && children.length > 0) {

			int w = (int) Math.floor(getWidth() / children.length);

			for (int i = 0; i < children.length; i++) {
				children[i].setBounds(i * w, 0, w, getHeight());
			}
		}
	}

	/**
	 * 纵向排列
	 */
	public void splitVertical() {
		
		deIconifiedAllFrames();
		
		JInternalFrame[] children = getAllFrames();
		if (children != null && children.length > 0) {

			int h = (int) Math.floor(getHeight() / children.length);

			for (int i = 0; i < children.length; i++) {
				children[i].setBounds(0, i * h, getWidth(), h);
			}
		}
	}

	/**
	 * 平均分布
	 */
	public void splitTitle() {
		
		deIconifiedAllFrames();
		
		JInternalFrame[] children = getAllFrames();
		if (children != null && children.length > 0) {

			// 行数、列数
			int cols = (int) Math.ceil(Math.sqrt(children.length));
			int rows = cols;

			int w = (int) Math.floor(getWidth() / cols);
			int h = (int) Math.floor(getHeight() / cols);

			int i = 0;
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {

					children[i++].setBounds(col * w, row * h, w, h);
				}
			}
		}
	}

	/**
	 * 窗口层叠
	 */
	public void stacking() {
		
		deIconifiedAllFrames();
		
		JInternalFrame[] children = getAllFrames();
		if (children != null && children.length > 0) {

			int theShortestSide = getWidth() > getHeight() ? getHeight() : getWidth();
			int gaps = (int) Math.floor(theShortestSide / children.length);

			// 最大间隔为30
			gaps = gaps > 30 ? 30 : gaps;

			for (int i = 0; i < children.length; i++) {
				children[i].setLocation(i * gaps, i * gaps);

				// 选中
				try {
					if (children[i] instanceof JInternalFrame) {
						((JInternalFrame) children[i]).setSelected(true);
					}
				} catch (PropertyVetoException e) {
				}
			}
		}
	}
}