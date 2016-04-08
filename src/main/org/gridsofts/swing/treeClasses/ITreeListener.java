/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import java.util.EventListener;

import org.gridsofts.util.EventObject;

/**
 * 树监听
 * 
 * @author lei
 */
public interface ITreeListener extends EventListener {

	/**
	 * 选中一个节点
	 * 
	 * @param event
	 */
	public void onSelectedNode(EventObject<ITreeNode> event);

	/**
	 * 节点发生更改
	 * 
	 * @param event
	 */
	public void onTreeNodeChanged(EventObject<ITreeNode> event);
}
