/**
 * 版权所有 ©2011-2020 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq.impl;

import java.io.Serializable;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.gridsofts.mq.IMQProducer;
import org.gridsofts.util.BeanUtil;
import org.gridsofts.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Kafka消息总线的生产者实现类
 * <p>
 * Created by lei on 2016/12/6.
 */
public class KfkProducer implements IMQProducer, Serializable {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(KfkProducer.class);

	private Producer<java.lang.String, byte[]> producer;
	protected Properties kfkConfig;

	protected java.lang.String servers;
	protected java.lang.String topicName;

	public java.lang.String getServers() {
		return servers;
	}

	public void setServers(java.lang.String servers) {
		this.servers = servers;
	}

	public java.lang.String getTopicName() {
		return topicName;
	}

	public void setTopicName(java.lang.String topicName) {
		this.topicName = topicName;
	}

	public boolean start() {

		logger.debug("准备连接至 Kafka Cluster [{}] ...", servers);

		try {
			kfkConfig = new Properties();
			kfkConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
			kfkConfig.put(ProducerConfig.ACKS_CONFIG, "all");
			kfkConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
			kfkConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
			kfkConfig.put(ProducerConfig.LINGER_MS_CONFIG, 1);
			kfkConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
			kfkConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringSerializer");

			// 初始化二进制消息发送器
			kfkConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.ByteArraySerializer");
			producer = new KafkaProducer<>(kfkConfig);

			logger.debug("连接至 Kafka Cluster [{}] ... OK", servers);

			return true;
		} catch (Throwable e) {
			logger.debug("无法连接至 Kafka Cluster [{}]", servers, e);

			return false;
		}
	}

	/**
	 * 销毁连接
	 */
	public void destroy() {

		try {
			if (producer != null) {
				producer.close();
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public boolean sendMessage(Serializable message) {

		try {
			if (message == null) {
				return true;
			}
			
			if (message instanceof byte[]) {
				return sendBytesMessage((byte[]) message);
			} else {
				return sendBytesMessage(BeanUtil.convertToBytes(message));
			}
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public boolean sendBytesMessage(byte[] message) {

		try {
			if (message == null) {
				return true;
			}
			
			producer.send(new ProducerRecord<>(topicName, UUID.randomUUID(), message));
			producer.flush();
			
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
}
