/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.taglib.logic.forEachClasses;

import java.util.List;

/**
 * 用于说明某个标记可以生成被ForEach标记利用的结果集
 * 
 * @author Lei
 * 
 */
public interface IEachable {

	public List<?> getEachList(String name);
}
