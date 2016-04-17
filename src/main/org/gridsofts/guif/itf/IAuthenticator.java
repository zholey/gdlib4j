/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

/**
 * 提供身份验证的方法
 * 
 * @author lei
 */
public interface IAuthenticator {

	/**
	 * 身份验证
	 * 
	 * @param notifyObj
	 * @return
	 */
	public void execute(Object notifyObj);
	
	/**
	 * 登录成功，登录对话框关闭后执行的方法
	 */
	public <T extends ICredential> void afterAuthenticated(T credential);
}
