package com.aug3.sys.log;

import java.util.Date;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import com.aug3.sys.AppContext;

/**
 * 
 * @author xial
 *
 */
@SuppressWarnings("serial")
public class LogRecord extends LoggingEvent {

	/**
	 * the main purpose of this class is to hold these extra variables
	 */
	private transient AppContext ctx = null;
	private transient Category logger = null;
	private transient Priority priority = null;
	private transient Date date = null;
	private transient String nickName = null;
	private transient int msgID = 0;
	private transient String topic = null;
	private transient LoggingContext lctx = null;

	// protected constructor
	protected LogRecord(Category logger, Priority priority, String msg,
			Throwable thrown) {

		super(logger.getName(), logger, priority, msg, thrown);
		date = new Date();
	}

	public static LogRecord getRecord(AppContext ctx, Category logger,
			Priority priority, String msg, Throwable thrown, String nickName) {
		LogRecord rec = new LogRecord(logger, priority, msg, thrown);
		rec.setAppContext(ctx);
		rec.setLogger(logger);
		rec.setPriority(priority);
		rec.setNickName(nickName);

		return rec;
	}

	/*
	 * creating this new API instead re-use the above one so that it is more
	 * explicit.
	 */
	public static LogRecord getDetailedRecord(AppContext ctx,
			LoggingContext lctx, Category logger, Priority priority,
			String topic, int msgID, String msg, Throwable thrown,
			String nickName) {

		LogRecord rec = new LogRecord(logger, priority, msg, thrown);
		rec.setAppContext(ctx);
		rec.setLogger(logger);
		rec.setPriority(priority);
		rec.setNickName(nickName);

		rec.setLoggingContext(lctx);
		rec.setTopic(topic);
		rec.setMessageID(msgID);

		return rec;
	}

	AppContext getAppContext() {
		return ctx;
	}

	public Category getLogger() {
		return logger;
	}

	Priority getPriority() {
		return priority;
	}

	Date getDate() {
		return date;
	}

	String getNickName() {
		return nickName;
	}

	String getTopic() {
		return topic;
	}

	int getMessageID() {
		return msgID;
	}

	LoggingContext getLoggingContext() {
		return lctx;
	}

	void setAppContext(AppContext ctx) {
		this.ctx = ctx;
	}

	void setLogger(Category logger) {
		this.logger = logger;
	}

	void setPriority(Priority priority) {
		this.priority = priority;
	}

	void setDate(Date date) {
		this.date = date;
	}

	void setNickName(String nickName) {
		this.nickName = nickName;
	}

	void setTopic(String topic) {
		this.topic = topic;
	}

	void setMessageID(int msgID) {
		this.msgID = msgID;
	}

	void setLoggingContext(LoggingContext lctx) {
		this.lctx = lctx;
	}

}
