/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.treeClasses;

import org.gridsofts.event.Event;
import org.gridsofts.event.EventType;

/**
 * 树节点事件
 * 
 * @author lei
 */
public class TreeEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<TreeEvent> Action = new EventType<>(TreeEvent.class, "TreeAction");

	/**
	 * @param source
	 * @param payload
	 */
	public TreeEvent(Object source, ITreeNode payload) {
		super(source, payload);
	}

	@Override
	public ITreeNode getPayload() {
		return (ITreeNode) super.getPayload();
	}
}
