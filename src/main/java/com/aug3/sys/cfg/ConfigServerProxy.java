package com.aug3.sys.cfg;

import java.util.Map;

import com.aug3.sys.AppContext;
import com.aug3.sys.CommonException;
import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.action.AbstractAction;
import com.aug3.sys.log.MLogger;
import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.rmi.ServiceLocator;

/**
 * This class is a singleton, responsible for identifying and communicating with
 * a remote configure server in order to store/retrieve configuration
 * information.
 * 
 * @author xial
 */
public class ConfigServerProxy implements ConfigServer {

	private static final MLogger log = MLogger.getLog(ConfigServerProxy.class);

	// number of attempts at connecting before giving up
	private static final int MAX_TRIES = 3;

	// The thread-local proxy
	private static ThreadLocal<ConfigServerProxy> configServerProxy = new ThreadLocal<ConfigServerProxy>() {
		@Override
		protected ConfigServerProxy initialValue() {
			return new ConfigServerProxy();
		}
	};

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private ConfigMgr remoteServer;
	private Map<String, Object> valueSetCache;
	private String currentServerUrl;
	private boolean serverUpdateNotifDisabled = false;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS / FACTORY METHODS
	// ---------------------------------------------------------------------

	public static ConfigServer getInstance() {
		return configServerProxy.get();
	}

	private ConfigServerProxy() {
		int cacheSize = BootProperties.getInstance().getProperty(
				ConfigConstants.CONFIG_CACHE_SIZE,
				ConfigConstants.CONFIG_DEFAULT_CACHE_SIZE);
		valueSetCache = new MonitoringMap<String, Object>(cacheSize);
		reconnect();
		UpdateNotifier.addCallback(new UpdateNotification(),
				new UpdateAction(),
				ConfigConstants.UPDATE_NOTIFY_CALLBACK_PRIORITY_NORMAL);
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	public String getTextFile(AppContext ctx, String filename) {
		MgrAction action = new MgrAction() {
			public Object execute(Object[] args) {
				try {
					return remoteServer.getTextFile((AppContext) args[0],
							(String) args[1]);
				} catch (Exception e) {
					String msg = "failed for " + args[0] + ":" + args[1];
					throw new CommonRuntimeException(msg, e);
				}
			}
		};
		return (String) execute(action, new Object[] { ctx, filename });
	}

	public Long getLastModified(AppContext ctx, String filename) {
		MgrAction action = new MgrAction() {
			public Object execute(Object[] args) {
				try {
					return remoteServer.getLastModified((AppContext) args[0],
							(String) args[1]);
				} catch (Exception e) {
					String msg = "failed for " + args[0] + ":" + args[1];
					throw new CommonRuntimeException(msg, e);
				}
			}
		};
		return (Long) execute(action, new Object[] { ctx, filename });
	}

	public Map<String, ConfigType> getConfigTypes() throws CommonException {
		MgrAction action = new MgrAction() {
			public Object execute(Object[] args) {
				try {
					return remoteServer.getConfigTypes();
				} catch (Exception e) {
					throw new CommonRuntimeException(
							"failed retrieving config types", e);
				}
			}
		};
		return (Map<String, ConfigType>) execute(action, null);
	}

	public void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		MgrAction action = new MgrAction() {
			public Object execute(Object[] args) {
				try {
					remoteServer.setValueSet((ValueSetLookupInfo) args[0],
							(ValueSet) args[1]);
				} catch (Exception e) {
					String msg = "failed for "
							+ ((ValueSetLookupInfo) args[0]).getKeyString();
					throw new CommonRuntimeException(msg, e);
				}
				return null;
			}
		};
		execute(action, new Object[] { li, vs });
	}

	public void setValue(ValueSetLookupInfo li, String key, Object val)
			throws Exception {
		MgrAction action = new MgrAction() {
			public Object execute(Object[] args) {
				try {
					remoteServer.setValue((ValueSetLookupInfo) args[0],
							(String) args[1], args[2]);
				} catch (Exception e) {
					ValueSetLookupInfo li = (ValueSetLookupInfo) args[0];
					String msg = "failed for "
							+ li.getKeyString((String) args[1]);
					throw new CommonRuntimeException(msg, e);
				}
				return null;
			}
		};
		execute(action, new Object[] { li, key, val });
	}

	public ValueSet getValueSet(ValueSetLookupInfo li) throws Exception {
		ValueSet result = (ValueSet) valueSetCache.get(li.getKeyString());
		if (result == null) {
			MgrAction action = new MgrAction() {
				public Object execute(Object[] args) {
					try {
						return remoteServer
								.getValueSet((ValueSetLookupInfo) args[0]);
					} catch (Exception e) {
						String msg = "failed for "
								+ ((ValueSetLookupInfo) args[0]).getKeyString();
						throw new CommonRuntimeException(msg, e);
					}
				}
			};
			result = (ValueSet) execute(action, new Object[] { li });
			valueSetCache.put(li.getKeyString(), result);
		}
		return result;
	}

	public Object getValue(ValueSetLookupInfo li, String key) throws Exception {
		String cacheKey = li.getKeyString(key);
		Object value = valueSetCache.get(cacheKey);
		if (value == null) {
			MgrAction action = new MgrAction() {
				public Object execute(Object[] args) {
					try {
						return remoteServer.getValue(
								(ValueSetLookupInfo) args[0], (String) args[1]);
					} catch (Exception e) {
						ValueSetLookupInfo li = (ValueSetLookupInfo) args[0];
						String msg = "failed for "
								+ li.getKeyString((String) args[1]);
						throw new CommonRuntimeException(msg, e);
					}
				}
			};
			value = execute(action, new Object[] { li, key });
			valueSetCache.put(cacheKey, value);
		}
		return value;
	}

	/**
	 * Resets the configuration manager so it reconnects to the configuration
	 * server of the currently assigned appserver.
	 */
	public void reset() {
		valueSetCache.clear();
		reconnect();
	}

	/**
	 * Disables update notifications requesting server changes. Instead will
	 * rely only on EJB access-failure to determine the next server.
	 */
	public void disableServerUpdate() {
		serverUpdateNotifDisabled = true;
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Attempts to execute the task the required number of times, reconnecting
	 * to a configure server if necessary.
	 */
	private Object execute(MgrAction action, Object[] args) {

		Exception problem = null;

		int triesLeft = MAX_TRIES;
		while (triesLeft > 0) {
			try {
				return action.execute(args);
			} catch (Exception e) {
				problem = e;
				reconnect();
				triesLeft--;
			}
		}
		throw new CommonRuntimeException("failed executing action", problem);
	}

	/**
	 * Identify the application server to use and connects to it, try 3 times if
	 * needed.
	 */
	private void reconnect() {

		log.debug("Attempting a reconnect.");
		int tries = MAX_TRIES;

		// Tries to call another election if things fail.
		ConfigFinderProxy finder = new ConfigFinderProxy();

		while (tries > 0) {
			try {
				currentServerUrl = finder.getServerUrl();
				remoteServer = getManager(currentServerUrl);
				if (!remoteServer.isAlive()) {
					throw new CommonException("could not access server "
							+ currentServerUrl);
				}
				tries = 0;
			} catch (Exception e) {
				log.debug("Connection to config manager failed.");
				finder.elect();
				tries--;
				if (tries == 0) {
					throw new CommonRuntimeException(
							"unable to connect to config finder", e);
				}
			}
		}
	}

	private ConfigMgr getManager(String url) throws CommonException {
		int port = BootProperties.getInstance().getProperty(
				ConfigConstants.CONFIG_RMI_PORT,
				ConfigConstants.CONFIG_RMI_PORT_DEFAULT);
		String serviceName = ConfigMgr.SERVICE_NAME;
		ConfigMgr manager = (ConfigMgr) ServiceLocator.locate(url, port,
				serviceName);
		return manager;
	}

	// ---------------------------------------------------------------------
	// HELPER CLASSES
	// ---------------------------------------------------------------------

	/**
	 * The base class for all actions.
	 */
	private static abstract class MgrAction {
		abstract Object execute(Object[] args);
	}

	/**
	 * Called for notification updates. Will executed the perform method, which
	 * will reconnect if the message is one indicating the server changed.
	 */
	private class UpdateAction extends AbstractAction {

		/*
		 * Checks that the update notification is one for a change in the remote
		 * server (indicated by the long value 1 in the message and resets the
		 * remote ejb, if necessary.
		 * 
		 * Expects the following values in the notification: <ul>
		 * <li><em>String1</em> contains the new URL.</li> <li><em>long 1</em>
		 * contains the server change notificatin code</li> </ul>
		 */
		public Object perform(Map args) throws CommonException {

			if (serverUpdateNotifDisabled) {
				return null;
			}

			assert (args.get(UpdateNotifier.NOTIFICATION) != null);
			UpdateNotification un = (UpdateNotification) args
					.get(UpdateNotifier.NOTIFICATION);

			if ((un != null)
					&& (un.getCustomLongVal1() == UpdateNotification.SERVER_CHANGE_CODE)) {
				String nextServerUrl = un.getCustomStrVal1();
				log.debug("updating config server to " + nextServerUrl);
				if (!currentServerUrl.equals(nextServerUrl)) {
					currentServerUrl = nextServerUrl;
					remoteServer = getManager(currentServerUrl);
				}
			}

			return null;
		}

	}

}
