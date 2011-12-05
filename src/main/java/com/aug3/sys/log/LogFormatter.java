package com.aug3.sys.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.aug3.sys.AppConstants;
import com.aug3.sys.AppContext;
import com.aug3.sys.properties.BootProperties;

/**
 * this format the MLogger message
 * 
 * formatted as follow:
 * 
 * | [Info, <AppContext>, Category, Time] message
 * 
 * or
 * 
 * | [Info, Topic, msgID, <AppContext>, Category, Time] message
 * 
 * 
 * @author xial
 * 
 */
public class LogFormatter extends PatternLayout {

	private static final String SP = ", ";
	private static final int maxMsgLen = BootProperties.getInstance()
			.getProperty("log4j.appender.Default.MaxMsgLength", 6000);

	/**
	 * %p -- log level, DEBUG,INFO,WARN,ERROR,FATAL
	 * 
	 * %c -- class name
	 * 
	 * %m -- message
	 * 
	 * %n -- new line, Windows : "\r\n", Unix : "\n"
	 */
	private static final String pattern = "[%p,%c{1}] %m%n";

	private static final DateFormat dateformat = new SimpleDateFormat(
			"<yyyy-MM-dd hh:mm:ss a>");

	private static String nl = System.getProperty("line.separator");

	public LogFormatter() {
		super(pattern);
	}

	public String format(LoggingEvent le) {
		if (le instanceof LogRecord) {
			return format((LogRecord) le);
		}
		return super.format(le);
	}

	public String format(LogRecord rec) {

		String dateStr = dateformat.format(rec.getDate());
		AppContext ctx = rec.getAppContext();
		String topic = rec.getTopic();
		LoggingContext lctx = rec.getLoggingContext();

		String topicStr = (topic == null ? "" : +rec.getMessageID() + SP
				+ rec.getTopic());

		StringBuilder ctxStrBuilder = new StringBuilder();
		ctxStrBuilder
				.append("<")
				.append((ctx == null ? "no-ctx" : (AppConstants.APP_NAMES[ctx
						.getAppID()] + SP + (ctx.getUser() == null ? "no-user"
						: ctx.getUser().getId()))))
				.append((lctx == null ? " " : " {" + lctx.getMessageCategory()
						+ SP + lctx.getCustomer() + SP + lctx.getUser() + SP
						+ lctx.getComponent() + SP + lctx.getSubComponent()
						+ SP + "}")).append(">");

		StringBuilder msgbuilder = new StringBuilder();
		msgbuilder.append("[").append(rec.getPriority()).append(SP)
				.append(topicStr).append(SP).append(rec.getNickName())
				.append(SP).append(ctxStrBuilder.toString()).append(SP)
				.append(dateStr).append("] ").append(rec.getMessage())
				.append(nl);

		String msg = msgbuilder.toString();

		if (msg.length() > maxMsgLen) {
			msg = msg.substring(0, maxMsgLen) + nl + "    ..." + nl
					+ "    ... log message too long, the rest is truncated";
		}

		return msg;
	}

}
