package com.aug3.sys.properties;

import java.util.Properties;

import com.aug3.sys.AppContext;
import com.aug3.sys.log.MLogger;

/**
 * 
 * This is a utility class for retrieving properties in the system runtime
 * environment.
 * 
 * The properties come from four different sources:
 * <ul>
 * <li>the temporary properties users added within the current run of the
 * process. Note the values added are available from the entire VM.
 * <li>the virtual machine system properties
 * <li>the environment variables
 * <li>properties file system.properties (in the class path)
 * </ul>
 * 
 * <p>
 * A property is a named value. If a property is defined in more than one
 * sources, the precedence is determined by the order listed above.
 * <p>
 * 
 * The fourth source, the properties from system.properties, are retrieved
 * through the <b>SystemProp</b> component.
 * 
 * @author xial
 */
public class AppProperties {

	private static MLogger log = MLogger.getLog(AppProperties.class);

	private static boolean bInit = false;

	private static Properties tmpProperties = new Properties();
	private static Properties sysProperties = System.getProperties();
	private static Properties envProperties = EnvProperties.getInstance();
	private static Properties bootProperties = BootProperties.getInstance();

	private final static String EXPOSED_CFG_TYPE = PropConstants.APP_CONFIG_PROPERTIES;
	private static AppProp appProperties = new AppProp(EXPOSED_CFG_TYPE);

	private static Properties[] props = { tmpProperties, sysProperties,
			envProperties, bootProperties };

	public final static String KEY_PROXY_SERVER_HOST = BootProperties.KEY_PROXY_HOST;

	static {
		init();
	}

	private static synchronized void init() {
		if (bInit) {
			return;
		}

		if (null == tmpProperties)
			tmpProperties = new Properties();
		if (null == sysProperties)
			sysProperties = System.getProperties();
		if (null == envProperties)
			envProperties = EnvProperties.getInstance();
		if (null != bootProperties) {
			// Need to reload the boot.properties
			((BootProperties) bootProperties).reset();
		}

		bootProperties = BootProperties.getInstance();
		if (null == appProperties) {
			appProperties = new AppProp(EXPOSED_CFG_TYPE);
		}
		// Make sure this array is valid
		if (props == null) {
			props = new Properties[4];
			props[0] = tmpProperties;
			props[1] = sysProperties;
			props[2] = envProperties;
			props[3] = bootProperties;
		}

		bInit = true;

	}

	// ==========================================================================
	// public methods
	// ==========================================================================

	/**
	 * @return the system env.
	 */
	public static Properties getEnv() {
		return envProperties;
	}

	/**
	 * Returns the property value with the given property name.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @return the value of the key, or null if the key doesn't exist
	 */
	public static String getProperty(String key) {
		return getProperty(new AppContext(), key);
	}

	public static String getProperty(AppContext ctx, String key) {

		String prop = getPredefinedProperty(key);
		if (prop != null) {
			return prop;
		}
		// If its not defined, return the exposed value regardless of whether or
		// not its defined.
		return getPersistedValue(ctx, key);
	}

	public static String getPredefinedProperty(String key) {
		// hack, to work around a class loading problem. if this method is
		// invoked by another class's static initializer, the props variable
		// is not initialized
		init();
		// check whether the property exists in the properties, never go to file
		for (int i = 0; i < props.length; i++) {
			String prop = props[i].getProperty(key);
			if (null != prop)
				return prop;
		}
		return null;
	}

	public static synchronized void setBootProperty(String key, String val) {
		bootProperties.setProperty(key, val);
	}

	/**
	 * Fetches the value from the properties file (as opposed to from temp,
	 * system or boot properties). Loads the file data if necessary.
	 * 
	 * @param ctx
	 *            the context to use
	 * @param key
	 *            the property name
	 * @return String the property value or null if no property is available.
	 */
	private static String getPersistedValue(AppContext ctx, String key) {
		try {
			String track = "";
			String org = ctx.getOrganization();
			return appProperties.get(track, org, key);
		} catch (Exception e) {
			log.warn(ctx, MLogger.TOPIC_CONFIGURATION, 1004,
					"Failed to get property [" + key + "] for " + ctx, e);
			return null;
		}
	}

	/**
	 * Returns the property value with the given property name, with a default.
	 * If the property cannot be found, the provided default values is returned.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @param defaultValue
	 *            the value to return if the key doesn't exist
	 * @return the value of the key, or the default value
	 */
	public static String getProperty(String key, String defaultValue) {
		return getProperty(new AppContext(), key, defaultValue);
	}

	public static String getProperty(AppContext ctx, String key,
			String defaultValue) {
		String prop = getProperty(ctx, key);

		return null == prop ? defaultValue : prop;
	}

	/**
	 * Returns the property value with the given property name, as a boolean.
	 * Non-parseable or non-existent entries default to false
	 * 
	 * @param key
	 *            the key to get the value of
	 * @return property value as a boolean, or false, if non-parseable
	 */
	public static boolean getBoolean(String key) {
		return getBoolean(new AppContext(), key, false);
	}

	public static boolean getBoolean(AppContext ctx, String key) {
		return getBoolean(ctx, key, false);
	}

	/**
	 * Returns the property value with the given property name, as a boolean.
	 * Non-parseable or non-existent entries return the default value
	 * 
	 * @param key
	 *            the key to get the value of
	 * @param dft
	 *            the value to return if the key doesn't exist
	 * @return property value as a boolean, or the default value
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(new AppContext(), key, defaultValue);
	}

	public static boolean getBoolean(AppContext ctx, String key,
			boolean defaultValue) {
		String prop = getProperty(ctx, key);

		if (null == prop) {
			return defaultValue;
		} else {
			return (Boolean.valueOf(prop)).booleanValue();
		}
	}

	/**
	 * Returns the property value with the given property name, as a long.
	 * Non-parseable or non-existent entries default to 0.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @return property value as a long, or 0, if non-parseable
	 */
	public static long getLong(String key) {
		return getLong(new AppContext(), key);
	}

	public static long getLong(AppContext ctx, String key) {
		return getLong(ctx, key, 0);
	}

	/**
	 * Returns the property value with the given property name, as a long, with
	 * a given default value. Non-parseable or non-existent entries return the
	 * default value.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @param defaultValue
	 *            the value to return if the key doesn't exist
	 * @return property value as a long, or the default, if non-parseable
	 */
	public static long getLong(String key, long defaultValue) {
		return getLong(new AppContext(), key, defaultValue);
	}

	public static long getLong(AppContext ctx, String key, long dft) {
		String prop = getProperty(ctx, key);

		if (null == prop) {
			return dft;
		} else {
			return Long.parseLong(prop);
		}
	}

	/**
	 * Returns the property value with the given property name, as an int.
	 * Non-parseable or non-existent entries default to 0.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @return property value as an int, or 0, if non-parseable
	 */
	public static int getInt(String key) {
		return getInt(new AppContext(), key);
	}

	public static int getInt(AppContext ctx, String key) {
		return getInt(ctx, key, 0);
	}

	/**
	 * Returns the property value with the given property name, as an int, with
	 * a given default value. Non-parseable or non-existent entries return the
	 * default value.
	 * 
	 * @param key
	 *            the key to get the value of
	 * @param defaultValue
	 *            the value to return if the key doesn't exist
	 * @return property value as an int, or the default, if non-parseable
	 */
	public static int getInt(String key, int defaultValue) {
		return getInt(new AppContext(), key, defaultValue);
	}

	public static int getInt(AppContext ctx, String key, int dft) {
		String prop = getProperty(ctx, key);

		if (null == prop) {
			return dft;
		} else {
			return Integer.parseInt(prop);
		}
	}

	/**
	 * Sets a property value to the given property name. The value is stored as
	 * a temporary value. It will not rewrite the environment variables.
	 * 
	 * @param key
	 *            the key to set the value of
	 * @param val
	 *            the value to set it to
	 */
	public static void setProperty(String key, String val) {
		tmpProperties.setProperty(key, val);
	}

	/**
	 * Removes the entry from the associated temporary map. The value is not
	 * removed from the backing platform.properties file.
	 * 
	 * @param key
	 *            the key of the entry being removed.
	 */
	public static void removeTemp(String key) {
		tmpProperties.remove(key);
	}

}
