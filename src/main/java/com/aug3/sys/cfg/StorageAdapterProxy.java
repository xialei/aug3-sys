package com.aug3.sys.cfg;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.aug3.sys.CommonException;
import com.aug3.sys.log.MLogger;

/**
 * This class figures out which <code>ValueGetter</code> and <code>ValueSetter
 * </code> instance to delegate for a ConfigType which is defined in 
 * ConfigType.xml. The delegations implement the
 * real thing to access the persistent storage of the configuration values.
 * 
 * @author xial
 */
class StorageAdapterProxy implements ValueGetter, ValueSetter {

	private final static MLogger log = MLogger
			.getLog(StorageAdapterProxy.class);

	// ==========================================================================
	// private members
	// ==========================================================================

	// HashMap to keep getter and setter
	private Map<String, Object> storageAdapters;

	// ==========================================================================
	// constructors
	// ==========================================================================
	/**
	 * constructor that takes the available system configuration types
	 */
	StorageAdapterProxy() {
		storageAdapters = new HashMap<String, Object>();
	}

	// ==========================================================================
	// interface implementation for ValueSetter and ValueGetter
	// ==========================================================================
	/**
	 * Return a ValueSet for the given Lookup info
	 * 
	 * @param li
	 *            - the lookup info
	 * @return ValueSet - The ValueSet value object
	 * @throws Exception
	 */
	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception {
		ValueGetter valueGetter = (ValueGetter) getValueGetterOrSetter(li, true);
		return valueGetter.getValueSet(li);
	}

	/**
	 * Return a single value
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the key for the configuration value
	 * @return Object - The value object that client interestd in
	 * @throws Exception
	 */
	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		ValueGetter valueGetter = (ValueGetter) getValueGetterOrSetter(li, true);
		return valueGetter.getValue(li, key);
	}

	/**
	 * Set the ValueSet in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param vs
	 *            - the value set, as <code>ValueSet</code> object
	 * @throws Exception
	 */
	public void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		ValueSetter valueSetter = (ValueSetter) getValueGetterOrSetter(li,
				false);
		valueSetter.setValueSet(li, vs);
	}

	/**
	 * Set the Value in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 * @throws Exception
	 */
	public void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception {
		ValueSetter valueSetter = (ValueSetter) getValueGetterOrSetter(li,
				false);
		valueSetter.setValue(li, key, val);
	}

	// ==========================================================================
	// package methods
	// ==========================================================================

	/**
	 * Return the set of ConfigTypes defined for the system.
	 * 
	 * @return Map - the set of ConfigTypes defined for the system.
	 * @throws CommonException
	 */
	static Map<String, ConfigType> getConfigTypes() throws CommonException {
		return parseConfigTypesXML();
	}

	/**
	 * Reset the Value in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param action
	 *            - the way to reset the cache - all, company or type
	 * @param localPatch
	 *            - true if local memory patch is done.
	 */
	void updateLocalDependency(ValueSetLookupInfo li, int action,
			boolean localPatch) {
		if (li == null || action == ConfigConstants.RESET_CONFIG_ALL) {
			resetAdapter();
		}

		// for configure server itself, there is no need to update the
		// dependency since we only keep default properties and
		// the memory patch is done already
		// if there is more than one server running (it should not happen).then
		// we destroy the whole storage adaptor
		if (!localPatch) {
			resetAdapter();
		}
	}

	// ==========================================================================
	// private methods
	// ==========================================================================

	/**
	 * Reset all the Setter or Getter
	 * 
	 */
	private synchronized void resetAdapter() {
		storageAdapters = new HashMap<String, Object>();
	}

	/**
	 * Return the ValueGetter or ValueSetter for the given ValueSetLookupInfo
	 * 
	 * @param li
	 *            - the lookup info
	 * @param getter
	 *            - a flag indicate to fetch a getter or setter
	 * 
	 * @return Object - The ValueSetter or ValueGetter
	 * 
	 * @throws Exception
	 */
	private Object getValueGetterOrSetter(ValueSetLookupInfo li, boolean getter)
			throws Exception {

		String hashKey = li.getConfigTypeName() + li.getStorageType() + getter;

		Object storageAdapter = storageAdapters.get(hashKey);

		// create ValueGetter
		if (storageAdapter == null) {
			ConfigType configType = getConfigTypes()
					.get(li.getConfigTypeName());

			// sanity check
			if (null == configType) {
				throw new ConfigException("configuration type ["
						+ li.getConfigTypeName() + "] is not configured");
			}

			// figure out the storage type
			int storageType = li.getStorageType();

			// if storage type is not defined in the lookup info, use the
			// default storage type defined for the configure type
			storageType = ConfigConstants.STORAGE_TYPE_UNKNOWN == storageType ? configType
					.getStorageType() : storageType;

			// parse the storage adapter implementation class name
			String storageAdapterClassName = null;
			if (getter) {
				// get the value getter implementation
				switch (storageType) {
				case ConfigConstants.STORAGE_TYPE_FILE:
					storageAdapterClassName = configType
							.getFileValueGetterClassName();
					break;
				case ConfigConstants.STORAGE_TYPE_DB:
					storageAdapterClassName = configType
							.getDbValueGetterClassName();
					break;
				case ConfigConstants.STORAGE_TYPE_LDAP:
					storageAdapterClassName = configType
							.getLdapValueGetterClassName();
					break;
				}
			} else {
				// get the value setter implementation
				switch (storageType) {
				case ConfigConstants.STORAGE_TYPE_FILE:
					storageAdapterClassName = configType
							.getFileValueSetterClassName();
					break;
				case ConfigConstants.STORAGE_TYPE_DB:
					storageAdapterClassName = configType
							.getDbValueSetterClassName();
					break;
				case ConfigConstants.STORAGE_TYPE_LDAP:
					storageAdapterClassName = configType
							.getLdapValueSetterClassName();
					break;
				}
			}

			// make sure the storage adapter implementation is defined
			if (storageAdapterClassName == null) {
				String msg = "No " + (getter ? "ValueGetter" : "ValueSetter")
						+ " for ConfigType '" + configType.getName()
						+ "' and Storage Medium " + storageType;
				log.error(msg);
				throw new CommonException(msg);
			}

			// instantiate and put in cache
			try {
				storageAdapter = Class.forName(storageAdapterClassName)
						.newInstance();
			} catch (Exception e) {
				throw new CommonException(
						"failed to load storage adapter class ["
								+ storageAdapterClassName + "]", e);
			}

			storageAdapters.put(hashKey, storageAdapter);
		}

		return storageAdapter;
	}

	/**
	 * Parses ConfigTypes.xml to get the set of ConfigTypes
	 * 
	 * @return Map - the set of ConfigTypes defined for the system.
	 * @throws CommonException
	 */
	private static Map<String, ConfigType> parseConfigTypesXML()
			throws CommonException {
		Map<String, ConfigType> configTypes = new HashMap<String, ConfigType>();

		// create an XML dom object with the file contents
		Document doc = ConfigDocument.get("ConfigTypes.xml", null, false);

		// get ConfigType elements
		NodeList nodes = doc.getDocumentElement().getElementsByTagName(
				"ConfigType");

		for (int i = 0; i < nodes.getLength(); i++) {
			Element configTypeElem = (Element) nodes.item(i);
			ConfigType configType = new ConfigType(configTypeElem);
			configTypes.put(configType.getName(), configType);
		}
		return configTypes;
	}

}
