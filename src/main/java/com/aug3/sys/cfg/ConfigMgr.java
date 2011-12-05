package com.aug3.sys.cfg;

import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonException;
import com.aug3.sys.rmi.IConfigMgr;

/**
 * 
 * provides remote access to the configuration server. It is an wrapper around
 * the LocalConfigServer class, mostly delegating calls to it.
 * 
 * 
 */
public class ConfigMgr implements IConfigMgr {

	private ConfigServer localServer;
	private String appserver = AppSystem.getHostFullName();

	public ConfigMgr() throws CommonException {
		localServer = LocalConfigServer.getInstance();
		try {
			UpdateNotifier.start();
		} catch (ConfigException e) {
			CommonException ex = new CommonException(
					"unable to start configserver's update component");
			ex.initCause(e);
			throw ex;
		}
	}

	// ---------------------------------------------------------------------
	// public methods
	// ---------------------------------------------------------------------
	/**
	 * Gets the ValueSet for the given lookup info
	 * 
	 * @param li
	 *            - the lookup info
	 * @return value set
	 * @throws Exception
	 * 
	 */
	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception {
		checkIsActiveServer();
		return localServer.getValueSet(li);
	}

	/**
	 * Gets the Value for the given lookup info and value name
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the configuration value key
	 * @return configuration value
	 * @throws Exception
	 * 
	 */
	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		checkIsActiveServer();
		return localServer.getValue(li, key);
	}

	/**
	 * Sets the ValueSet in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param vs
	 *            - value set
	 * @throws Exception
	 * 
	 */
	public void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		checkIsActiveServer();
		localServer.setValueSet(li, vs);
	}

	/**
	 * Sets the Value in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the configuration value key
	 * @param val
	 *            - the configuration value
	 * @throws Exception
	 * 
	 */
	public void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception {
		checkIsActiveServer();
		localServer.setValue(li, key, val);
	}

	/**
	 * Retrieves the contents of the configuration file specified. No cache for
	 * this file
	 * 
	 * @param ctx
	 *            the name of the company, use "defaultcompany.com" if you want
	 *            default values.
	 * @param filename
	 *            the name of the file you want.
	 * @return the contents of the file or null if no file is found.
	 * 
	 */
	public String getTextFile(AppContext ctx, String filename) throws Exception {
		checkIsActiveServer();
		return localServer.getTextFile(ctx, filename);

	}

	/**
	 * Retrieves the timestamp of the configuration file.
	 * 
	 */
	public Long getLastModified(AppContext ctx, String filename)
			throws Exception {
		checkIsActiveServer();
		return localServer.getLastModified(ctx, filename);
	}

	/**
	 * Retrieves a map of config types
	 * 
	 */
	public Map<String, ConfigType> getConfigTypes() throws Exception {
		checkIsActiveServer();
		return localServer.getConfigTypes();
	}

	/**
	 * Method used to check if access to the bean is working properly.
	 * 
	 */
	public boolean isAlive() {
		return true;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Checks that this bean is the actual running server by comparing its
	 * appserver field with the one returned by the ConfigFinder url code. If
	 * this is the active server, those values should be the same.
	 * 
	 * @throws CommonException
	 *             if this is not the active server.
	 */
	private void checkIsActiveServer() throws CommonException {
		LocalConfigFinder finder = LocalConfigFinder.getInstance();
		if (!appserver.equals(finder.getServerUrl())) {
			throw new CommonException(appserver
					+ " is not the active config server");
		}

	}
}
