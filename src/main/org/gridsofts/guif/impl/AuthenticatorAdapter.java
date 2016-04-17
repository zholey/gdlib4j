/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.impl;

import org.gridsofts.guif.Application;
import org.gridsofts.guif.itf.IAuthenticator;
import org.gridsofts.guif.itf.ICredential;

/**
 * @author lei
 */
public abstract class AuthenticatorAdapter implements IAuthenticator {
	
	private Object notifyObj;

	@Override
	public void execute(Object notifyObj) {
		this.notifyObj = notifyObj == null ? Application._instance : notifyObj;
	}

	@Override
	public <T extends ICredential> void afterAuthenticated(T credential) {
		
		synchronized(notifyObj) {
			notifyObj.notifyAll();
		}
	}
}
