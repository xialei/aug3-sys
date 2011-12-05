package com.aug3.sys.cache.memcached;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;

import com.aug3.sys.cache.memcached.MemCachedConfig;

/**
 * Tests the MemCachedConfig class test. Test uses the property file
 * memcached.properties in src/test/resources.
 * 
 * 
 */
@Ignore
public class MemCachedConfigTest {

	private MemCachedConfig config;

	@Before
	public void setUp() {
		config = new MemCachedConfig();
	}

	public void testGettingIntegers() {
		Assert.assertEquals(5, config.getMinConnections());
		Assert.assertEquals(50, config.getMaxConnections());
	}

	public void testGettingServerAndWeight() {
		String[] servers = config.getServers();
		Assert.assertEquals(1, servers.length);
		Assert.assertEquals("16.173.244.242:11211", servers[0]);

		Integer[] weights = config.getWeights();
		Assert.assertEquals(1, weights.length);
		Assert.assertEquals(1, (int) weights[0]);
	}

}
