package com.aug3.sys.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.aug3.sys.AppContext;
import com.aug3.sys.util.ObjectIO;

/**
 * MLogger API for logging message
 * 
 * this is just a strip down version of jdk's Logger API make the API usage more
 * apparent and define some logging categories for our application.
 * 
 * 
 * usage example :
 * 
 * public static MLogger log = MLogger.getLog( ThisClass.class ); log.info(ctx,
 * " tell something meaningful here ");
 * 
 * or
 * 
 * private static MLogger log = MySuperClass.log;
 * log.info("saving preferences for user " + userID);
 * 
 * or
 * 
 * private static MLogger log = MLogger.getLog( "my.log.category" );
 * log.debug("user updating following fields " + fields + " with values " +
 * values ");
 * 
 * 
 * the first one declare it's own public log object, use it's own package path
 * to partition the log-category.
 * 
 * the second one take it's super/same category class's log object and use it.
 * 
 * the third one create it's own private log with it's own logging category name
 * space.
 * 
 * 
 * @author xial
 */
public class MLogger implements Comparable<Object> {

	public static final String TOPIC_IO = "I/O";
	public static final String TOPIC_PERMISSION = "Permission";
	public static final String TOPIC_NETWORK = "Network";
	public static final String TOPIC_FILE = "File";
	public static final String TOPIC_JNDI_LOOKUP = "JNDI-lookup";
	public static final String TOPIC_CONFIGURATION = "Configuration";
	public static final String TOPIC_JMS = "JMS";
	public static final String TOPIC_DB = "DB";
	public static final String TOPIC_INTERNAL_ERROR = "Interal_Error";
	public static final String TOPIC_USER_INPUT_ERROR = "User_Input_Error";

	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String WARN = "WARN";
	public static final String ERROR = "ERROR";

	private LoggingContext loggingCtx = null;

	public static final String ROOT = "com.hp";
	public static final String Web = ROOT + ".web";

	/**
	 * log4j's logger object
	 */
	Logger logger;
	String nickName;

	static {
		// initial LogMgr too
		LogMgr.init();
	}

	/**
	 * constructor
	 */
	protected MLogger(Logger logger) {
		this.logger = logger;
		nickName = logger.getName();
		int index = nickName.lastIndexOf('.');
		if (index != -1 && index < nickName.length())
			nickName = nickName.substring(index + 1);
	}

	public static MLogger getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	/**
	 * factory method, should go thrugh this API so we can do more interesting
	 * things later.
	 * 
	 * @param component
	 * @return
	 */
	public static MLogger getLog(String component) {
		// register a callback to the notifier
		// TODO : LogMgr.registerCallback();

		// TODO : though thin and in-expensive,
		// we should cache it when it's used extensively
		// not worth it right now.
		MLogger log = new MLogger(Logger.getLogger(component));
		return log;
	}

	/**
	 * if the simple wrapper interface is not good resort to the real one.
	 * 
	 * but should think about improving the wrapper without making it too
	 * complex first.
	 */

	public Logger getLogger() {
		return logger;
	}

	public String toString() {
		return "<MLogger " + logger.getName() + "  level: " + logger.getLevel()
				+ " />";
	}

	@Override
	public int compareTo(Object o) {
		// get the name of this logger
		String self = logger.getName();

		// the name of other logger (or just 'string' if it's not a logger
		String other = ((o instanceof MLogger) ? ((MLogger) o).getLogger()
				.getName() : o.toString());

		// compare by string.
		return self.compareTo(other);
	}

	String objectAsString(Object obj) {

		if (obj instanceof String)
			return (String) obj;

		String name = "unknown";
		if (obj != null)
			name = obj.getClass().getName();
		return "\n"
				+ ObjectIO.objectAsStringBuffer(obj, name, "", "        ")
						.toString();
	}

	/**
	 * Log message using MLoggergingContext
	 */
	private boolean logContext(Object obj, Level p) {
		if (!logger.isEnabledFor(p)) {
			// Inform caller logging doesn't need to be performed.
			return true;
		}
		if (obj instanceof LoggingContext) {
			LoggingContext lCtx = (LoggingContext) obj;
			// Create the LoggingEvent
			LogCtxRecord rec = LogCtxRecord.getRecord(logger, p,
					obj.toString(), null);
			rec.setNickName(nickName);
			rec.setLoggingContext(lCtx);
			logger.callAppenders(rec);
			// Inform caller logging has been performed.
			return true;
		} else {
			// Inform caller logging hasn't been performed
			return false;
		}
	}

	// ===========================================
	// Basic log API
	// ===========================================

	/**
	 * DEBUG msg, turn off by default put whatever worth look at, like instance
	 * variable, params
	 */
	public MLogger debug(Object obj) {
		// If we're passed a MLoggingContext, we'll use the std. format
		if (logContext(obj, Level.DEBUG))
			return this;
		String msg = objectAsString(obj);
		return this.debug(null, msg, null);
	}

	public MLogger debug(String msg) {
		return this.debug(null, msg, null);
	}

	public MLogger debug(String msg, Throwable thrown) {
		return this.debug(null, msg, thrown);
	}

	public MLogger debug(AppContext ctx, String msg) {
		return this.debug(ctx, msg, null);
	}

	public MLogger debug(AppContext ctx, String msg, Throwable thrown) {
		if (logger.isEnabledFor(Level.DEBUG))
			this.doLog(ctx, msg, thrown, Level.DEBUG);
		return this;
	}

	/**
	 * general info that's interesting. Usually turned on and it show what's the
	 * system doing.
	 */
	public MLogger info(Object obj) {
		// If we're passed a MLoggingContext, we'll use the std. format
		if (logContext(obj, Level.INFO))
			return this;
		String msg = objectAsString(obj);
		return this.info(null, msg, null);
	}

	public MLogger info(String msg) {
		return this.info(null, msg, null);
	}

	public MLogger info(String msg, Throwable thrown) {
		return this.info(null, msg, thrown);
	}

	public MLogger info(AppContext ctx, String msg) {
		return this.info(ctx, msg, null);
	}

	public MLogger info(AppContext ctx, String msg, Throwable thrown) {
		if (logger.isEnabledFor(Level.INFO))
			this.doLog(ctx, msg, thrown, Level.INFO);
		return this;
	}

	/**
	 * when there is some problem occurred. like some proc is down and need
	 * restart, turned on by default, get should provide email/pager handler
	 */
	public MLogger warn(String msg) {
		return this.warn(null, msg, null);
	}

	public MLogger warn(String msg, Throwable thrown) {
		return this.warn(null, msg, thrown);
	}

	public MLogger warn(AppContext ctx, String msg) {
		return this.warn(ctx, msg, null);
	}

	public MLogger warn(AppContext ctx, String msg, Throwable thrown) {
		if (logger.isEnabledFor(Level.WARN))
			this.doLog(ctx, msg, thrown, Level.WARN);
		return this;
	}

	/**
	 * @param msgID
	 *            this is used for (possible) action lookup.
	 */
	public void warn(AppContext ctx, String topic, int msgID, String msg) {
		this.doDetailLog(ctx, topic, msgID, msg, null, Level.WARN);

	}

	/**
	 * @param msgID
	 *            this is used for (possible) action lookup.
	 */
	public void warn(AppContext ctx, String topic, int msgID, String msg,
			Throwable thrown) {
		this.doDetailLog(ctx, topic, msgID, msg, thrown, Level.WARN);
	}

	/**
	 * something really wrong, like can't contact DB server, turned on and need
	 * email/pager handler to alert PS/developer immediately.
	 */
	public MLogger error(String msg) {
		return this.error(null, msg, null);
	}

	public MLogger error(String msg, Throwable thrown) {
		return this.error(null, msg, thrown);
	}

	public MLogger error(AppContext ctx, String msg) {
		return this.error(ctx, msg, null);
	}

	public MLogger error(AppContext ctx, String msg, Throwable thrown) {
		if (logger.isEnabledFor(Level.ERROR))
			this.doLog(ctx, msg, thrown, Level.ERROR);
		return this;
	}

	/**
	 * @param msgID
	 *            this is used for (possible) action lookup.
	 */
	public void error(AppContext ctx, String topic, int msgID, String msg) {
		this.doDetailLog(ctx, topic, msgID, msg, null, Level.ERROR);
	}

	/**
	 * @param msgID
	 *            this is used for (possible) action lookup.
	 */
	public void error(AppContext ctx, String topic, int msgID, String msg,
			Throwable thrown) {
		this.doDetailLog(ctx, topic, msgID, msg, thrown, Level.ERROR);
	}

	/**
	 * ordinary message, e.g, 'debug'. (maybe some 'info')
	 */
	private void doLog(AppContext ctx, String msg, Throwable thrown,
			Priority priority) {
		// check if we should forward this message
		if (!logger.isEnabledFor(priority))
			return;

		try {
			LogRecord record = LogRecord.getRecord(ctx, logger, priority, msg,
					thrown, nickName);
			logger.callAppenders(record);
		} catch (Throwable t) {
			// logging should never thrown any Exception to upper layer.
			try {
				Logger.getRootLogger().warn(
						" faild to log a msg --> " + msg + " logger + "
								+ logger, t);
			} catch (Throwable th) {
				th.printStackTrace();
			}

		}
	}

	/**
	 * @param ctx
	 *            the AppContext, which should have user, company, build and
	 *            other info print a detail msg, with error code and topic
	 */
	private void doDetailLog(AppContext ctx, String topic, int msgID,
			String msg, Throwable t, Priority priority) {

		// check if we should forward this mgs
		if (!logger.isEnabledFor(priority))
			return;

		try {
			LogRecord rec = LogRecord.getDetailedRecord(ctx, loggingCtx,
					logger, priority, topic, msgID, msg, t, nickName);
			logger.callAppenders(rec);

		} catch (Throwable th) {
			// logging should never thrown any Exception to upper layer.
			try {
				Logger.getRootLogger().warn(
						" faild to log a msg --> " + msg + " logger + "
								+ logger, t);
			} catch (Throwable thr) {
				// desperate measure
				t.printStackTrace();
			}
		}
	}

	/**
	 * Allow people to check before doing heavy logging work
	 */
	public boolean isDebug() {
		return logger.isEnabledFor(Level.DEBUG);
	}

	public boolean isInfo() {
		return logger.isEnabledFor(Level.INFO);
	}

	public boolean isWarn() {
		return logger.isEnabledFor(Level.WARN);
	}

	public boolean isError() {
		return logger.isEnabledFor(Level.ERROR);
	}

}
