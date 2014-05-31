package pl.pkk82.filehierarchygenerator.xml;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.collect.Lists;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlElement {

	private Element element;

	private int level;

	public XmlElement(Element element) {
		this(element, 0);
	}

	private XmlElement(Element element, int level) {
		checkArgument(element != null);
		checkArgument(level >= 0);
		this.element = element;
		this.level = level;
	}

	public String getTagName() {
		return element.getTagName();
	}

	public List<XmlElement> getChildren() {
		NodeList childNodes = element.getChildNodes();
		List<XmlElement> xmlElements = Lists.newArrayList();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof Element) {
				xmlElements.add(new XmlElement((Element) item, level + 1));
			}
		}
		return xmlElements;
	}

	public int getLevel() {
		return level;
	}
}
