/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.closeableTabbedPaneClasses;

/**
 * 准备在JCloseableTabbedPane中显示的组件都要实现此接口
 * 
 * @author zholey
 * 
 */
public interface ITabbed {

	public boolean isClosable();

	public boolean tabClosing();
}
