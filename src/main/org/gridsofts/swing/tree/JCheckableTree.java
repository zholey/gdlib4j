/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.gridsofts.swing.treeClasses.DefaultCellRenderer;
import org.gridsofts.swing.treeClasses.ICheckableNode;
import org.gridsofts.swing.treeClasses.ITreeNode;

/**
 * @author lei
 */
public class JCheckableTree extends AbstractTree {
	private static final long serialVersionUID = 1L;

	public JCheckableTree() {
		super();

		setCellRenderer(new DefaultCellRenderer(ICheckableNode.class));
		addMouseListener(new TreeMouseListener());
	}

	/**
	 * 递归查找所有选中的节点
	 * 
	 * @return
	 */
	public List<ITreeNode> getLastCheckedNodes() {
		return _getLastCheckedNodes(null);
	}

	/**
	 * 递归查找所有选中的节点
	 * 
	 * @param mutableNode
	 * @return
	 */
	private List<ITreeNode> _getLastCheckedNodes(DefaultMutableTreeNode mutableNode) {
		List<ITreeNode> checkedList = new ArrayList<ITreeNode>();

		if (mutableNode == null) {
			mutableNode = (DefaultMutableTreeNode) getModel().getRoot();
		}

		if (mutableNode != null) {
			ICheckableNode treeNode = (ICheckableNode) mutableNode.getUserObject();

			if (mutableNode.isLeaf() && treeNode.isSelected()) {
				checkedList.add(treeNode);
			}

			else if (!mutableNode.isLeaf() && mutableNode.getChildCount() > 0) {
				for (int i = 0; i < mutableNode.getChildCount(); i++) {
					checkedList.addAll(_getLastCheckedNodes((DefaultMutableTreeNode) mutableNode.getChildAt(i)));
				}
			}
		}

		return checkedList;
	}

	/**
	 * 设置指定节点的选中状态，递归调用；
	 * 
	 * @param mutableNode
	 *            将要设置选中状态的节点
	 * @param isSelected
	 *            选中标志
	 * @param direction
	 *            递归搜索方向（0:双方向搜索； 1:只搜索子级节点； -1:只搜索父级节点）
	 */
	private void _setTreeNodeSelected(DefaultMutableTreeNode mutableNode, boolean isSelected, int direction) {
		ICheckableNode treeNode = (ICheckableNode) mutableNode.getUserObject();
		treeNode.setSelected(isSelected);

		if (treeNode.isSelected()) {

			// 如果选中，则将其所有的子结点都选中
			if (direction >= 0 && mutableNode.getChildCount() > 0) {
				for (int i = 0; i < mutableNode.getChildCount(); i++) {
					DefaultMutableTreeNode childMutableNode = (DefaultMutableTreeNode) mutableNode.getChildAt(i);
					ICheckableNode childTreeNode = (ICheckableNode) childMutableNode.getUserObject();

					if (!childTreeNode.isSelected()) {
						_setTreeNodeSelected(childMutableNode, true, 1);
					}
				}
			}

			// 向上检查，如果父结点的所有子结点都被选中，那么将父结点也选中
			SelectUp: if (direction <= 0 && mutableNode.getParent() != null) {
				DefaultMutableTreeNode parentMutableNode = (DefaultMutableTreeNode) mutableNode.getParent();
				ICheckableNode parentTreeNode = (ICheckableNode) parentMutableNode.getUserObject();

				// 如果当前节点的父节点已经选中，则立即跳出
				if (parentTreeNode.isSelected()) {
					break SelectUp;
				}

				// 开始检查所有子节点是否都被选中
				boolean isAllSelected = true;
				for (int i = 0; i < parentMutableNode.getChildCount(); i++) {
					DefaultMutableTreeNode childMutableNode = (DefaultMutableTreeNode) parentMutableNode.getChildAt(i);
					ICheckableNode childTreeNode = (ICheckableNode) childMutableNode.getUserObject();

					if (!childTreeNode.isSelected()) {
						isAllSelected = false;
						break;
					}
				}

				// 所有子结点都已经选中，则选中父结点
				if (isAllSelected) {
					_setTreeNodeSelected(parentMutableNode, true, -1);
				}
			}
		} else {

			// 父节点取消，那么所有子节点都要取消
			if (direction >= 0 && mutableNode.getChildCount() > 0) {

				// 从上向下取消的时候
				for (int i = 0; i < mutableNode.getChildCount(); ++i) {
					DefaultMutableTreeNode childMutableNode = (DefaultMutableTreeNode) mutableNode.getChildAt(i);
					ICheckableNode childTreeNode = (ICheckableNode) childMutableNode.getUserObject();

					if (childTreeNode.isSelected()) {
						_setTreeNodeSelected(childMutableNode, false, 1);
					}
				}
			}

			// 向上取消，只要存在一个子节点未选中，那么父节点就不应该被选中
			if (direction <= 0 && mutableNode.getParent() != null) {
				DefaultMutableTreeNode parentMutableNode = (DefaultMutableTreeNode) mutableNode.getParent();

				_setTreeNodeSelected(parentMutableNode, false, -1);
			}
		}
	}

	/**
	 * 鼠标按下事件监听； 处理勾选动作
	 * 
	 * @author lei
	 */
	private class TreeMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent event) {

			if (MouseEvent.BUTTON1 != event.getButton() || event.isControlDown() || event.isAltDown()
					|| event.isShiftDown() || event.getClickCount() > 1) {
				return;
			}

			int rowForLocation = JCheckableTree.this.getRowForLocation(event.getX(), event.getY());
			TreePath pathForLocation = JCheckableTree.this.getPathForRow(rowForLocation);

			if (pathForLocation != null && pathForLocation.getLastPathComponent() != null
					&& pathForLocation.getLastPathComponent() instanceof DefaultMutableTreeNode) {

				DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();

				if (mutableNode.getUserObject() instanceof ICheckableNode) {
					ICheckableNode treeNode = (ICheckableNode) mutableNode.getUserObject();
					_setTreeNodeSelected(mutableNode, !treeNode.isSelected(), 0);

					JCheckableTree.this.updateUI();
				}
			}
		}
	}
}
