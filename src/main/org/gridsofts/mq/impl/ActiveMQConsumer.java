/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq.impl;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.gridsofts.mq.IMQConsumer;
import org.gridsofts.mq.IMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于ActiveMQ的消息消费者实现类
 * 
 * @author lei
 */
public class ActiveMQConsumer implements IMQConsumer, MessageListener {
	private Logger logger = LoggerFactory.getLogger(ActiveMQConsumer.class);

	private IMessageListener listener;

	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	private String brokerURL;

	private String queueName;
	private String topicName;

	public void setMessageListener(IMessageListener listener) {
		this.listener = listener;
	}

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public ActiveMQConsumer() {
	}

	/**
	 * 连接至MQ
	 * 
	 * @return
	 */
	public boolean start() {
		logger.debug("准备连接至消息队列[{}] ...", brokerURL);

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);

		try {
			connection = factory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

			// 获取消息目的地
			Destination dest = null;

			// P2P
			if (queueName != null) {
				dest = session.createQueue(queueName);
			}

			// pub/sub
			else if (topicName != null) {
				dest = session.createTopic(topicName);
			}

			consumer = session.createConsumer(dest);
			consumer.setMessageListener(this);

			logger.debug("连接至消息队列[{}] ... OK", brokerURL);
			logger.debug("监听目标消息地址[{}] ...", dest);

			return true;
		} catch (JMSException e) {
			logger.debug("无法连接至消息队列[{}]", brokerURL, e);

			try {
				if (connection != null) {
					connection.close();
				}
			} catch (JMSException e1) {
			}
		}

		return false;
	}

	/**
	 * 销毁连接
	 */
	public void destroy() {

		try {
			if (session != null) {
				session.close();
			}

			if (connection != null) {
				connection.close();
			}
		} catch (JMSException e) {
		}
	}

	public void onMessage(Message message) {

		if (message != null && message instanceof ObjectMessage) {

			try {
				if (listener.onMessage(((ObjectMessage) message).getObject())) {
					message.acknowledge();
				}
			} catch (JMSException e) {
				logger.error("收取MQ消息时出现异常", e);
			}
		}
		
		else if (message != null && message instanceof BytesMessage) {
			BytesMessage msg = (BytesMessage) message;

			try {
				// 创建一个缓冲区
				byte[] bytesMsg = new byte[(int) msg.getBodyLength()];
				
				// 从消息体中读取数据内容
				msg.readBytes(bytesMsg);
				
				if (listener.onBytesMessage(bytesMsg)) {
					message.acknowledge();
				}
			} catch (JMSException e) {
				logger.error("收取MQ消息时出现异常", e);
			}
		}
	}
}
