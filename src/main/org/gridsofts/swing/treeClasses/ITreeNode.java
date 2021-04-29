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
	
	public String getName();
	
	public void setName(String name);

	public void add(ITreeNode child);

	public void remove(ITreeNode child);

	public void removeAll();

	public List<?> getChildren();

}
