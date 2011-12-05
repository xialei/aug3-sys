package com.aug3.sys;

import java.util.HashMap;
import java.util.Map;

import com.aug3.sys.jms.JMSProvider;
import com.aug3.sys.properties.BootProperties;

/**
 * This class returns a new instance of the requested service in this module.
 * 
 * Services include JMS, and they are requested by constants defined in this
 * class.
 * 
 * @author xial
 */
public class ServiceLocator {
	// ==========================================================================
	// service JNDI name constants
	// ==========================================================================
	private final static String PNAME_JMS_PROVIDER = "jms.provider";
	private final static String DEFAULT_JMS_PROVIDER = "activemq";
	private final static String PRODUCTION_JMS_PROVIDER = "activemq";

	private final static Map<String, String> JMS_PROVIDER_MAP = new HashMap<String, String>();

	// static initializer to set up the JMS providers map
	static {
		JMS_PROVIDER_MAP.put(DEFAULT_JMS_PROVIDER,
				com.aug3.sys.jms.ActiveMQ.class.getName());
		JMS_PROVIDER_MAP.put(PRODUCTION_JMS_PROVIDER,
				com.aug3.sys.jms.ActiveMQ.class.getName());
	}

	// ==========================================================================
	// private members
	// ==========================================================================
	private static JMSProvider jmsProvider;

	// ==========================================================================
	// public methods
	// ==========================================================================

	/**
	 * Given a generic queue name, quaify it based on the JMS server deployed
	 * with the system.
	 * <p>
	 * 
	 * @param name
	 *            - the unqualified JMS queue name
	 * @return qualified JMS queue name
	 * @throws ServiceLocatorException
	 */
	public static String qualifyQueueName(String name)
			throws ServiceLocatorException {
		return getJMSProvider().qualifyDestinationName(name);
	}

	/**
	 * Given a generic topic name, quaify it based on the JMS server deployed
	 * with the system.
	 * <p>
	 * 
	 * @param name
	 *            - the unqualified JMS topic name
	 * @return qualified JMS topic name
	 * @throws ServiceLocatorException
	 */
	public static String qualifyTopicName(String name)
			throws ServiceLocatorException {
		return getJMSProvider().qualifyDestinationName(name);
	}

	// figure out the JMS provider deployed with the system
	synchronized public static JMSProvider getJMSProvider()
			throws ServiceLocatorException {
		// check whether we already figured this out
		if (null != jmsProvider)
			return jmsProvider;

		// check the jms provider defined in the system properties
		String name = BootProperties.getInstance().getProperty(
				PNAME_JMS_PROVIDER, DEFAULT_JMS_PROVIDER);

		// look up the implementation for the jms provider
		String impl = (String) JMS_PROVIDER_MAP.get(name);

		try {
			return (JMSProvider) Class.forName(impl).newInstance();
		} catch (Exception e) {
			throw new ServiceLocatorException(
					"failed to initialize JMS provider with key [" + name
							+ "] and implementation [" + impl + "]");
		}
	}

}