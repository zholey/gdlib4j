/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import java.util.List;

/**
 * @author lei
 */
public interface TreeUserObject {
	
	public void setName(String name);
	
	public void addChild(TreeUserObject child);

	public List<? extends TreeUserObject> getChildren();
}
