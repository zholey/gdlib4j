/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

/**
 * @author lei
 */
public interface IWindowListener {

	/**
	 * 窗口即将关闭前触发此事件
	 * 
	 * @return 返回True，正常关闭；返回False，不做任何处理
	 */
	public boolean windowClosing();
}
