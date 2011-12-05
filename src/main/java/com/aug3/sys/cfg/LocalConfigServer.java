package com.aug3.sys.cfg;

import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonException;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.properties.BootProperties;

/**
 * This implementation of the ConfigServer interface fetches and stores
 * configuration information that is in a locally accessible file system.
 * 
 * This class is a singleton, to minimize the amount of cached data kept around.
 * 
 * Note that this class is part of the configure server implementation and
 * should not be called outside of a Configure Server. That is, it should never
 * be called from within a proc/job_servers.
 * 
 * @author xial
 */
class LocalConfigServer implements ConfigServer {

	// The singleton
	private static ConfigServer configServer;

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	// the objects to which most of the work is delegated.
	private ValueGetter configReader;
	private ValueSetter configWriter;
	private ConfigManager fileReader = new ConfigManager();

	// ---------------------------------------------------------------------
	// CONSTRUCTORS AND FACTORY METHODS
	// ---------------------------------------------------------------------

	static synchronized ConfigServer getInstance() {

		if (AppSystem.inProc() && (!ConfigConstants.FORCE_LOCAL_PROP)) {
			throw new CommonRuntimeException(
					"Should not use LocalConfigServer inside a proc instance.");
		}

		if (configServer == null) {
			configServer = new LocalConfigServer();
		}
		return configServer;
	}

	/**
	 * Default constructor, uses values in boot properties to determine whether
	 * it needs a cache or not, and if so which size.
	 */
	private LocalConfigServer() {
		Boolean useCache = Boolean.valueOf(BootProperties.getInstance()
				.getProperty(ConfigConstants.CONFIG_CACHE_ENABLE));
		int cacheSize = BootProperties.getInstance().getProperty(
				ConfigConstants.CONFIG_CACHE_SIZE,
				ConfigConstants.CONFIG_DEFAULT_CACHE_SIZE);

		StorageAdapterProxy storage = new StorageAdapterProxy();
		if (useCache.booleanValue()) {
			configReader = new CacheValueGetter(storage, cacheSize);
		} else {
			configReader = storage;
		}
		configWriter = new NotifValueSetter(storage);
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	public String getTextFile(AppContext ctx, String filename) {
		return fileReader.getConfigFile(ctx, filename);
	}

	public Long getLastModified(AppContext ctx, String filename) {
		return fileReader.getLastModified(ctx, filename);
	}

	public Map<String, ConfigType> getConfigTypes() throws CommonException {
		return StorageAdapterProxy.getConfigTypes();
	}

	public void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		configWriter.setValueSet(li, vs);
	}

	public void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception {
		configWriter.setValue(li, key, val);
	}

	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception {
		return configReader.getValueSet(li);
	}

	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		return configReader.getValue(li, key);
	}

	/**
	 * If we have a cached value getter, flushes its cache, otherwise do
	 * nothing.
	 */
	public void reset() {
		if (configReader instanceof CacheValueGetter) {
			((CacheValueGetter) configReader).reset();
		}
	}
}
