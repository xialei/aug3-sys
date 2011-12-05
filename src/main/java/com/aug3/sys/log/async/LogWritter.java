package com.aug3.sys.log.async;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.aug3.sys.log.LogConfig;
import com.aug3.sys.util.DateUtil;

public class LogWritter {

	private static final String PATH = new LogConfig().getProperty(
			"asynclog.path", "");

	/**
	 * Write log with format : time\tname\tcontent\r\n
	 * 
	 * @param name
	 * @param content
	 */
	public static void writeLog(String content) {
		if (content == null)
			return;
		content = filter(content);
		StringBuilder sb = new StringBuilder();
		sb.append(content).append("\r\n");
		String file = PATH + DateUtil.getCurrentTime(DateUtil.DATE_PATTERN_CN);
		writeFile(file, sb.toString(), "utf-8", true);
	}

	/**
	 * write text file
	 * 
	 * @param url
	 *            file
	 * @param content
	 *            log content
	 * @param encoder
	 *            encoder
	 * @param append
	 *            true to append
	 * 
	 * @return boolean
	 */
	private static boolean writeFile(String url, String content,
			String encoder, boolean append) {

		File file = new File(url);
		String parent = file.getParent();
		File dir = new File(parent);
		if (!dir.exists())
			dir.mkdirs();
		FileOutputStream fos = null;

		boolean success = false;
		try {
			fos = new FileOutputStream(url, append);
			fos.write(content.getBytes(encoder));
			success = true;
		} catch (FileNotFoundException e) {
			System.err.println("file not found, " + url);
		} catch (UnsupportedEncodingException e) {
			System.err.println("UnsupportedEncodingException, " + url);
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	 * filter out \t \r \n
	 * 
	 * @param src
	 * @return
	 */
	private static String filter(String src) {
		if (src == null)
			return src;
		return src.replaceAll("\r", "").replaceAll("\n", "");
	}

}
