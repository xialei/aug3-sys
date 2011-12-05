package com.aug3.sys.log;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.net.SyslogAppender;

import com.aug3.sys.action.AbstractAction;
import com.aug3.sys.cfg.UpdateNotification;
import com.aug3.sys.cfg.UpdateNotifier;
import com.aug3.sys.properties.BootProperties;
import com.aug3.sys.properties.PropConstants;
import com.aug3.sys.util.StringUtil;

/**
 * this class manager the behavior of our logging.
 * 
 * 
 * think of our logging category is a tree structure and normally this maps
 * directly to the java's package structure
 * 
 * 
 * you can make a node and all it's children to a specific Log Level e.g.
 * ./bin/logmgr.sh com.[company].[product].platform debug
 * 
 * will cause MLogger under the platform node to be in debug mode.
 * 
 * to reset every-node to 'INFO' mode, do:
 * 
 * ./bin/logmgr.sh -resetAll
 * 
 * 
 * @author xial
 */
public class LogMgr {

	private static final Logger LOG = Logger.getLogger(LogMgr.class);

	private static Map<String, Level> logLevel;
	private static boolean callbackRegistered;
	private static MLogger logger;

	static {

		logLevel = new HashMap<String, Level>(5);
		logLevel.put("DEBUG", Level.DEBUG);
		logLevel.put("INFO", Level.INFO);
		logLevel.put("WARN", Level.WARN);
		logLevel.put("ERROR", Level.ERROR);
		logLevel.put("FATAL", Level.FATAL);

	}

	/**
	 * set a category (and all it's sub category) to the specified log level
	 * 
	 * @param category
	 * @param level
	 */
	public static void setLogLevel(String category, String level) {

		if (level == null) {
			throw new IllegalArgumentException(
					" level of logger can't be null ");
		}

		Level p = logLevel.get(level.toUpperCase());
		if (p == null) {
			throw new IllegalArgumentException(" level not defined. level --> "
					+ level);
		}

		List<MLogger> logs = getSubLoggers(category);
		if (logs.size() == 0) {
			// create it and set the log level
			MLogger log = MLogger.getLog(category);
			log.getLogger().setLevel(p);
		} else {
			// set the DTLog (and it's sub-logs) log-level
			for (MLogger log : logs) {
				log.getLogger().setLevel(p);
			}
		}

	}

	/**
	 * 
	 * @param categoryName
	 * @return list of sub-categoies of categoryName
	 */
	private static List<MLogger> getSubLoggers(String categoryName) {

		Enumeration<?> e = LogManager.getCurrentLoggers();
		Vector<MLogger> subLoggers = new Vector<MLogger>();
		while (e.hasMoreElements()) {
			Logger logger = (Logger) e.nextElement();
			if (logger.getName().startsWith(categoryName)) {
				subLoggers.add(MLogger.getLog(logger.getName()));
			}
		}
		return subLoggers;
	}

	/**
	 * reset the MLogger configuration
	 */
	public static void reset() {
		init();
	}

	static void init() {
		PropertyConfigurator.configure(getLogProperties());
		Enumeration e = Logger.getRootLogger().getAllAppenders();
		LogFormatter formatter = new LogFormatter();
		while (e.hasMoreElements()) {
			AppenderSkeleton as = (AppenderSkeleton) e.nextElement();

			if (as.getName().equals("Default") || as.getName().equals("Syslog")) {
				as.setLayout(formatter);

				if (as.getName().equals("Syslog")) {
					// log4j configuration doesn't work for this properties
					// setting it manually
					SyslogAppender appender = (SyslogAppender) as;
					String sysLogHost = BootProperties.getInstance()
							.getProperty("log4j.appender.Syslog.SyslogHost");
					if (StringUtil.isBlank(sysLogHost)
							|| sysLogHost.startsWith("@")) {
						sysLogHost = "localhost";
						LOG.warn("log4j.appender.Syslog.SyslogHost is not defined, use localhost as default");
					}
					appender.setSyslogHost(sysLogHost);
				}

			}

			as.setThreshold(Level.DEBUG);
		}

	}

	/**
	 * This function gets the log4j properties from the boot.properties file
	 */
	private static Properties getLogProperties() {
		Properties logProps = new Properties();

		try {

			BootProperties props = BootProperties.getInstance();

			// extract log4j properties.
			for (Iterator i = props.getKeys(); i.hasNext();) {
				String key = (String) i.next();
				if (key.startsWith("log4j.")) {
					logProps.setProperty(key, props.getProperty(key));
				}
			}
		} catch (Exception e) {
			LOG.warn("failed to get custom logging properties. will use the default");
			LOG.warn("cause:" + e.getMessage());
		}

		return logProps;
	}

	/**
	 * get the state of all MLoggers
	 */
	public static String getStatus() {

		StringBuffer result = new StringBuffer("\n");

		List<MLogger> logs = getSubLoggers(MLogger.ROOT);

		Enumeration<?> e = Logger.getRootLogger().getAllAppenders();
		while (e.hasMoreElements()) {
			AppenderSkeleton as = (AppenderSkeleton) e.nextElement();
			result.append("<Appender : ").append(as.getName())
					.append("   threshold : ").append(as.getThreshold())
					.append(" />");
			result.append("\t<Layout : ").append(as.getLayout())
					.append(" />\n");
		}

		result.append("\n\tTotal MLoggers : " + logs.size() + "\n\n");

		Collections.sort(logs);
		for (MLogger log : logs) {
			result.append(log).append("\n");
		}

		return result.toString();
	}

	/**
	 * register a callback to the notifier
	 */
	synchronized static void registerCallback() {
		// cannot register this twice
		if (callbackRegistered)
			return;

		// don't do registration this for forced local config
		if (Boolean.getBoolean(PropConstants.FORCE_LOCAL_PROP)) {
			return;
		}

		// if we are here, jms is already initialized, we can register the
		// callback now.
		UpdateNotification un = new UpdateNotification();
		UpdateNotifier.addCallback(un, new AbstractAction() {
			public Object perform(Map parms) throws Exception {

				if (null == logger) {
					logger = MLogger.getLog(LogMgr.class);
				}

				logger.info("received configuration update notification. refresh logging properties");

				// reset the logging properties
				LogMgr.reset();
				return null;
			}
		}, UpdateNotifier.UPDATE_NOTIFY_CALLBACK_PRIORITY_NORMAL);

		callbackRegistered = true;
	}

	public static void main(String args[]) {

	}

}
