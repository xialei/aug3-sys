package com.aug3.sys.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * datetime and numeric formatting
 * 
 * @author xial
 */
public abstract class Formatter {
	// public date format types
	public static final String DATE_SHORT = "date.short";
	public static final String DATE_MEDIUM = "date.medium";
	public static final String DATE_LONG = "date.long";
	public static final String DATE_WEEKDAY_MED = "date.weekday.medium";
	public static final String TIME_SHORT = "time.short";
	public static final String DATETIME_MEDIUM = "datetime.medium";
	public static final String DATETIME_SHORT = "datetime.short";

	// private date format types
	// date format for parsing date values in import files
	private static final String DATE_IMPORT = "date.import";
	// short date format for the 3.2 release, for example: 02-Apr-03
	private static final String DATE_SHORT_I18N_32 = "date.short.i18n.32";
	// short datetime format for the 3.2 release, for example: 02-Apr-03 1:23 PM
	private static final String DATETIME_SHORT_I18N_32 = "datetime.short.i18n.32";

	private static final String DATE_MEDIUM_I18N_32 = "date.medium.i18n.32";

	/**
	 * This date format is produced by JavaScript editor in a case of inline
	 * editing absolute date (date that should be the same regardless of the
	 * time zone and locale) in the grid. This format is for INTERNAL USE ONLY
	 * and is not displayed anywhere in the UI
	 */
	private static final String ABSOLUTE_DATE_PATTERN = "yyyy-MM-dd";
	private static final String ABSOLUTE_DATE_FMT = "date.absolute";

	// month and year format style

	// number format types
	public static final String NUMBER_I18N_DISPLAY = "number.i18n.display";
	public static final String NUMBER_I18N_EDIT = "number.i18n.edit";
	public static final String NUMBER_COST = "number.cost";
	public static final String NUMBER_PRICE = "number.price";
	public static final String NUMBER_PERCENT = "number.percent";
	public static final String NUMBER_PERCENT_FLOOR = "number.percent.floor";
	public static final String NUMBER_PERCENT_CEILING = "number.percent.ceiling";
	public static final String NUMBER_PERCENT_ONE = "number.percent.one";
	public static final String NUMBER_PERCENT_NONE = "number.percent.none";
	public static final String NUMBER_FLOAT = "number.float"; // use for
																// floating
																// points
	public static final String NUMBER_FLOAT_TWO = "number.float2"; // use for 2
																	// decimal
																	// place
																	// float
																	// number
	public static final String NUMBER_FLOAT_THREE = "number.float3"; // use for
																		// 2
																		// decimal
																		// place
																		// float
																		// number
	public static final String NUMBER_FLOAT_TWO_FLOOR = "number.float2.floor"; // use
																				// for
																				// 2
																				// decimal
																				// place
																				// float
																				// number
																				// with
																				// floor
																				// rounding
	public static final String NUMBER_FLOAT_TWO_CEILING = "number.float2.ceiling"; // use
																					// for
																					// 2
																					// decimal
																					// place
																					// float
																					// number
																					// with
																					// floor
																					// rounding
	public static final String NUMBER_INT = "number.integer"; // use for
																// integers and
																// longs
	public static final String NUMBER_ID = "number.id"; // use for displayed
														// database ids, not
														// grouping separators
	public static final String NUMBER_PROFIT = "number.profit"; // use for price
																// without dp
	public static final String NUMBER_VOLUME = "number.volume"; // use for
																// volume float
																// without dp
	public static final String NUMBER_INDEX = "number.index"; // use for index
																// (cpi) float
																// with 1 dp

	// private number format for parsing
	private static final String NUMBER_PARSE = "number.parse"; // for parsing
																// all numbers

	public static final int NAN = -2123456789;
	public static final String NAN_STRING = "-2123456789";
	public static final String NOT_APPLICABLE = "--";

	// number of decimal places for each number format
	static final int COST_DP = 3;
	static final int PRICE_DP = 2;
	// the first 2 digits of percent value are before the decimal point
	static final int PERCENT_DP = 4;
	static final int FLOAT_DP = 2;
	static final int PROFIT_DP = 0;
	static final int VOLUME_DP = 0;
	static final int INDEX_DP = 3;

	// array of all date formats
	private static final String[] dateParseFormats = { // try the longer formats
			// first
			DATETIME_MEDIUM, DATETIME_SHORT_I18N_32, DATETIME_SHORT,
			DATE_WEEKDAY_MED, DATE_LONG, DATE_MEDIUM_I18N_32, DATE_MEDIUM,
			DATE_SHORT_I18N_32, DATE_SHORT, TIME_SHORT, ABSOLUTE_DATE_FMT };

	/**
	 * Formats the given date in the specified format type and locale and time
	 * zone.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param tz
	 *            the time zone to use for formatting; null means use default
	 * @param type
	 *            the date format type to use; must not be null
	 * @param date
	 *            the date object to format; must not be null
	 * @return the formatted string representing the given date
	 */
	public static String formatDate(Locale l, TimeZone tz, String type,
			Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Null date given to formatDate");
		}

		if (DATE_SHORT.equals(type)) {
			// for 3.2 release, all short dates are formatted in the special
			// format
			type = DATE_SHORT_I18N_32;
		} else if (DATETIME_SHORT.equals(type)) {
			// for 3.2 release, all short datetimes are formatted in the special
			// format
			type = DATETIME_SHORT_I18N_32;
		} else if ((DATE_MEDIUM.equals(type)) && (tz == null)) {
			type = DATE_MEDIUM_I18N_32;
		}
		DateFormat df = getDateFormatter(l, tz, type);
		return df.format(date);
	}

	/**
	 * Formats the given GMT date in the specified format type and locale and
	 * time zone.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param tz
	 *            the time zone to use for formatting; null means use default
	 * @param type
	 *            the date format type to use; must not be null
	 * @param date
	 *            the date object, in GMT timezone, to format; must not be null
	 * @return the formatted string representing the given date
	 */
	public static String formatGMTDate(Locale l, TimeZone tz, String type,
			Date date) {
		if (date == null) {
			throw new IllegalArgumentException(
					"Null date given to formatGMTDate");
		}
		TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
		Calendar localCal = Calendar.getInstance(tz, l);
		Calendar gmtCal = Calendar.getInstance(gmtTZ);

		gmtCal.setTime(date);
		gmtCal.add(Calendar.MILLISECOND, localCal.get(Calendar.ZONE_OFFSET));
		if (tz.inDaylightTime(gmtCal.getTime())) {
			gmtCal.add(Calendar.MILLISECOND, tz.getDSTSavings());
		}
		return Formatter.formatDate(l, tz, type, gmtCal.getTime());
	}

	/**
	 * Formats the given date in the import date format. This is equivalent to
	 * the medium date format in the UK locale. For example, "02-Apr-03" for
	 * April 2, 2003. However, the name of the month will be localized to the
	 * language of the given locale.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param date
	 *            the date string to parse; must not be null
	 * @return the formatted string representing the given date
	 */
	public static String formatExportDate(Locale l, Date date) {
		if (date == null) {
			throw new IllegalArgumentException(
					"Null date given to formatExportDate");
		}

		DateFormat df = getDateFormatter(l, null, DATE_IMPORT);
		return df.format(date);
	}

	/**
	 * Formats the given numeric string in the specified format type and locale.
	 * The number is rounded first.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @param numStr
	 *            the numeric string to format; must not be null
	 * @return the formatted string representing the given number
	 */
	public static String formatNumber(Locale l, String type, String numStr) {
		if (numStr == null) {
			throw new IllegalArgumentException(
					"formatNumber: Null number given.");
		}

		if (isNaN(numStr))
			return NOT_APPLICABLE;

		NumberFormat nf = getNumberFormatter(l, type);

		// first round the number to the appropriate number of decimal places
		// so we don't lose data by truncating during format.
		double roundedVal = roundNumber(numStr, type);
		return nf.format(roundedVal);
	}

	/**
	 * Formats the given number object in the specified format type and locale.
	 * The number is rounded first.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @param numObj
	 *            the number object to format; must not be null
	 * @return the formatted string representing the given number
	 */
	public static String formatNumber(Locale l, String type, Number numObj) {
		if (numObj == null) {
			throw new IllegalArgumentException(
					"formatNumber: Null number given.");
		}

		if (isNaN(numObj))
			return NOT_APPLICABLE;

		NumberFormat nf = getNumberFormatter(l, type);

		// first round the number to the appropriate number of decimal places
		// so we don't lose data by truncating during format.
		double roundedVal = roundNumber(numObj.doubleValue(), type);

		return nf.format(roundedVal);
	}

	public static String formatNumberNoRound(Locale l, String type,
			Number numObj) {
		if (numObj == null) {
			throw new IllegalArgumentException(
					"formatNumber: Null number given.");
		}

		if (isNaN(numObj))
			return NOT_APPLICABLE;

		// first round the number to the appropriate number of decimal places
		// so we don't lose data by truncating during format.
		double roundedVal = roundNumber(numObj.doubleValue(), type);

		return Double.toString(roundedVal);
	}

	/**
	 * Formats the given long number in the specified format type and locale.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @param number
	 *            the number to format
	 * @return the formatted string representing the given number
	 */
	public static String formatNumber(Locale l, String type, long number) {
		if (isNaN(number))
			return NOT_APPLICABLE;

		NumberFormat nf = getNumberFormatter(l, type);

		return nf.format(number);
	}

	/**
	 * Formats the given double number in the specified format type and locale.
	 * The number is rounded first.
	 * 
	 * @param l
	 *            the locale to use for the formatting; must not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @param number
	 *            the number to format
	 * @return the formatted string representing the given number
	 */
	public static String formatNumber(Locale l, String type, double number) {
		if (isNaN(number))
			return NOT_APPLICABLE;

		NumberFormat nf = getNumberFormatter(l, type);

		// first round the number to the appropriate number of decimal places
		// so we don't lose data by truncating during format.
		double roundedVal = roundNumber(number, type);
		return nf.format(roundedVal);
	}

	/**
	 * Parses the given date string in the specified format type and locale and
	 * time zone.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param tz
	 *            the time zone to use for parsing; use null if none available
	 * @param type
	 *            the date format type to use; must not be null
	 * @param dateStr
	 *            the date string to parse; must not be null
	 * @return the Date object represented by the given date string
	 * @throws ParseException
	 *             if the date string doesn't contain a date in the specified
	 *             format
	 */
	public static Date parseDate(Locale l, TimeZone tz, String type,
			String dateStr) throws ParseException {
		if (dateStr == null) {
			throw new IllegalArgumentException(
					"Null date string given to parseDate");
		}

		if (DATE_SHORT.equals(type)) {
			// for 3.2 release, all short dates are in the special format
			type = DATE_SHORT_I18N_32;
		} else if (DATETIME_SHORT.equals(type)) {
			// for 3.2 release, all short datetimes are in the special format
			type = DATETIME_SHORT_I18N_32;
		} else if ((DATE_MEDIUM.equals(type)) && (tz == null)) {
			type = DATE_MEDIUM_I18N_32;
		}

		DateFormat df = getDateFormatter(l, tz, type);
		return df.parse(dateStr);
	}

	/**
	 * Trys to parse the given date string in all available date formats, using
	 * the given locale and time zone. The first successful format creates the
	 * the return value, starting with the longest format.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param tz
	 *            the time zone to use for parsing; use null if none available
	 * @param dateStr
	 *            the date string to parse; must not be null
	 * @return the Date object represented by the given date string; it's never
	 *         null.
	 * @throws ParseException
	 *             if the date string doesn't contain a date in any supported
	 *             format
	 */
	public static Date parseDate(Locale l, TimeZone tz, String dateStr)
			throws ParseException {
		if (dateStr == null) {
			throw new IllegalArgumentException(
					"Null date string given to parseDate");
		}

		Date date = null;
		int i = 0;
		while (i < dateParseFormats.length) {
			// get a date format object for this format
			DateFormat df = getDateFormatter(l, tz, dateParseFormats[i]);

			try {
				// try to parse using this format
				date = df.parse(dateStr);
				break; // this format worked; we're done.
			} catch (ParseException ex) {
				// this format didn't work
				if (++i >= dateParseFormats.length) {
					throw ex; // no more formats to try; give up.
				}
			}
		}

		return date;
	}

	/**
	 * Trys to parse the given date string in all available date formats, using
	 * the given locale and time zone. The first successful format creates the
	 * the return value, starting with the longest format.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param tz
	 *            the time zone to use for parsing; use null if none available
	 * @param dateStr
	 *            the date string to parse; must not be null
	 * @param outdate
	 *            output date by the given date string; it's can't be null.
	 * @return date format used to parse.
	 * @throws ParseException
	 *             if the date string doesn't contain a date in any supported
	 *             format
	 */
	public static DateFormat parseDate(Locale l, TimeZone tz, String dateStr,
			Date outdate) throws ParseException {
		if (dateStr == null)
			throw new IllegalArgumentException(
					"Null date string given to parseDate");

		if (outdate == null)
			throw new IllegalArgumentException("outdate can't be null");

		Date date = null;
		DateFormat df = null;
		int i = 0;
		while (i < dateParseFormats.length) {
			// get a date format object for this format
			df = getDateFormatter(l, tz, dateParseFormats[i]);

			try {
				// try to parse using this format
				date = df.parse(dateStr);
				break;
			} catch (ParseException ex) {
				// this format didn't work
				if (++i >= dateParseFormats.length) {
					throw ex; // no more formats to try; give up.
				}
			}
		}
		if (date != null)
			outdate.setTime(date.getTime());
		return df;
	}

	/**
	 * Parses the given date string in the import date format. This is
	 * equivalent to the medium date format in the UK locale. For example,
	 * "02-Apr-03" for April 2, 2003. However, the name of the month is expected
	 * to be in the language of the given locale.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param dateStr
	 *            the date string to parse; must not be null
	 * @return the Date object represented by the given date string
	 * @throws ParseException
	 *             if the date string doesn't contain a date in the specified
	 *             format
	 */
	public static Date parseImportDate(Locale l, String dateStr)
			throws ParseException {
		if (dateStr == null) {
			throw new IllegalArgumentException(
					"Null date string given to parseImportDate");
		}

		DateFormat df = getDateFormatter(l, null, DATE_IMPORT);
		return df.parse(dateStr);
	}

	/**
	 * This method parses the given date string into the absolute date format .
	 * Currently absolute date format is yyyy-MM-dd This method is for internal
	 * use only and parses the date using server Locale
	 * 
	 * @param l
	 *            Locale should contain Locale of the server
	 * @param dateString
	 * @return Date object representation of the given string
	 * @throws ParseException
	 *             if the date string does not contain a date in specified
	 *             format
	 */
	public static Date parseAbsoluteDate(Locale l, String dateString)
			throws ParseException {
		if (dateString == null) {
			throw new IllegalArgumentException(
					"Null date string given to parseAbsoluteDate");
		}

		DateFormat df = getDateFormatter(getServerLocale(l), null,
				ABSOLUTE_DATE_FMT);
		return df.parse(dateString);
	}

	/**
	 * Parses the given number string in the specified locale. Will allow group
	 * separators such as ','.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param numberStr
	 *            the number string to parse; must not be null
	 * @return the Number object represented by the given number string
	 * @throws ParseException
	 *             if the number string doesn't contain a number in a decimal
	 *             format
	 */
	public static Number parseNumber(Locale l, String numberStr)
			throws ParseException {
		if (numberStr == null) {
			throw new IllegalArgumentException(
					"Null number string given to parseNumber");
		}

		NumberFormat nf = getNumberFormatter(l, NUMBER_PARSE);
		return nf.parse(numberStr);
	}

	/**
	 * Parses the given number string in the specified locale using the
	 * specified number type. Will allow group separators such as ','.
	 * 
	 * @param l
	 *            the locale to use for the parsing; must not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @param numberStr
	 *            the number string to parse; must not be null
	 * @return the Number object represented by the given number string
	 * @throws ParseException
	 *             if the number string doesn't contain a number in the
	 *             specified format
	 */
	public static Number parseNumber(Locale l, String type, String numberStr)
			throws ParseException {
		if (numberStr == null) {
			throw new IllegalArgumentException(
					"Null number string given to parseNumber");
		}

		NumberFormat nf = getNumberFormatter(l, type);
		return nf.parse(numberStr);
	}

	/**
	 * Returns the currency symbol for the given locale
	 * 
	 * @param l
	 *            the locale from which to get the currency symbol
	 */
	public static String getCurrencySymbol(Locale l) {
		// get the currency symbol from a decimal format object
		DecimalFormat dfmt = (DecimalFormat) (NumberFormat
				.getCurrencyInstance(l));
		String currencySym = dfmt.getDecimalFormatSymbols().getCurrencySymbol();

		return currencySym;
	}

	//
	// Private helpers
	//

	/**
	 * Returns the cached date format object for the specified format type and
	 * locale and time zone. If a cached object doesn't exist, create one and
	 * cache it.
	 * 
	 * @param l
	 *            the locale to use for creating a date format object; must not
	 *            be null
	 * @param tz
	 *            the time zone to use for creating a date format object; use
	 *            null if none available
	 * @param type
	 *            the date format type to use; must not be null
	 * @return the date format object
	 */
	private static DateFormat getDateFormatter(Locale l, TimeZone tz,
			String type) {
		if (l == null || type == null) {
			throw new IllegalArgumentException(
					"getDateFormatter: Null Locale or type; l=" + l + ", type="
							+ type);
		}

		TimeZone keyTZ = tz;
		if (keyTZ == null) {
			// no timezone given, use the default timezone,
			// which is the timezone where the server is located.
			keyTZ = TimeZone.getDefault();
		}

		// build the key consisting of the locale, timezone and format type
		String key = l.toString() + keyTZ.getID() + type;

		DateFormat df = date_formats.get().get(key);
		if (df == null) {
			df = createDateFormatter(l, tz, type);
			date_formats.get().put(key, df);
		}

		return df;
	}

	public static DateFormat getClonedDateFormatter(Locale l, TimeZone tz,
			String type) {
		return (DateFormat) getDateFormatter(l, tz, type).clone();

	}

	/**
	 * Returns the cached number format object for the specified format type and
	 * locale. If a cached object doesn't exist, create one and cache it.
	 * 
	 * Changing this to be package-private as a unit test uses this method (GC,
	 * 5.8 merge, 3/27/2006)
	 * 
	 * @param l
	 *            the locale to use for creating a number format object; must
	 *            not be null
	 * @param type
	 *            the number format type to use; must not be null
	 * @return the number format object
	 */
	public static NumberFormat getNumberFormatter(Locale locale, String type) {
		if (locale == null || type == null)
			throw new IllegalArgumentException(
					"getNumberFormatter: Null Locale or type; Locale=" + locale
							+ ", type=" + type);

		Map<String, NumberFormat> numberFormatMap = number_formats.get().get(
				locale);
		if (numberFormatMap == null) {
			numberFormatMap = new HashMap<String, NumberFormat>();
			number_formats.get().put(locale, numberFormatMap);
		}

		NumberFormat numberFormat = numberFormatMap.get(type);
		if (numberFormat == null) {
			numberFormat = createNumberFormatter(locale, type);
			numberFormatMap.put(type, numberFormat);
		}

		return numberFormat;
	}

	/**
	 * Returns true if the given number is the designated NaN.
	 * 
	 * @param number
	 *            the number to test
	 * @return true if the number is the designated NaN
	 */
	public static boolean isNaN(long number) {
		return (number == NAN);
	}

	/**
	 * Returns true if the given number is the designated NaN.
	 * 
	 * @param number
	 *            the number to test
	 * @return true if the number is the designated NaN
	 */
	public static boolean isNaN(double number) {
		return ((int) NumericOps.roundDouble(number, 0) == NAN);
	}

	/**
	 * Returns true if the given number is the designated NaN.
	 * 
	 * @param number
	 *            the number to test
	 * @return true if the number is the designated NaN or null.
	 */
	public static boolean isNaN(Object number) {
		if (number == null) {
			return true; // null is also not-a-number
		} else if (number instanceof Number) {
			return (((Number) number).intValue() == NAN);
		} else if (number instanceof String) {
			boolean nan = false;
			try {
				Double num = Double.valueOf((String) number);
				nan = (num.intValue() == NAN);
			} catch (java.lang.NumberFormatException e) {
				nan = true;
			}
			return nan;
		} else {
			return true;
		}
	}

	private static DateFormat createDateFormatter(Locale l, TimeZone tz,
			String type) {
		if (l == null || type == null) {
			throw new IllegalArgumentException(
					"createDateFormatter: Null Locale or type; l=" + l
							+ ", type=" + type);
		}

		DateFormat df = null;
		type = type.intern();

		if (l.equals(new Locale("th", "TH", ""))) {
			df = createDateFormatterForThailand(type);
		} else if (type == DATE_SHORT_I18N_32) {
			df = DateFormat.getDateInstance(DateFormat.MEDIUM, l);
		} else if (type == DATE_IMPORT) {
			// This is also used for import date parsing and export date
			// formatting.
			df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		} else if (type == DATE_SHORT) {
			df = DateFormat.getDateInstance(DateFormat.SHORT, l);
		} else if (type == DATE_MEDIUM) {
			df = DateFormat.getDateInstance(DateFormat.MEDIUM, l);
		} else if (type == DATE_MEDIUM_I18N_32) {
			df = DateFormat.getDateInstance(DateFormat.MEDIUM, l);
		} else if (type == DATE_LONG) {
			df = DateFormat.getDateInstance(DateFormat.LONG, l);
		} else if (type == DATE_WEEKDAY_MED) {
			df = DateFormat.getDateInstance(DateFormat.MEDIUM, l);

			// prefix pattern with day of week
			SimpleDateFormat sdf = (SimpleDateFormat) df;
			sdf.applyPattern("EEEE, " + sdf.toPattern());
		} else if (type == DATETIME_MEDIUM) {
			df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.SHORT, l);

		} else if (type == DATETIME_SHORT_I18N_32) {

			SimpleDateFormat dateFmt = (SimpleDateFormat) DateFormat
					.getDateInstance(DateFormat.MEDIUM, l);
			SimpleDateFormat timeFmt = (SimpleDateFormat) DateFormat
					.getTimeInstance(DateFormat.SHORT, l);

			df = new SimpleDateFormat(dateFmt.toPattern() + " "
					+ timeFmt.toPattern(), l);
		} else if (type == DATETIME_SHORT) {
			df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
					DateFormat.SHORT, l);
		} else if (type == TIME_SHORT) {
			df = DateFormat.getTimeInstance(DateFormat.SHORT, l);

			// postfix pattern with timezone if not present
			SimpleDateFormat sdf = (SimpleDateFormat) df;
			String pattern = sdf.toPattern();
			if (!pattern.endsWith(" z"))
				sdf.applyPattern(pattern + " z");
		} else if (type == ABSOLUTE_DATE_FMT) {
			// this is for internal use only!
			// this formatter only uses ABSOLUTE_DATE_PATTERN and US locale
			// (serverl locale)
			df = new SimpleDateFormat(ABSOLUTE_DATE_PATTERN, l);
		} else {
			throw new IllegalArgumentException(
					"createDateFormatter: Unrecognized type=" + type);
		}

		if (tz != null) {
			df.setTimeZone(tz);
		}

		df.setLenient(true); // for parsing

		return df;
	}

	private static DateFormat createDateFormatterForThailand(String type) {
		String TH_TIME_SHORT = "H:mm";
		String TH_DATE_SHORT = "d/M/yyyy";
		String TH_DATE_MEDIUM = "d MMM yyyy";
		String TH_DATE_LONG = "d MMMM yyyy";
		String TH_DATETIME_SHORT = TH_DATE_SHORT + ", " + TH_TIME_SHORT;
		String TH_DATETIME_SHORT_I18N_32 = TH_DATE_MEDIUM + " " + TH_TIME_SHORT;
		String TH_DATETIME_MEDIUM = TH_DATE_MEDIUM + ", " + TH_TIME_SHORT;
		DateFormat df = null;

		if (type == DATE_SHORT_I18N_32 || type == DATE_MEDIUM
				|| type == DATE_MEDIUM_I18N_32) {
			df = new SimpleDateFormat(TH_DATE_MEDIUM, Locale.US);
		} else if (type == DATE_IMPORT) {
			// This is also used for import date parsing and export date
			// formatting.
			df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		} else if (type == DATE_SHORT) {
			df = new SimpleDateFormat(TH_DATE_SHORT, Locale.US);
		} else if (type == DATE_LONG) {
			df = new SimpleDateFormat(TH_DATE_LONG, Locale.US);
		} else if (type == DATE_WEEKDAY_MED) {
			df = new SimpleDateFormat(TH_DATE_MEDIUM, Locale.US);
			// prefix pattern with day of week
			SimpleDateFormat sdf = (SimpleDateFormat) df;
			sdf.applyPattern("EEEE, " + sdf.toPattern());
		} else if (type == DATETIME_MEDIUM) {
			df = new SimpleDateFormat(TH_DATETIME_MEDIUM, Locale.US);
		} else if (type == DATETIME_SHORT_I18N_32) {
			df = new SimpleDateFormat(TH_DATETIME_SHORT_I18N_32, Locale.US);
		} else if (type == DATETIME_SHORT) {
			df = new SimpleDateFormat(TH_DATETIME_SHORT, Locale.US);
		} else if (type == TIME_SHORT) {
			df = new SimpleDateFormat(TH_TIME_SHORT, Locale.US);
			// postfix pattern with timezone if not present
			SimpleDateFormat sdf = (SimpleDateFormat) df;
			String pattern = sdf.toPattern();
			if (!pattern.endsWith(" z"))
				sdf.applyPattern(pattern + " z");
		} else if (type == ABSOLUTE_DATE_FMT) {
			// this is for internal use only!
			// this formatter only uses ABSOLUTE_DATE_PATTERN and US locale
			// (serverl locale)
			df = new SimpleDateFormat(ABSOLUTE_DATE_PATTERN, Locale.US);
		} else {
			throw new IllegalArgumentException(
					"createDateFormatter: Unrecognized type=" + type);
		}
		return df;
	}

	private static NumberFormat createNumberFormatter(Locale l, String type) {
		if (l == null || type == null)
			throw new IllegalArgumentException(
					"createNumberFormatter: Null Locale or type; l=" + l
							+ ", type=" + type);

		NumberFormat nf = null;
		type = type.intern();

		if (type == NUMBER_COST) {
			nf = NumberFormat.getCurrencyInstance(l);
			if (nf.getMaximumFractionDigits() > 0) {
				// nf.setMaximumIntegerDigits(15);
				nf.setMinimumIntegerDigits(1);
				nf.setMinimumFractionDigits(COST_DP);
				nf.setMaximumFractionDigits(COST_DP);
				nf.setParseIntegerOnly(false);
			}
		} else if (type == NUMBER_PRICE) {
			nf = NumberFormat.getCurrencyInstance(l);
			if (nf.getMaximumFractionDigits() > 0) {
				// nf.setMaximumIntegerDigits(15);
				nf.setMinimumIntegerDigits(1);
				nf.setMinimumFractionDigits(PRICE_DP);
				nf.setMaximumFractionDigits(PRICE_DP);
				nf.setParseIntegerOnly(false);
			}
		} else if (type == NUMBER_PERCENT || type == NUMBER_PERCENT_FLOOR
				|| type == NUMBER_PERCENT_CEILING) {
			nf = NumberFormat.getPercentInstance(l);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(PERCENT_DP - 2); // -2 to show 1 digit
															// right of '.'
			nf.setMaximumFractionDigits(PERCENT_DP - 2);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_PERCENT_ONE) {
			nf = NumberFormat.getPercentInstance(l);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(PERCENT_DP - 3); // -3 to show 1 digit
															// right of '.'
			nf.setMaximumFractionDigits(PERCENT_DP - 3);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_PERCENT_NONE) {
			nf = NumberFormat.getPercentInstance(l);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(PERCENT_DP - 4); // -4 to show none
															// digit right of
															// '.'
			nf.setMaximumFractionDigits(PERCENT_DP - 4);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_INDEX) {
			DecimalFormat dft = (DecimalFormat) (NumberFormat
					.getNumberInstance(l));
			dft.setMinimumIntegerDigits(1);
			dft.setMinimumFractionDigits(INDEX_DP - 2);
			dft.setMaximumFractionDigits(INDEX_DP - 2);
			dft.setMultiplier(100);
			nf = dft;
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_FLOAT) {
			nf = NumberFormat.getNumberInstance(l);
			// nf.setMaximumIntegerDigits(10);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(0);
			nf.setMaximumFractionDigits(FLOAT_DP);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_FLOAT_TWO) {
			DecimalFormat dft = (DecimalFormat) (NumberFormat
					.getNumberInstance(l));
			dft.setMinimumIntegerDigits(1);
			dft.setMinimumFractionDigits(FLOAT_DP);
			dft.setMaximumFractionDigits(FLOAT_DP);
			nf = dft;
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_FLOAT_THREE) {
			DecimalFormat dft = (DecimalFormat) (NumberFormat
					.getNumberInstance(l));
			dft.setMinimumIntegerDigits(1);
			dft.setMinimumFractionDigits(COST_DP);
			dft.setMaximumFractionDigits(COST_DP);
			nf = dft;
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_FLOAT_TWO_FLOOR) {
			DecimalFormat dft = (DecimalFormat) (NumberFormat
					.getNumberInstance(l));
			dft.setMinimumIntegerDigits(1);
			dft.setMinimumFractionDigits(FLOAT_DP);
			dft.setMaximumFractionDigits(FLOAT_DP);
			nf = dft;
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_FLOAT_TWO_CEILING) {
			DecimalFormat dft = (DecimalFormat) (NumberFormat
					.getNumberInstance(l));
			dft.setMinimumIntegerDigits(1);
			dft.setMinimumFractionDigits(FLOAT_DP);
			dft.setMaximumFractionDigits(FLOAT_DP);
			nf = dft;
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_INT) {
			nf = NumberFormat.getNumberInstance(l);
			// nf.setMaximumIntegerDigits(10);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(0);
			nf.setMaximumFractionDigits(0);
			nf.setParseIntegerOnly(true);
		} else if (type == NUMBER_ID) {
			nf = NumberFormat.getNumberInstance(l);
			nf.setGroupingUsed(false);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(0);
			nf.setMaximumFractionDigits(0);
			nf.setParseIntegerOnly(true);
		} else if (type == NUMBER_PROFIT) {
			nf = NumberFormat.getCurrencyInstance(l);
			// nf.setMaximumIntegerDigits(15);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(PROFIT_DP);
			nf.setMaximumFractionDigits(PROFIT_DP);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_VOLUME) {
			nf = NumberFormat.getNumberInstance(l);
			// nf.setMaximumIntegerDigits(15);
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(VOLUME_DP);
			nf.setMaximumFractionDigits(VOLUME_DP);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_PARSE) {
			nf = NumberFormat.getNumberInstance(l);
			nf.setParseIntegerOnly(false);
		} else if (type == NUMBER_I18N_DISPLAY) {
			nf = NumberFormat.getNumberInstance(l);
			nf.setMaximumFractionDigits(10);
		} else if (type == NUMBER_I18N_EDIT) {
			nf = NumberFormat.getNumberInstance(l);
			nf.setGroupingUsed(false);
			nf.setMaximumFractionDigits(10);
		} else {
			throw new IllegalArgumentException(
					"createNumberFormatter: Unrecognized type=" + type);
		}

		return nf;
	}

	private static double roundNumber(double input, String type) {
		double retval;

		type = type.intern();
		if (type == NUMBER_COST) {
			retval = NumericOps.roundDouble(input, COST_DP);
		} else if (type == NUMBER_PRICE) {
			retval = NumericOps.roundDouble(input, PRICE_DP);
		} else if (type == NUMBER_PERCENT) {
			retval = NumericOps.roundDouble(input, PERCENT_DP);
		} else if (type == NUMBER_PERCENT_FLOOR) {
			retval = NumericOps.roundDouble(BigDecimal.valueOf(input),
					PERCENT_DP, BigDecimal.ROUND_FLOOR);
		} else if (type == NUMBER_PERCENT_CEILING) {
			retval = NumericOps.roundDouble(BigDecimal.valueOf(input),
					PERCENT_DP, BigDecimal.ROUND_CEILING);
		} else if (type == NUMBER_INDEX) {
			retval = NumericOps.roundDouble(input, INDEX_DP);
		} else if (type == NUMBER_FLOAT) {
			retval = NumericOps.roundDouble(input, PERCENT_DP);
		} else if (type == NUMBER_FLOAT_TWO) {
			retval = NumericOps.roundDouble(input, FLOAT_DP);
		} else if (type == NUMBER_FLOAT_THREE) {
			retval = NumericOps.roundDouble(input, COST_DP);
		} else if (type == NUMBER_FLOAT_TWO_FLOOR) {
			retval = NumericOps.roundDouble(BigDecimal.valueOf(input),
					FLOAT_DP, BigDecimal.ROUND_FLOOR);
		} else if (type == NUMBER_FLOAT_TWO_CEILING) {
			retval = NumericOps.roundDouble(BigDecimal.valueOf(input),
					FLOAT_DP, BigDecimal.ROUND_CEILING);
		} else if (type == NUMBER_INT) {
			retval = NumericOps.roundDouble(input, 0);
		} else if (type == NUMBER_PROFIT) {
			retval = NumericOps.roundDouble(input, PROFIT_DP);
		} else if (type == NUMBER_VOLUME) {
			retval = NumericOps.roundDouble(input, VOLUME_DP);
		} else if (type == NUMBER_I18N_DISPLAY || type == NUMBER_I18N_EDIT) {
			BigDecimal bigDec = new BigDecimal(input);
			retval = bigDec.doubleValue();
		} else {
			throw new IllegalArgumentException(
					"roundNumber: Unrecognized type=" + type);
		}

		return retval;
	}

	private static double roundNumber(String input, String type) {
		double retval;

		type = type.intern();
		if (type == NUMBER_COST) {
			retval = NumericOps.roundDouble(input, COST_DP);
		} else if (type == NUMBER_PRICE) {
			retval = NumericOps.roundDouble(input, PRICE_DP);
		} else if (type == NUMBER_PERCENT) {
			retval = NumericOps.roundDouble(input, PERCENT_DP);
		} else if (type == NUMBER_PERCENT_FLOOR) {
			retval = NumericOps.roundDouble(input, PERCENT_DP,
					BigDecimal.ROUND_FLOOR);
		} else if (type == NUMBER_PERCENT_CEILING) {
			retval = NumericOps.roundDouble(input, PERCENT_DP,
					BigDecimal.ROUND_CEILING);
		} else if (type == NUMBER_PERCENT_ONE) {
			retval = NumericOps.roundDouble(input, PERCENT_DP - 1);
		} else if (type == NUMBER_PERCENT_NONE) {
			retval = NumericOps.roundDouble(input, PERCENT_DP - 2);
		} else if (type == NUMBER_INDEX) {
			retval = NumericOps.roundDouble(input, INDEX_DP);
		} else if (type == NUMBER_FLOAT) {
			retval = NumericOps.roundDouble(input, FLOAT_DP);
		} else if (type == NUMBER_FLOAT_TWO) {
			retval = NumericOps.roundDouble(input, FLOAT_DP);
		} else if (type == NUMBER_FLOAT_THREE) {
			retval = NumericOps.roundDouble(input, COST_DP);
		} else if (type == NUMBER_FLOAT_TWO_FLOOR) {
			retval = NumericOps.roundDouble(input, FLOAT_DP,
					BigDecimal.ROUND_FLOOR);
		} else if (type == NUMBER_FLOAT_TWO_CEILING) {
			retval = NumericOps.roundDouble(input, FLOAT_DP,
					BigDecimal.ROUND_CEILING);
		} else if (type == NUMBER_INT) {
			retval = NumericOps.roundDouble(input, 0);
		} else if (type == NUMBER_ID) {
			retval = NumericOps.roundDouble(input, 0);
		} else if (type == NUMBER_PROFIT) {
			retval = NumericOps.roundDouble(input, PROFIT_DP);
		} else if (type == NUMBER_VOLUME) {
			retval = NumericOps.roundDouble(input, VOLUME_DP);
		} else if (type == NUMBER_I18N_DISPLAY || type == NUMBER_I18N_EDIT) {
			BigDecimal bigDec = new BigDecimal(input);
			retval = bigDec.doubleValue();
		} else {
			throw new IllegalArgumentException(
					"roundNumber: Unrecognized type=" + type);
		}

		return retval;
	}

	/**
	 * This method returns the list of localized short month names for display.
	 * This method calls getListOfMonthsForDisplay
	 * 
	 * @param locale
	 *            Locale containing language information
	 * @return List of 12 localized short month names
	 */
	public static List getShortMonthsNamesList(Locale locale, String fmt) {
		if (locale.equals(new Locale("th", "TH", ""))) {
			return getShortMonthsNamesList(Locale.US, fmt);
		}
		Calendar cal = getLocalizedCalendarInstance(locale);
		return getListOfMonthsForDisplay(locale, fmt, cal);
	}

	/**
	 * This method returns the list of localized months names for display It
	 * iterates through all the Calendar.MONTH to get all the localized months
	 * names.
	 * 
	 * @param locale
	 *            Locale containing language information
	 * @param cal
	 *            Localized instances of the Calendar
	 * @return List of 12 localized short month names
	 */
	static List getListOfMonthsForDisplay(Locale locale, String fmt,
			Calendar cal) {
		List<String> months = new ArrayList<String>(12);
		if (StringUtils.isNotBlank(fmt) && StringUtils.isNotEmpty(fmt)) {
			SimpleDateFormat sdf = new SimpleDateFormat(fmt, locale);
			// setting the day of the month to 1 to get all the months correctly
			cal.set(Calendar.DAY_OF_MONTH, 1);
			for (int i = 0; i < 12; i++) {
				cal.set(Calendar.MONTH, i);
				months.add(sdf.format(cal.getTime()));
			}
		}
		return months;
	}

	/**
	 * This method is only used for chinese and japanese in order to get the
	 * year character.
	 * 
	 * @param year
	 * @return full year format string
	 */
	public static String getFullYear(Locale locale, int year) {
		DateFormat fullDateFormat = DateFormat.getDateTimeInstance(
				DateFormat.FULL, DateFormat.FULL, locale);
		Calendar cal = Calendar.getInstance(locale);
		cal.set(Calendar.YEAR, year);
		String fdt = fullDateFormat.format(cal.getTime());
		return fdt;
	}

	/**
	 * Get year display style for japanese language i.e. 2006 return 2006?
	 * 
	 * @param year
	 * @return string
	 */
	public static String getFormatYear(Locale locale, List yList, int year) {
		if (yList != null && !yList.isEmpty()) {
			String yFmt = yList.get(0).toString();
			SimpleDateFormat sdf = new SimpleDateFormat(yFmt, locale);
			Calendar cal = getLocalizedCalendarInstance(locale);
			cal.set(Calendar.YEAR, year); // we may need to set the month and
											// day to Jan 1
			String yearStr = sdf.format(cal.getTime());

			String fully = getFullYear(locale, year);
			StringBuffer sbYear = new StringBuffer(yearStr);
			int start = (Integer) yList.get(1);
			int length = (Integer) yList.get(2);
			if (start >= 0 && length > 0) {
				sbYear.append(fully.substring(start, start + length));
			}
			return sbYear.toString();
		} else {
			return String.valueOf(year);
		}
	}

	// prevent anyone from subclassing this class
	private Formatter() {
	}

	private static Calendar getLocalizedCalendarInstance(Locale l) {
		return Calendar.getInstance(l);
	}

	private static Locale getServerLocale(Locale l) {
		Locale serverLocale = l;
		if (!l.equals(Locale.getDefault())) {
			serverLocale = Locale.getDefault();
		}
		return serverLocale;
	}

	// for testing
	public static void main(String[] args) {
		// run tests with this locale
		// Locale l = Locale.US;
		// Locale l = new Locale("en", "IE", "EURO");
		Locale l = new Locale("ja", "JP");

		if (args.length > 0) {
			// do the number format tests using the first argument as a number
			String sInput = args[0];
			Double input = Double.valueOf(sInput);

			System.out.println("Number = " + sInput);
			System.out.println("Formatted as none="
					+ formatNumber(l, NUMBER_I18N_DISPLAY, input));
			System.out.println("Formatted as none="
					+ formatNumber(l, NUMBER_I18N_EDIT, input));
			System.out.println("Formatted as cost="
					+ formatNumber(l, NUMBER_COST, input));
			System.out.println("Formatted as price="
					+ formatNumber(l, NUMBER_PRICE, input));
			System.out.println("Formatted as percent="
					+ formatNumber(l, NUMBER_PERCENT, input));
			System.out.println("Formatted as index="
					+ formatNumber(l, NUMBER_INDEX, input));
			System.out.println("Formatted as float="
					+ formatNumber(l, NUMBER_FLOAT, input));
			System.out.println("Formatted as integer="
					+ formatNumber(l, NUMBER_INT, input));
			System.out.println("Formatted as profit="
					+ formatNumber(l, NUMBER_PROFIT, input));
			System.out.println("Formatted as volume="
					+ formatNumber(l, NUMBER_VOLUME, input));
			System.out.println();
			System.out.println("Formatted as string cost="
					+ formatNumber(l, NUMBER_COST, sInput));
			System.out.println("Formatted as string price="
					+ formatNumber(l, NUMBER_PRICE, sInput));
			System.out.println("Formatted as string percent="
					+ formatNumber(l, NUMBER_PERCENT, sInput));
			System.out.println("Formatted as string index="
					+ formatNumber(l, NUMBER_INDEX, sInput));
			System.out.println("Formatted as string float="
					+ formatNumber(l, NUMBER_FLOAT, sInput));
			System.out.println("Formatted as string integer="
					+ formatNumber(l, NUMBER_INT, sInput));
			System.out.println("Formatted as string profit="
					+ formatNumber(l, NUMBER_PROFIT, sInput));
			System.out.println("Formatted as string volume="
					+ formatNumber(l, NUMBER_VOLUME, sInput));
			System.out.println();
		}

		// do the date formatting tests
		Date today = new Date();
		Date twelvepm;
		String timeStr = "12/10/2002 12:00 pm";
		try {
			twelvepm = parseDate(l, null, timeStr);
		} catch (ParseException ex) {
			throw new RuntimeException("\nException during parse of \""
					+ timeStr + "\" " + ex);
		}
		System.out.println("Today formatted as date short="
				+ formatDate(l, null, DATE_SHORT, today));
		System.out.println("Today formatted as date med="
				+ formatDate(l, null, DATE_MEDIUM, today));
		System.out.println("Today formatted as date long="
				+ formatDate(l, null, DATE_LONG, today));
		System.out.println("Today formatted as date wk med="
				+ formatDate(l, null, DATE_WEEKDAY_MED, today));
		System.out.println("Today formatted as time short="
				+ formatDate(l, null, TIME_SHORT, today));
		System.out.println("Today formatted as date time med="
				+ formatDate(l, null, DATETIME_MEDIUM, twelvepm));
		System.out.println("Today formatted as date time short="
				+ formatDate(l, null, DATETIME_SHORT, today));
		System.out.println("Today formatted as export date="
				+ formatExportDate(l, today));
		System.out.println();

		// do the date parsing tests
		try {
			Date testDate = parseDate(l, null,
					formatDate(l, null, DATE_SHORT, today));
			System.out.println("Short formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null,
					formatDate(l, null, DATE_MEDIUM, today));
			System.out.println("Medium formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null, formatDate(l, null, DATE_LONG, today));
			System.out.println("Long formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null,
					formatDate(l, null, DATE_WEEKDAY_MED, today));
			System.out.println("Weekday Med. formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null,
					formatDate(l, null, TIME_SHORT, today));
			System.out.println("Short time formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null,
					formatDate(l, null, DATETIME_MEDIUM, today));
			System.out.println("Medium datetime formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
			testDate = parseDate(l, null, "11/5/03");
			System.out.println("11/5/03 formatted as date time med="
					+ formatDate(l, null, DATETIME_MEDIUM, testDate));
		} catch (ParseException ex) {
			System.out.println("\n" + ex);
		}
	}

	// map of DateFormat objects
	private static final ThreadLocal<Map<String, DateFormat>> date_formats = new ThreadLocal<Map<String, DateFormat>>() {
		protected Map<String, DateFormat> initialValue() {
			return new HashMap<String, DateFormat>();
		}
	};

	// private final static Map date_formats = new ThreadHashMap();

	// map of NumberFormat objects
	private static final ThreadLocal<Map<Locale, Map<String, NumberFormat>>> number_formats = new ThreadLocal<Map<Locale, Map<String, NumberFormat>>>() {
		protected Map<Locale, Map<String, NumberFormat>> initialValue() {
			return new HashMap<Locale, Map<String, NumberFormat>>();
		}
	};
	// private final static Map number_formats = new ThreadHashMap();
}
