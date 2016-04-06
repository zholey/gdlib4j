/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.tree;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.gridsofts.swing.treeClasses.TreeUserObject;

/**
 * 可编辑的树
 * 
 * @author lei
 */
public class JEditableTree extends JTree implements TreeModelListener, TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode selectdNode;
	private TreeUserObject root;

	public JEditableTree(TreeUserObject root) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(root)));
		
		this.root = root;
		
		setRowHeight(20);
		setEditable(true);
		setDragEnabled(true);
		
		// 单选模式
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		getDefaultTreeModel().addTreeModelListener(this);
		addTreeSelectionListener(this);
	}
	
	public void setData(TreeUserObject root) {
		
	}
	

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
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
	
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		evt.getPath().getLastPathComponent();
	}
}
