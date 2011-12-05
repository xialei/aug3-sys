package com.aug3.sys.cache.smart;

/**
 * The Reader interface. Implementations of Reader are used by the SmartCache to fetch the desired
 * data when there is a miss on the cache.
 * 
 * @author xial
 * 
 * @param <T>
 *            the type of object that the Reader fetches
 */
public interface Reader<T> {

	/**
	 * Fetches or creates the desired data corresponding to the uri.
	 * 
	 * @param uri
	 *            URI describing the data to be fetched.
	 * @return the object corresponding to the URI.
	 */
	T fetch(String uri);

}
