package com.aug3.sys.log;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * 
 * @author xial
 *
 */
@SuppressWarnings("serial")
public class LogCtxRecord extends LogRecord {
	private LoggingContext m_lCtx = null;

	// private constructor
	private LogCtxRecord(Category logger, Priority priority, String msg,
			Throwable thrown) {
		super(logger, priority, msg, thrown);
	}

	public static LogCtxRecord getRecord(Category logger, Priority priority,
			String msg, Throwable thrown) {

		return new LogCtxRecord(logger, priority, msg, thrown);
	}

	public void setLoggingContext(LoggingContext lCtx) {
		m_lCtx = lCtx;
	}

	public LoggingContext getLoggingContext() {
		return m_lCtx;
	}

}