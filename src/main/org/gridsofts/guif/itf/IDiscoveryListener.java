/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.EventListener;

/**
 * @author lei
 */
public interface IDiscoveryListener extends EventListener {

	/**
	 * 选中一个树节点
	 * 
	 * @param selectedNode
	 */
	public void selectedTreeNode(INode selectedNode);
}
