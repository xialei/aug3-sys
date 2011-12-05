package com.aug3.sys.rmi;

import java.rmi.Remote;
import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.cfg.ConfigType;
import com.aug3.sys.cfg.ValueSet;
import com.aug3.sys.cfg.ValueSetLookupInfo;

/**
 * 
 * provides remote access to the configuration server. It is an wrapper around
 * the LocalConfigServer class, mostly delegating calls to it.
 * 
 * stub
 * 
 */
public interface IConfigMgr extends Remote {

	public static final String SERVICE_NAME = "configMgr";

	/**
	 * Gets the ValueSet for the given lookup info
	 * 
	 * @param li
	 *            - the lookup info
	 * @return value set
	 * @throws Exception
	 * 
	 */
	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception;

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
	public Object getValue(ValueSetLookupInfo li, String key) throws Exception;

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
			throws Exception;

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
			throws Exception;

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
	public String getTextFile(AppContext ctx, String filename) throws Exception;

	/**
	 * Retrieves the timestamp of the configuration file.
	 * 
	 */
	public Long getLastModified(AppContext ctx, String filename)
			throws Exception;

	/**
	 * Retrieves a map of config types
	 * 
	 */
	public Map<String, ConfigType> getConfigTypes() throws Exception;

	/**
	 * Method used to check if access to the bean is working properly.
	 * 
	 */
	public boolean isAlive();

}
