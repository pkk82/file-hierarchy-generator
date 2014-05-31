package pl.pkk82.filehierarchygenerator.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlDocument {
	public static XmlDocument create(XmlContent xmlContent) {
		Document document = xmlContent.getDocument();
		return new XmlDocument(document);
	}

	private final Document document;

	private XmlDocument(Document document) {
		this.document = document;
	}

	public XmlElement getDocumentElement() {
		Element documentElement = document.getDocumentElement();
		return documentElement == null ? null : new XmlElement(documentElement);
	}
}
