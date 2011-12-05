package com.aug3.sys.properties;

import junit.framework.TestCase;

import com.aug3.sys.properties.LazyPropLoader;

/**
 * Tests the behavior of the LazyPropLoader class. Uses the class resource
 * "/loadertest.properties" for this test.
 * 
 * 
 */
public class LazyPropLoaderTest extends TestCase {

	// ---------------------------------------------------------------------
	// SETUP / FIXTURES
	// ---------------------------------------------------------------------

	private LazyPropLoader testee;

	@Override
	public void setUp() {
		testee = new LazyPropLoader("/loadertest.properties");
	}

	// ---------------------------------------------------------------------
	// UNIT TESTS
	// ---------------------------------------------------------------------

	/** checks that loading is indeed lazy */
	public void testLazyGet() {
		assertEquals(0, testee.size());
		testee.getProperty("somevalue");
		assertEquals(1, testee.size());
	}

	/** checks normal get */
	public void testGet() {
		assertEquals("boise", testee.getProperty("idaho.capital"));
	}

	/** checks default get */
	public void testGetWithDefault() {
		assertEquals("boise", testee.getProperty("idaho.capital", "Who cares?"));
		assertEquals("Who cares?",
				testee.getProperty("ohio.capital", "Who cares?"));
	}

}
