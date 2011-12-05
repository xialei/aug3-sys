package com.aug3.sys.util;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.aug3.sys.util.FileUtil;

@Ignore
public class FileUtilTest {

	@Test
	public void testGetMemberFiles() {

		try {
			System.out.println(FileUtil.getMemberFiles("E://", 2));
		} catch (IOException e) {
		}
	}

}
