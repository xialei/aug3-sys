package com.aug3.sys.util;

import org.junit.Test;

import com.aug3.sys.util.DateUtil;


public class DateUtilTest {

	@Test
	public void TestGetPreNextNDays() {

		int nextDay = (Integer)DateUtil.getPreOrNextDays(-1, 0);
		System.out.println(nextDay);
	}
}
