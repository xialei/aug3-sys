package com.aug3.sys.cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aug3.sys.AppSystem;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.rmi.ServiceLocator;

/**
 * A proxy elector is used by appservers that are not config servers to run an
 * election. Instead of trying to become the new server, they will look for a
 * config server and ask it to become the config server.
 * 
 * @author xial
 */
public class ProxyElector implements Elector {

	// ---------------------------------------------------------------------
	// CONSTANTS
	// ---------------------------------------------------------------------

	private static final MLogger LOG = MLogger.getLog(ProxyElector.class);

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private Map<String, String> _configInfo;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	ProxyElector(Map<String, String> configInfo) {
		_configInfo = configInfo;
	}

	// ----------------------------------------------------------------------
	// PUBLIC METHODS
	// ----------------------------------------------------------------------

	/**
	 * Looks at every config server it can find, and asks it to become to become
	 * the next config server.
	 * 
	 * @return the URL of the new candidate.
	 */
	public String electNewServer() {

		List<String> configServers = getConfigServerList();
		if (configServers.isEmpty()) {
			LOG.error("Could not find any registered config server.");
		}

		for (String serverUrl : configServers) {

			try {
				int port = BootProperties.getInstance().getProperty(
						ConfigConstants.CONFIG_RMI_PORT,
						ConfigConstants.CONFIG_RMI_PORT_DEFAULT);
				String serviceName = ConfigFinder.SERVICE_NAME;
				ConfigFinder confFinder = (ConfigFinder) ServiceLocator.locate(
						serverUrl, port, serviceName);
				return confFinder.getServerUrl();
			} catch (Exception e) {
				LOG.warn("could not connect to config server" + serverUrl);
				LOG.warn("cause: " + e.getMessage());
			}

		}
		throw new CommonRuntimeException(
				"unable to connect to any config server");
	}

	/**
	 * Registers this appserver as a candidate
	 */
	public void register() {
		_configInfo.put(AppSystem.getHostFullName(), NOT_CANDIDATE);
	}

	public void unregister() {
		_configInfo.put(AppSystem.getHostFullName(), NOT_CANDIDATE);
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------

	private List<String> getConfigServerList() {

		List<String> servers = new LinkedList<String>();

		Set<Map.Entry<String, String>> entries = _configInfo.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			if (entry.getValue().equals(CONFIG_CANDIDATE)) {
				servers.add((String) entry.getKey());
			}
		}
		return servers;
	}

}
