package com.aug3.sys.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * utility method for collections, such as sortMapByValue
 * 
 * @author xial
 *
 */
public class CollectionsUtil {

	@SuppressWarnings("unchecked")
	public static <K, V extends Comparable<V>> Map.Entry<K, V>[] sortMapByValue(Map<K, V> map) {
		Set<Map.Entry<K, V>> set = map.entrySet();

		Map.Entry<K, V>[] entries = set.toArray(new Map.Entry[set.size()]);

		Arrays.sort(entries, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Map.Entry<K, V> obj1 = (Map.Entry<K, V>) o1;
				Map.Entry<K, V> obj2 = (Map.Entry<K, V>) o2;
				return ((V) obj2.getValue()).compareTo((V) obj1.getValue());
			}
		});

		return entries;
	}
}
