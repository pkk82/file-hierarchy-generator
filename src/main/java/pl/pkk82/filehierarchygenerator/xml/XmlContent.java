package pl.pkk82.filehierarchygenerator.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import pl.pkk82.filehierarchygenerator.IllegalInvocationException;

public class XmlContent {

	private Document document;
	private Node currentElement;


	public XmlContent() {
		createDocument();
		currentElement = document;
	}

	private void createDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new FileContentXmlGeneratorException(e);
		}
	}

	public void addElement(String tagName) {
		Element element = document.createElement(tagName);
		currentElement.appendChild(element);
		currentElement = element;
	}

	public Document getDocument() {
		return document;
	}

	public void up() {
		Node parentNode = currentElement.getParentNode();
		if (parentNode instanceof Document) {
			throw new IllegalInvocationException("up method should not be invoked in current context (root element)");
		} else if (parentNode instanceof Element) {
			currentElement = parentNode;
		} else {
			throw new IllegalStateException("should be element");
		}
	}
}
