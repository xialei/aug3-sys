package com.aug3.sys.cache;

import junit.framework.Assert;

import org.junit.Test;

import com.aug3.sys.cache.SystemCache;

public class SystemCacheTest {

	@Test
	public void testStoreInOneFetchfromAnother() {
		SystemCache cache1 = new SystemCache();
		SystemCache cache2 = new SystemCache();
		cache1.put("myKey", new Object());
		Assert.assertEquals(cache1.get("myKey"), cache2.get("myKey"));
	}

	public void testCacheSize() {
		SystemCache cache1 = new SystemCache();
		cache1.put("key1", new Object());
		cache1.put("key2", new Object());
		cache1.put("key3", new Object());
		Assert.assertFalse(cache1.containsKey("key1"));
		Assert.assertTrue(cache1.containsKey("key2"));
		Assert.assertTrue(cache1.containsKey("key3"));
	}

}
