package pl.pkk82.filehierarchygenerator.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class XmlWriter {

	private XmlContent xmlContent;
	private XmlDocument xmlDocument;
	private boolean useEmptyTags = false;
	private boolean keepLineBreaks = false;
	private boolean useSpaceInEmptyTags = false;
	private boolean useDeclaration = false;
	private boolean useIndents = false;

	public XmlWriter(XmlContent xmlContent) {
		this.xmlContent = xmlContent;
	}

	void writeTo(Writer writer) {
		xmlDocument = XmlDocument.create(xmlContent);
		XmlElement documentElement = xmlDocument.getDocumentElement();
		if (documentElement != null) {
			try {
				writeDeclaration(writer);
				writeElement(writer, documentElement);
			} catch (IOException e) {
				throw new FileContentXmlGeneratorException(e);
			}
		}
	}

	void setFormatted() {
		useEmptyTags = true;
		useSpaceInEmptyTags = true;
		keepLineBreaks = true;
		useIndents = true;
	}

	void setDeclaration() {
		useDeclaration = true;
	}

	private void writeDeclaration(Writer writer) throws IOException {
		if (useDeclaration) {
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + getLineSeparator());
		}
	}

	private String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	private void writeElement(Writer writer, XmlElement element) throws IOException {
		List<XmlElement> childXmlElements = element.getChildren();
		if (childXmlElements.isEmpty()) {
			writeLastElement(writer, element);
		} else {
			writeElementWithChilds(writer, element);
		}

	}

	private void writeLastElement(Writer writer, XmlElement element) throws IOException {
		if (useEmptyTags) {
			writeLastElementAsEmpty(writer, element);
		} else {
			writeLastElementNormally(writer, element);
		}
	}

	private void writeLastElementAsEmpty(Writer writer, XmlElement xmlElement) throws IOException {
		if (useSpaceInEmptyTags) {
			writer.write(createLastElementWithSpace(xmlElement));
		} else {
			writer.write(createLastElement(xmlElement));
		}
	}

	private void writeLastElementNormally(Writer writer, XmlElement xmlElement) throws IOException {
		writer.write(createStartTag(xmlElement));
		writer.write(createEndTag(xmlElement));
	}


	private void writeElementWithChilds(Writer writer, XmlElement element) throws IOException {
		writer.write(createStartTag(element));
		writeChildren(writer, element.getChildren());
		writer.write(createEndTag(element));
	}

	private void writeChildren(Writer writer, List<XmlElement> xmlElements) throws IOException {
		for (XmlElement xmlElement : xmlElements) {
			writeElement(writer, xmlElement);
		}
	}

	private String createStartTag(XmlElement xmlElement) {
		return createElementWithStartAndStop(xmlElement, "<", ">");
	}

	private String createEndTag(XmlElement xmlElement) {
		return createElementWithStartAndStop(xmlElement, "</", ">");
	}

	private String createLastElement(XmlElement xmlElement) {
		return createElementWithStartAndStop(xmlElement, "<", "/>");
	}

	private String createLastElementWithSpace(XmlElement xmlElement) {
		return createElementWithStartAndStop(xmlElement, "<", " />");
	}

	private String createElementWithStartAndStop(XmlElement xmlElement, String beforeTag, String afterTag) {
		StringBuffer buffer = new StringBuffer();
		appendIndentTo(buffer, xmlElement);
		buffer.append(beforeTag);
		buffer.append(xmlElement.getTagName());
		buffer.append(afterTag);
		appendNewLineTo(buffer);
		return buffer.toString();
	}

	private void appendIndentTo(StringBuffer buffer, XmlElement xmlElement) {
		if (useIndents) {
			char[] indent = new char[4 * xmlElement.getLevel()];
			Arrays.fill(indent, ' ');
			buffer.append(indent);
		}
	}

	private void appendNewLineTo(StringBuffer buffer) {
		if (keepLineBreaks) {
			buffer.append(getLineSeparator());
		}
	}
}
