package com.aug3.sys.xml;

import org.w3c.dom.Node;

/**
 * 
 * @author xial
 */
public class XmlHelper {
	/**
	 * TODO Need to unit test? Protect the code against any ampersands that get
	 * through but that aren't part of legal XML entities.
	 * 
	 * Since this method is expected to make no changes when everything is
	 * working, it is optimized for the case when no changes are made. This
	 * method originally lived in ldap.UserPermissions, but it doesn't need to
	 * be there.
	 * 
	 * @param xml
	 *            the XML string to check
	 * @return the XML string after any needed conversions
	 */
	public static String xmlAmpersandProtection(String xml) {
		// Handle null input
		if (null == xml)
			return xml;

		// Loop for each ampersand in the XML...
		for (int i = 0; 0 <= (i = xml.indexOf('&', i)); i++) {
			// If it's part of a legal entity that we put there, skip it
			if (xml.regionMatches(i, "&amp;", 0, 5)
					|| xml.regionMatches(i, "&lt;", 0, 4)
					|| xml.regionMatches(i, "&gt;", 0, 4)
					|| xml.regionMatches(i, "&quot;", 0, 6)
					|| xml.regionMatches(i, "&euro;", 0, 6)
					|| xml.regionMatches(i, "&pound;", 0, 7)) {
				continue;
			}

			// Replace the ampersand with "&amp;"
			xml = xml.substring(0, i) + "&amp;" + xml.substring(i + 1);
		}

		// Return the result (usually what we started with)
		return xml;
	}

	/**
	 * Help Method to get Text Node Value.
	 * 
	 * @param node
	 * @return
	 */
	public static String getTextNodeValue(Node node) {

		Node textNode = node.getFirstChild();
		if (textNode != null && textNode.getNodeType() == Node.TEXT_NODE) {
			return textNode.getNodeValue();
		}

		return "";
	}

}
