/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.event;

import java.io.Serializable;

/**
 * 事件类型类
 * 
 * @author lei
 */
public class EventType<E extends Event> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Class<E> eventClass;
	// 事件类型名称
	private String name;

	public EventType(Class<E> eventClass, String name) {
		this.eventClass = eventClass;
		this.name = name;
	}

	public Class<E> getEventClass() {
		return eventClass;
	}

	public void setEventClass(Class<E> eventClass) {
		this.eventClass = eventClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return (name != null) ? name : super.toString();
	}

	@Override
	public int hashCode() {
		return (eventClass != null && name != null) ? (eventClass.getName() + name).hashCode() : super.hashCode();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {

		if (obj != null && obj instanceof EventType) {
			EventType target = (EventType) obj;

			return (eventClass != null && name != null && eventClass.equals(target.getEventClass())
					&& name.equals(target.getName()));
		}

		return false;
	}
}
