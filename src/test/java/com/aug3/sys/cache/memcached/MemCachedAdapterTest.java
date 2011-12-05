package com.aug3.sys.cache.memcached;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;

import com.aug3.sys.cache.memcached.MemCachedAdapter;

@Ignore
public class MemCachedAdapterTest {

	MemCachedAdapter<String> cache;

	@Before
	public void setUp() throws Exception {
		cache = new MemCachedAdapter<String>();
	}

	public void testCache() {
		cache.put("junit:test", "value");
		Assert.assertTrue(cache.containsKey("junit:test"));
		Assert.assertEquals("value", cache.get("junit:test"));
		cache.remove("junit:test");
		Assert.assertFalse(cache.containsKey("junit:test"));

	}

	public void testExpire() {
		cache.set("junit:test:expire", "value1", 10);
		Assert.assertTrue(cache.containsKey("junit:test:expire"));
		Assert.assertEquals("value1", cache.get("junit:test:expire"));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		cache.set("junit:test:expire", "value2");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
		Assert.assertEquals("value2", cache.get("junit:test:expire"));
	}

}
