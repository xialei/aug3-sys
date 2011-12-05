package com.aug3.sys.properties;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aug3.sys.cache.ICache;
import com.aug3.sys.cache.smart.SmartCache;

/**
 * This class provides access to a two-tiered property system. Properties are
 * defined in up to three files and the data is appropriately combined, with
 * level2 properties overriding level1 properties (which in turns override the
 * default properties). This class manages all data caching so as to minimize
 * the reloading of data.
 * 
 * Property files should be stored in a directory structure reflecting the
 * tiered-model. Namely, give an base configuration directory $BASE_DIR, and a
 * property file name data will be looked for in the following order:
 * <ol>
 * <li>$BASE_DIR/<em>level1</em>/<em>level2<em>/appconfig.properties</li>
 * <li>$BASE_DIR/<em>level1</em>/appconfig.properties</li>
 * <li>$BASE_DIR/<em>level1</em>/default/appconfig.properties</li>
 * </ol>
 * 
 * <ol>
 * <li>$BASE_DIR/default/company01/appconfig.properties</li>
 * <li>$BASE_DIR/default/appconfig.properties</li>
 * <li>$BASE_DIR/default/default/appconfig.properties</li>
 * </ol>
 * 
 * The following properties should be defined in a file named
 * appconfig.properties and place in the ${classpath} of the application:
 * <table>
 * <tr>
 * <th>property</th>
 * <th>definition</th>
 * <th>default</th>
 * </tr>
 * <td>appconfig.props.basedir</td>
 * <td>The base directory to search for property files</td>
 * <td>The current directory</td>
 * </tr>
 * <tr>
 * <td>dtconfig.props.cache.class</td>
 * <td>the cache class to be used, either local or memcached</td>
 * <td>local</td>
 * </tr>
 * </table>
 * 
 * @author xial
 */
public class AppProp {

	private static final Logger LOG = Logger.getLogger(AppProp.class);

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private ICache<String, Properties> cache;
	private String name;
	private PropWriter writer;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	public AppProp(String appPropName) {
		this(appPropName, new ConfigProperties());
	}

	public AppProp(String appPropName, ConfigProperties config) {
		this(appPropName, config.getBasedir(), config.getCacheClassName());
	}

	/**
	 * Spring-friendly constructor
	 * 
	 * @param appPropName
	 *            the base name of the company properties file
	 * @param basedir
	 *            the base directory under which all level1/level2 files reside
	 * @param cacheType
	 *            the name of the cache class to be instantiated
	 */
	public AppProp(String appPropName, String basedir, String cacheType) {
		name = appPropName;
		ICache<String, Properties> underlyingCache = getCacheImpl(cacheType);
		cache = new SmartCache<Properties>(underlyingCache, new PropReader(
				basedir));
		writer = new PropWriter(basedir);
	}

	/**
	 * Test-friendly constructor, does not initalize the writer!
	 * 
	 * @param appPropName
	 * @param c
	 */
	AppProp(String appPropName, ICache<String, Properties> c) {
		name = appPropName;
		cache = c;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	/**
	 * @param level1
	 *            -- leave empty, or you can choose something like track
	 * @param level2
	 *            -- organization
	 * @param propName
	 *            -- the name of the property to get
	 */
	public String get(String level1, String level2, String propName) {
		validateNotNull("property name", propName);
		CacheKey key = new CacheKey(name, level1, level2);
		Properties propSet = cache.get(key.toString());
		return propSet.getProperty(propName);
	}

	/**
	 * Writes the value of the property into a file and flushes the cache to
	 * keep it coherent. This is an expensive operation as all values will be
	 * flushed from the cache and will need to be reloaded.
	 * 
	 * This operation will block if another process is attempting to set a
	 * property in the same file.
	 * 
	 * @param level1
	 *            leave empty, or you can choose something like track
	 * @param level2
	 *            organization
	 * @param propName
	 *            the name of the property being set
	 * @param value
	 *            the value of the property being set.
	 */
	public void put(String level1, String level2, String propName, String value) {
		validateNotNull("property name", propName);
		validateNotNull("value", value);
		CacheKey key = new CacheKey(name, level1, level2);
		writer.write(key, propName, value);
		cache.flush();
	}

	public void putAll(String level1, String level2, Map<String, String> vals) {
		validateNotNull("value map", vals);
		CacheKey key = new CacheKey(name, level1, level2);
		writer.writeAll(key, vals);
		cache.flush();
	}

	// ----------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------
	private ICache<String, Properties> getCacheImpl(String cacheClassName) {
		try {
			Class cacheClass = Class.forName(cacheClassName);
			return (ICache<String, Properties>) cacheClass.newInstance();
		} catch (Exception e) {
			LOG.error("Could not create cache " + cacheClassName
					+ ", please check the property "
					+ ConfigProperties.CACHE_CLASS
					+ " in the propcfg.properties file");
			throw new IllegalStateException(
					"problems creating underlying cache: " + e.getMessage(), e);
		}
	}

	private void validateNotNull(String msg, Object val) {
		if (val == null) {
			throw new IllegalArgumentException(msg + " cannot be null.");
		}
	}

}
