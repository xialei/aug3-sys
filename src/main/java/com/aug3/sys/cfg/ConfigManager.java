package com.aug3.sys.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.CommonException;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.util.IOUtil;

/**
 * This is the class through which users access the configuration server. Usage
 * is simple, just create an instance and make the calls.
 * 
 * This class creates an instance of the appropriate ConfigServer implementation
 * (local or remote, depending on the value of the <em>system</em> property
 * <code>FORCE_LOCAL_PROP</code> : ''true'' causes it to use local, anything
 * else causes it to use the remote server.
 * 
 * @author xial
 */
public class ConfigManager implements ConfigServer {

	private static final MLogger log = MLogger.getLog(ConfigManager.class);

	private ConfigServer actualServer;

	// ---------------------------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------------------------
	/**
	 * Creates the default instance of the configuration manager that can be
	 * used to access the appropriate configuration server.
	 */
	public ConfigManager() {
		actualServer = ConfigConstants.FORCE_LOCAL_PROP ? LocalConfigServer
				.getInstance() : ConfigServerProxy.getInstance();
	}

	// ---------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------

	public String getTextFile(AppContext ctx, String filename) {
		return getConfigFile(ctx, filename);
	}

	/**
	 * Returns the timestamp associated with the configuration file. Follows the
	 * class rules for finding the file.
	 * 
	 * @param ctx
	 *            the company-defining context
	 * @param filename
	 *            the name of the file.
	 * 
	 * @return the last modified timestamp associated with the file, or 0 if the
	 *         file does not exist.
	 */
	public Long getLastModified(AppContext ctx, String filename) {
		File configFile = getFile(ctx, filename);
		return (configFile != null) ? new Long(configFile.lastModified())
				: new Long(0);
	}

	public Map<String, ConfigType> getConfigTypes() throws CommonException {
		return actualServer.getConfigTypes();
	}

	public void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		actualServer.setValueSet(li, vs);
	}

	public void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception {
		actualServer.setValue(li, key, val);
	}

	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception {
		return actualServer.getValueSet(li);
	}

	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		return actualServer.getValue(li, key);
	}

	public void reset() {
		actualServer.reset();
	}

	/**
	 * Fetches the requested configuration file from the appropriate directory.
	 * If the company name is not "defaultcompany.com" the file is retrieved
	 * from companyName; if the name is "defaultcompany.com" the file is
	 * retrieved from up-level of company if it exists, or $APP_HOME/default if
	 * not.
	 * 
	 * If a proper file cannot be found, an empty String is returned.
	 * 
	 * 
	 * @param ctx
	 *            the company-defining context
	 * @param filename
	 *            the name of the file
	 * 
	 * @return the configuration for the company, or an empty String if no
	 *         configure file is found or can be read.
	 */
	public String getConfigFile(AppContext ctx, String filename) {

		String company = ctx.getOrganization();
		File configFile = getFile(ctx, filename);
		String config = "";
		if ((configFile != null)) {
			config = readFile(configFile);
		} else {
			log.warn("no config file for company " + company);
		}
		return config;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Returns the File object corresponding to the company.
	 * 
	 * @param ctx
	 *            the context for the desired filename.
	 * 
	 * @return the File object corresponding to the specific company.
	 */
	private File getFile(AppContext ctx, String filename) {

		String company = ctx.getOrganization();

		if ((company != null)
				&& (!company.equals(ConfigConstants.DEFAULT_COMPANY))) {
			File companyFile = new File(ConfigPaths.getCompanyPath(company),
					filename);
			if (companyFile.exists()) {
				return companyFile;
			}
		}

		String defaultCfgDir = ConfigPaths.getDefaultPath();
		File defaultFile = new File(defaultCfgDir, filename);
		if (defaultFile.exists()) {
			return defaultFile;
		}

		return null;
	}

	/** Reads the file and returns the equivalent String */
	private String readFile(File file) {

		String fileContents = "";

		try {
			log.debug("loading configuration file " + file);
			InputStream in = new FileInputStream(file);
			try {
				fileContents = IOUtil.toString(in);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			log.warn("could not read contents of config file "
					+ file.getAbsolutePath());
		}

		return fileContents;
	}
}
