package com.aug3.sys.properties;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import com.aug3.sys.CommonRuntimeException;

/**
 * This class is for starting up the server and the values in the source file
 * boot.properties is read only. This file fetches the properties file from a
 * path independent of the track.config.home in the properties file.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class BootProperties extends Properties {

	// properties entry for the track configuration home path name
	public final static String KEY_TRACK_CFG_HOME = "track.config.home";
	public final static String KEY_PROXY_HOST = "proxy.ServerHost";
	public final static String KEY_PARTITION_NAME = "appsvr.group.name";

	private static final String FILE_NAME = "boot.properties";
	private static BootProperties instance = null;

	/**
	 * Retrieve instance of this class.
	 * 
	 * @return
	 */
	public static synchronized BootProperties getInstance() {
		if (instance == null) {
			instance = new BootProperties();
		}
		return instance;
	}

	/**
	 * Private constructor for singleton implementation.
	 * 
	 */
	private BootProperties() {
		load();
	}

	public void reset() {
		load();
	}

	public synchronized void load() {
		String strPathName = getBasePath() + File.separator + FILE_NAME;
		FileInputStream is = null;
		try {
			is = new FileInputStream(strPathName);
			this.load(is);
		} catch (Exception e1) {
			// Existing clients don't catch or throw exceptions
			try {
				is = new FileInputStream(File.separator + FILE_NAME);
				this.load(is);
			} catch (Exception e2) {
				// Existing clients don't catch or throw exceptions
				throw new CommonRuntimeException(
						"Unable to initialize BootProperties.", e1);
			}
		} finally {
			try {
				is.close();
			} catch (Exception eIs) {
			}
		}
	}

	private String getBasePath() {
		return EnvProperties.getInstance().getAppHome();
	}

	/**
	 * Getter
	 * 
	 * @param key
	 * @param iNum
	 * @return
	 */
	public synchronized int getProperty(String key, int iNum) {
		return Integer.parseInt(getProperty(key, String.valueOf(iNum)));
	}

	/*
	 * getter
	 * 
	 * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
	 */
	public synchronized String getProperty(String key, String strDefault) {
		String strVal = getProperty(key);
		return (strVal == null ? strDefault : strVal);
	}

	public synchronized boolean getProperty(String key, boolean booDefault) {
		String strVal = getProperty(key);
		return (strVal == null ? booDefault : (strVal
				.equalsIgnoreCase(Boolean.TRUE.toString()) ? true : false));
	}

	/**
	 * Get a set of property keys.
	 * 
	 * @return Iterator
	 */
	public synchronized Iterator getKeys() {
		return this.keySet().iterator();
	}

	public synchronized String getTrackCfgHome() {
		String home = this.getProperty(KEY_TRACK_CFG_HOME);
		if (home.contains("@")) {
			home = getBasePath();
		}
		return home;
	}

	public synchronized String getProxyHost() {
		return this.getProperty(KEY_PROXY_HOST);
	}

}
