package com.aug3.sys.cache.smart;

import junit.framework.TestCase;

import com.aug3.sys.cache.LRUCache;
import com.aug3.sys.cache.smart.SmartCache;

/**
 * Tests the behavior of smart cache.
 * 
 * 
 */
public class SmartCacheTest extends TestCase {

	private static final String FETCHED_VALUE = "VALUE FETCHED";
	private static final String PUT_VALUE = "Some value";

	private static final String KEY = "dtcache:test:sample";

	private SmartCache<String> testee;

	@Override
	protected void setUp() {
		MockReader reader = new MockReader();
		reader.theFetchedValue = FETCHED_VALUE;
		testee = new SmartCache<String>(new LRUCache<String, String>(), reader);
	}

	public void testSaveAndGet() {
		testee.put(KEY, PUT_VALUE);
		assertEquals(PUT_VALUE, testee.get(KEY));
	}

	public void testFetching() {
		assertEquals(FETCHED_VALUE, testee.get(KEY));
	}

}
