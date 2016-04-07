/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gridsofts.swing.treeClasses.IEditableTreeListener;
import org.gridsofts.swing.treeClasses.IEditableTreeNode;
import org.gridsofts.util.EventDispatcher;
import org.gridsofts.util.EventObject;

/**
 * 可编辑的树
 * 
 * @author lei
 */
public class JEditableTree extends JTree implements TreeModelListener, TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	private EventDispatcher<IEditableTreeListener, EventObject<IEditableTreeNode>> evtDispatcher;

	private DefaultMutableTreeNode selectedNode;
	private IEditableTreeNode rootUserObj, selectedUserObj;

	public JEditableTree(IEditableTreeNode root) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(root)));

		evtDispatcher = new EventDispatcher<IEditableTreeListener, EventObject<IEditableTreeNode>>();

		this.rootUserObj = root;

		setRowHeight(20);
		setEditable(true);
		setDragEnabled(true);

		// 单选模式
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		getModel().addTreeModelListener(this);
		addTreeSelectionListener(this);
	}

	/**
	 * 注册树事件监听
	 * 
	 * @param listener
	 */
	public void addTreeListener(IEditableTreeListener listener) {
		evtDispatcher.addEventListener(listener);
	}

	/**
	 * 移除树事件监听
	 * 
	 * @param listener
	 */
	public void remoteTreeListener(IEditableTreeListener listener) {
		evtDispatcher.removeEventListener(listener);
	}

	/**
	 * 重设根元素
	 * 
	 * @param root
	 */
	public void setRoot(IEditableTreeNode root) {

		if (root == null) {
			return;
		}

		((DefaultTreeModel) getModel()).setRoot(new DefaultMutableTreeNode(root));

		this.rootUserObj = root;
	}

	/**
	 * 添加新节点； </br>
	 * 如果当前选定了一个节点，则新节点是当前选定节点的子节点，如果当前未选定任何节点，则新节点是根节点的子节点
	 * 
	 * @param newObj
	 * @param selectNow
	 *            是否立即选定
	 */
	public void addUserObject(IEditableTreeNode newObj, boolean selectNow) {

		// 查找父节点
		IEditableTreeNode parentObj = selectedUserObj != null ? selectedUserObj : rootUserObj;
		DefaultMutableTreeNode parentNode = selectedNode != null ? selectedNode
				: (DefaultMutableTreeNode) getModel().getRoot();

		if (parentObj != null && parentNode != null) {

			// 创建新节点
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newObj);

			// 将新节点添加至父节点
			parentObj.addChild(newObj);
			parentNode.add(newNode);

			// 展开并选定新添加的节点
			expandPath(_getNodeTreePath(newNode, null, false));
			if (selectNow) {
				addSelectionPath(_getNodeTreePath(newNode, null, true));
			}

			// 刷新视图
			updateUI();
		}
	}

	/**
	 * 移除选定的节点
	 * 
	 * @param obj
	 */
	public void removeSelectedNode() {

		// 查找父节点
		DefaultMutableTreeNode parentNode = null;
		if (selectedNode != null && selectedNode.getParent() != null) {
			parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
		}

		if (parentNode != null) {
			IEditableTreeNode parentObj = (IEditableTreeNode) parentNode.getUserObject();

			if (parentObj != null) {

				parentNode.remove(selectedNode);
				parentObj.removeChild(selectedUserObj);

				selectedNode = null;
				selectedUserObj = null;

				// 刷新视图
				updateUI();
			}
		}
	}

	/**
	 * 获取当前选定的节点
	 * 
	 * @return
	 */
	public IEditableTreeNode getSelectedUserObject() {
		return selectedUserObj;
	}

	/**
	 * 展开所有节点
	 */
	public void expandAll() {

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
		List<TreeNode> leafNodes = _getAllLeafNodes(rootNode, null);

		for (TreeNode node : leafNodes) {
			expandPath(_getNodeTreePath(node, null, false));
		}
	}

	/**
	 * 关闭所有节点
	 */
	public void collapseAll() {

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
		List<TreeNode> leafNodes = _getAllLeafNodes(rootNode, null);

		for (TreeNode node : leafNodes) {
			collapsePath(_getNodeTreePath(node, null, false));
		}
	}

	/**
	 * 返回所有节点的路径
	 * 
	 * @param node
	 * @param leafNodes
	 * @return
	 */
	private List<TreeNode> _getAllLeafNodes(TreeNode node, List<TreeNode> leafNodes) {

		if (leafNodes == null) {
			leafNodes = new ArrayList<TreeNode>();
		}

		if (node.isLeaf()) {
			leafNodes.add(node);
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			_getAllLeafNodes(node.getChildAt(i), leafNodes);
		}

		return leafNodes;
	}

	/**
	 * 返回指定节点的路径
	 * 
	 * @param node
	 * @param pathObjs
	 * @param hasLeaf
	 * @return
	 */
	private TreePath _getNodeTreePath(TreeNode node, List<TreeNode> pathObjs, boolean hasLeaf) {

		if (pathObjs == null) {
			pathObjs = new ArrayList<TreeNode>();
		}

		if (node.getParent() != null) {
			_getNodeTreePath(node.getParent(), pathObjs, hasLeaf);
		}

		if (hasLeaf || !node.isLeaf()) {
			pathObjs.add(node);
		}

		return new TreePath(pathObjs.toArray(new TreeNode[0]));
	}

	@Override
	public void treeNodesChanged(TreeModelEvent evt) {

		if (selectedNode != null) {
			selectedUserObj.setName(selectedNode.getUserObject().toString());
			selectedNode.setUserObject(selectedUserObj);

			evtDispatcher.dispatchEvent("onTreeNodeChanged", new EventObject<IEditableTreeNode>(this, selectedUserObj));
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

	@Override
	public void valueChanged(TreeSelectionEvent evt) {
		
		selectedNode = null;
		selectedUserObj = null;

		if (getLastSelectedPathComponent() != null
				&& getLastSelectedPathComponent() instanceof DefaultMutableTreeNode) {

			selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
			selectedUserObj = (IEditableTreeNode) selectedNode.getUserObject();
		}
		
		evtDispatcher.dispatchEvent("onSelectedNode", new EventObject<IEditableTreeNode>(this, selectedUserObj));
	}
}
