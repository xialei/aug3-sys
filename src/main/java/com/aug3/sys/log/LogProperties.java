package com.aug3.sys.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.aug3.sys.CommonException;
import com.aug3.sys.properties.AppProperties;
import com.aug3.sys.properties.PropConstants;

/**
 * 
 * @author xial
 *
 */
@SuppressWarnings("serial")
public class LogProperties extends Properties {

	private static final String FS = File.separator;
	private static final String LOG_FILE_NAME = "MLogging";
	private static final String LOG_FILE_EXT = ".properties";
	private static final String LOG_RELATIVE_PATH = FS + "lib" + FS + "client"
			+ FS;

	private static String LOG_PATHNAME;
	static {
		LOG_PATHNAME = AppProperties.getProperty(PropConstants.APP_HOME);
		LOG_PATHNAME += LOG_RELATIVE_PATH + LOG_FILE_NAME + LOG_FILE_EXT;
	}
	private static LogProperties props = null;

	/**
	 * Fetches an instance of LogProperties using the internally specified file
	 */
	public static LogProperties getInstance() throws CommonException {
		if (props == null)
			props = new LogProperties();
		return props;
	}

	private LogProperties() throws CommonException {
		init(LOG_PATHNAME);
	}

	private void init(String strFile) throws CommonException {
		try {
			FileInputStream fis = new FileInputStream(strFile);
			load(fis);
		} catch (FileNotFoundException exFne) {
			throw new CommonException(
					"2:Error:File I/O::Platform:Logging:There was a deployment issue because the logging component can't find file "
							+ strFile, exFne);
		} catch (IOException exIO) {
			throw new CommonException(
					"1:Error:File I/O::Platform:Logging:The logging component encountered an error occurred reading file "
							+ strFile, exIO);
		}
	}

	public String getProperty(int iKey) {
		return getProperty(String.valueOf(iKey));
	}

}
