/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif.itf;

import java.util.List;

/**
 * 提供数据的抽象服务接口
 * 
 * @author lei
 */
public interface IDataProvider<T> {

	/**
	 * 获取数据
	 * 
	 * @return
	 */
	public List<? extends T> listData();

	/**
	 * 注册数据更新事件监听
	 * 
	 * @param listener
	 */
	public void addEventListener(IDataChangedListener listener);
}
