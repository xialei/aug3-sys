package com.aug3.sys.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author xial
 *
 */
public class DomSerializer {

	private String indent;
	private String linesep;

	public DomSerializer() {
		indent = "  ";
		linesep = "\n";

	}

	public void setLineSeparator(String linesep) {
		this.linesep = linesep;
	}

	public void serialize(Document doc, File file) throws IOException {
		Writer writer = new FileWriter(file);
		serialize(doc, writer);
	}

	public void serialize(Document doc, Writer writer) throws IOException {
		serializeNode(doc, writer, "");
		writer.flush();
	}

	public void serializeNode(Node node, Writer writer, String indentLevel)
			throws IOException {

		switch (node.getNodeType()) {

		case Node.DOCUMENT_NODE:
			writer.write("<?xml version=\"1.0\"?>");
			writer.write(linesep);

			NodeList nodes = node.getChildNodes();
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					serializeNode(nodes.item(i), writer, "");
				}
			}
			break;

		case Node.ELEMENT_NODE:
			String name = node.getNodeName();
			StringBuilder sb = new StringBuilder();
			sb.append(indentLevel).append("<").append(name);
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node current = attributes.item(i);
				sb.append(" ").append(current.getNodeName()).append("=\"")
						.append(current.getNodeValue()).append("\"");
			}
			sb.append(">");
			writer.write(sb.toString());

			NodeList children = node.getChildNodes();
			if (children != null) {
				if ((children.item(0) != null)
						&& (children.item(0).getNodeType() == Node.ELEMENT_NODE)) {

					writer.write(linesep);
				}
				for (int i = 0; i < children.getLength(); i++) {
					serializeNode(children.item(i), writer, indentLevel
							+ indent);
				}
				if ((children.item(0) != null)
						&& (children.item(children.getLength() - 1)
								.getNodeType() == Node.ELEMENT_NODE)) {

					writer.write(indentLevel);
				}
			}
			writer.write("</" + name + ">");
			writer.write(linesep);

			break;

		case Node.TEXT_NODE:
			writer.write(node.getNodeValue());
			break;

		case Node.CDATA_SECTION_NODE:
			writer.write("<<![CDATA[" + node.getNodeValue() + "]]>");
			break;

		case Node.COMMENT_NODE:
			writer.write(indentLevel + "<!-- " + node.getNodeValue() + " -->");
			writer.write(linesep);
			break;

		case Node.DOCUMENT_TYPE_NODE:
			DocumentType docType = (DocumentType) node;
			StringBuilder docTypeStrBuff = new StringBuilder();
			docTypeStrBuff.append("<!DOCTYPE ").append(docType.getName());
			if (docType.getPublicId() != null) {
				docTypeStrBuff.append(" PUBLIC \"")
						.append(docType.getPublicId()).append("\">");
			} else {
				docTypeStrBuff.append(" SYSTEM ").append("\"")
						.append(docType.getSystemId()).append("\">");
			}
			writer.write(docTypeStrBuff.toString());
			writer.write(linesep);
			break;

		case Node.ENTITY_REFERENCE_NODE:
			writer.write("&" + node.getNodeName() + ";");
			break;

		case Node.PROCESSING_INSTRUCTION_NODE:
			writer.write("<?" + node.getNodeName() + " " + node.getNodeValue()
					+ "?>");
			writer.write(linesep);
			break;
		}
	}

}
