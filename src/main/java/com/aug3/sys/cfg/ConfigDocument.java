package com.aug3.sys.cfg;

import java.io.File;

import org.w3c.dom.Document;

import com.aug3.sys.CommonException;
import com.aug3.sys.xml.Parser;

/**
 * Utility methods used by the configuration management component.
 * 
 * @author xial
 */
public class ConfigDocument {

	private final static String FS = File.separator;

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Finds and parses the configuration type definition files.
	 * 
	 * @param fn
	 *            - the configuration type definition file name
	 * @param cn
	 *            - the company name
	 */
	public static Document get(String fn, String cn) throws CommonException {
		return get(fn, cn, false);
	}

	/**
	 * Finds and parses the configuration type definition files.
	 * 
	 * @param fn
	 *            - the configuration type definition file name
	 * @param cn
	 *            - the company name
	 * @param validation
	 *            - whether to validate the XML against schema
	 */
	public static Document get(String fn, String cn, boolean validation)
			throws CommonException {
		// find and open the config file
		StringBuffer sbuf = new StringBuffer();

		// construct the full path name to the configuration file
		sbuf.append(ConfigPaths.getAppHome());
		sbuf.append(File.separator);
		sbuf.append("etc");
		sbuf.append(File.separator);
		sbuf.append("xml");
		sbuf.append(File.separator);

		// try company dir
		if (cn != null) {
			String fullPath = sbuf.toString() + cn + FS + fn;
			File file = new File(fullPath);

			if (file.exists()) {
				return Parser.parseXMLFile(null, fullPath, validation);
			}
		}

		// try default directory
		String fullPath = sbuf.toString() + fn;
		File file = new File(fullPath);

		if (file.exists()) {
			return Parser.parseXMLFile(null, fullPath, validation);
		} else {
			String msg = "Couldn't find configuration type definition file ["
					+ fullPath + "]";
			throw new CommonException(msg);
		}
	}
}
