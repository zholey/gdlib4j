/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.EventListener;

import org.gridsofts.util.EventObject;

/**
 * 提供数据变动的公共接口
 * 
 * @author lei
 */
public interface IDataChangedListener extends EventListener {

	/**
	 * 数据更新通知
	 * 
	 * @param event
	 */
	public void onDataChanged(EventObject<?> event);
}
