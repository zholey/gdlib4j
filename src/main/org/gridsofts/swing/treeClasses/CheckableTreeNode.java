/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 可勾选的树节点模型
 * 
 * @author lei
 */
public class CheckableTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	protected boolean isSelected;

	public CheckableTreeNode() {
		this(null);
	}
	
	public CheckableTreeNode(Object userObject) {
		this(userObject, false);
	}

	public CheckableTreeNode(Object userObject, boolean isSelected) {
		super(userObject);
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean _isSelected) {
		this.isSelected = _isSelected;

		if (_isSelected) {
			// 如果选中，则将其所有的子结点都选中
			if (children != null) {
				for (Object obj : children) {
					CheckableTreeNode node = (CheckableTreeNode) obj;
					if (_isSelected != node.isSelected()) {
						node.setSelected(_isSelected);
					}
				}
			}
			
			// 向上检查，如果父结点的所有子结点都被选中，那么将父结点也选中
			CheckableTreeNode pNode = (CheckableTreeNode) parent;
			
			// 开始检查pNode的所有子节点是否都被选中
			if (pNode != null) {
				boolean isAllSelected = true;
				for (int i = 0; i < pNode.children.size(); i++) {
					CheckableTreeNode pChildNode = (CheckableTreeNode) pNode.children.get(i);
					if (!pChildNode.isSelected()) {
						isAllSelected = false;
						break;
					}
				}
				/*
				 * pNode所有子结点都已经选中，则选中父结点
				 */
				if (isAllSelected) {
					if (pNode.isSelected() != _isSelected)
						pNode.setSelected(_isSelected);
				}
			}
		} else {
			/*
			 * 如果是取消父结点导致子结点取消，那么此时所有的子结点都应该是选择上的；
			 * 否则就是子结点取消导致父结点取消，然后父结点取消导致需要取消子结点，但 是这时候是不需要取消子结点的。
			 */
			if (children != null) {
				boolean isAllSelected = true;
				for (int i = 0; i < children.size(); i++) {
					CheckableTreeNode childNode = (CheckableTreeNode) children.get(i);
					if (!childNode.isSelected()) {
						isAllSelected = false;
						break;
					}
				}
				// 从上向下取消的时候
				if (isAllSelected) {
					for (int i = 0; i < children.size(); ++i) {
						CheckableTreeNode node = (CheckableTreeNode) children.get(i);
						if (node.isSelected() != _isSelected) {
							node.setSelected(_isSelected);
						}
					}
				}
			}

			// 向上取消，只要存在一个子节点不是选上的，那么父节点就不应该被选上。
			CheckableTreeNode pNode = (CheckableTreeNode) parent;
			if (pNode != null && pNode.isSelected() != _isSelected) {
				pNode.setSelected(_isSelected);
			}
		}
	}
}
