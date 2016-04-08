/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.gridsofts.resource.Resources;
import org.gridsofts.swing.BorderLayoutII;
import org.gridsofts.swing.border.ScatterLineBorder;

/**
 * 可勾选的树节点渲染器
 * 
 * @author lei
 */
public class DefaultCellRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	private ImageIcon branchOpenIcon, branchCloseIcon, leafIcon;
	private JCheckBox chkbox;
	private JLabel label;

	public DefaultCellRenderer(Class<?> userObjectClass) {
		this(userObjectClass, new ImageIcon(Resources.Image.get("folder-open.png")),
				new ImageIcon(Resources.Image.get("folder.png")), new ImageIcon(Resources.Image.get("document.png")));
	}

	public DefaultCellRenderer(Class<?> userObjectClass, ImageIcon branchOpenIcon, ImageIcon branchCloseIcon,
			ImageIcon leafIcon) {

		super(new BorderLayoutII(BorderLayoutII.VERTICAL_CENTER));

		this.branchOpenIcon = branchOpenIcon;
		this.branchCloseIcon = branchCloseIcon;
		this.leafIcon = leafIcon;

		setOpaque(false);

		// 节点文字
		label = new JLabel();
		add(label, BorderLayout.CENTER);

		// checkbox
		if (ICheckableNode.class.isAssignableFrom(userObjectClass)) {
			chkbox = new JCheckBox();
			chkbox.setOpaque(false);

			add(chkbox, BorderLayout.WEST);
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		if (value != null && DefaultMutableTreeNode.class.isAssignableFrom(value.getClass())) {
			DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode) value;

			if (mutableNode.getUserObject() != null) {
				label.setText(mutableNode.getUserObject().toString());

				// 如果当前节点支持自定义图标
				if (Iconified.class.isAssignableFrom(mutableNode.getUserObject().getClass())) {
					Iconified iconableNode = (Iconified) mutableNode.getUserObject();
					label.setIcon(iconableNode.getIcon(selected, expanded, leaf, hasFocus));
				} 
				
				// 一般节点的默认图标处理
				else {
					if (leaf) {
						label.setIcon(leafIcon);
					} else if (expanded) {
						label.setIcon(branchOpenIcon);
					} else {
						label.setIcon(branchCloseIcon);
					}
				}

				if (chkbox != null && ICheckableNode.class.isAssignableFrom(mutableNode.getUserObject().getClass())) {
					ICheckableNode treeNode = (ICheckableNode) mutableNode.getUserObject();

					chkbox.setSelected(treeNode.isSelected());
				}
			}

			if (selected) {
				setOpaque(true);
				setBackground(new Color(0xefefef));
				setBorder(new ScatterLineBorder(ScatterLineBorder.TOP | ScatterLineBorder.BOTTOM));
			} else {
				setOpaque(false);
				setBorder(BorderFactory.createEmptyBorder());
			}
		}

		return this;
	}
}
