/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.fileSelectorClasses;

import java.io.File;

/**
 * 文件选择器事件监听
 * 
 * @author lei
 */
public interface IFileSelectorListener {

	/**
	 * 点击添加按钮，返回True允许添加，返回False取消动作
	 * 
	 * @return
	 */
	public boolean preAdd(int fileCount);

	/**
	 * 用户选择文件完成，添加至控件
	 * 
	 * @param files
	 */
	public void addFile(File[] files);

	/**
	 * 用户点击删除文件按钮，返回True则删除，返回False取消动作
	 * 
	 * @return
	 */
	public boolean preRemove(File file);

	/**
	 * 用户删除文件完成
	 * 
	 * @param file
	 */
	public void removeFile(File file);
}
