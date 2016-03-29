/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.List;

/**
 * 树形节点模型
 * 
 * @author lei
 */
public interface INode {

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
	 * 指示当前节点是否已勾选
	 * 
	 * @return
	 */
	public boolean isSelected();

	/**
	 * 获取子节点列表
	 * 
	 * @return
	 */
	public List<INode> getChildren();
}
