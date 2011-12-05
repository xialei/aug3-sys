package com.aug3.sys.util;

public class IPUtil {

	/**
	 * change ip address to long e.g. 58.14.0.0 --> 58*256*256*256 + 14*256*256
	 * + 0*256 + 0 -1 = 973996031
	 * 
	 * @param ipAddress
	 * @return
	 */
	public static long ip2long(String ipAddress) {
		if (StringUtil.isBlank(ipAddress)) {
			throw new IllegalArgumentException(
					"income ip address is blank. xxxx.xxxx.xxxx.xxxx");
		}
		String[] nums = ipAddress.split("\\.");
		if (nums.length != 4) {
			throw new IllegalArgumentException(
					"income ip address is invalid. xxxx.xxxx.xxxx.xxxx");
		}
		return Integer.parseInt(nums[0]) * 256 * 256 * 256
				+ Integer.parseInt(nums[1]) * 256 * 256
				+ Integer.parseInt(nums[2]) * 256 + Integer.parseInt(nums[3])
				- 1;

	}

	public static boolean checkIPArea(String ip, String startIP, String endIP) {

		long ip1 = ip2long(ip);
		long ip2 = ip2long(startIP);
		long ip3 = ip2long(endIP);

		return ip1 >= ip2 && ip1 <= ip3;

	}

}
