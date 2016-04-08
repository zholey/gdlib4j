/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gridsofts.swing.treeClasses.ICheckableNode;
import org.gridsofts.swing.treeClasses.ITreeNode;

/**
 * 树节点
 * 
 * @author lei
 */
public class DiscoveryNode implements ICheckableNode, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String nodeName;
	private boolean selected;
	
	private List<ITreeNode> children = new ArrayList<ITreeNode>();
	
	public DiscoveryNode(String name) {
		nodeName = name;
	}

	@Override
	public String getNodeId() {
		return id;
	}

	@Override
	public String getNodeName() {
		return nodeName;
	}

	@Override
	public void setNodeName(String name) {
		nodeName = name;		
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean value) {
		selected = value;		
	}

	@Override
	public void add(ITreeNode child) {
		children.add(child);		
	}

	@Override
	public void remove(ITreeNode child) {
		children.remove(child);		
	}
	
	public void removeAll() {
		children.removeAll(children);
	}

	@Override
	public List<ITreeNode> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		return getNodeName();
	}
}
