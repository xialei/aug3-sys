package com.aug3.sys.cache.smart;

import com.aug3.sys.cache.ICache;
import com.aug3.sys.cache.LRUCache;

/**
 * SmartCaches are caches that know how to get data after a miss happens so the
 * user never has to worry about it. They do so by having an associated Reader
 * implementation. When a hit to the underlying cache returns null, the smart
 * cache will uses its associated reader to fetch the desired data. Cache
 * identifiers are used to associate the cache with the reader.
 * 
 * SmartCaches use a possibly user-defined underlying ICache implementation.
 * Behavior might vary depending on the class used. For example, if a global
 * (static) LRU cache is used, all SmartCache instances would share the same
 * actual cache. Look at the javadoc for all the ICache implementations.
 * 
 * SmartCache keys are always URI strings of the form scache:<em>domain</em>:
 * <em>type</em>[parameter list....]. The parameter part of the URI is passed,
 * as a string, to the underlying Reader to fetch the actual data. The parameter
 * names and values are, therefore, reader-dependent.
 * 
 * SmartCaches are configured through a class resource,
 * <code>smartcache.properties</code> which should be placed in the classpath.
 * This is a property file defining all the underlying ICache implementations
 * and Readers associated with each cache identifier.
 * 
 * For each cache identifier <em>cacheid</em>, the following properties can be
 * defined:
 * 
 * <table>
 * <tr>
 * <th>property</th>
 * <th>description</th>
 * <th>default value</th>
 * </tr>
 * <tr>
 * <td>scache.basecache.<em>cacheid</em></td>
 * <td>The class of the underlying cache to be used</td>
 * <td>com.hp.sys.cache.LRUCache</td>
 * </tr>
 * <tr>
 * <td>scache.reader.<em>cacheid</em></td>
 * <td>The reader associated with the cache. F.</td>
 * <td>com.hp.sys.cache.smart.NullReader</td>
 * </tr>
 * </table>
 * 
 * For example, suppose you want to have a SmartCache that fetches user data
 * from ldap. You want to use <code>com.roger.ldap.UserReader</code> as the Reader
 * for this class, and want the underlying cache to be a global StaticLRUCache.
 * You decide that the identifier for the ldap cache is "ldap.user" You then add
 * the following to the smartcache.properties file:
 * 
 * <code>
 * scache.basecache.ldap.user=com.hp.platform.cache.StaticLRUCache
 * scache.reader.ldap.user=com.roger.ldap.UserReader
 * </code>
 * 
 * In the code, when you create your cache you use the following snippet:
 * <verbatim> SmartCache<User> myUserCache = new SmartCache<User>("ldap.user");
 * </verbatim> and now you have a cache which will automatically fetch all users
 * for you.
 * 
 * @author xial
 */
public class SmartCache<V> implements ICache<String, V> {

	private final Reader<? extends V> reader;
	private final ICache<String, V> cache;

	public SmartCache(SmartCacheConfig<V> config, String cacheId) {
		cache = config.getCache(cacheId);
		reader = config.getReader(cacheId);
	}

	/**
	 * Constructs a SmartCache with the specified cache identifier. The cache
	 * identifier will be used to select the appropriate reader from the
	 * configuration file.
	 * 
	 * @param cacheId
	 *            a string used to identify which reader is associated with this
	 *            cache.
	 * 
	 */
	public SmartCache(String cacheId) {
		this(new SmartCacheConfig<V>(), cacheId);
	}

	public SmartCache(ICache<String, V> cacheImpl, Reader<? extends V> r) {
		cache = cacheImpl;
		reader = r;
	}

	public SmartCache(Reader<? extends V> r) {
		this(new LRUCache<String, V>(), r);
	}

	public boolean containsKey(String key) {
		return cache.containsKey(key);
	}

	public V get(String key) {
		V val = cache.get(key);
		if (val == null) {
			val = reader.fetch(key);
			cache.put(key, val);
		}
		return val;
	}

	public V put(String key, V value) {
		return cache.put(key, value);
	}

	public V remove(String key) {
		return cache.remove(key);
	}

	@Override
	public void flush() {
		cache.flush();

	}

}
