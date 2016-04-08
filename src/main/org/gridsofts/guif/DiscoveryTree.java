/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.gridsofts.guif.itf.IDataProvider;
import org.gridsofts.guif.tree.DiscoveryNode;
import org.gridsofts.resource.Resources;
import org.gridsofts.swing.border.ScatterLineBorder;
import org.gridsofts.swing.tree.JCheckableTree;
import org.gridsofts.swing.treeClasses.ITreeListener;
import org.gridsofts.swing.treeClasses.ITreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方便的树操作面板
 * 
 * @author lei
 */
public class DiscoveryTree extends JPanel {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(DiscoveryTree.class);

	private JToolBar toolbar;
	private JCheckableTree tree;

	private DiscoveryNode treeRootNode;

	// 工具栏按钮
	private JButton btnRefresh, btnExpandAll, btnCollapseAll;

	// 工具栏按钮动作
	private ActionListener btnRefreshAction, btnExpandAllAction;

	private IDataProvider<ITreeNode> dataProvider;

	public DiscoveryTree() {
		super(new BorderLayout());

		setPreferredSize(new Dimension(220, 0));
		setMinimumSize(new Dimension(150, 0));

		// 工具栏
		toolbar = new JToolBar();
		toolbar.setBorder(new ScatterLineBorder(ScatterLineBorder.BOTTOM, new Color(0xbfbfbf)));
		add(toolbar, BorderLayout.NORTH);

		toolbar.setFloatable(false);

		btnRefresh = new JButton();
		toolbar.add(btnRefresh);

		btnRefresh.setIcon(new ImageIcon(Resources.Image.get("arrow-circle-double.png")));
		btnRefresh.setToolTipText("刷新");
		btnRefresh.addActionListener(btnRefreshAction = new BtnRefreshAction());

		btnExpandAll = new JButton();
		toolbar.add(btnExpandAll);

		btnExpandAll.setIcon(new ImageIcon(Resources.Image.get("expand-all.png")));
		btnExpandAll.setToolTipText("展开全部");
		btnExpandAll.addActionListener(btnExpandAllAction = new BtnExpandAllAction());

		btnCollapseAll = new JButton();
		toolbar.add(btnCollapseAll);

		btnCollapseAll.setIcon(new ImageIcon(Resources.Image.get("collapse-all.png")));
		btnCollapseAll.setToolTipText("关闭全部");
		btnCollapseAll.addActionListener(new BtnCollapseAllAction());

		// 树
		treeRootNode = new DiscoveryNode("[ROOT]");

		tree = new JCheckableTree();
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

		JScrollPane treeScroller = new JScrollPane(tree);
		treeScroller.setBorder(BorderFactory.createEmptyBorder());
		add(treeScroller, BorderLayout.CENTER);
	}

	/**
	 * @return the dataProvider
	 */
	public IDataProvider<ITreeNode> getDataProvider() {
		return dataProvider;
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataProvider
	 */
	public void setDataProvider(IDataProvider<ITreeNode> dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * 注册树事件监听器
	 * 
	 * @param listener
	 */
	public void addTreeListener(ITreeListener listener) {
		tree.addTreeListener(listener);
	}

	/**
	 * 获取当前选择的树节点
	 * 
	 * @return
	 */
	public ITreeNode getSelectedTreeNode() {
		return tree.getSelectedTreeNode();
	}

	/**
	 * 立即刷新树
	 */
	public void refresh() {
		btnRefreshAction.actionPerformed(null);
	}

	/**
	 * “关闭所有节点”按钮动作
	 */
	private class BtnCollapseAllAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			tree.collapseAll();
		}
	}

	/**
	 * “展开所有节点”按钮动作
	 */
	private class BtnExpandAllAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			tree.expandAll();
		}
	}

	/**
	 * “立即刷新”按钮动作
	 */
	private class BtnRefreshAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (dataProvider == null) {
				return;
			}

			logger.debug("刷新树节点 ...");

			treeRootNode.removeAll();
			treeRootNode.setSelected(false);
			tree.updateUI();

			List<? extends ITreeNode> treeNodes = dataProvider.listData();
			if (treeNodes != null && treeNodes.size() > 0) {
				
				for (ITreeNode nodeObj : treeNodes) {
					treeRootNode.add(nodeObj);
				}
				
				tree.setRootTreeNode(treeRootNode);
			}

			btnExpandAllAction.actionPerformed(null);
		}
	}
}
