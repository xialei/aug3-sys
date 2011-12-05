package com.aug3.sys.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A file switching appender for log4j. which will write to different files
 * depending on the value of an "id" stored in a ThreadLocal variable set
 * through the <code>setCurrentAppenderId</code> method. It will use a base
 * file name and substitute the id for the "@id@" sequence in the name and write
 * to that file.
 * 
 * The properties you can define for the appender in the log4j properties file
 * are:
 * <ol>
 * <li>filepattern - the base filename to be used</li>
 * 
 * It uses a collection of rolling appenders to do the actual work. These
 * appenders are hardcoded to use the LogFormatter pattern layout.
 * 
 * @author xial
 * 
 */
public class FileSwitchingAppender extends AppenderSkeleton {

	// ---------------------------------------------------------------------
	// CONSTANTS
	// ---------------------------------------------------------------------
	static final String DEFAULT_FILESWITCHER_ID = "fileswitcher.id.default";

	// ---------------------------------------------------------------------
	// CLASS (STATIC) FIELDS
	// ---------------------------------------------------------------------
	private static String defaultId = System
			.getProperty(DEFAULT_FILESWITCHER_ID);

	private static ThreadLocal<String> key = new ThreadLocal<String>() {
		protected String initialValue() {
			return (defaultId == null) ? "" : defaultId;
		}
	};

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------
	private Map<String, FileAppender> appenderMap = new HashMap<String, FileAppender>();
	private String filePattern;
	private int maxBackupIndex;
	private String maxFileSize;
	private boolean shouldAppend;
	private String defaultLayout;

	// ---------------------------------------------------------------------
	// STATIC METHODS
	// ---------------------------------------------------------------------

	public static void setId(String id) {
		key.set(id);
	}

	public static void resetId() {
		key.set(defaultId);
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	public void setFilePattern(String p) {
		filePattern = p;
	}

	public void setMaxBackupIndex(int i) {
		maxBackupIndex = i;

	}

	public void setMaxFileSize(String size) {
		maxFileSize = size;

	}

	public void setDefaultLayout(String l) {
		defaultLayout = l;
	}

	public void setAppend(boolean append) {
		shouldAppend = append;
	}

	protected synchronized void append(LoggingEvent loggingEvent) {
		FileAppender appender = appenderMap.get(key.get());
		if (appender == null) {
			try {
				appender = createAppenderForId(key.get());
				appenderMap.put(key.get(), appender);
			} catch (IOException e) {
				appender = appenderMap.get(defaultId);
			}
		}
		appender.doAppend(loggingEvent);
	}

	public synchronized void close() {
		for (FileAppender appender : appenderMap.values()) {
			appender.close();
		}
	}

	// we return false even though we use a layout because we do
	// the layout creation internally.
	public boolean requiresLayout() {
		return false;
	}

	@Override
	public synchronized void activateOptions() {
		super.activateOptions();
		try {
			FileAppender defaultAppender = createAppenderForId(key.get());
			appenderMap.put(defaultId, defaultAppender);
		} catch (IOException e) {
			throw new RuntimeException("unable to create appender", e);
		}
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	private FileAppender createAppenderForId(String id) throws IOException {
		String fileName = filePattern.replaceAll("@id@", id);
		FileAppender newAppender = createAppender(fileName);
		newAppender.activateOptions();
		return newAppender;
	}

	// ---------------------------------------------------------------------
	// For testability
	// ---------------------------------------------------------------------

	protected FileAppender createAppender(String filename) throws IOException {
		PatternLayout layout = new LogFormatter();
		layout.setConversionPattern(defaultLayout);
		RollingFileAppender app = new RollingFileAppender(layout, filename);
		app.setMaxBackupIndex(maxBackupIndex);
		app.setMaxFileSize(maxFileSize);
		app.setAppend(shouldAppend);
		return app;
	}

}
