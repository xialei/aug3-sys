package com.aug3.sys.cfg;

/**
 * This is the interface for setting configuration values. 
 * 
 * @see ValueGetter
 */
public interface ValueSetter {
	/**
	 * Sets the ValueSet in storage.
	 * 
	 * @param li
	 *            - the lookup info
	 * @param vs
	 *            - the value set, as <code>ValueSet</code> object
	 * @throws Exception
	 */
	void setValueSet(ValueSetLookupInfo li, ValueSet vs) throws Exception;

	/**
	 * Sets the Value in storage
	 * 
	 * @param li
	 *            - the lookup info
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 * @throws Exception
	 */
	void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception;
}