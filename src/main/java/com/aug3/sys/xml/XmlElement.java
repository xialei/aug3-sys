package com.aug3.sys.xml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class makes it easier to use DOM Element instances. It provides simple
 * methods for getting an element's text and sub-elements.
 * 
 * DESIGN NOTES:
 * <ol>
 * <li>Class should be modified to implement org.w3c.dom</li>
 * <li>Actually, we should instead be using the Jdom package</li>
 * </ol>
 * 
 * @author xial
 */
public class XmlElement {

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private Element _element;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	public XmlElement(Element element) {
		_element = element;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	/**
	 * Returns a list of XmlElements containing all elements that are direct
	 * (one-level deep) children of this element.
	 */
	public List getChildElements() {

		List elements = new LinkedList();

		NodeList children = _element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elements.add(new XmlElement((Element) node));
			}
		}

		return elements;
	}

	/**
	 * Returns all child elements of this element that have the name defined by
	 * tagname, or an empty list otherwise.
	 * 
	 * @param tagname
	 *            the tag name of the child elements
	 * 
	 * @return a list ofXmlElement that are children of this element and whose
	 *         tagname is equal to <code>name</code>
	 */
	public List getChildElements(String tagname) {

		List children = getChildElements();
		for (Iterator it = children.iterator(); it.hasNext();) {
			XmlElement child = (XmlElement) it.next();
			if (!child.getTagName().equals(tagname)) {
				it.remove();
			}
		}
		return children;
	}

	/**
	 * Returns the text associated with this element (actually a concatenation
	 * of all the Text nodes of this element.
	 */
	public String getText() {

		StringBuffer buffer = new StringBuffer();

		NodeList children = _element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				buffer.append(node.getNodeValue());
			}
		}

		return buffer.toString();

	}

	/** Returns the attributes of this element as a Properties map */
	public Properties getAttributes() {
		Properties props = new Properties();
		NamedNodeMap attributes = _element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			props.setProperty(node.getNodeName(), node.getNodeValue());
		}
		return props;
	}

	/** Returns the tag associated with this element */
	public String getTagName() {
		return _element.getTagName();
	}

	/** Returns the underlying DOM element */
	public Element getElement() {
		return _element;
	}

	/**
	 * Returns the attribute for this element identified by the
	 * <code>attrib</code> parameter.
	 * 
	 * @param attrib
	 *            the attribute name.
	 * 
	 * @return the value of the attribute or the empty string if the attribute
	 *         is not set.
	 */
	public String getAttribute(String attrib) {
		return _element.getAttribute(attrib);
	}
}
