package com.aug3.sys.cfg;

import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.CommonException;

/**
 * Implementations of ConfigServer are classes that provide configuration
 * information for consumption by other system components
 * 
 * @author xial
 */
public interface ConfigServer extends ValueSetter, ValueGetter {

	/**
	 * Returns the contents of the file specified by the parameters as a String.
	 * 
	 * @param ctx
	 *            the company context for the configuration
	 * @param filename
	 *            the name of the file.
	 * @return the contents of the file, or null if no file exists.
	 */
	String getTextFile(AppContext ctx, String filename);

	/**
	 * Returns the time when the specified configure file was last modified.
	 * 
	 * @param ctx
	 *            the company context for the configuration.
	 * @param filename
	 *            the name of the file.
	 * @return the time when the file was last modified, or -1 if the file does
	 *         not exist.
	 */
	Long getLastModified(AppContext ctx, String filename);

	/**
	 * Returns a Map of configure types
	 */
	Map<String, ConfigType> getConfigTypes() throws CommonException;

	/**
	 * Resets the server, flushing its cache
	 */
	public void reset();
}