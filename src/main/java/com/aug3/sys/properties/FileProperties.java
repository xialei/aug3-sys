package com.aug3.sys.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.aug3.sys.AppContext;
import com.aug3.sys.CommonException;
import com.aug3.sys.cfg.ConfigPaths;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.util.StringUtil;

/**
 * This class provides functionality to access properties from properties file.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class FileProperties implements Serializable {

	private final static MLogger log = MLogger.getLog(FileProperties.class);

	protected String propName;
	protected FilePropertiesEntry defaultProps;
	protected Map<String, FilePropertiesEntry> companyProps = new Hashtable<String, FilePropertiesEntry>();
	protected Set<String> companyPropsInCache = new HashSet<String>(5);

	// ==========================================================================
	// constructors
	// ==========================================================================
	/**
	 * constructor with the properties file name
	 * 
	 * @param name
	 *            the properties file name
	 * @throws CommonException
	 */
	public FileProperties(String name) throws CommonException {
		// set the file name of the properties
		propName = name;

		// load the properties from file
		load();
	}

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Returns the properties set name.
	 * 
	 * @return properties set name
	 */
	String getName() {
		return propName;
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns null
	 * if the property is not found.
	 * 
	 * @param ctx
	 *            the execution context
	 * @param key
	 *            the hashtable key
	 * @return the value in this property list with the specified key
	 * @throws CommonException
	 */
	public String getProperty(AppContext ctx, String key)
			throws CommonException {
		// get the properties by company
		if (ctx == null) {
			throw new CommonException("AppContext is a required argument!");
		}
		FilePropertiesEntry propEntry = getFilePropertiesEntryForCompany(ctx
				.getOrganization());

		// return the value from the properties list
		return propEntry.props.getProperty(key);
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns the
	 * default value argument if the property is not found.
	 * 
	 * @param ctx
	 *            the execution context
	 * @param key
	 *            the hashtable key
	 * @param dft
	 *            a default value
	 * @return the value in this property list with the specified key
	 * @throws CommonException
	 */
	String getProperty(AppContext ctx, String key, String dft)
			throws CommonException {
		String val = getProperty(ctx, key);

		return null != val ? val : dft;
	}

	/**
	 * Calls the Hashtable method put. Provided for parallelism with the
	 * getProperty method. Enforces use of strings for property keys and values.
	 * The value returned is the result of the Hashtable call to put.
	 * 
	 * @param ctx
	 *            the execution context
	 * @param key
	 *            the key to be placed into this property list
	 * @param val
	 *            the value corresponding to key
	 * @return the previous value of the specified key in this property list, or
	 *         null if it did not have one.
	 * @throws CommonException
	 */
	Object setProperty(AppContext ctx, String key, String val)
			throws CommonException {
		// get the properties by company
		FilePropertiesEntry fpe = getFilePropertiesEntryForCompany(ctx
				.getOrganization());

		// safe guard the value
		if (StringUtil.isBlank(val)) {
			return fpe.props.remove(key);
		} else {
			return fpe.props.setProperty(key, val);
		}
	}

	/**
	 * Resets a property value to its default. This means taking the overwrite
	 * value from the company specific storage so the lookup will return the
	 * default value.
	 * <p>
	 * 
	 * @param ctx
	 *            the execution context
	 * @param key
	 *            the property key
	 * @throws CommonException
	 */
	void resetProperty(AppContext ctx, String key) throws CommonException {
		FilePropertiesEntry fpe = getFilePropertiesEntryForCompany(ctx
				.getOrganization());

		fpe.props.remove(key);
	}

	/**
	 * Writes the values in the properties set to the backing storage.
	 * 
	 * @param ctx
	 *            the execution context
	 * @throws CommonException
	 */
	void storeProperties(AppContext ctx) throws CommonException {
		// find the company name from the context
		String cn = ctx.getOrganization();

		// get the properties by company
		FilePropertiesEntry fpe = getFilePropertiesEntryForCompany(cn);

		// writes the new value to the backing store
		fpe.store();
	}

	/**
	 * Returns all the properties from the file. It recognizes the company name
	 * from the <code>AppContext</code>. Therefore the returned values will have
	 * the over-ridden values for the company.
	 * 
	 * @param ctx
	 *            the execution context
	 * @throws CommonException
	 */
	Properties getProperties(AppContext ctx) throws CommonException {
		// get the properties by company
		FilePropertiesEntry fpe = getFilePropertiesEntryForCompany(ctx
				.getOrganization());

		// return the properties
		return fpe.props;
	}

	protected FilePropertiesEntry getFilePropertiesEntryForCompany(String cn) {

		synchronized (companyPropsInCache) {
			if (!companyPropsInCache.contains(cn)) {
				try {
					companyPropsInCache.add(cn);
					getFilePropertiesEntry(cn);
				} catch (CommonException e) {
				}
			}
		}

		FilePropertiesEntry fpe = (FilePropertiesEntry) companyProps.get(cn);

		if (fpe == null) {
			fpe = defaultProps;
		}
		return fpe;
	}

	/**
	 * dump the loaded properties for debugging
	 */
	void dump() {
		defaultProps.props.list(System.out);
	}

	/**
	 * load the properties from the file storage
	 * 
	 * @throws CommonException
	 */
	protected synchronized void load() throws CommonException {
		Properties ps = new Properties();

		String fn = ConfigPaths.getDefaultPathName(propName
				+ ConfigPaths.PROPERTIES_SUFFIX);
		log.debug("Default file==>" + fn);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fn);
			ps.load(fis);
		} catch (FileNotFoundException e) {
			// no updated properties file, that means there's no custom values
		} catch (IOException e) {
			String msg = "failed to read properties from file [" + fn + "]";
			log.warn(null, MLogger.TOPIC_IO, 1002, msg, e);
			throw new CommonException(msg, e);
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				log.warn(null, MLogger.TOPIC_IO, 1002,
						"failed to close file handle [" + fn + "]", e);
			}
		}

		// load the updated properties that apply to all companies
		ps = new Properties(ps);
		fn = ConfigPaths.getAllCompaniesPathName(propName
				+ ConfigPaths.PROPERTIES_SUFFIX);
		log.debug("Company default file==>" + fn);
		try {
			fis = new FileInputStream(fn);
			ps.load(fis);
		} catch (FileNotFoundException e) {
			// no updated properties file, that means there's no custom values
		} catch (IOException e) {
			throw new CommonException("failed to read properties from file ["
					+ fn + "]", e);
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (IOException e) {
				log.warn(null, MLogger.TOPIC_IO, 1002,
						"failed to close file handle [" + fn + "]", e);

			}
		}
		defaultProps = new FilePropertiesEntry(fn, ps);

	}

	/**
	 * get the company specific properties
	 */
	protected FilePropertiesEntry getFilePropertiesEntry(String cn)
			throws CommonException {
		// if company is null, use the default
		if (null == cn) {
			return defaultProps;
		}

		// check whether we already cached properties for the company
		FilePropertiesEntry fpe = (FilePropertiesEntry) companyProps.get(cn);

		if (null == fpe) {
			Properties ps = new Properties(defaultProps.props);

			// load the company specific values from files
			String fn = ConfigPaths.getCompanyPathName(cn, propName
					+ ConfigPaths.PROPERTIES_SUFFIX);
			if (fn == null) {
				throw new CommonException("Called path without valid arguments"
						+ " ,org=>" + cn);
			}
			log.debug("Company file==>" + fn);

			FileInputStream fis = null;
			try {
				fis = new FileInputStream(fn);
				ps.load(fis);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				throw new CommonException(
						"failed to read properties from file [" + fn + "]", e);
			} finally {
				try {
					if (null != fis)
						fis.close();
				} catch (IOException e) {
					log.warn(null, MLogger.TOPIC_IO, 1002,
							"failed to close file handle [" + fn + "]");
				}
			}

			// create entry to hold the properties
			fpe = new FilePropertiesEntry(fn, ps);

			// add the company properties to cache
			companyProps.put(cn, fpe);
		}

		// return the company specific properties
		return fpe;
	}

	/**
	 * This class defines a data structure that contains a Properties object and
	 * the file that backs up the object. The file kept as a full path file
	 * name.
	 */
	protected class FilePropertiesEntry implements Serializable {

		String filename;
		Properties props;

		protected FilePropertiesEntry(String fn, Properties ps) {
			filename = fn;
			props = ps;
		}

		/**
		 * writes the new value to the backing store
		 * 
		 * @throws CommonException
		 */
		protected void store() throws CommonException {
			FileOutputStream fos = null;
			try {
				// check whether a file already exists for the backing store
				File f = new File(filename);

				// if the file is not there yet, create it
				if (!f.exists()) {
					// get the directory path from the file name
					String dirPath = filename.substring(0,
							filename.lastIndexOf(ConfigPaths.FS));

					// make sure the directory exists
					File dir = new File(dirPath);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					f.createNewFile();
				}

				// open the file for writing
				fos = new FileOutputStream(f);
				props.store(fos, "#");
			} catch (IOException e) {
				throw new CommonException(
						"failed, to write properties to file [" + filename
								+ "]", e);
			} finally {
				// clean up resource
				try {
					if (null != fos)
						fos.close();
				} catch (IOException e) {
					log.warn(null, MLogger.TOPIC_IO, 1002,
							"failed to close file handle [" + fos + "]", e);
				}
			}
		}
	}

}
