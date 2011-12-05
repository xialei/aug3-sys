package com.aug3.sys.cfg;

import com.aug3.sys.AppConstants;

/**
 * Constant definitions for the configuration management package.
 * 
 * @author xial
 */
public interface ConfigConstants {

	/** Configuration storage type, this is undefined */
	int STORAGE_TYPE_UNKNOWN = 0;
	/** Configuration storage type, the storage medium is file */
	int STORAGE_TYPE_FILE = 1;
	/** Configuration storage type, the storage medium is database */
	int STORAGE_TYPE_DB = 2;
	/** Configuration storage type, the storage medium is LDAP */
	int STORAGE_TYPE_LDAP = 3;

	/** cache key string separator */
	String KEY_SEP = "$";

	/**
	 * When registering callbacks for configuration update notification, clients
	 * can assign a priotity value. The callbacks registered with higher
	 * priority will gets invoked first. Callbacks registered with the same
	 * priorities are invoked with the order they are registered.
	 * <p>
	 */
	int UPDATE_NOTIFY_CALLBACK_PRIORITY_HIGH = 0;
	/**
	 * Callback priority value. Callbacks with this priority will get called
	 * after those with high priority.
	 * 
	 * @see #UPDATE_NOTIFY_CALLBACK_PRIORITY_HIGH
	 */
	int UPDATE_NOTIFY_CALLBACK_PRIORITY_NORMAL = 1;
	/**
	 * Callback priority value. Callbacks with this priority will get called
	 * last.
	 * 
	 * @see #UPDATE_NOTIFY_CALLBACK_PRIORITY_HIGH
	 */
	int UPDATE_NOTIFY_CALLBACK_PRIORITY_LOW = 2;

	// use to refresh the ValueSet.
	static final int RESET_CONFIG_ALL = 19101;

	// the number of API retry for config manager
	static final int DEFAULT_NUM_RETRY = 3;
	static final int MAX_NUM_RETRY = 5;
	static final int MIN_NUM_RETRY = 0;

	// the wait time before the next retry
	static final int DEFAULT_RETRY_WAIT_TIME = 5000; // mil seconds
	static final int MAX_RETRY_WAIT_TIME = 500; // mil seconds
	static final int MIN_RETRY_WAIT_TIME = 10000; // mil seconds

	// wait time for keep alive thread for file master slave controller
	static final int DEFAULT_KEEP_ALIVE_WAIT_TIME = 60 * 1000; // 1 min
	static final int MAX_KEEP_ALIVE_WAIT_TIME = 5000; // 5 second
	static final int MIN_KEEP_ALIVE_WAIT_TIME = 60 * 1000 * 10; // 10 min

	static final boolean FORCE_LOCAL_PROP = AppConstants.IS_LOCAL_CONFIG;

	// name of sharable path defined in temp, system or environment properties
	static String CONFIG_SHARE_PATH = "config.path";

	static final String CONFIG_RMI_PORT = "cfg.rmi.port";
	static final int CONFIG_RMI_PORT_DEFAULT = 1099;

	static final String CONFIG_CACHE_ENABLE = "cfg.cache.enable";
	static final String CONFIG_CACHE_SIZE = "cfg.cache.size";
	static final int CONFIG_DEFAULT_CACHE_SIZE = 20;

	// number of retry for config manager API
	static final String CONFIG_NUM_RETRY = "config.mum.retry";

	// wait time for retrying config manager API
	static final String CONFIG_RETRY_WAIT_TIME = "config.retry.wait.time";

	// wait time for keep alive thread for file master slave controller
	static final String CONFIG_KEEP_ALIVE_WAIT_TIME = "config.keep_alive.wait.time";

	static final String CONFIG_PROXY_SERVER_HOST = "proxy.ServerHost";

	static final String CONFIG_LOG4J_APPENDER_DEFAULT_MAX_MSG_LENGTH = "log4j.appender.Default.MaxMsgLength";

	static final String CONFIG_GLOBAL_APP_HOME = "config.global.app.home";

	static final String DEFAULT_COMPANY = AppConstants.DEFAULT_ORGANIZATION;
}
