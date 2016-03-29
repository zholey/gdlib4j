/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.impl;

import org.gridsofts.guif.Application;
import org.gridsofts.guif.itf.IAuthentication;

/**
 * @author lei
 */
public abstract class AuthenticationImpl implements IAuthentication {

	@Override
	public void afterAuthenticated() {
		
		synchronized(Application._instance) {
			Application._instance.notifyAll();
		}
	}
}
