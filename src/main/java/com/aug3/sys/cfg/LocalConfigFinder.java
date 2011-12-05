package com.aug3.sys.cfg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonException;
import com.aug3.sys.cache.memcached.MemCachedAdapter;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.properties.BootProperties;

/**
 * LocalConfigFinder is responsible for finding and electing default
 * configuration servers. This is done through a distributed cache, stored in
 * the <code>configinfo</code> field. All local configure finders that are part
 * of the same configure group (identified by the cfg.group.name boot property)
 * should share the instance of this variable.
 * 
 * If the group is named "none", this class will assume that it is the only
 * member of the group and instantiate local versions of <code>configinfo</code>
 * and the elector code to improve performance.
 * 
 * @author xial
 */
public class LocalConfigFinder {

	private static MLogger LOG = MLogger.getLog(LocalConfigFinder.class);
	private static LocalConfigFinder theSingleton;
	
	// ---------------------------------------------------------------------
	// CONSTANTS
	// ---------------------------------------------------------------------

	/** the boot property determining whether this is a server or not */
	private static final String IS_SERVER_PROP = "cfg.server";

	/** Default value for the IS_SERVER_GROUP property */
	private static final String IS_SERVER_DEFAULT = "false";

	/** The boot property defining the JGroup name to be used */
	public static final String CONFIG_GROUP_PROP = "cfg.group.name";

	public static final String SINGLETON_CONFIG_GROUP = "none";

	public static final String CACHE_KEY_CONFIG_INFO = "CACHE_KEY_CONFIG_INFO";

	private static final String NULL_VALUE = "null-value";

	/** election duration interval */
	private static final long ONE_SECOND = 1000;

	public static final String CONFIG_JGROUP_PROP = "cfg.jgroups.props";

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private Map<String, String> configInfo;
	private BootProperties bootProps = BootProperties.getInstance();
	private Elector elector;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS / FACTORY METHODS
	// ---------------------------------------------------------------------

	/**
	 * Retrieves the LocalConfigFinder singleton.
	 * 
	 * @return the config finder singleton
	 * @throws CommonException
	 *             if it cannot retrieve an instance.
	 */
	public synchronized static LocalConfigFinder getInstance()
			throws CommonException {
		if ((!AppSystem.inServer()) && (!ConfigConstants.FORCE_LOCAL_PROP)) {
			LOG.warn("Using ConfigPath in an improper (i.e, not a config server) environment.");
			LOG.warn("This is the stack trace for this call:");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
				builder.append(el.toString());
			}
			LOG.warn(builder.toString());

		}
		if (theSingleton == null) {
			theSingleton = new LocalConfigFinder();
		}
		return theSingleton;
	}

	/**
	 * Creates an instance of the LocalConfigFinder, backed by a JGroups
	 * distributed hashtable. The associated group is defined by the
	 * cfg.jgroup.name property in the boot properties file.
	 * 
	 * @throws CommonException
	 *             if it cannot create an instance.
	 */
	private LocalConfigFinder() throws CommonException {

		getConfigInfo();

		if (isServer()) {
			LOG.info("configured to be a config server");
			String groupName = bootProps.getProperty(CONFIG_GROUP_PROP);
			long waitPeriod = SINGLETON_CONFIG_GROUP.equals(groupName) ? 0
					: ONE_SECOND;
			elector = new ServerElector(configInfo, waitPeriod);
		} else {
			LOG.info("not configured to be a config server");
			elector = new ProxyElector(configInfo);
		}
		elector.register();
		updateConfigInfo();
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	/**
	 * Fetches the URL of the appserver that is the current ConfigServer. Calls
	 * for an election if no appserver is currently available.
	 * 
	 * @return the url of the config server.
	 */
	String getServerUrl() {
		String url = configInfo.get(Elector.CONFIG_URL_KEY);
		if ((url == null) || (url.equals(NULL_VALUE))) {
			url = elector.electNewServer();
			updateConfigInfo();
		}
		return url;
	}

	/**
	 * Resets the system, which will cause a new config server election when
	 * <code>getServerUrl</code> is next called.
	 */
	void reset() {
		configInfo.put(Elector.CONFIG_URL_KEY, NULL_VALUE);
		updateConfigInfo();
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Returns true if this appserver is configured to act as a config server.
	 * This is determined by the boot property cfg.server
	 * 
	 * @return true if this machine is configured to be a server.
	 */
	private boolean isServer() {
		String isServerProp = bootProps.getProperty(IS_SERVER_PROP,
				IS_SERVER_DEFAULT);
		return isServerProp.equalsIgnoreCase("true");
	}

	private boolean updateConfigInfo() {
		if (!SINGLETON_CONFIG_GROUP.equals(bootProps
				.getProperty(CONFIG_GROUP_PROP))) {
			MemCachedAdapter<Map<String, String>> distributedCache = new MemCachedAdapter<Map<String, String>>();
			distributedCache.put(CACHE_KEY_CONFIG_INFO, configInfo);
		}
		return true;
	}

	private Map<String, String> getConfigInfo() {
		if (SINGLETON_CONFIG_GROUP.equals(bootProps
				.getProperty(CONFIG_GROUP_PROP))) {
			return new ConcurrentHashMap<String, String>();
		} else {
			MemCachedAdapter<Map<String, String>> distributedCache = new MemCachedAdapter<Map<String, String>>();
			return distributedCache.get(CACHE_KEY_CONFIG_INFO) == null ? new ConcurrentHashMap<String, String>()
					: distributedCache.get(CACHE_KEY_CONFIG_INFO);
		}
	}

}
