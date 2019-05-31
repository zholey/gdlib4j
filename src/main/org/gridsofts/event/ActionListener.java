/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.event;

/**
 * 一般事件监听
 * 
 * @author lei
 */
public interface ActionListener<E extends Event> extends java.util.EventListener {

	/**
	 * 执行事件动作
	 * 
	 * @param event
	 */
	public void actionPerformed(E event);
}
