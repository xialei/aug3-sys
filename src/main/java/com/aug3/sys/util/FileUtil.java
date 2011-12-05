package com.aug3.sys.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utilities for processing files.
 * 
 * @author xial
 * 
 */
public class FileUtil {

	public static List<String> getMemberFiles(String path) throws IOException {

		return getMemberFiles(new File(path), 1);
	}

	/**
	 * fetch member file names of the specified path, directories are excluded.
	 * You can specify the level as follows: 1 -- the first child level 2 -- the
	 * grand child level 3 -- the child of the grand child level ...
	 * 
	 * @param path
	 * @param level
	 * @return
	 * @throws IOException
	 */
	public static List<String> getMemberFiles(String path, int level)
			throws IOException {

		return getMemberFiles(new File(path), level);
	}

	public static List<String> getMemberFiles(File file, int level)
			throws IOException {

		if (!file.exists()) {
			throw new RuntimeException("Exception, file path does not exist, "
					+ file.getPath());
		}

		File[] files = file.listFiles();

		List<String> fileNames = new ArrayList<String>();

		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && level > 1) {
					fileNames.addAll(getMemberFiles(f, level - 1));
				} else {
					fileNames.add(f.getName());
				}
			}
		}

		return fileNames;

	}

}
