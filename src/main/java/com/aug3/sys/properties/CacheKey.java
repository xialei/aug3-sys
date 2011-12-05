package com.aug3.sys.properties;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import com.aug3.sys.util.StringUtil;

/**
 * The cache key for the props components. The key is represented as a 4-tuple
 * with the values defining the name space, the file basename(like appconfig),
 * level1 and level2(company/default/null) keys. The cache key is immutable.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
class CacheKey implements Serializable {

	private static final String NAMESPACE = PropConstants.PROPS_CACHE_KEY_NAMESPACE;

	private String[] keyvals;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	CacheKey(String fbasename, String level1, String level2) {
		assert fbasename != null;
		if (StringUtil.isBlank(level1) && StringUtil.isBlank(level2)) {
			throw new IllegalArgumentException(
					"Key of the cache can not be formatted, as level1 and level2 should not be empty at the same time!");
		}
		keyvals = new String[] { NAMESPACE, fbasename, level1, level2 };
	}

	/**
	 * 
	 * @param uri
	 *            a uri of the form "app-props:name:level1:level2", e.g.
	 *            "app-props:appconfig:companyA"
	 */
	CacheKey(String uri) {
		assert uri.startsWith(NAMESPACE);
		keyvals = uri.split(":");
		if (keyvals.length == 3) {
			keyvals = new String[] { keyvals[0], keyvals[1], null, keyvals[2] };
		}
	}

	// ---------------------------------------------------------------------
	// GENERAL METHODS
	// ---------------------------------------------------------------------

	public String getBasename() {
		return keyvals[1];
	}

	public String getLevel1() {
		return keyvals[2];
	}

	public String getLevel2() {
		return keyvals[3];
	}

	public String getUriFilePath() {
		StringBuilder path = new StringBuilder();
		path.append(getLevel1());
		path.append(File.separator);
		if (getLevel2() != null) {
			path.append(getLevel2());
			path.append(File.separator);
		}
		path.append(getBasename());
		path.append(".properties");
		return path.toString();
	}

	/**
	 * format cache key
	 * 
	 * namespace:filebasename:level1:level2
	 * 
	 * e.g. app-props:appconfig:companyA
	 */
	@Override
	public String toString() {
		StringBuilder uri = new StringBuilder();
		boolean first = true;
		for (String val : keyvals) {
			if (StringUtil.isBlank(val)) {
				continue;
			}
			if (!first) {
				uri.append(":");
			} else {
				first = false;
			}
			uri.append(val);
		}
		return uri.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CacheKey) {
			return equals((CacheKey) o);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(keyvals);
	}

	public boolean equals(CacheKey key) {
		return Arrays.equals(keyvals, key.keyvals);

	}
}
