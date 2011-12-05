package com.aug3.sys.cfg;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.action.AbstractAction;
import com.aug3.sys.cache.LRUCache;

/**
 * A MonitoringMap is a LRU cache implementation that monitors a JMS topic to
 * determine whether to flush its data or not. That is, it registers itself as
 * an update callback and removes data when the associated key value has been
 * updated.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class MonitoringMap<K, V> extends LRUCache<K, V> {

	private static final int DEFAULT_SIZE = 20;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	public MonitoringMap() {
		this(DEFAULT_SIZE);
	}

	public MonitoringMap(int size) {
		super(size);
		UpdateNotifier.addCallback(new UpdateNotification(),
				new MapUpdateAction(),
				ConfigConstants.UPDATE_NOTIFY_CALLBACK_PRIORITY_NORMAL);
		try {
			UpdateNotifier.start();
		} catch (ConfigException e) {
			throw new CommonRuntimeException(
					"unable to start monitoring cache update listener", e);
		}
	}

	// testing purposes only, so we don't try to connect to the update notifier
	MonitoringMap(boolean dummy) {
	}

	// ----------------------------------------------------------------------
	// PUBLIC METHODS
	// ----------------------------------------------------------------------

	public synchronized V get(Object key) {
		return super.get(key);
	}

	public synchronized V put(K key, V value) {
		return super.put(key, value);
	}

	// ----------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------

	/**
	 * Removes all entries whose key starts with the prefix passed as a
	 * parameter. We do this so that when a value set is updated, all
	 * key-related entries are also removed.
	 */
	void removePrefixedEntries(String prefix) {
		Set<K> mapKeys = keySet();
		for (Iterator<K> it = mapKeys.iterator(); it.hasNext();) {
			String mapKey = (String) it.next();
			if (mapKey.startsWith(prefix)) {
				it.remove();
			}
		}
	}

	/**
	 * Clears the cache (or just the entry) for a particular message.
	 */
	synchronized void clearCache(UpdateNotification notification) {

		if (notification == null) {
			clear();
		} else if (notification.getCustomLongVal1() != UpdateNotification.SERVER_CHANGE_CODE) {
			String key = notification.getCustomStrVal1();
			if (key == null) {
				clear();
			} else {
				removePrefixedEntries(key);
			}
		}
	}

	// ---------------------------------------------------------------------
	// HELPER CLASSES
	// ---------------------------------------------------------------------

	private class MapUpdateAction extends AbstractAction {

		/**
		 * Removes the entry specified by the update notification from the map
		 * if it defined. Otherwise, clears the cache. The custom string value 1
		 * in the update notification is assumed to be the map key.
		 * 
		 * @param parms
		 *            a mpat with the update notification object.
		 * 
		 * @return null.
		 */
		public Object perform(Map parms) {
			UpdateNotification notification = (UpdateNotification) parms
					.get(UpdateNotifier_JMS.NOTIFICATION);
			clearCache(notification);
			return null;
		}
	}

}
