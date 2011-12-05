package com.aug3.sys.jms;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.TopicConnectionFactory;

/**
 * This module contains custom codes that are specific to the Active MQ
 * implementation.
 * 
 * @author xial
 */
public class ActiveMQ implements JMSProvider {
	/**
	 * @see com.aug3.sys.jms.JMSProvider#qualifyDestinationName(java.lang.String)
	 */
	public String qualifyDestinationName(String destName) {
		return destName;
	}

	public String getProviderName() {
		return "ACTIVEMQ";
	}

	/**
	 * Does nothing, as the Active JMS queue requires no special configuration.
	 */
	public void configure(QueueReceiver receiver) {
	}

	/**
	 * Does nothing, as the Active JMS QueueSender object requires no special
	 * configuration.
	 * 
	 */
	public void configure(MessageProducer sender) throws JMSException {
	}

	/**
	 * Does nothing, as the Active object requires no special configuration.
	 * 
	 */
	public void configure(QueueConnectionFactory factory) throws JMSException {
	}

	/**
	 * Does nothing, as the Active object requires no special configuration.
	 * 
	 */
	public void configure(TopicConnectionFactory factory) throws JMSException {
	}
}
