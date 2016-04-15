/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gridsofts.swing.treeClasses.ITreeListener;
import org.gridsofts.swing.treeClasses.ITreeNode;
import org.gridsofts.util.EventDispatcher;
import org.gridsofts.util.EventObject;

/**
 * 抽象树类；提供一些基础的便利方法
 * 
 * @author lei
 */
public abstract class AbstractTree extends JTree implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	protected EventDispatcher<ITreeListener, EventObject<ITreeNode>> evtDispatcher;

	protected ITreeNode selectedTreeNode;

	public AbstractTree() {
		super(new DefaultTreeModel(new DefaultMutableTreeNode("[ROOT]")));

		evtDispatcher = new EventDispatcher<>();

		setRowHeight(20);

		// 单选模式
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// 注册监听
		addTreeSelectionListener(this);
	}

	/**
	 * 注册树事件监听
	 * 
	 * @param listener
	 */
	public void addTreeListener(ITreeListener listener) {
		evtDispatcher.addEventListener(listener);
	}

	/**
	 * 移除树事件监听
	 * 
	 * @param listener
	 */
	public void remoteTreeListener(ITreeListener listener) {
		evtDispatcher.removeEventListener(listener);
	}

	/**
	 * 重设根元素
	 * 
	 * @param root
	 */
	public void setRootTreeNode(ITreeNode root) {

		if (root == null) {
			return;
		}

		((DefaultTreeModel) getModel()).setRoot(recursionBuildMutableTreeNode(root));

		// 刷新视图
		updateUI();
	}

	/**
	 * 获取根节点
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode getRootMutableNode() {

		if (getModel().getRoot() != null && getModel().getRoot() instanceof DefaultMutableTreeNode) {
			return (DefaultMutableTreeNode) ((DefaultTreeModel) getModel()).getRoot();
		}

		return null;
	}

	/**
	 * 获取根节点
	 * 
	 * @return
	 */
	public ITreeNode getRootTreeNode() {

		if (getRootMutableNode() != null && getRootMutableNode().getUserObject() != null
				&& ITreeNode.class.isAssignableFrom(getRootMutableNode().getUserObject().getClass())) {

			return (ITreeNode) getRootMutableNode().getUserObject();
		}

		return null;
	}

	/**
	 * 获取选中的节点
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode getSelectedMutableNode() {

		if (getLastSelectedPathComponent() != null
				&& getLastSelectedPathComponent() instanceof DefaultMutableTreeNode) {
			return (DefaultMutableTreeNode) getLastSelectedPathComponent();
		}

		return null;
	}

	/**
	 * 获取选中的节点
	 * 
	 * @return
	 */
	public ITreeNode getSelectedTreeNode() {

		if (getSelectedMutableNode() != null && getSelectedMutableNode().getUserObject() != null
				&& ITreeNode.class.isAssignableFrom(getSelectedMutableNode().getUserObject().getClass())) {
			return (ITreeNode) getSelectedMutableNode().getUserObject();
		}

		return null;
	}

	/**
	 * 添加新节点； </br>
	 * 如果当前选定了一个节点，则新节点是当前选定节点的子节点，如果当前未选定任何节点，则新节点是根节点的子节点
	 * 
	 * @param newObj
	 * @param expandNow
	 *            是否立即展开
	 * @param selectNow
	 *            是否立即选定
	 */
	public void addTreeNode(ITreeNode newObj, boolean expandNow, boolean selectNow) {

		// 查找父节点
		ITreeNode selectedTreeNode = getSelectedTreeNode();
		ITreeNode parentTreeNode = selectedTreeNode != null ? selectedTreeNode : getRootTreeNode();

		DefaultMutableTreeNode parentMutableNode = null;
		if (getLastSelectedPathComponent() != null) {
			parentMutableNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		} else {
			parentMutableNode = (DefaultMutableTreeNode) getModel().getRoot();
		}

		if (parentTreeNode != null && parentMutableNode != null) {

			// 创建新节点
			DefaultMutableTreeNode newNode = recursionBuildMutableTreeNode(newObj);

			// 将新节点添加至父节点
			parentTreeNode.add(newObj);
			parentMutableNode.add(newNode);

			// 展开并选定新添加的节点
			if (expandNow) {
				expandPath(getNodeTreePath(newNode, null, false));
			}
			if (selectNow) {
				addSelectionPath(getNodeTreePath(newNode, null, true));
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

		DefaultMutableTreeNode selectedMutableNode = getSelectedMutableNode();
		ITreeNode selectedTreeNode = getSelectedTreeNode();

		// 查找父节点
		DefaultMutableTreeNode parentMutableNode = null;
		ITreeNode parentTreeNode = null;

		if (selectedMutableNode != null && selectedMutableNode.getParent() != null) {
			parentMutableNode = (DefaultMutableTreeNode) selectedMutableNode.getParent();
			parentTreeNode = (ITreeNode) parentMutableNode.getUserObject();

			if (parentTreeNode != null) {

				parentMutableNode.remove(selectedMutableNode);
				parentTreeNode.remove(selectedTreeNode);

				selectedTreeNode = null;

				// 刷新视图
				updateUI();
			}
		}
	}

	/**
	 * 展开所有节点
	 */
	public void expandAll() {

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
		List<TreeNode> leafNodes = getAllLeafNodes(rootNode, null);

		for (TreeNode node : leafNodes) {
			expandPath(getNodeTreePath(node, null, false));
		}
	}

	/**
	 * 关闭所有节点
	 */
	public void collapseAll() {

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
		List<TreeNode> leafNodes = getAllLeafNodes(rootNode, null);

		for (TreeNode node : leafNodes) {
			collapsePath(getNodeTreePath(node, null, false));
		}
	}

	/**
	 * 构造树节点
	 * 
	 * @param root
	 * @return
	 */
	protected DefaultMutableTreeNode recursionBuildMutableTreeNode(ITreeNode root) {

		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(root);

		if (root.getChildren() != null && root.getChildren().size() > 0) {
			List<?> children = root.getChildren();
			
			for (int i = 0; i < children.size(); i++) {
				treeNode.add(recursionBuildMutableTreeNode((ITreeNode) children.get(i)));
			}
		}

		return treeNode;
	}

	/**
	 * 返回所有叶节点
	 * 
	 * @param node
	 * @param leafNodes
	 * @return
	 */
	protected List<TreeNode> getAllLeafNodes(TreeNode node, List<TreeNode> leafNodes) {

		if (leafNodes == null) {
			leafNodes = new ArrayList<>();
		}

		if (node.isLeaf()) {
			leafNodes.add(node);
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			getAllLeafNodes(node.getChildAt(i), leafNodes);
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
	protected TreePath getNodeTreePath(TreeNode node, List<TreeNode> pathObjs, boolean hasLeaf) {

		if (pathObjs == null) {
			pathObjs = new ArrayList<>();
		}

		if (node.getParent() != null) {
			getNodeTreePath(node.getParent(), pathObjs, hasLeaf);
		}

		if (hasLeaf || !node.isLeaf()) {
			pathObjs.add(node);
		}

		return new TreePath(pathObjs.toArray(new TreeNode[0]));
	}

	@Override
	public void valueChanged(TreeSelectionEvent evt) {

		evtDispatcher.dispatchEvent("onSelectedNode",
				new EventObject<>(this, selectedTreeNode = getSelectedTreeNode()));
	}
}
