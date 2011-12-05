package com.aug3.sys.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aug3.sys.cache.SystemCache;

/**
 * Reads and makes available to users the properties defined in the file
 * propcfg.properties which should be placed in the classpath. File is read when
 * class is loaded.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class ConfigProperties extends Properties {

	private static final Logger LOG = Logger.getLogger(ConfigProperties.class);

	private static final String CFG_RESOURCE = "/propcfg.properties";
	
	static final String CONFIG_DIR = "propcfg.props.basedir";
	static final String CACHE_CLASS = "propcfg.props.cache";

	private static final String DEFAULT_CACHE_CLASS = SystemCache.class
			.getCanonicalName();
	private static final String DEFAULT_CONFIG_DIR = ".";

	private static Properties cfgProps = new Properties();

	static {
		try {
			InputStream cfgFile = ConfigProperties.class
					.getResourceAsStream(CFG_RESOURCE);
			if (cfgFile == null) {
				LOG.info("No resource "
						+ CFG_RESOURCE
						+ " was found in the classpath. AppProp will use default configuration values");
			} else {
				try {
					cfgProps.load(cfgFile);
				} finally {
					cfgFile.close();
				}
			}
		} catch (IOException e) {
			LOG.warn("Could not open resource " + CFG_RESOURCE + " , Cause: "
					+ e.getMessage());
		}
	}

	// ---------------------------------------------------------------------
	// GETTER METHODS
	// ---------------------------------------------------------------------

	@Override
	public String getProperty(String propName) {
		return cfgProps.getProperty(propName);
	}

	@Override
	public String getProperty(String propName, String defaultValue) {
		return cfgProps.getProperty(propName, defaultValue);
	}

	@Override
	public Object get(Object key) {
		return cfgProps.get(key);
	}

	public String getBasedir() {
		return cfgProps.getProperty(CONFIG_DIR, DEFAULT_CONFIG_DIR);
	}

	public String getCacheClassName() {
		return cfgProps.getProperty(CACHE_CLASS, DEFAULT_CACHE_CLASS);
	}

}
