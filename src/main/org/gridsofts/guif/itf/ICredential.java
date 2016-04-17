/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

/**
 * 登录成功后的凭据
 * 
 * @author lei
 */
public interface ICredential {

	/**
	 * 获取凭据ID
	 * 
	 * @return
	 */
	public String getCredentialId();

	/**
	 * 获取凭据名称
	 * 
	 * @return
	 */
	public String getCredentialName();
}
