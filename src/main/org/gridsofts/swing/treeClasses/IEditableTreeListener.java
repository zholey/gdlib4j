/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import org.gridsofts.util.EventObject;

/**
 * 可编辑树事件监听
 * 
 * @author lei
 */
public interface IEditableTreeListener {

	public void onTreeNodeChanged(EventObject<? extends TreeUserObject> event);
}
