package com.aug3.sys.jms;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.TopicConnectionFactory;

/**
 * This interface isolates codes that are dependent on different types of JMS
 * providers.
 * 
 * @author xial
 */
public interface JMSProvider {
	/**
	 * When creating JMS destinations, the application code will use generic
	 * names. This method will further qualify the name depends on how the real
	 * destination name is configured on the given JMS server.
	 * 
	 * @param destName
	 *            - JMS provider independent destination name
	 * @return qualified destination name with the deployed JMS server
	 */
	String qualifyDestinationName(String destName);

	/**
	 * This is useful in runtime scenarios where managed objects need to
	 * registered back in to their appropriate service-locations
	 * 
	 * @return String; //name of provider
	 */
	String getProviderName();

	/**
	 * This will apply implementation-specific configuration parameters to a
	 * particular queue receiver
	 */
	void configure(QueueReceiver receiver) throws JMSException;

	/**
	 * This will apply implementation-specific configuration parameters to a
	 * particular message producer.
	 */
	void configure(MessageProducer sender) throws JMSException;

	/**
	 * This will apply implementation-specific configuration parameters to a
	 * particular queue connection factory
	 */
	void configure(QueueConnectionFactory factory) throws JMSException;

	/**
	 * This will apply implementation-specific configuration parameters to a
	 * particular topic connection factory
	 */
	void configure(TopicConnectionFactory factory) throws JMSException;

}
