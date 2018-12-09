/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import org.gridsofts.event.EventDispatcher;

/**
 * @author lei
 */
public class EvtDispatcher extends EventDispatcher {
	private static final long serialVersionUID = 1L;

	private EvtDispatcher() {
	}
	
	private static class SingletonHolder {
		private static EvtDispatcher instance = new EvtDispatcher();
	}
	
	public static EvtDispatcher getInstance() {
		return SingletonHolder.instance;
	}
}
