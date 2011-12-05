package com.aug3.sys.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date utility method
 * 
 * @author xial
 * 
 */
public class DateUtil {

	// # of milliseconds in a day
	public static final long kMilliSecPerDay = 1000 * 60 * 60 * 24;

	public static final String TIMESTAMP_PATTERN_CN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN_CN = "yyyy-MM-dd";

	private static final DateFormat DEFAULT_FORMATTER_CN = new SimpleDateFormat(
			TIMESTAMP_PATTERN_CN);

	private static DateFormat formatter = new SimpleDateFormat("''ddMMMyy''");
	private static DateFormat formatter2 = new SimpleDateFormat("\"ddMMMyy\"");
	private static DateFormat formatter3 = new SimpleDateFormat("MM/dd/yy");
	private static DateFormat formatter5 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy");

	/**
	 * Calculate # of days between supplied arguments
	 * 
	 * @param dteStart
	 *            1st date
	 * @param dteEnd
	 *            2nd date
	 * @return long # of days between the 2 supplied arguments
	 */
	public static long getDurationInDays(Date dteStart, Date dteEnd) {
		if (dteStart == null || dteEnd == null)
			return 0;

		return (long) Math.abs(Math.round((dteEnd.getTime() - dteStart
				.getTime()) / kMilliSecPerDay));
	}

	/**
	 * Returns a date from adding # of days to a supplied date
	 * 
	 * @param dteStart
	 *            starting date
	 * @param lDuration
	 *            # of days to add
	 * @return Date Result of adding # of days to a date
	 */
	public static Date determineEndDate(Date dteStart, long lDuration) {
		long lMilliSec = dteStart.getTime() + (kMilliSecPerDay * lDuration);
		return new Date(lMilliSec);
	}

	/**
	 * Parse a date from supported date formats. These include: ''ddMMMyy'',
	 * "ddMMMyy", MM/dd/yy, EEE MMM dd HH:mm:ss z yyyy
	 * 
	 * @param s
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parseDate(String s) throws ParseException {
		String value = s;
		if (!value.endsWith("d")) {

			if (value.charAt(2) == '/')
				return formatter3.parse(value);
			else
				return formatter5.parse(value);
		} else {
			value = value.substring(0, value.length() - 1);
			// attempt to parse as an Integer
			if (value != null) {
				if (value.startsWith("\"") && value.endsWith("\""))
					return formatter2.parse(value);
				else if (value.startsWith("'") && value.endsWith("'"))
					return formatter.parse(value);
				else
					throw new ParseException("incorrect format for Date(" + s
							+ "): correct format is 'ddMMMyy'd", 0);
			}
		}
		return null;
	}

	public static final Date parseDate(String strPattern, String strDate)
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(strPattern);
		return df.parse(strDate);
	}

	public static java.sql.Date toSqlDate(Timestamp ts) {
		if (ts == null)
			return (java.sql.Date) null;
		return new java.sql.Date(ts.getTime());
	}

	public static String getCurrentTime() {
		return DEFAULT_FORMATTER_CN.format(new Date());
	}

	public static String getCurrentTime(String timeFormatPattern) {
		if (StringUtil.isBlank(timeFormatPattern)) {
			timeFormatPattern = TIMESTAMP_PATTERN_CN;
		}
		DateFormat formatter = new SimpleDateFormat(timeFormatPattern);
		return formatter.format(new Date());
	}

	public static String formatCurrentDate() {
		DateFormat formatter = new SimpleDateFormat(DATE_PATTERN_CN);
		return formatter.format(new Date());
	}

	/**
	 * Get pre or next n days
	 * 
	 * @param n
	 *            positive for next, negative for pre
	 * @param format
	 *            0: 20100901 1: "2010-09-01" 2: Date
	 * @return
	 */
	public static Object getPreOrNextDays(int n, int format) {
		Calendar a = new GregorianCalendar();
		a.add(Calendar.DATE, n);
		int nYear = a.get(Calendar.YEAR);
		int nMonth = a.get(Calendar.MONTH) + 1;
		int nDay = a.get(Calendar.DATE);
		if (format == 0) {
			return nYear * 10000 + nMonth * 100 + nDay;
		} else if (format == 1) {
			return nYear + "-" + nMonth + "-" + nDay;
		} else {
			return a.getTime();
		}
	}

	public static Object getPreOrNextMonths(int n, int format) {
		Calendar a = new GregorianCalendar();
		a.add(Calendar.MONTH, n);
		int nYear = a.get(Calendar.YEAR);
		int nMonth = a.get(Calendar.MONTH) + 1;
		int nDay = a.get(Calendar.DATE);
		if (format == 0) {
			return nYear * 10000 + nMonth * 100 + nDay;
		} else if (format == 1) {
			return nYear + "-" + nMonth + "-" + nDay;
		} else {
			return a.getTime();
		}
	}

}
