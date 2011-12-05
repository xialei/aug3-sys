package com.aug3.sys.cfg;

import com.aug3.sys.CommonException;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.rmi.IConfigFinder;

/**
 * 
 * delegates all the work to the LocalConfigFinder singleton.
 * 
 * @author xial
 */
public class ConfigFinder implements IConfigFinder {

	/**
	 * 
	 * @return the active config server url
	 * 
	 */
	public String getServerUrl() {
		try {
			LocalConfigFinder finder = LocalConfigFinder.getInstance();
			return finder.getServerUrl();
		} catch (CommonException e) {
			throw new CommonRuntimeException("Config finder failure", e);
		}
	}

	public void reset() {
		try {
			LocalConfigFinder finder = LocalConfigFinder.getInstance();
			finder.reset();
		} catch (CommonException e) {
			throw new CommonRuntimeException("Config finder failure", e);
		}
	}

}
