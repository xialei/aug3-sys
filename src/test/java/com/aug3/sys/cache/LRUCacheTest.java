package com.aug3.sys.cache;

import com.aug3.sys.cache.ICache;
import com.aug3.sys.cache.LRUCache;

import junit.framework.TestCase;

/**
 * Tests for the LRU cache code
 * 
 * 
 */
public class LRUCacheTest extends TestCase {

	/** Checks to see if capacity is indeed respected. */
	public void testSizeLimit() {
		LRUCache<String, String> cache = new LRUCache<String, String>(3);
		cache.put("a", "one");
		cache.put("b", "two");
		cache.put("c", "three");
		assertEquals(3, cache.size());
		cache.put("d", "four");
		assertEquals(3, cache.size());
	}

	/** tests to see if the least recently used is indeed the one removed */
	public void testLRUremoval() {
		ICache<String, String> cache = new LRUCache<String, String>(3);
		cache.put("a", "one");
		cache.put("b", "two");
		cache.put("c", "three");
		cache.put("d", "four");
		assertNull(cache.get("a"));

		assertEquals("two", cache.get("b"));
		cache.put("e", "five");
		assertEquals("two", cache.get("b"));
		assertNull(cache.get("c"));
	}

}
