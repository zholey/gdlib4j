/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.EventListener;

import org.gridsofts.guif.Menubar;
import org.gridsofts.util.EventObject;

/**
 * 定义菜单栏可能抛出的所有事件
 * 
 * @author lei
 */
public interface IMenuListener extends EventListener {

	void menuItemClicked(EventObject<Menubar.Action> event);
}
