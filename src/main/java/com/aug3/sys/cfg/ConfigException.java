package com.aug3.sys.cfg;

import java.io.Serializable;

import com.aug3.sys.CommonException;

/**
 * 
 * @author xial
 *
 */
public class ConfigException extends CommonException implements Serializable {

	public ConfigException() {
		super();
	}

	public ConfigException(String why) {
		super(why);
	}

	public ConfigException(String why, int errCode) {
		super(why, errCode);
	}

	public ConfigException(String why, Throwable t) {
		super(why, t);
	}

	/**
	 * constructs a configuration exception with a reason, an error code, and
	 * the causal exception
	 */
	public ConfigException(String why, int errCode, Throwable t) {
		super(why, errCode, t);
	}
}
