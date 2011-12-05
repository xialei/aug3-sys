package com.aug3.sys.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.aug3.sys.util.CollectionsUtil;

public class CollectionsUtilTest {

	@Test
	public void TestSortMapByValue() {

		HashMap<String, Double> map = new HashMap<String, Double>();

		map.put("a", 1.35);
		map.put("b", 1.60);
		map.put("c", 1.00);
		map.put("c", 1.80);

		System.out.println(map);

		Map.Entry<String, Double>[] c = CollectionsUtil.sortMapByValue(map);

		org.junit.Assert.assertTrue(1.35 == c[2].getValue());
		org.junit.Assert.assertTrue(1.80 == c[0].getValue());
	}

}
