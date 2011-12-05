package com.aug3.sys.log;

import java.util.Properties;

import com.aug3.sys.properties.LazyPropLoader;

/**
 * 
 * @author xial
 * 
 */
public class LogConfig {

	// configuration properties
	private static final String CONFIG_RESOURCE = "/logconfig.properties";

	private Properties config = new LazyPropLoader(CONFIG_RESOURCE);

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public int getIntProperty(String key, String defaultValue) {
		return Integer.parseInt(getProperty(key, defaultValue));
	}

}
