/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.tree;

import org.gridsofts.guif.itf.INode;
import org.gridsofts.swing.treeClasses.CheckableTreeNode;

/**
 * 树节点
 * 
 * @author lei
 */
public class DiscoveryNode extends CheckableTreeNode {
	private static final long serialVersionUID = 1L;

	public DiscoveryNode(Object userObject) {
		super(userObject);
	}

	public DiscoveryNode(INode nodeObject) {
		super(nodeObject);
	}
	
	public INode getNodeObject() {
		
		if (getUserObject() != null && getUserObject() instanceof INode) {
			return (INode) getUserObject();
		}
		
		return null;
	}

	@Override
	public String toString() {

		if (getUserObject() != null && INode.class.isAssignableFrom(getUserObject().getClass())) {
			return ((INode) getUserObject()).getNodeName();
		}

		return super.toString();
	}
}