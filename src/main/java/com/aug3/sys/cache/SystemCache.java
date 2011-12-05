package com.aug3.sys.cache;

import java.util.Collections;
import java.util.Map;

import com.aug3.sys.properties.LazyPropLoader;

/**
 * SystemCache implements a system-wide/class-loader-level cache. Data stored in
 * one instance of this cache is available to all other SystemCache instances in
 * the system. 
 * 
 * It is a kind of LRUCache.
 * 
 * For example, the following code snippet:
 * 
 * <code>
 *   ICache<String,String> cache1 = new SystemCache<String,String>();
 *   ICache<String,String> cache2 = new SystemCache<String,String>();
 *   cache1.put("theKey","theValue");
 *   System.out.println("value is " + cache2.get("theKey"));
 * </code>
 * 
 * would print "value is theValue."
 * 
 * The cache can be configured through the file systemcache.properties, which
 * should be placed in the classpath. It defines the following properties:
 * <table>
 * <tr>
 * <th>property</th>
 * <th>description</th>
 * <th>default</th>
 * </tr>
 * <tr>
 * <td>cache.size</td>
 * <td>size of the cache</td>
 * <td>1000</td>
 * </tr>
 * </table>
 * 
 * @author xial
 */
public class SystemCache implements ICache<Object, Object> {

	private static final String CONFIG_RESOURCE = "/systemcache.properties";
	private static final String CACHE_SIZE = "cache.size";
	private static final String DEFAULT_CACHE_SIZE = "500";

	private static Map<Object, Object> theCache;

	static {
		LazyPropLoader props = new LazyPropLoader(CONFIG_RESOURCE);
		String size = props.getProperty(CACHE_SIZE, DEFAULT_CACHE_SIZE);
		Map<Object, Object> cache = new LRUCache<Object, Object>(
				Integer.parseInt(size));
		theCache = Collections.synchronizedMap(cache);
	}

	@Override
	public Object get(Object key) {
		return theCache.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		return theCache.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return theCache.remove(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return theCache.containsKey(key);
	}

	@Override
	public void flush() {
		theCache.clear();
		
	}

}
