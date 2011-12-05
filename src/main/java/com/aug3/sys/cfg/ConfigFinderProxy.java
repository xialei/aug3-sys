package com.aug3.sys.cfg;

import com.aug3.sys.CommonException;
import com.aug3.sys.CommonRuntimeException;

/**
 * This is a proxy for the LocalConfigFinder bean. It frees the user from having
 * to figure out how to connect to the server in order to retrieve information.
 * 
 * @author xial
 */
class ConfigFinderProxy {

	private LocalConfigFinder configFinder;

	ConfigFinderProxy() {
		try {
			configFinder = LocalConfigFinder.getInstance();
		} catch (CommonException e) {
			throw new CommonRuntimeException("failed retrieving config finder",
					e);
		}
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	public String getServerUrl() {
		return configFinder.getServerUrl();
	}

	public void elect() {
		configFinder.reset();
	}
}
