/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.gridsofts.resource.Resources;
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

		this.branchOpenIcon = branchOpenIcon;
		this.branchCloseIcon = branchCloseIcon;
		this.leafIcon = leafIcon;

		setOpaque(false);

		if (ICheckableNode.class.isAssignableFrom(userObjectClass)) {
			chkbox = new JCheckBox();
			chkbox.setOpaque(false);

			add(chkbox, BorderLayout.WEST);
		}

		// 节点文字
		label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		add(label, BorderLayout.CENTER);
	}

	@Override
	public Insets getInsets() {
		return new Insets(2, 3, 2, 3);
	}

	// @Override
	// public Dimension getPreferredSize() {
	// Dimension dCheck = chkbox.getPreferredSize();
	// Dimension dLabel = label.getPreferredSize();
	//
	// return new Dimension(dCheck.width + dLabel.width,
	// dCheck.height < dLabel.height ? dLabel.height : dCheck.height);
	// }
	//
	// @Override
	// public void doLayout() {
	// Dimension dCheck = chkbox.getPreferredSize();
	// Dimension dLabel = label.getPreferredSize();
	//
	// int yCheck = 0;
	// int yLabel = 0;
	//
	// if (dCheck.height < dLabel.height) {
	// yCheck = (dLabel.height - dCheck.height) / 2;
	// } else {
	// yLabel = (dCheck.height - dLabel.height) / 2;
	// }
	//
	// chkbox.setLocation(0, yCheck);
	// chkbox.setBounds(0, yCheck, dCheck.width, dCheck.height);
	//
	// label.setLocation(dCheck.width, yLabel);
	// label.setBounds(dCheck.width, yLabel, dLabel.width, dLabel.height);
	// }

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		if (value != null && DefaultMutableTreeNode.class.isAssignableFrom(value.getClass())) {
			DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode) value;

			if (mutableNode.getUserObject() != null) {

				label.setText(mutableNode.getUserObject().toString());
				if (leaf) {
					label.setIcon(leafIcon);
				} else if (expanded) {
					label.setIcon(branchOpenIcon);
				} else {
					label.setIcon(branchCloseIcon);
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
