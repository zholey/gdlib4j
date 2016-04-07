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

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gridsofts.swing.treeClasses.CheckableTreeNode;
import org.gridsofts.swing.treeClasses.IEditableTreeNode;

/**
 * @author lei
 */
public class JCheckableTree extends JEditableTree {
	private static final long serialVersionUID = 1L;

	public JCheckableTree(IEditableTreeNode root) {
		super(root);

		addMouseListener(new TreeMouseListener());
	}

	public List<CheckableTreeNode> getLastCheckedNodes() {
		return _getLastCheckedNodes(null);
	}
	
	private List<CheckableTreeNode> _getLastCheckedNodes(Object rootTreeNode) {
		List<CheckableTreeNode> checkedList = new ArrayList<CheckableTreeNode>();

		if (rootTreeNode == null) {
			rootTreeNode = getModel().getRoot();
		}

		if (rootTreeNode != null && MutableTreeNode.class.isAssignableFrom(rootTreeNode.getClass())) {
			MutableTreeNode mutableTreeNode = (MutableTreeNode) rootTreeNode;

			if (mutableTreeNode.isLeaf() && CheckableTreeNode.class.isAssignableFrom(mutableTreeNode.getClass())
					&& ((CheckableTreeNode) mutableTreeNode).isSelected()) {

				checkedList.add((CheckableTreeNode) mutableTreeNode);
			}

			else if (!mutableTreeNode.isLeaf() && mutableTreeNode.getChildCount() > 0) {
				for (int i = 0; i < mutableTreeNode.getChildCount(); i++) {
					checkedList.addAll(_getLastCheckedNodes(mutableTreeNode.getChildAt(i)));
				}
			}
		}

		return checkedList;
	}

	private class TreeMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent event) {

			JTree tree = (JTree) event.getSource();

			TreePath path = tree.getPathForRow(tree.getRowForLocation(event.getX(), event.getY()));

			if (path != null && path.getLastPathComponent() != null
					&& path.getLastPathComponent() instanceof CheckableTreeNode) {
				CheckableTreeNode node = (CheckableTreeNode) path.getLastPathComponent();

				node.setSelected(!node.isSelected());
				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
			}
		}
	}
}
