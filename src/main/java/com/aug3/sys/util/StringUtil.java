package com.aug3.sys.util;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * String utilities
 * 
 * @author xial
 * 
 */
public class StringUtil {

	/**
	 * Use Apache Jakarta Commons StringUtils.isBlank() to checks if a String is
	 * whitespace, empty ("") or null
	 * 
	 * @param str
	 *            String to be checked
	 * @return true/false
	 */
	public static boolean isBlank(String str) {
		return StringUtils.isBlank(str);
	}

	/**
	 * StringUtil.join(null, *) = null
	 * 
	 * StringUtil.join([], *) = ""
	 * 
	 * StringUtil.join(["a", "b", "c"], ';') = "a;b;c"
	 * 
	 * StringUtil.join(["a", "b", "c"], null) = "abc"
	 * 
	 * StringUtil.join([null, "", "a"], ';') = ";;a"
	 * 
	 * @param collection
	 * @param delimiter
	 * @return
	 */
	public static String join(Collection collection, String delimiter) {
		return StringUtils.join(collection, delimiter);
	}

	public static String coalesce(String val, String valueIfNull) {
		return val == null ? valueIfNull : val;
	}

	/**
	 * replaces every occurance of a target character in the given string with a
	 * replacement string.
	 * 
	 * @param str
	 *            the string with the character to be replaced
	 * @param target
	 *            the character to replace in the string
	 * @param replacement
	 *            the string to use as replacement of the target char
	 * @return the string with replaced characters
	 */
	public static String replaceChar(String str, char target, String replacement) {
		if (str == null || replacement == null)
			return "";

		int curPos = str.indexOf(target, 0);
		if (curPos >= 0) {
			int strlen = str.length();
			StringBuilder newStr = new StringBuilder(strlen * 2);
			char[] chars = str.toCharArray();

			int lastPos = 0;
			do {
				newStr.append(chars, lastPos, curPos - lastPos);
				newStr.append(replacement);
				lastPos = curPos + 1; // skip the char we're replacing
				curPos = str.indexOf(target, lastPos);
			} while (curPos >= 0);

			// check for the last segment of the string
			if (lastPos < strlen)
				newStr.append(chars, lastPos, strlen - lastPos);

			return newStr.toString();
		}
		return str;
	}

	/**
	 * Returns a string with the proper HTML entities while not converting \n to
	 * &lt;br&gt;, and not converting spaces to &amp;nbps;'s
	 * 
	 * @param str
	 *            the string to protect
	 * @return the protected string.
	 */
	public static String HTMLEncode(String str) {
		return HTMLEncode(str, false, false);
	}

	/**
	 * Returns a string with the proper HTML entities.
	 * 
	 * @param str
	 *            the string to protect
	 * @param convNL
	 *            if true, convert newlines to "&lt;br&gt;s"
	 * @return the protected string.
	 */
	public static String HTMLEncode(String str, boolean convNL) {
		return HTMLEncode(str, convNL, false);
	}

	/**
	 * Returns a string with the proper HTML entities.
	 * 
	 * @param str
	 *            the string to protect
	 * @param convNL
	 *            if true, convert newlines to &lt;br&gt;'s
	 * @param convSpace
	 *            if true, convert " " to "&amp;nbps;"'s
	 * @return the protected string.
	 */
	public static String HTMLEncode(String str, boolean convNL,
			boolean convSpace) {
		if (str == null || str.length() == 0)
			return "";

		// first replace the ampersand so it wouldn't convert the converted.
		str = replaceChar(str, '&', "&amp;");
		str = replaceChar(str, '<', "&lt;");
		str = replaceChar(str, '>', "&gt;");
		str = replaceChar(str, '\"', "&quot;");
		str = replaceChar(str, '\u20AC', "&euro;"); // euro symbol
		str = replaceChar(str, '\u00A3', "&pound;"); // sterling pound symbol

		// Don't replace this because it breaks the XML Parser when this is seen
		// in a grid.
		// str = replaceChar(str, '\'', "&rsquo;");

		if (convNL)
			str = replaceChar(str, '\n', "<br>");

		if (convSpace)
			str = replaceChar(str, ' ', "&nbsp;");

		return str;
	}
}
