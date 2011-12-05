package com.aug3.sys.properties;

import java.util.Properties;

import com.aug3.sys.util.StringUtil;

/**
 * This properties class represents the current environment variables at the
 * time of creation.
 * 
 * NOTE: System.getenv() returns a properties map since java 1.5. We should
 * consider getting rid of it later.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class EnvProperties extends Properties {

	public final static String KEY_DEV_HOME = PropConstants.DEV_HOME;
	public final static String KEY_APP_HOME = PropConstants.APP_HOME;
	public final static String KEY_HOSTNAME = PropConstants.HOSTNAME;

	private static EnvProperties instance = null;

	/**
	 * @return singleton Properties instance with environment values.
	 */
	public static synchronized EnvProperties getInstance() {
		if (instance == null) {
			instance = new EnvProperties();
		}

		return instance;
	}

	private EnvProperties() {
		this.putAll(System.getenv());

		String buildPath = this.getClass().getClassLoader().getResource("/")
				.getPath();
		if (StringUtil.isBlank(getAppHome())) {
			this.setProperty(KEY_APP_HOME, buildPath);
		}
		if (StringUtil.isBlank(getDevHome())) {
			this.setProperty(KEY_DEV_HOME, buildPath);
		}
		if (StringUtil.isBlank(getHostName())) {
			this.setProperty(KEY_HOSTNAME, "localhost");
		}
	}

	public String getDevHome() {
		return getProperty(KEY_DEV_HOME);
	}

	public String getAppHome() {
		return getProperty(KEY_APP_HOME);
	}

	public String getHostName() {
		return getProperty(KEY_HOSTNAME);
	}

}
