package com.aug3.sys.cache.smart;

import java.util.Properties;

import com.aug3.sys.cache.ICache;
import com.aug3.sys.cache.LRUCache;
import com.aug3.sys.properties.LazyPropLoader;

/**
 * SmartCache configure contains the configuration information found in the class
 * resource <code>smartcache.properties</code>. It uses an underlying
 * class-level configure map so that we only load the data once.
 * 
 * @author xial
 * @param <V>
 */
public class SmartCacheConfig<V> {

	private static final String CONFIG_RESOURCE = "/smartcache.properties";
	private static final String BASE_CACHE_PREFIX = "scache.basecache.";
	private static final String BASE_READER_PREFIX = "scache.reader.";
	private static final String DEFAULT_CACHE = LRUCache.class
			.getCanonicalName();
	private static final String DEFAULT_READER = NullReader.class
			.getCanonicalName();

	/**
	 * the config information from <code>smartcache.properties</code>
	 */
	private static final Properties defaultConfig = new LazyPropLoader(
			CONFIG_RESOURCE);

	private Properties config;

	public SmartCacheConfig() {
		this(defaultConfig);
	}

	public SmartCacheConfig(Properties props) {
		config = props;
	}

	public ICache<String, V> getCache(String cacheId) {
		String cacheClassName = config.getProperty(BASE_CACHE_PREFIX + cacheId,
				DEFAULT_CACHE);
		return (ICache<String, V>) getInstance(cacheClassName);
	}

	public Reader<V> getReader(String cacheId) {
		String readerClassName = config.getProperty(BASE_READER_PREFIX
				+ cacheId, DEFAULT_READER);
		return (Reader<V>) getInstance(readerClassName);
	}

	private Object getInstance(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("failed creating instance for class "
					+ className, e);
		}

	}

}
