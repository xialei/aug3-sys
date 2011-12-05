package com.aug3.sys.jms;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.ServiceLocator;
import com.aug3.sys.ServiceLocatorException;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.properties.BootProperties;

/**
 * This is a class for retrieving JMS connections (both topic and queue) for use
 * by the system. Note that the methods are expensive. The class does no caching
 * and each call involves both a JNDI lookup and actual access to the
 * connection factory class.
 * 
 * However, the class does manage user/password issues when getting the
 * values. Namely, if the boot properties define <code>jms.user.</code>
 * <em>jndifactoryname</em> for a particular JNDI name, then it will attempt a
 * secure connection. If values are not provided, an anonymous connection will
 * be used.
 * 
 * @author xial
 */
public class JMSConnectionCreator {

	MLogger LOG = MLogger.getLog(JMSConnectionCreator.class);

	private static final String USER_PREFIX = "jms.user.";
	private static final String PASSWD_PREFIX = "jms.password.";

	private Context context;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	public JMSConnectionCreator() {
		this(AppSystem.getContext());
	}

	public JMSConnectionCreator(Context ctx) {
		context = ctx;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	/**
	 * Returns a new topic connection retrieved from the factory with the
	 * specified name.
	 * 
	 * @param factoryName
	 *            the JNDI name of the topic connection factory.
	 * @return a topic connection, for use by interested parties.
	 */
	public TopicConnection createTopicConnection(String factoryName)
			throws NamingException, JMSException {

		String username = BootProperties.getInstance().getProperty(
				USER_PREFIX + factoryName);
		String password = BootProperties.getInstance().getProperty(
				PASSWD_PREFIX + factoryName);

		TopicConnectionFactory topicFactory = (TopicConnectionFactory) context
				.lookup(factoryName);
		try {
			JMSProvider provider = ServiceLocator.getJMSProvider();
			provider.configure(topicFactory);
		} catch (ServiceLocatorException e) {
			throw new CommonRuntimeException(
					"Failed fetching the JMSProvider instance", e);
		}
		if (username != null) {
			return topicFactory.createTopicConnection(username, password);
		} else {
			return topicFactory.createTopicConnection();
		}
	}

	/**
	 * Returns a new queue connection retrieved from the factory with the
	 * specified name.
	 * 
	 * @param factoryName
	 *            the JNDI name of the topic connection factory.
	 * @return a queue connection, for use by interested parties.
	 * @throws JMSException
	 */
	public QueueConnection createQueueConnection(String factoryName)
			throws NamingException, JMSException {

		String username = BootProperties.getInstance().getProperty(
				USER_PREFIX + factoryName);
		String password = BootProperties.getInstance().getProperty(
				PASSWD_PREFIX + factoryName);

		QueueConnectionFactory factory = (QueueConnectionFactory) context
				.lookup(factoryName);
		try {
			JMSProvider provider = ServiceLocator.getJMSProvider();
			provider.configure(factory);
		} catch (ServiceLocatorException e) {
			throw new CommonRuntimeException(
					"Failed fetching the JMSProvider instance", e);
		}
		if (username != null) {
			return factory.createQueueConnection(username, password);
		} else {
			return factory.createQueueConnection();
		}
	}
}
