package com.aug3.sys;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.properties.PropConstants;

/**
 * This class contains runtime system information such as track, appserver to
 * use, etc. Some of this information is thread-specific, others are global
 * (within the JVM).
 * 
 * @author xial
 **/
public class AppSystem {

	private static final Logger log = Logger.getLogger(AppSystem.class);

	private final static String SERVER_URL_PROP = "java.naming.provider.url";

	// ==========================================================================
	// public members
	// ==========================================================================

	/** The name of this host, including domain path. */
	private static String hostFullName;

	/** The name of this host, without domain path. */
	private static String hostName;

	/** The default naming context on the local server */
	private static ThreadLocal<InitialContext> localCtx;

	/** the track name */
	private static ThreadLocal<String> trackName;

	/** local cfg, don't hit AppServer unless forced */
	public static boolean isLocalCfg = false;

	/** The default listening port of the App Server. */
	public static short serverPort = 8080;

	/** indicate whether we are running within in the app server */
	private static boolean inServer;

	private static boolean inProc = false;

	// =====================================================================
	// constructors / initializers
	// =====================================================================
	static {
		try {
			InetAddress hostAddr = InetAddress.getLocalHost();
			hostFullName = hostAddr.getHostName();
			hostName = hostFullName.split("\\.")[0];
			isLocalCfg = BootProperties.getInstance().getProperty(
					PropConstants.FORCE_LOCAL_PROP, true);
			trackName = new DefaultValueThreadLocal<String>(getPartitionName());
		} catch (UnknownHostException e) {
			throw new RuntimeException("failed to initialize host information",
					e);
		}
		try {
			localCtx = new DefaultValueThreadLocal<InitialContext>(
					createInitialContext());
			inServer = isRunningInServer();
		} catch (NamingException e) {
			log.warn("Failed to initialize the local context");
			log.warn(e);
		}
	}

	private static boolean isRunningInServer() throws NamingException {
		String url = (String) localCtx.get().getEnvironment()
				.get(SERVER_URL_PROP);
		return (null == url || 0 == url.length());
	}

	private static InitialContext createInitialContext() throws NamingException {
		String serverUrl = System.getProperty(SERVER_URL_PROP);
		InitialContext ctx = null;
		if (serverUrl != null) {
			try {
				ctx = createContext(serverUrl);
				log.info(("Target Host: " + localCtx.get().getEnvironment()
						.get(SERVER_URL_PROP)));
			} catch (Exception e) {
				log.info(("Failed to create initial Context for url " + serverUrl));
				log.info("Using default initial context");
				ctx = new InitialContext();
			}
		} else {
			ctx = new InitialContext();
		}
		return ctx;
	}

	/**
	 * Gets a partition name. First sees if there is a System property defining
	 * it, and if not gets it from boot properties.
	 * 
	 * @return the partition name
	 */
	private static String getPartitionName() {
		String partition = System.getProperty(PropConstants.PARTITION_NAME);
		if (partition != null) {
			return partition;
		}
		return BootProperties.getInstance().getProperty(
				PropConstants.PARTITION_NAME, hostName);
	}

	// ==========================================================================
	// public methods
	// ==========================================================================

	public static String getTrackName() {
		return trackName.get();
	}

	public static void setTrackName(String newTrackName) {
		trackName.set(newTrackName);
	}

	public static String getHostFullName() {
		return hostFullName;
	}

	public static String getHostName() {
		return hostName;
	}

	public static InitialContext getContext() {
		return localCtx.get();
	}

	/**
	 * @return true if the code is running inside an appserver.
	 */
	public static boolean inServer() {
		return inServer;
	}

	/**
	 * Sets the thread's JDNI Context to the specified host
	 */
	public static void setJNDIHost(String host) throws Exception {
		localCtx.set(createContext(host));
		log.info(("Target Host: " + localCtx.get().getEnvironment()
				.get(SERVER_URL_PROP)));
	}

	/**
	 * @return this thread's JNDI host name
	 * @throws NamingException
	 */
	public static String getJNDIHost() throws NamingException {
		String host = (String) localCtx.get().getEnvironment()
				.get(Context.PROVIDER_URL);
		return (host != null) ? host : hostFullName;
	}

	/**
	 * Returns a new InitialContext object for that particular host, getting all
	 * other properties from the jndi.properties in the classpath
	 * 
	 * @param host
	 *            the URL for the new host.
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	public static InitialContext createContext(String host) throws IOException,
			NamingException {
		Properties env = new Properties();
		InputStream in = null;
		try {
			in = AppSystem.class.getResourceAsStream("/jndi.properties");
			env.load(in);
		} catch (IOException e) {
			log.warn("unable to load jndi.properties file.");
			log.warn("cause: " + e.getMessage());
			log.debug(e);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		log.info("JNDI host set to " + host);
		env.put(Context.PROVIDER_URL, host);
		return new InitialContext(env);
	}

	/**
	 * Set whether we should operate in "local config" mode.
	 * 
	 * @param isLocal
	 *            if true, don't use the app server unless forced.
	 */
	public static void setLocalConfig(boolean isLocal) {
		isLocalCfg = isLocal;
	}

	/**
	 * Returns true if we are running inside a proc. Note that this value must
	 * be set by the proc itself;
	 * 
	 * @return true if inside a proc, false otherwise.
	 */
	public static synchronized boolean inProc() {
		return inProc;
	}

	/**
	 * Sets the "in proc" state to the value specified as a parameter.
	 * 
	 * @param value
	 *            whether we are inside a proc or not.
	 */
	public static synchronized void setInProc(boolean value) {
		inProc = value;
	}
}
