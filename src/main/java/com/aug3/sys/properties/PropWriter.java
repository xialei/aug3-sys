package com.aug3.sys.properties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * A PropWriter is used to write properties to the appropriate file. Writing
 * properties is an expensive operation, involving:
 * <ol>
 * <li>creating a lock file</li>
 * <li>reading the current properties file</li>
 * <li>adding the new value</li>
 * <li>saving the property file</li>
 * <li>deleting the lock file</li>
 * </ol>
 * 
 * @author xial
 */
public class PropWriter {

	private static final long FIVE_MINUTES = 5 * 60 * 1000; // in milliseconds

	private static final Logger LOG = Logger.getLogger(PropWriter.class);

	private final String basedir;

	PropWriter(String base) {
		basedir = base;
	}

	void write(CacheKey key, String property, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(property, value);
		writeAll(key, data);
	}

	void writeAll(CacheKey key, Map<String, String> data) {
		String filename = basedir + File.separator + key.getUriFilePath();
		File lockFile = getLockFile(key);
		removeLockIfStale(lockFile);
		try {
			lock(lockFile);
			try {
				Properties props = loadProps(filename);
				props.putAll(data);
				storeProps(filename, props);
			} finally {
				unlock(lockFile);
			}
		} catch (IOException e) {
			LOG.error("Could not write properties for " + key.getUriFilePath());
			LOG.error("cause: " + e.getMessage());
			LOG.error("changes will be ignored");
		}
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Removes the current lock file if it is older than 5 minutes.
	 * 
	 * This might be necessary if a process that owned the lock is terminated
	 * abruptly and leaves the lock behind. Since writing the file should not
	 * take more than a second, we can assume locks that have been around for 5
	 * minutes or more are really old and should be removed.
	 * 
	 * @param lockFile
	 *            the lockfile to be removed.
	 */
	private void removeLockIfStale(File lockFile) {
		if (lockFile.exists()) {
			long timeCreated = lockFile.lastModified();
			long age = System.currentTimeMillis() - timeCreated;
			if (age > FIVE_MINUTES) {
				LOG.warn("Stale lock file " + lockFile + " found.");
				LOG.warn("Some other process might have left properties.file in an inconsistent state.");
				LOG.warn("Stale lock will be removed");
				lockFile.delete();
			}
		}
	}

	private Properties loadProps(String filename) throws IOException {
		Properties props = new Properties();
		Reader in = getReader(filename);
		try {
			props.load(in);
		} finally {
			in.close();
		}
		return props;
	}

	private void storeProps(String filename, Properties props)
			throws IOException {
		LOG.debug("save file: " + filename);

		// create parent directories if doesn't exist first
		File directory = new File(new File(filename).getParent());
		if (!directory.exists()) {
			if (directory.mkdirs()) {
				LOG.debug("created directory : " + directory.getPath());
			} else {
				LOG.warn("failed to create directory : " + directory.getPath());
			}
		}

		Writer out = getWriter(filename);
		try {
			props.store(out, null);
		} finally {
			out.close();
		}
	}

	private void lock(File f) throws IOException {
		try {
			while (!f.createNewFile()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			LOG.warn("interrupted while trying to create lock file " + f);
			throw new RuntimeException("interrupted!", e);
		}
	}

	private void unlock(File f) {
		if (!f.delete()) {
			LOG.error("Unable to delete lock file " + f);
			LOG.error("Processe will no longer be able to update files!");
		}
	}

	// ---------------------------------------------------------------------
	// TESTABILITY METHODS
	// ---------------------------------------------------------------------

	protected File getLockFile(CacheKey key) {
		return new File(basedir + File.separator + key.getLevel1()
				+ File.separator + key.getBasename() + ".lock");
	}

	protected Reader getReader(String filename) throws IOException {
		Reader reader;
		File file = new File(filename);
		if (file.exists() && file.isFile()) {
			reader = new FileReader(filename);
		} else {
			reader = new StringReader("");
		}
		return reader;
	}

	protected Writer getWriter(String filename) throws IOException {
		return new FileWriter(filename);
	}

}
