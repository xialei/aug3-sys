package com.aug3.sys.cfg;

/**
 * Classes that implement elector participate in the process of choosing a new
 * configure server and returning its URL
 * 
 * @author xial
 */
public interface Elector {

	/** value indicating candidacy */
	String CONFIG_CANDIDATE = "SERVER CANDIDATE";

	/** value indicating it can't be a candidate */
	String NOT_CANDIDATE = "NOT CANDIDATE";

	/** The hashtable key for the configure server URL entry */
	String CONFIG_URL_KEY = "cfg.server.url";

	/** Returns the URL of the newly elected server */
	String electNewServer();

	/** Registers oneself as a server candidate */
	void register();

	/** Removes itself from server candidacy */
	void unregister();

}
