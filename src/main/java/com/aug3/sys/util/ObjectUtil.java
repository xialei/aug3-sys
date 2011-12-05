package com.aug3.sys.util;

/**
 * utility method for object
 * 
 * @author xial
 *
 */
public class ObjectUtil {

	public static <V> V getDefaultValueIfNull(V value, V default_value) {

		return value == null ? default_value : value;

	}

}
