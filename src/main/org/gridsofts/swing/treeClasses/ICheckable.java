/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

/**
 * 可勾选的节点抽象接口
 * 
 * @author lei
 */
public interface ICheckable {
	
	/**
	 * 指示当前节点是否已选中
	 * 
	 * @return
	 */
	public boolean isSelected();

	/**
	 * 设置当前节点的选中状态
	 * 
	 * @param value
	 */
	public void setSelected(boolean value);
}
