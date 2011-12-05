package com.aug3.sys.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aug3.sys.cache.smart.Reader;

/**
 * This very nice class builds and creates a Property instance with the
 * appropriate values. It is configured with a base directory which it will
 * traverse to find the files used to build the Properties instance.
 * 
 * The properties instance will be built from the contents of the following
 * files, presented in decreasing priority:
 * <ol>
 * <li>${basedir}/level1/level2/name.properties</li>
 * <li>${basedir}/level1/name.properties</li>
 * <li>${basedir}/level1/default/name.properties</li>
 * </ol>
 * where level1, level2 and name(e.g. appconfig) are retrieved from the fetch
 * URI.
 * 
 * @author xial
 */
class PropReader implements Reader<Properties> {

	private static final Logger LOG = Logger.getLogger(PropReader.class);

	private String basedir; // the base directory for searching.

	public PropReader(String dir) {
		basedir = dir;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	/**
	 * Creates a Properties instance suitably populated for that URI.
	 * 
	 * @param uri
	 *            a uri of the form "app-props:name:level1:level2", e.g.
	 *            "app-props:appconfig:companyA"
	 * 
	 * @return a Properties instance populated according to the rules in this
	 *         class description.
	 */
	@Override
	public Properties fetch(String uri) {
		CacheKey key = new CacheKey(uri);
		List<String> files = BootProperties.getInstance().getProperty(
				PropConstants.APP_PROPS_PATH_ENABLE, true) ? createEnabledPathList(key)
				: createPathList(key);
		Properties props = createPropertiesFromList(files);
		return props;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------
	/**
	 * If the configure properties file is always the same for different company
	 */
	private List<String> createEnabledPathList(CacheKey key) {
		String fname = key.getBasename() + ".properties";
		String enabledpath = BootProperties.getInstance().getProperty(
				PropConstants.APP_PROPS_PATH, "/");
		String path = enabledpath.equals("/") ? EnvProperties.getInstance()
				.getAppHome() : enabledpath + File.separator;
		List<String> paths = new ArrayList<String>(1);
		addIfAccessible(paths, path + fname);
		if (key.getLevel2() != null) {
			addIfAccessible(paths, path + key.getLevel2() + File.separator
					+ fname);
		}
		return paths;
	}

	/**
	 * Creates a list with the paths associated with the key. The paths are
	 * listed in ascending order of specificity (default, level1 and then
	 * level2). The files returned from the list exist and are readable.
	 * 
	 * @param key
	 *            the key for which we want the files
	 * @return a list of paths for the files.
	 */

	private List<String> createPathList(CacheKey key) {
		String fname = key.getBasename() + ".properties";
		String path = basedir + File.separator + key.getLevel1() != null ? (key
				.getLevel1() + File.separator) : "";
		List<String> paths = new ArrayList<String>(3);
		addIfAccessible(paths, path + "default" + File.separator + fname);
		addIfAccessible(paths, path + fname);
		if (key.getLevel2() != null) {
			addIfAccessible(paths, path + key.getLevel2() + File.separator
					+ fname);
		}
		return paths;
	}

	private void addIfAccessible(List<String> paths, String path) {
		File f = new File(path);
		if (f.exists() && f.canRead()) {
			paths.add(path);
		} else {
			LOG.info("skipping file as it doesn't exist or don't have read permission: "
					+ path);
		}
	}

	private Properties createPropertiesFromList(List<String> files) {
		Properties props = new Properties();
		for (String path : files) {
			try {
				InputStream in = new FileInputStream(path);
				try {
					props.load(in);
				} finally {
					in.close();
				}
			} catch (IOException e) {
				LOG.warn("Problem loading " + path + ": " + e.getMessage());
			}
		}
		return props;
	}

}
