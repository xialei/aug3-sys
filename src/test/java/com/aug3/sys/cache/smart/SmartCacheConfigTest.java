package com.aug3.sys.cache.smart;

import junit.framework.TestCase;

import com.aug3.sys.cache.LRUCache;
import com.aug3.sys.cache.MockICache;
import com.aug3.sys.cache.smart.NullReader;
import com.aug3.sys.cache.smart.SmartCacheConfig;

/**
 * Where we test that the SmartCacheConfig class is smart enough to get the
 * right configuration. It uses the smartcache.properties file in
 * ...../src/test/resources.
 * 
 * 
 */
public class SmartCacheConfigTest extends TestCase {

	private SmartCacheConfig<String> testee = new SmartCacheConfig<String>();

	public void testGetDefaultICache() {
		assertTrue(testee.getCache("undefined") instanceof LRUCache<?, ?>);
	}

	public void testGetDefaultReader() {
		assertTrue(testee.getReader("undefined") instanceof NullReader<?>);
	}

	public void testGetDefinedICache() {
	    //TODO: fix it
		//assertTrue(testee.getCache("defined") instanceof MockICache);
	}

	public void testGetDefinedIReader() {
		assertTrue(testee.getReader("defined") instanceof MockReader);

	}

}
