/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.event;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 事件派发器
 * 
 * @author lei
 */
@SuppressWarnings("rawtypes")
public class EventDispatcher implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<EventType, Set<EventListener>> listenerRegMap;
	private Lock listenerRegMapLock = new ReentrantLock();

	public EventDispatcher() {
		listenerRegMap = new HashMap<>();
	}

	/**
	 * 注册事件监听器
	 * 
	 * @param eventType
	 *            要注册的事件类型
	 * @param listener
	 *            事件监听器
	 */
	public <L extends EventListener> void addEventListener(EventType eventType, L listener) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.containsKey(eventType)) {
				listenerRegMap.put(eventType, new HashSet<>());
			}

			listenerRegMap.get(eventType).add(listener);
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 移除指定的监听器
	 * 
	 * @param listener
	 */
	public <L extends EventListener> void removeEventListener(L listener) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.keySet().forEach(key -> {

					if (listenerRegMap.get(key).contains(listener)) {
						listenerRegMap.get(key).remove(listener);
					}
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 移除所有注册了该事件类型的监听器
	 * 
	 * @param eventType
	 */
	public void removeEventListeners(EventType eventType) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.remove(eventType);
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 同步派发通知。
	 * 
	 * @param eventType
	 *            事件类型
	 */
	public void dispatchNotice(EventType eventType) {
		
		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).stream().filter(listener -> {
					return listener instanceof ActionListener;
				}).forEach(listener -> {
					dispatchAction((ActionListener) listener, null, false);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 同步派发通知。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param eventName
	 *            事件名称（即：方法名称）
	 */
	public void dispatchNotice(EventType eventType, String eventName) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).forEach(listener -> {
					dispatchEvent(listener, eventName, null, false);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}
	
	/**
	 * 异步派发通知。
	 * 
	 * @param eventType
	 *            事件类型
	 */
	public void asyncDispatchNotice(EventType eventType) {
		
		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).stream().filter(listener -> {
					return listener instanceof ActionListener;
				}).forEach(listener -> {
					dispatchAction((ActionListener) listener, null, true);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 异步派发通知。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param eventName
	 *            事件名称（即：方法名称）
	 */
	public void asyncDispatchNotice(EventType eventType, String eventName) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).forEach(listener -> {
					dispatchEvent(listener, eventName, null, true);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}
	
	/**
	 * 同步派发事件。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param event
	 *            要派发的事件。
	 */
	public void dispatchEvent(EventType eventType, Event event) {
		
		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).stream().filter(listener -> {
					return listener instanceof ActionListener;
				}).forEach(listener -> {
					dispatchAction((ActionListener) listener, event, false);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 同步派发事件。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param eventName
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 */
	public void dispatchEvent(EventType eventType, String eventName, Event event) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).forEach(listener -> {
					dispatchEvent(listener, eventName, event, false);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}
	
	/**
	 * 异步派发事件。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param event
	 *            要派发的事件。
	 */
	public void asyncDispatchEvent(EventType eventType, Event event) {
		
		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).stream().filter(listener -> {
					return listener instanceof ActionListener;
				}).forEach(listener -> {
					dispatchAction((ActionListener) listener, event, true);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}

	/**
	 * 异步派发事件。
	 * 
	 * @param eventType
	 *            事件类型
	 * @param eventName
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 */
	public void asyncDispatchEvent(EventType eventType, String eventName, Event event) {

		listenerRegMapLock.lock();
		try {
			if (!listenerRegMap.isEmpty()) {
				listenerRegMap.get(eventType).forEach(listener -> {
					dispatchEvent(listener, eventName, event, true);
				});
			}
		} finally {
			listenerRegMapLock.unlock();
		}
	}
	
	/**
	 * 向指定的收听者同步派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 */
	public void dispatchNotice(ActionListener listener) {
		dispatchAction(listener, null, false);
	}

	/**
	 * 向指定的收听者同步派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param eventName
	 *            事件名称（即：方法名称）
	 */
	public void dispatchNotice(EventListener listener, String eventName) {
		dispatchEvent(listener, eventName, null, false);
	}
	
	/**
	 * 向指定的收听者派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param isAsync
	 *            是否异步发送
	 */
	public void asyncDispatchAction(ActionListener listener) {
		dispatchAction(listener, null, true);
	}

	/**
	 * 向指定的收听者派发通知。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param eventName
	 *            事件名称（即：方法名称）
	 * @param isAsync
	 *            是否异步发送
	 */
	public void asyncDispatchNotice(EventListener listener, String eventName) {
		dispatchEvent(listener, eventName, null, true);
	}

	/**
	 * 向指定的收听者派发事件。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param eventName
	 *            事件名称（即：方法名称）
	 * @param event
	 *            要派发的事件。
	 * @param isAsync
	 *            是否异步发送
	 * 
	 */
	public void dispatchEvent(EventListener listener, String eventName, Event event, boolean isAsync) {

		if (isAsync) {

			// 异步派发事件
			new Thread(new Runnable() {

				@Override
				public synchronized void run() {
					dispatchEvent(listener, eventName, event, false);
				}
			}).start();

		} else {

			try {
				Class<? extends EventListener> listenerCls = listener.getClass();

				if (listenerCls != null) {
					Method method = null;

					if (event != null) {
						method = listenerCls.getMethod(eventName, event.getClass());

						if (method != null) {
							method.setAccessible(true);
							method.invoke(listener, event);
						}
					} else {
						method = listenerCls.getMethod(eventName);

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
	 * 向指定的收听者派发事件。
	 * 
	 * @param listener
	 *            指定的收听者
	 * @param event
	 *            要派发的事件。
	 * @param isAsync
	 *            是否异步发送
	 * 
	 */
	public void dispatchAction(ActionListener listener, Event event, boolean isAsync) {
		
		if (isAsync) {
			
			// 异步派发事件
			new Thread(new Runnable() {
				
				@Override
				public synchronized void run() {
					listener.actionPerformed(event);
				}
			}).start();
			
		} else {
			listener.actionPerformed(event);
		}
	}
}
