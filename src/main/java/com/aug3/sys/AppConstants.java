package com.aug3.sys;

import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.properties.PropConstants;

/**
 * This interface defines constant definitions used in the application system.
 * 
 * @author xial
 */
public interface AppConstants {

	// Default organization name for configuration
	static String DEFAULT_ORGANIZATION = BootProperties.getInstance()
			.getProperty(PropConstants.DEFAULT_ORGANIZATION);

	static boolean IS_LOCAL_CONFIG = BootProperties.getInstance().getProperty(
			PropConstants.FORCE_LOCAL_PROP, true);

	String[] APP_NAMES = { "Shared", "no-such-app1", "appcomp", "no-such-app2",
			"custmodule", "no-such-app3", "dal", "no-such-app4", "usm",
			"no-such-app5", "workflow", "no-such-app6", "report" };

	// ==========================================================================
	// Application ID definitions; these are also bitmasks for the prefs manager
	// The values need to be in powers of 2.
	// ==========================================================================
	int kAPP_SYSTEM = 0;

}
