/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import java.util.List;

/**
 * 树形节点模型
 * 
 * @author lei
 */
public interface ITreeNode {

	/**
	 * 获取节点ID
	 * 
	 * @return
	 */
	public String getNodeId();

	/**
	 * 获取节点名称
	 * 
	 * @return
	 */
	public String getNodeName();

	/**
	 * 设置节点名称
	 * 
	 * @return
	 */
	public void setNodeName(String name);

	/**
	 * 添加子节点
	 * 
	 * @param child
	 */
	public void add(ITreeNode child);

	/**
	 * 移除子节点
	 * 
	 * @param child
	 */
	public void remove(ITreeNode child);

	/**
	 * 获取子节点列表
	 * 
	 * @return
	 */
	public List<ITreeNode> getChildren();

	/**
	 * 返回节点在树中的描述
	 * 
	 * @return
	 */
	public String toString();
}
