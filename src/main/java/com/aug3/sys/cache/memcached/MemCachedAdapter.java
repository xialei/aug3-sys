package com.aug3.sys.cache.memcached;

import java.util.Date;

import com.aug3.sys.cache.ICache;
import com.danga.MemCached.MemCachedClient;

/**
 * This adapter allows one to use an memcached client within the ICache
 * framework, especially as the base cache in a <code>SmartCache</code>
 * instance.
 * 
 * Memcached is a distributed cache that allows data to be shared by several
 * different applications in different machines within the same network. Placing
 * a value in one instance of the cache makes it visible in <em>all</em>
 * instances of the cache.
 * 
 * Our MemCached requires the following to work:
 * <ol>
 * <li>java_memcached-release_2.0.1.jar must be in the class_path</li>
 * <li>log4j.jar must be in the class_path</li>
 * <li>the configuration file memcached.properties</li> must be in the
 * class_path</li>
 * </ol>
 * 
 * For information on creating and using configuration files
 * 
 * @see MemCachedConfig
 * 
 * @author xial
 * 
 * @param <V>
 */
public class MemCachedAdapter<V> implements ICache<String, V> {

	static {
		MemCachedPoolMgr poolMgr = new MemCachedPoolMgr();
		poolMgr.reset();
	}

	private static MemCachedClient cache = new MemCachedClient();

	public MemCachedAdapter() {

		// enable compress
		// compress if data exceed 64k
		cache.setCompressEnable(true);
		cache.setCompressThreshold(64 * 1024);
	}

	@Override
	public V get(String key) {
		synchronized (cache) {
			return (V) cache.get(key);
		}
	}

	@Override
	public V put(String key, V value) {
		synchronized (cache) {
			V o = (V) cache.get(key);
			cache.set(key, value);
			return o;
		}
	}

	@Override
	public V remove(String key) {
		synchronized (cache) {
			V o = (V) cache.get(key);
			cache.delete(key);
			return o;
		}
	}

	@Override
	public boolean containsKey(String key) {
		synchronized (cache) {
			return cache.keyExists(key);
		}
	}

	public boolean set(String key, V value, long expireSeconds) {
		synchronized (cache) {
			return cache.set(key, value, new Date(System.currentTimeMillis()
					+ expireSeconds * 1000));
		}
	}

	public boolean set(String key, V value) {
		synchronized (cache) {
			return cache.set(key, value);
		}
	}

	public MemCachedClient getCacheClient() {
		return cache;
	}

	@Override
	public void flush() {
		synchronized (cache) {
			cache.flushAll();
		}

	}

}
