package com.aug3.sys.xml;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author xial
 */
public class ErrorHandler implements org.xml.sax.ErrorHandler {

	private static final Logger LOG = Logger.getLogger(ErrorHandler.class);

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		printError(exception);

	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		printError(exception);

	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		printError(exception);

	}

	private void printError(SAXParseException ex) {
		LOG.warn(ex);
		LOG.warn("Warning while parsing XML file \n" + ex.getMessage());
		LOG.warn("Public ID : " + ex.getPublicId() + "   SystemID : "
				+ ex.getSystemId());
		LOG.warn("Line: " + ex.getLineNumber() + "   Column : "
				+ ex.getColumnNumber());
	}

}
