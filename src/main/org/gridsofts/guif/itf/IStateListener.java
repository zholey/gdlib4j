/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.EventListener;

import org.gridsofts.guif.event.EventObject;

/**
 * @author lei
 */
public interface IStateListener extends EventListener {
	
	/**
	 * 显示状态栏消息
	 * 
	 * @param event
	 */
	public void statusMsg(EventObject<String> event);
}
