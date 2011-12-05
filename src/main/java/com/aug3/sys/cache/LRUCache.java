package com.aug3.sys.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A least-recently-used (LRU) cache map. This class is a map that stores up to
 * <em>CAPACITY</em> entries at a time. If one attempts to add an entry to the
 * map exceeding the cache's capacity, the least-recently-used entry is removed.
 * 
 * @author xial
 * 
 * @param <K> key of the cached object
 * @param <V> the cached object
 */
@SuppressWarnings("serial")
public class LRUCache<K, V> extends LinkedHashMap<K, V> implements ICache<K, V> {

	static final int DEFAULT_CAPACITY = 100;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private int capacity;

	public LRUCache() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public LRUCache(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public LRUCache(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, true);
		capacity = initialCapacity;

	}

	public void flush() {
		super.clear();
	}

	/**
	 * Determines whether old value should be removed or not. In this case, old
	 * values are removed if capacity has been exceeded.
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
}
