/**
 * 版权所有 ©2011-2020 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq.impl;

import java.io.Serializable;

import org.gridsofts.mq.IMQConsumer;
import org.gridsofts.mq.IMQProducer;
import org.gridsofts.mq.IMessageListener;
import org.gridsofts.mq.Message;
import org.gridsofts.util.BeanUtil;
import org.gridsofts.util.UUID;

/**
 * @author lei
 */
public class SimpleBridge implements IMQProducer, IMQConsumer {

	private static class SingletonHolder {
		private static SimpleBridge instance = new SimpleBridge();
	}

	public static SimpleBridge getInstance() {
		return SingletonHolder.instance;
	}

	private IMessageListener listener;

	private SimpleBridge() {
	}

	@Override
	public void setMessageListener(IMessageListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean sendMessage(Serializable msgObj) {
		return listener.onMessage(new Message(UUID.randomUUID32(), msgObj));
	}

	@Override
	public boolean sendBytesMessage(byte[] message) {
		return listener.onBytesMessage(BeanUtil.convertToBytes(new Message(UUID.randomUUID32(), message)));
	}
}
