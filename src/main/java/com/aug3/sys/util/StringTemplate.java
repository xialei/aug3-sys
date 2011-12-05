package com.aug3.sys.util;

import java.util.Iterator;
import java.util.Map;

/**
 * This class creates Strings from a template String, first performing
 * substitutions according to a Map The Map is used to define key / value pairs
 * and, during substitution, any <em>@key@</em> value found in the template text
 * is substituted by the corresponding object's String value (as defined by the
 * <code>toString()</code> method.
 * 
 * For example, if the instance is created with the following String template:
 * <center>"Fred is @attribute@"</center> and the <code>getString(map)</code>
 * method is called, with the map defined as:
 * <center>{attribute=kind,name=Roger}</code> the result will be:
 * <center>"Fred is kind"/center>
 * 
 * @author xial
 */
public class StringTemplate {

	private String template;

	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	public StringTemplate(String tplt) {
		if (tplt == null) {
			throw new IllegalArgumentException("Template can't be null!");
		}
		template = tplt;
	}

	// ----------------------------------------------------------------------
	// PUBLIC METHODS
	// ----------------------------------------------------------------------

	/**
	 * Performs the transformations defined in the Map, returning the resulting
	 * String.
	 */
	public String createString(Map map) {
		String result = template;
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = "@" + entry.getKey() + "@";
			result = result.replace(key, entry.getValue().toString());
		}
		return result;
	}

	/** Returns the underlying String template for this object */
	public String toString() {
		return template;
	}

}
