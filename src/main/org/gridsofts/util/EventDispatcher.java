/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

public class EventDispatcher<L extends EventListener, E extends EventObject> implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<L> listeners;

	public EventDispatcher() {
		listeners = new ArrayList<L>();
	}

	public synchronized void addEventListener(L listener) {

		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public synchronized void removeEventListener(L listener) {
		listeners.remove(listener);
	}
	
	/**
	 * 同步派发通知。
	 * 
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param isAsync
	 *            是否异步发送
	 */
	public synchronized void dispatchEvent(String name) {
		dispatchEvent(name, null, false);
	}

	/**
	 * 派发通知。
	 * 
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param isAsync
	 *            是否异步发送
	 */
	public synchronized void dispatchEvent(String name, boolean isAsync) {
		dispatchEvent(name, null, isAsync);
	}
	
	/**
	 * 同步派发事件。
	 * 
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 */
	public synchronized void dispatchEvent(String name, E event) {
		dispatchEvent(name, event, false);
	}

	/**
	 * 派发事件。
	 * 
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 * @param isAsync
	 *            是否异步发送
	 */
	public synchronized void dispatchEvent(String name, E event, boolean isAsync) {

		for (L listener : listeners) {
			dispatchEvent(listener, name, event, isAsync);
		}
	}

	/**
	 * 向指定的收听者同步派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param name
	 *            事件名称（即：方法名称）
	 */
	public synchronized void dispatchEvent(L listener, String name) {
		dispatchEvent(listener, name, null, false);
	}
	
	/**
	 * 向指定的收听者派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param isAsync
	 *            是否异步发送
	 */
	public synchronized void dispatchEvent(L listener, String name, boolean isAsync) {
		dispatchEvent(listener, name, null, isAsync);
	}
	
	/**
	 * 向指定的收听者同步派发事件。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 * 
	 */
	public synchronized void dispatchEvent(L listener, String name, E event) {
		dispatchEvent(listener, name, event, false);
	}

	/**
	 * 向指定的收听者派发事件。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param name
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 * @param isAsync
	 *            是否异步发送
	 * 
	 */
	public synchronized void dispatchEvent(L listener, String name, E event, boolean isAsync) {

		if (isAsync) {
			
			// 异步派发事件
			new AsyncEventDispatcher(listener, name, event).start();
			
		} else {
			
			try {
				Class<? extends EventListener> listenerCls = listener.getClass();

				if (listenerCls != null) {
					Method method = null;

					if (event != null) {

						method = listenerCls.getMethod(name, event.getClass());

						if (method != null) {
							method.setAccessible(true);
							method.invoke(listener, event);
						}
					} else {

						method = listenerCls.getMethod(name);

						if (method != null) {
							method.setAccessible(true);
							method.invoke(listener);
						}
					}
				}
			} catch (Throwable t) {
			}
		}
	}

	/**
	 * 异步事件派发器
	 * 
	 * @author Lei
	 * 
	 */
	class AsyncEventDispatcher extends Thread {

		private L listener;
		private String name;
		private E event;

		public AsyncEventDispatcher(L listener, String name, E event) {
			this.listener = listener;
			this.name = name;
			this.event = event;
		}

		@Override
		public synchronized void run() {
			dispatchEvent(listener, name, event);
		}
	}
}
