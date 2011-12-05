package com.aug3.sys.cache;

import com.aug3.sys.cache.ICache;

public class MockICache implements ICache<String, String> {

	public boolean containsKey(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String get(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String put(String key, String value) {
		throw new UnsupportedOperationException("not implemented");
	}

	public String remove(String key) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException("not implemented");
	}

}
