package com.aug3.sys.cache.smart;

/**
 * A null reader always returns a null value for any URI it is asked to fetch.
 * 
 * @author xial
 * @param <T>
 */
public class NullReader<T> implements Reader<T> {

	public T fetch(String uri) {
		return null;
	}

}
