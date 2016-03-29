/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.event;

/**
 * 通用的事件对象
 * 
 * @author lei
 */
public class EventObject<T> extends java.util.EventObject {
	private static final long serialVersionUID = 1L;
	
	private T payload;

	public EventObject(Object source, T payload) {
		super(source);
		
		this.payload = payload;
	}
	
	public T getPayload() {
		return payload;
	}
}
