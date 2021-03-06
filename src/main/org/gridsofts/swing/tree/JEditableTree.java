/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gridsofts.event.Event;
import org.gridsofts.swing.treeClasses.DefaultCellRenderer;
import org.gridsofts.swing.treeClasses.ITreeNode;
import org.gridsofts.swing.treeClasses.TreeEvent;

/**
 * 可编辑的树
 * 
 * @author lei
 */
public class JEditableTree extends AbstractTree implements TreeModelListener {
	private static final long serialVersionUID = 1L;

	public JEditableTree(boolean editable) {
		super();

		setRootVisible(false);
		setCellRenderer(new DefaultCellRenderer(ITreeNode.class));

		if (editable) {
			setEditable(true);
			setDragEnabled(true);
			setInvokesStopCellEditing(true);
		}

		// 注册监听
		getModel().addTreeModelListener(this);
	}

	@Override
	public void treeNodesChanged(TreeModelEvent evt) {

		DefaultMutableTreeNode selectedMutableNode = getSelectedMutableNode();

		if (selectedMutableNode != null && selectedTreeNode != null) {

			selectedTreeNode.setName(selectedMutableNode.getUserObject().toString());
			selectedMutableNode.setUserObject(selectedTreeNode);

			evtDispatcher.dispatchEvent(TreeEvent.Action, "onTreeNodeChanged", new Event(this, selectedTreeNode));
		}
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
	}
}
