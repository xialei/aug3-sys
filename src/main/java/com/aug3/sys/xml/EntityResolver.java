package com.aug3.sys.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aug3.sys.properties.AppProperties;
import com.aug3.sys.properties.PropConstants;

/**
 * 
 * @author xial
 */
public class EntityResolver implements org.xml.sax.EntityResolver {

	private static final Logger LOG = Logger.getLogger(EntityResolver.class);

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		File file = new File(systemId);
		String devHome = AppProperties.getProperty(PropConstants.APP_HOME);
		String xsdPath = AppProperties.getProperty(
				PropConstants.DEFAULT_XSD_DIR,
				PropConstants.DEFAULT_XSD_DIR_VALUE);
		String fileName = devHome + xsdPath + file.getName();

		File DTDFile = new File(fileName);
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(DTDFile);
		} catch (FileNotFoundException ex) {
			LOG.warn("Could not find the XML file " + fileName);
			return null;
		}
		return new InputSource(fileReader);
	}

}
