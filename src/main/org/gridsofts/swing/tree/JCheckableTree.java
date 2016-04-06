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
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gridsofts.swing.treeClasses.CheckableTreeNode;

/**
 * @author lei
 */
public class JCheckableTree extends JTree {
	private static final long serialVersionUID = 1L;

	public JCheckableTree() {
		this(getDefaultTreeModel());
	}

	public JCheckableTree(TreeModel treeModel) {
		super(treeModel);

		TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setSelectionModel(selectionModel);

		addMouseListener(new TreeMouseListener());
	}

	public List<CheckableTreeNode> getLastCheckedNodes() {
		return getLastCheckedNodes(null);
	}
	
	private List<CheckableTreeNode> getLastCheckedNodes(Object rootTreeNode) {
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
					checkedList.addAll(getLastCheckedNodes(mutableTreeNode.getChildAt(i)));
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
