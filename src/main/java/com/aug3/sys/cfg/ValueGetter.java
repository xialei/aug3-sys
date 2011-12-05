package com.aug3.sys.cfg;

/**
 * This is the interface for retrieving configuration values. 
 * 
 * @see ValueSetter
 */
public interface ValueGetter {
	/**
	 * Returns a ValueSet for the given Lookup info
	 * 
	 * @param li
	 *            - the lookup info
	 * @throws Exception
	 */
	ValueSet getValueSet(ValueSetLookupInfo li) throws Exception;

	/**
	 * Returns the individual value for the given name. Depending on the
	 * LookupInfo's ConfigType, all the values for that ConfigType might be
	 * loaded into memory, but only one value is returned to the caller. This
	 * method returns the same value as:
	 * <p> 
	 * ValueSet vs = getValueSet(lookupInfo);<br>
	 * Object value = vs.getValue(valueName);
	 * <p>
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the key for the configuration value
	 * @throws Exception
	 */
	Object getValue(ValueSetLookupInfo li, String key) throws Exception;
}