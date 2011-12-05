package com.aug3.sys.cfg;

import java.util.Map;

/**
 * CacheValueGetter is a decorator for ValueGetters that adds caching
 * functionality. The cache will also register itself for callbacks, so that it
 * will automatically flush stale data.
 * 
 * @author xial
 */
class CacheValueGetter implements ValueGetter {

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private Map<String, Object> valuesetCache;
	private ValueGetter reader;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	CacheValueGetter(ValueGetter reader) {
		this.valuesetCache = new MonitoringMap<String, Object>();
		this.reader = reader;
	}

	CacheValueGetter(ValueGetter reader, int cacheSize) {
		this.valuesetCache = new MonitoringMap<String, Object>(cacheSize);
		this.reader = reader;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	public synchronized ValueSet getValueSet(ValueSetLookupInfo li)
			throws Exception {
		String key = li.getKeyString();
		ValueSet values = (ValueSet) valuesetCache.get(key);
		if (values == null) {
			values = reader.getValueSet(li);
			valuesetCache.put(key, values);
		}
		return values;
	}

	public synchronized Object getValue(ValueSetLookupInfo li, String key)
			throws Exception {
		String cacheKey = li.getKeyString(key);
		Object value = valuesetCache.get(cacheKey);
		if (value == null) {
			value = reader.getValue(li, key);
			valuesetCache.put(cacheKey, value);
		}
		return value;
	}

	public void reset() {
		valuesetCache.clear();
	}
}
