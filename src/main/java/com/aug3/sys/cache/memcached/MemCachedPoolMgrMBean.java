package com.aug3.sys.cache.memcached;

/**
 * Manager bean for memcached pool
 * 
 * @author xial
 */
public interface MemCachedPoolMgrMBean {

	/**
	 * resets the memcached pools to the values in the configuration file
	 */
	void reset();

	/**
	 * @return the current configuration of the memcached pool
	 */
	String currentConfig();

}
