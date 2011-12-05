package com.aug3.sys.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class IOUtil {

	public static String toString(InputStream inputStream) throws IOException {

		return IOUtils.toString(inputStream);

	}

}
