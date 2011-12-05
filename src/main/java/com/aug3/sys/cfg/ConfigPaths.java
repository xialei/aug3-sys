package com.aug3.sys.cfg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonException;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.action.AbstractAction;
import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.properties.EnvProperties;
import com.aug3.sys.util.StringUtil;

/**
 * Helper class for constructing properties and configuration path names.
 * 
 * The paths defined here are only meaningful inside the configure server
 * context. That is, this code should be running inside an application server
 * that is also configured to be a configuration server. If you try to use this
 * class outside of this context it should throw an exception.
 * 
 * @author xial
 * 
 */
public class ConfigPaths {

	private static final Logger log = Logger.getLogger(ConfigPaths.class);

	// OS file separator
	public final static String FS = File.separator;

	// Properties file suffix
	public final static String PROPERTIES_SUFFIX = ".properties";

	// Default folder name
	public final static String DEFAULT_FOLDER = "default";

	public final static String DEFAULT_ORG = ConfigConstants.DEFAULT_COMPANY;

	// Cached map of path names by company
	private static final Map<String, String> mapOrgPaths = new HashMap<String, String>();

	// -----------------------------------------------------------------------
	// CONSTRUCTORS / INITIALIZERS
	// -----------------------------------------------------------------------

	static {

		// NOTE: ConfigPaths has meaning only inside
		// an ConfigServer, which must either be inside an application server or
		// defined by the "app.localcfg" system property.
		if (AppSystem.inProc() && (!ConfigConstants.FORCE_LOCAL_PROP)) {
			throw new CommonRuntimeException(
					"Should not use ConfigPaths inside a proc instance.");
		}

		// Prints a warning if using this class outside of a server.
		if ((!AppSystem.inServer()) && (!ConfigConstants.FORCE_LOCAL_PROP)) {
			log.warn("Using ConfigPath in an improper (i.e, not a config server) environment.");
			log.warn("This is the stack trace for this call:");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
				builder.append(el.toString());
			}
			log.warn(builder.toString());

		}

		UpdateNotifier.addCallback(

		new AbstractAction() {
			public Object perform(Map params) {
				try {
					LocalConfigFinder.getInstance();
				} catch (CommonException e) {
					log.warn("cause: " + e.getMessage());
					log.debug(e);
				}
				return null;
			}
		});
	}

	/**
	 * static methods class, therefore we don't want instances
	 */
	private ConfigPaths() {
	}

	// -----------------------------------------------------------------------
	// PUBLIC METHODS
	// -----------------------------------------------------------------------
	/**
	 * Get full default path file name.
	 * 
	 * @param strPropertiesName
	 */
	public static String getDefaultPathName(String strPropertiesName) {
		return getDefaultPath() + FS + strPropertiesName;
	}

	/** 
	 * Get default path name
	 * "/var/cfg/default/"
	 */
	public static String getDefaultPath() {
		return getTrackPath() + FS + DEFAULT_FOLDER;
	}

	/**
	 * @param strPropertiesName
	 * @return Path "/var/cfg/"
	 */
	public static String getAllCompaniesPathName(String strPropertiesName) {
		return getTrackPath() + FS + strPropertiesName;
	}

	/**
	 * Get track path name
	 * 
	 * @param strTrack
	 * @return Path: [mount]/[track name]
	 */
	public static String getTrackPath() {
		return BootProperties.getInstance().getTrackCfgHome();
	}

	/**
	 * Get full path name for a company configuration file
	 * 
	 * @param strOrg
	 * @param strFileName
	 */
	public static String getCompanyPathName(String strOrg, String strFileName) {
		return getCompanyPath(strOrg) + FS + strFileName;
	}

	/**
	 * Get the company path. Company path is usually $CONFIG_ROOT/$COMPANY_NAME,
	 * unless the company is DEFAULT_ORGANIZATION, in which case it will be
	 * $CONFIG_ROOT/
	 * 
	 * @param strOrg
	 * 
	 * @return Path: [mount]/[company name] or [mount]/ if DEFAULT_ORGNIZATION
	 *         or empty
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the parameters is null or the organization does not
	 *             belong to the track.
	 */
	public static String getCompanyPath(String strOrg) {

		// DEFAULT_ORGANIZATION is special, when that is the org name it means
		// we want the up-company-level information.
		if (DEFAULT_ORG.equalsIgnoreCase(strOrg) || StringUtil.isBlank(strOrg)) {
			return getTrackPath();
		}

		if (StringUtil.isBlank(strOrg)) {
			throw new IllegalArgumentException(
					"company cannot be null or empty");
		}

		// Fetch cached path
		synchronized (mapOrgPaths) {
			String strKey = strOrg.trim();
			// Get cached entry
			String strPath = (String) mapOrgPaths.get(strKey);
			if (strPath == null) {
				// No cached entry, so create one
				strPath = getTrackPath() + ConfigPaths.FS + strOrg.trim();
				mapOrgPaths.put(strKey, strPath);
			}
			return strPath;
		}
	}

	/**
	 * Reset any cached values.
	 * 
	 */
	public static void reset() {
		synchronized (mapOrgPaths) {
			mapOrgPaths.clear();
		}
	}

	/** Get value of APP_HOME */
	public static String getAppHome() {
		return EnvProperties.getInstance().getAppHome();
	}

	/**
	 * Returns the appropriate file object. Looks for the file first in the
	 * company directory, then the track directory and finally in the default
	 * track directory.
	 * 
	 * @return the file name if the file was found, null if no file exists.
	 */
	static File getFile(String track, String org, String name) {

		File f = new File(getCompanyPath(org), name);

		if (!f.exists()) {
			f = new File(getTrackPath(), name);
		}

		if (!f.exists()) {
			f = new File(getDefaultPath(), name);
		}

		if (!f.exists()) {
			f = null;
		}

		return f;
	}

}
