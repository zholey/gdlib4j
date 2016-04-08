/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import javax.swing.Icon;

/**
 * 让节点支持自定义图标
 * 
 * @author lei
 */
public interface Iconified {

	/**
	 * 获取节点的图标
	 * 
	 * @param selected
	 * @param expanded
	 * @param leaf
	 * @param hasFocus
	 * @return
	 */
	public Icon getIcon(boolean selected, boolean expanded, boolean leaf, boolean hasFocus);
}
