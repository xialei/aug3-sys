package com.aug3.sys.properties;

import java.util.Locale;

import com.aug3.sys.AppContext;

/*
 * Utility class to generate the properties for for anything that needs internationalization.
 *
 * The properties files are stored where the FileProperties class stores them.
 * The names of the files include the Locale, since FileProperties does not
 * currently understand Locale. The file names are stringresource.properties for
 * the default Locale, and names like stringresource_en_GB.properties for other
 * Locales.
 */
public class StringResourceProperties {

	/**
	 * HashMap for remembering the FileProperties we've already seen.
	 * 
	 * The map key is the Locale string.
	 */
	private static I18NProperties i18nProperties;

	/**
	 * Constructor (private, never used).
	 * 
	 * There is no reason to construct an instance, and this constructor being
	 * private makes that clear. It also eliminates a code coverage failure.
	 */
	private StringResourceProperties() {
	}

	/**
	 * Get the internationalized (and possibly company-specific) string for the
	 * specified resource key from stringresource resource bundle..
	 * 
	 * @param ctx
	 *            AppContext (used to determine the company)
	 * @param resourceKey
	 *            resource Key e.g. LIS_UPC
	 * @param locale
	 *            Locale (used to determine the language)
	 * @return internationalized (and possibly company-specific) string
	 */
	public static String getValue(AppContext ctx, String resourceKey,
			Locale locale) {

		if (i18nProperties == null) {
			synchronized (StringResourceProperties.class) {
				if (i18nProperties == null) {
					i18nProperties = new I18NProperties("stringresource");
				}
			}
		}
		return i18nProperties.getValue(ctx, resourceKey, locale);
	}

	static void refreshProperties() {
		if (i18nProperties != null) {
			i18nProperties.refreshProperties();
		}
	}

}
