package com.aug3.sys.jms;

import java.util.Properties;

import com.aug3.sys.properties.LazyPropLoader;

/**
 * This class loads and parses the configuration information for MQ.
 * Configuration info is fetched from the properties resource file
 * <code>mq.properties</code> which should be placed in the classpath.
 * 
 * 
 * @author xial
 */
public class MQConfig {

	// configuration properties
	private static final String CONFIG_RESOURCE = "/mq.properties";

	public static final String ACTIVEMQ_CONN_URL = "activemq.connection.url";
	public static final String ACTIVEMQ_CONN_USER = "activemq.connection.user";
	public static final String ACTIVEMQ_CONN_PASSWD = "activemq.connection.passwd";

	public static final String ACTIVEMQ_POOL = "activemq.pool";
	public static final String ACTIVEMQ_POOL_CONN_MAX = "activemq.pool.max.connection";
	public static final String ACTIVEMQ_POOL_ACTIVE_SESSION_MAX = "activemq.pool.max.active.session";
	public static final String ACTIVEMQ_POOL_IDLETIMEOUT = "activemq.pool.idletimeout";
	public static final String ACTIVEMQ_POOL_USE_ASYNCSEND = "activemq.pool.useAsyncSend";

	private Properties config = new LazyPropLoader(CONFIG_RESOURCE);

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public int getIntProperty(String key, String defaultValue) {
		return Integer.parseInt(getProperty(key, defaultValue));
	}

}
