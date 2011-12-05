package com.aug3.sys.util;

import java.util.Random;

/**
 * Random utilities
 * 
 * @author xial
 *
 */
public class RandomUtil {

	/**
	 * Get random int from 0 : n
	 */
	public static int getRandom(int n) {
		Random random = new Random();
		return (random.nextInt() << 1 >>> 1) % n;
	}

	/**
	 * Get random int value from m .. n
	 */
	public static int getRandom(int m, int n) {
		if (m > n) {
			m = m + n;
			n = m - n;
			m = m - n;
		}
		return m + getRandom(n - m + 1);
	}

	/**
	 * Get randoms from given seed to ensure no repeat numbers.
	 * 
	 * @param seed
	 *            Should not contain repeat numbers. e.g. int[] seed = { 1, 2, 3, 4,
	 *            5, 6, 7, 8, 13, 11 };
	 * @return int[] e.g. [5, 2, 11, 4, 3, 8, 13, 6, 7, 1]
	 */
	public static int[] randomInSeed(int[] seed) {
		int[] randomArr = new int[seed.length];
		Random random = new Random();
		// 数量你可以自己定义。
		for (int i = 0; i < seed.length; i++) {
			// 得到一个位置
			int j = random.nextInt(seed.length - i);
			// 得到那个位置的数值
			randomArr[i] = seed[j];
			// 将最后一个未用的数字放到这里
			seed[j] = seed[seed.length - 1 - i];
		}
		return randomArr;
	}
}
