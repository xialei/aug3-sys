package com.aug3.sys.util;

import java.math.BigDecimal;

/**
 * numeric utility class
 * 
 * @author xial
 */
public abstract class NumericOps {
	public static final double MAXIMUM_RELATIVE_DOUBLE_ERROR = 1e-8d;
	public static final double EPSILON_DOUBLE = Double.MIN_VALUE * 10.0d;

	/**
	 * Rounds the given double number to the specified number of decimal places.
	 * This uses the BigDecimal(double) constructor so it is less accurate than
	 * the String input version.
	 * 
	 * @param input
	 *            the number to round
	 * @param decimalPlaces
	 *            the number of decimal places to round to using the
	 *            conventional rule for rounding
	 * @return the rounded double number
	 */
	public static double roundDouble(double input, int decimalPlaces) {
		// ROUND_HALF_UP is the rounding method we learned in grade school.
		// ROUND_HALF_EVEN minimizes cumulative error when applied repeatedly
		// over a sequence of calculations.
		return roundDouble(new BigDecimal(input), decimalPlaces,
				BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Rounds the given number to the specified number of decimal places. This
	 * version is more accurate than the version that takes a double instead of
	 * string.
	 * 
	 * @param input
	 *            the number to round (must be a numerical string. Allows
	 *            '+','-' and exponent 'e'.)
	 * @param decimalPlaces
	 *            the number of decimal places to round to using the
	 *            conventional rule for rounding
	 * @return the rounded double number
	 */
	public static double roundDouble(String input, int decimalPlaces) {
		// ROUND_HALF_UP is the rounding method we learned in grade school.
		// ROUND_HALF_EVEN minimizes cumulative error when applied repeatedly
		// over a sequence of calculations.
		return roundDouble(new BigDecimal(input), decimalPlaces,
				BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Rounds the given number to the specified number of decimal places. This
	 * version is more accurate than the version that takes a double instead of
	 * string.
	 * 
	 * @param input
	 *            the number to round (must be a numerical string. Allows
	 *            '+','-' and exponent 'e'.)
	 * @param decimalPlaces
	 *            the number of decimal places to round to using the
	 *            conventional rule for rounding
	 * @return the rounded double number
	 */
	public static double roundDouble(String input, int decimalPlaces,
			int roundingMode) {
		return roundDouble(new BigDecimal(input), decimalPlaces, roundingMode);
	}

	/**
	 * Rounds the given number to the specified number of decimal places. This
	 * version is more accurate than the version that takes a double instead of
	 * string.
	 * 
	 * @param bigDec
	 *            BigDecimal the number to round
	 * @param decimalPlaces
	 *            the number of decimal places to round to using the
	 *            conventional rule for rounding
	 * @param roundingMode
	 *            int specifies rounding mode
	 * @see BigDecimal#ROUND_HALF_UP for example
	 * @return the rounded double number
	 */
	public static double roundDouble(BigDecimal bigDec, int decimalPlaces,
			int roundingMode) {
		// ROUND_HALF_UP is the rounding method we learned in grade school.
		// ROUND_HALF_EVEN minimizes cumulative error when applied repeatedly
		// over a sequence of calculations.
		return bigDec.setScale(decimalPlaces, roundingMode).doubleValue();
	}

	/**
	 * compare two double value, return true if these two double are 'equal'
	 * equal when : -- both are 'NaN' -- both represent exact same value. --
	 * both represent values within 'tolerance' range
	 * 
	 * from Lee Gordon, modified by rtam.
	 */
	public static boolean equals(double expected, double actual,
			double tolerance) {
		double error = 0.0d;
		if ((Double.isNaN(actual) && Double.isNaN(expected))
				|| (actual == expected)) {
			// Check easy cases that easily have error zero
		} else if (Double.isInfinite(expected) || Double.isInfinite(actual)) {
			// If either value is infinite and they aren't the same value
			// (previous check) then the error must be infinite
			error = Double.POSITIVE_INFINITY;
		} else {
			// Normal case of relative error
			error = Math.abs(expected - actual);
			if ((error > EPSILON_DOUBLE) && (expected > EPSILON_DOUBLE)) {
				error = error / expected;
			}
		}

		return error <= tolerance;
		// return error <= MAXIMUM_RELATIVE_DOUBLE_ERROR;
	}

	public static boolean equals(double expected, double actual) {
		return equals(expected, actual, MAXIMUM_RELATIVE_DOUBLE_ERROR);
	}

	/** Make the constructor private so it cannot be subclassed */
	private NumericOps() {
	}
}
