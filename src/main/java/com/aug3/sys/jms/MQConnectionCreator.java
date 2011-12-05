package com.aug3.sys.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

/**
 * This class is used to create connection for defined MQ. 
 * 
 * Currently, we use ActiveMQ as the provider and the connection factory pool is used.
 * 
 * @author xial
 * 
 */
public class MQConnectionCreator {

	private static PooledConnectionFactory pool_conn_factory;

	public static final String DEFAULT_URL = "tcp://16.173.244.242:61616";

	public static final String DEFAULT_USER = ActiveMQConnection.DEFAULT_USER;

	public static final String DEFAULT_PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

	static {
		initMQConnectionFactory();
	}

	public static Connection createConnection() throws JMSException {
		return pool_conn_factory.createConnection();
	}

	private static synchronized void initMQConnectionFactory() {

		pool_conn_factory = new PooledConnectionFactory();

		MQConfig config = new MQConfig();

		String url = config
				.getProperty(MQConfig.ACTIVEMQ_CONN_URL, DEFAULT_URL);

		String user = config.getProperty(MQConfig.ACTIVEMQ_CONN_USER,
				DEFAULT_USER);

		String passwd = config.getProperty(MQConfig.ACTIVEMQ_CONN_PASSWD,
				DEFAULT_PASSWORD);

		ActiveMQConnectionFactory activeMQConnFactory = new ActiveMQConnectionFactory(
				user, passwd, url);

		if (Boolean.TRUE.toString().equalsIgnoreCase(
				config.getProperty(MQConfig.ACTIVEMQ_POOL_USE_ASYNCSEND,
						"false"))) {
			activeMQConnFactory.setUseAsyncSend(true);
		}

		pool_conn_factory.setConnectionFactory(activeMQConnFactory);

		pool_conn_factory.setMaxConnections(config.getIntProperty(
				MQConfig.ACTIVEMQ_POOL_CONN_MAX, "10"));

		// Sets the maximum number of active sessions per connection
		pool_conn_factory.setMaximumActive(config.getIntProperty(
				MQConfig.ACTIVEMQ_POOL_ACTIVE_SESSION_MAX, "200"));

		pool_conn_factory.setIdleTimeout(config.getIntProperty(
				MQConfig.ACTIVEMQ_POOL_IDLETIMEOUT, "30000"));

	}

}
