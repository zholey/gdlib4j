/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq.impl;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.gridsofts.mq.IMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于ActiveMQ的消息生产者实现类
 * 
 * @author lei
 */
public class ActiveMQProducer implements IMQProducer {
	private Logger logger = LoggerFactory.getLogger(ActiveMQProducer.class);

	private Connection connection;
	private Session session;
	private MessageProducer producer;

	private String brokerURL;

	private String queueName;
	private String topicName;

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public ActiveMQProducer() {
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

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
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
			
			producer = session.createProducer(dest);

			logger.debug("连接至消息队列[{}] ... OK", brokerURL);

			return true;
		} catch (JMSException e) {
			logger.debug("无法连接至消息队列[{}]", brokerURL, e);

			try {
				connection.close();
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

	/**
	 * 发送消息
	 */
	public boolean sendMessage(Serializable message) {

		try {
			producer.send(session.createObjectMessage(message));
			
			logger.debug("SENDING [{}] => [{}] ...", message, producer.getDestination());
			
			return true;
		} catch (JMSException e) {
			logger.error("无法正常发送MQ消息", e);
		}

		return false;
	}

	/**
	 * 发送消息
	 */
	public boolean sendBytesMessage(byte[] message) {

		try {
			// 创建数组消息对象
			BytesMessage msg = session.createBytesMessage();
			
			// 使用传入的数组值填充消息体
			msg.writeBytes(message);
			
			// 发送消息
			producer.send(msg);
			
			logger.debug("SENDING [{}] => [{}] ...", message, producer.getDestination());
			
			return true;
		} catch (JMSException e) {
			logger.error("无法正常发送MQ消息", e);
		}

		return false;
	}
}
