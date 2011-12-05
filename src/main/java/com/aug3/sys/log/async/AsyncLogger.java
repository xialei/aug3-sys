package com.aug3.sys.log.async;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import com.aug3.sys.util.DateUtil;

/**
 * 
 * 
 * @author xial
 * 
 */
public abstract class AsyncLogger {
	private static Level globalLevel = Level.ALL;

	private String loggerName;

	public static String DEFAULT_MODULE = "DAS-ASYNC-LOG";

	private static final String FIELD_SEPARATOR = "\t";

	private String moduleName;

	private static String DEFAULT_SUBJECT = "ASYNC.LOG";

	public static AsyncLogger getLogger(Class<?> clazz) {
		return getLogger(DEFAULT_MODULE, clazz);
	}

	public static AsyncLogger getLogger(String moduleName, Class<?> clazz) {
		return getLogger(DEFAULT_SUBJECT, moduleName, clazz.getCanonicalName());
	}

	public static AsyncLogger getLogger(String subject, String moduleName,
			Class<?> clazz) {
		return getLogger(subject, moduleName, clazz.getCanonicalName());
	}

	public static AsyncLogger getLogger(String subject, String moduleName,
			String tag) {
		return getLogger(subject, moduleName, tag);
	}

	public static AsyncLogger getLogger(String moduleName, String tag,
			String user, String password, String url) {
		return getLogger(DEFAULT_SUBJECT, moduleName, tag);
	}

	public static AsyncLogger getLogger(String subject, String moduleName,
			String tag, String user, String password, String url) {
		return new JMSLogger(subject, moduleName, tag);
	}

	public static void setLevel(Level level) {
		globalLevel = level;
	}

	public static void setDefaultModuleName(String moduleName) {
		DEFAULT_MODULE = moduleName;
	}

	protected abstract void logImpl(Level level, String message);

	public AsyncLogger(String moduleName, String tag) {
		this.moduleName = moduleName;
		this.loggerName = tag;
	}

	public void debug(String message) {
		log(Level.FINE, message);
	}

	public void info(String message) {
		log(Level.INFO, message);
	}

	public void info(Throwable t) {
		log(Level.INFO, t);
	}

	public void error(String message) {
		log(Level.SEVERE, message);
	}

	public void error(Throwable t) {
		log(Level.SEVERE, t);
	}

	public String getTag() {
		return loggerName;
	}

	public String getModuleName() {
		return moduleName;
	}

	private void log(Level level, Throwable t) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(baos));
		log(level, baos.toString());
	}

	private void log(Level level, String message) {
		if (level.intValue() < globalLevel.intValue()) {
			return;
		}
		String logMessage = formatMessage(level, message);
		logImpl(level, logMessage);
	}

	private String formatMessage(Level level, String message) {
		StringBuilder logMessage = new StringBuilder();
		String currentTime = DateUtil.getCurrentTime();
		logMessage.append(currentTime).append(FIELD_SEPARATOR)
				.append(level.getName()).append(FIELD_SEPARATOR)
				.append(getModuleName()).append(FIELD_SEPARATOR)
				.append(getTag()).append(FIELD_SEPARATOR).append(message);
		return logMessage.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	protected abstract void close();

	public static void main(String[] args) {
		AsyncLogger logger = AsyncLogger.getLogger(AsyncLogger.class);
		logger.info("with module name");
		logger.close();
	}

}
