package com.aug3.sys.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * XML parser
 * 
 * @author xial
 */
public class Parser {

	private static final Logger LOG = Logger.getLogger(Parser.class);

	public static Document parseXMLFile(Object context, String fileName) {

		return parseXMLFile(context, fileName, false);

	}

	public static Document parseXMLFile(Object context, String filename,
			boolean validate) {
		InputSource is = new InputSource(filename);
		return parseXMLFile(context, is, validate);
	}

	public static Document parseXMLFile(Object ctx, InputStream inStream) {
		return parseXMLFile(ctx, new InputSource(inStream));
	}

	public static Document parseXMLFile(Object ctx, InputSource source) {
		return parseXMLFile(ctx, source, false);
	}

	public static Document parseXMLFile(Object ctx, Reader reader) {
		return parseXMLFile(ctx, new InputSource(reader));
	}

	public static Document parseXMLFile(Object ctx, Reader reader,
			boolean validate) {
		return parseXMLFile(ctx, new InputSource(reader), validate);
	}

	public static Document parseXMLFile(Object ctx, InputSource source,
			boolean validate) {
		DOMParser parser = new DOMParser();
		parser.setErrorHandler(new ErrorHandler());
		parser.setEntityResolver(new EntityResolver());

		try {
			parser.setFeature(
					"http://apache.org/xml/features/validation/schema",
					validate);
		} catch (SAXNotRecognizedException ex) {
			LOG.warn("Could not set feature on the parser");
			LOG.warn("cause: " + ex.getMessage());
			return null;
		} catch (SAXNotSupportedException ex) {
			LOG.warn("Feature not supported by the parser");
			LOG.warn(ex.getMessage());
			return null;
		}
		try {
			parser.parse(source);
		} catch (SAXException ex) {
			LOG.warn("Failed while parsing with SAXException" + ex.getMessage());
			LOG.debug(ex);
			return null;
		} catch (IOException ex) {
			LOG.warn("Failed while parsing with IOException");
			LOG.debug(ex);
			return null;
		}
		return parser.getDocument();
	}
}
