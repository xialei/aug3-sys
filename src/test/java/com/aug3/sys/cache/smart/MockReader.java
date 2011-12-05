package com.aug3.sys.cache.smart;

import com.aug3.sys.cache.smart.Reader;


public class MockReader implements Reader<String> {

	String theFetchedValue;

	public String fetch(String uri) {
		return theFetchedValue;
	}

}
