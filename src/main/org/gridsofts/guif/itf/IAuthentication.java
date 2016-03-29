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
public interface IAuthentication {

	/**
	 * 通过用户名和密码进行身份验证
	 * 
	 * @param userId
	 * @param passWd
	 * @return
	 */
	public boolean execute(String userId, String passWd);
	
	/**
	 * 登录成功，登录对话框关闭后执行的方法
	 */
	public void afterAuthenticated();
}
