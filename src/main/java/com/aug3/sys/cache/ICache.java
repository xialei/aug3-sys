package com.aug3.sys.cache;

/**
 * The base interface of all different cache implementations provided by this
 * framework. It is pretty much a limited form of a map.
 * 
 * @author xial
 * 
 * @param <K>  key of the cached object
 * @param <V>  the cached object
 */
public interface ICache<K, V> {

	V get(K key);

	V put(K key, V value);

	V remove(K key);

	boolean containsKey(K key);
	
	void flush();
}
