package com.aug3.sys.cfg;

import java.util.Map;

import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.log.MLogger;

/**
 * Elector implementation used by servers. When an election is called, this
 * elector will attempt to become the new server.
 * 
 * @author xial
 */
class ServerElector implements Elector {

	private static final MLogger LOG = MLogger.getLog(ServerElector.class);

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private Map<String, String> _configInfo;
	private long _waitPeriod;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	ServerElector(Map<String, String> configInfo, long waitPeriod) {
		_configInfo = configInfo;
		_waitPeriod = waitPeriod;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	/**
	 * Registers this appserver as a candidate
	 */
	public void register() {
		_configInfo.put(AppSystem.getHostFullName(), CONFIG_CANDIDATE);
	}

	public void unregister() {
		_configInfo.put(AppSystem.getHostFullName(), NOT_CANDIDATE);
	}

	/**
	 * Runs an election by the 'power grab' method. That is, it tries to become
	 * the new server by setting the value in the config info table. After that
	 * it waits a second to see if anyone else elected itself in its place.
	 * 
	 * @return the url of the new active config server.
	 * 
	 * @throws com.aug3.sys.CommonRuntimeException
	 *             if boot property "appsvr.grp" is not defined.
	 */
	public String electNewServer() {
		String myUrl = AppSystem.getHostFullName();
		assert (myUrl != null);
		LOG.debug(myUrl + " has called for an election.");
		_configInfo.put(CONFIG_URL_KEY, myUrl);
		try {
			Thread.sleep(_waitPeriod);
		} catch (InterruptedException e) {
			throw new CommonRuntimeException("electionInterrupted");
		}

		String newUrl = _configInfo.get(CONFIG_URL_KEY);
		if (myUrl.equals(newUrl)) {
			publishElectionResult(newUrl);
		}
		LOG.debug("election winner is " + newUrl);
		return newUrl;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Publishes information about the new config server. This includes the url
	 * in string 1 and the appropriate code in long 1.
	 */
	private void publishElectionResult(String url) {
		try {
			UpdateNotification un = new UpdateNotification();
			un.setCustomLongVal1(UpdateNotification.SERVER_CHANGE_CODE);
			un.setCustomStrVal1(url);
			UpdateNotifier.publish(un);
			LOG.info(url + " has become the new config server.");
		} catch (ConfigException e) {
			LOG.warn("unable to publish election result.");
			LOG.warn(e.getMessage());
			LOG.debug(e);
		}
	}
}
