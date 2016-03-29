/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.gridsofts.resource.Resources;
import org.gridsofts.swing.closeableTabbedPaneClasses.ITabbed;

/**
 * 可以关闭的选项卡容器
 * 
 * @author Lei
 * 
 */
public class JCloseableTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	
	public Component addCloseableTab(Component comp) {
		return addCloseableTab(null, null, comp, null);
	}
	
	public Component addCloseableTab(String title, Component comp) {
		return addCloseableTab(title, null, comp, null);
	}
	
	public Component addCloseableTab(String title, Icon icon, Component comp) {
		return addCloseableTab(title, icon, comp, null);
	}

	public Component addCloseableTab(String title, Icon icon, Component comp, String tip) {
		
		int index = getTabCount();

		super.addTab(title, icon, comp, tip);

		if (comp instanceof ITabbed) {
			setTabComponentAt(index, new TabRendererComponent(comp, title, icon, tip));
		}

		return comp;
	}

	/**
	 * Tab页标题栏
	 * 
	 * @author Lei
	 * 
	 */
	private class TabRendererComponent extends JPanel {
		private static final long serialVersionUID = 1L;

		private Component comp;

		private JLabel lab;
		private JLabel btnClose;

		private Icon btnIcon0, btnIcon1, btnIcon2;

		public TabRendererComponent(Component comp, String title, Icon icon, String tip) {
			super(new BorderLayout());

			this.comp = comp;

			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

			lab = new JLabel();

			if (title != null) {
				lab.setText(title);
			} else {
				lab.setText(comp.getName());
			}

			if (icon != null) {
				lab.setIcon(icon);
			}

			if (tip != null) {
				lab.setToolTipText(tip);
			}

			add(lab, BorderLayout.CENTER);

			// 如果此Tab是允许被关闭的，则显示关闭按钮
			if (((ITabbed) comp).isClosable()) {

				lab.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

				btnIcon0 = new ImageIcon(Resources.Image.get("close_0.png"));
				btnIcon1 = new ImageIcon(Resources.Image.get("close_1.png"));
				btnIcon2 = new ImageIcon(Resources.Image.get("close_2.png"));

				btnClose = new IconButton(btnIcon0, btnIcon1, btnIcon2) {
					private static final long serialVersionUID = 1L;

					@Override
					public void mouseClicked(MouseEvent event) {

						// 在Tab关闭之前调用tabClosing方法，返回TRUE时正常关闭
						if (((ITabbed) TabRendererComponent.this.comp).tabClosing()) {
							JCloseableTabbedPane.this.remove(TabRendererComponent.this.comp);
							JCloseableTabbedPane.this.updateUI();
						}
					}
				};

				add(btnClose, BorderLayout.EAST);
			}
		}
	}
}
