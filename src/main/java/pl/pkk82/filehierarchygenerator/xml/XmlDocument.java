package pl.pkk82.filehierarchygenerator.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlDocument implements XmlPrintable {
	public static XmlDocument create(XmlContent xmlContent) {
		Document document = xmlContent.getDocument();
		return new XmlDocument(document);
	}

	private final Document document;

	private XmlDocument(Document document) {
		this.document = document;
	}


	@Override
	public String toString(XmlFormatter xmlFormatter) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlFormatter.getDeclaration());
		XmlElement documentElement = getDocumentElement();
		if (documentElement != null) {
			buffer.append(documentElement.toString(xmlFormatter));
		}
		return buffer.toString();
	}

	private XmlElement getDocumentElement() {
		Element documentElement = document.getDocumentElement();
		return documentElement == null ? null : new XmlElement(documentElement);
	}
}
