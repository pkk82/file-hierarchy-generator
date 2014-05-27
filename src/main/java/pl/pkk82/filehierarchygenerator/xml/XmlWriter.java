package pl.pkk82.filehierarchygenerator.xml;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlWriter {

	private XmlContent xmlContent;
	private boolean useEmptyTags = false;
	private boolean keepLineBreaks = false;
	private boolean useSpaceInEmptyTags = false;
	private boolean useDeclaration = false;

	public XmlWriter(XmlContent xmlContent) {
		this.xmlContent = xmlContent;
	}

	public void writeTo(Writer writer) {
		Document document = xmlContent.getDocument();
		Element documentElement = document.getDocumentElement();
		if (documentElement != null) {
			try {
				writeDeclaration(writer);
				writeElement(writer, documentElement);
			} catch (IOException e) {
				throw new FileContentXmlGeneratorException(e);
			}
		}
	}

	private void writeDeclaration(Writer writer) throws IOException {
		if (useDeclaration) {
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + getLineSeparator());
		}
	}

	private String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	private void writeElement(Writer writer, Element element) throws IOException {
		NodeList childNodes = element.getChildNodes();
		if (childNodes.getLength() == 0) {
			writeLastElement(writer, element);
		} else {
			writeElementWithChilds(writer, element, childNodes);
		}

	}

	private void writeLastElement(Writer writer, Element element) throws IOException {
		String tagName = element.getTagName();
		if (useEmptyTags) {
			writeLastElementAsEmpty(writer, tagName);
		} else {
			writeLastElement(writer, tagName);
		}
	}

	private void writeLastElementAsEmpty(Writer writer, String tagName) throws IOException {
		if (useSpaceInEmptyTags) {
			writer.write(createLastElementWithSpace(tagName));
		} else {
			writer.write(createLastElement(tagName));
		}

	}

	private void writeLastElement(Writer writer, String tagName) throws IOException {
		writer.write(createStartTag(tagName));
		writer.write(createEndTag(tagName));
	}

	private void writeElementWithChilds(Writer writer, Element element, NodeList childNodes) throws IOException {
		String tagName = element.getTagName();
		writer.write(createStartTag(tagName));
		writeChildren(writer, childNodes);
		writer.write(createEndTag(tagName));
	}

	private void writeChildren(Writer writer, NodeList nodeList) throws IOException {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			if (item instanceof Element) {
				writeElement(writer, (Element) item);
			}
		}
	}


	private String createStartTag(String tagName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('<');
		buffer.append(tagName);
		buffer.append('>');
		return buffer.toString();
	}

	private String createEndTag(String tagName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("</");
		buffer.append(tagName);
		buffer.append('>');
		return buffer.toString();
	}

	private String createLastElement(String tagName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('<');
		buffer.append(tagName);
		buffer.append("/>");
		return buffer.toString();
	}

	private String createLastElementWithSpace(String tagName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('<');
		buffer.append(tagName);
		buffer.append(" />");
		return buffer.toString();
	}

	public void setFormatted() {
		useEmptyTags = true;
		useSpaceInEmptyTags = true;
		keepLineBreaks = true;
	}

	public void setDeclaration() {
		useDeclaration = true;
	}
}
