package com.aug3.sys.properties;

import com.aug3.sys.CommonException;
import com.aug3.sys.cfg.ValueGetter;
import com.aug3.sys.cfg.ValueSet;
import com.aug3.sys.cfg.ValueSetLookupInfo;
import com.aug3.sys.log.MLogger;

/**
 * properties file getter
 * 
 * @author xial
 * 
 */
public class FilePropertiesGetter implements ValueGetter {

	private final static MLogger log = MLogger
			.getLog(FilePropertiesGetter.class);

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	/**
	 * Undefined method. Should not be called.
	 * 
	 * @throws UnsupportedOperationException
	 *             always.
	 */
	public ValueSet getValueSet(ValueSetLookupInfo li) {
		throw new UnsupportedOperationException("not implemented");
	}

	/**
	 * Returns the FileProperties instance indicated by <code>key</code>. A null
	 * value will be return if a new FileProperties is failed to initialize.
	 * 
	 * @param li
	 *            The descriptor with the company name
	 * @param key
	 *            The properties file name
	 * @return The FileProperties instance or null if failed to initialize.
	 */
	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		FileProperties fp = null;
		try {
			fp = new FileProperties(key);
		} catch (CommonException e) {
			log.warn(
					"Failed to get the FileProperties object for properties file "
							+ key + ". ", e);
		}
		return fp;
	}

	public Object getValue(String key) throws Exception {
		FileProperties fp = null;
		try {
			fp = new FileProperties(key);
		} catch (CommonException e) {
			log.warn(
					"Failed to get the FileProperties object for properties file "
							+ key + ". ", e);
		}
		return fp;
	}
}
