/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.event;

import org.gridsofts.event.Event;
import org.gridsofts.event.EventType;

/**
 * @author lei
 */
public class StateEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<StateEvent> Action = new EventType<>(StateEvent.class, "StateAction");

	/**
	 * @param source
	 * @param payload
	 */
	public StateEvent(Object source, String payload) {
		super(source, payload);
	}

	@Override
	public String getPayload() {
		return (String) super.getPayload();
	}
}
