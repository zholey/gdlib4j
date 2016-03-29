/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq;

import java.io.Serializable;

/**
 * 消息生产者接口
 * 
 * @author lei
 */
public interface IMQProducer {
	
	/**
	 * 发送对象消息至消息队列
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendMessage(Serializable message);
	
	/**
	 * 发送字节数组消息至消息队列
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendBytesMessage(byte[] message);
}
