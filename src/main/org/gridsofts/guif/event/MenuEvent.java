/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.event;

import org.gridsofts.event.Event;
import org.gridsofts.event.EventType;
import org.gridsofts.guif.Menubar;

/**
 * @author lei
 */
public class MenuEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<MenuEvent> Action = new EventType<>(MenuEvent.class, "MenuAction");

	/**
	 * @param source
	 * @param payload
	 */
	public MenuEvent(Object source, Menubar.Action payload) {
		super(source, payload);
	}

	@Override
	public Menubar.Action getPayload() {
		return (Menubar.Action) super.getPayload();
	}
}
