/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq;

/**
 * 消息消费者接口
 * 
 * @author lei
 */
public interface IMQConsumer {

	/**
	 * 绑定消息监听
	 * 
	 * @param listener
	 */
	public void setMessageListener(IMessageListener listener);
}
