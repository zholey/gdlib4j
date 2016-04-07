/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.tree;

import java.util.List;

import org.gridsofts.guif.itf.INode;
import org.gridsofts.swing.treeClasses.CheckableTreeNode;
import org.gridsofts.swing.treeClasses.IEditableTreeNode;

/**
 * 树节点
 * 
 * @author lei
 */
public class DiscoveryNode extends CheckableTreeNode implements IEditableTreeNode {
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

	/* (non-Javadoc)
	 * @see org.gridsofts.swing.treeClasses.IEditableTreeNode#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.gridsofts.swing.treeClasses.IEditableTreeNode#addChild(org.gridsofts.swing.treeClasses.IEditableTreeNode)
	 */
	@Override
	public void addChild(IEditableTreeNode childObject) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.gridsofts.swing.treeClasses.IEditableTreeNode#removeChild(org.gridsofts.swing.treeClasses.IEditableTreeNode)
	 */
	@Override
	public void removeChild(IEditableTreeNode childObject) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.gridsofts.swing.treeClasses.IEditableTreeNode#getChildren()
	 */
	@Override
	public List<? extends IEditableTreeNode> getChildren() {
		// TODO Auto-generated method stub
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