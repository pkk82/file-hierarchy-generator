package pl.pkk82.filehierarchygenerator.xml;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import pl.pkk82.filehierarchygenerator.util.ListUtil;

public class XmlElement implements XmlPrintable {

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

	@Override
	public String toString(XmlFormatter xmlFormatter) {
		if (calculateChildCount() == 0) {
			return toStringAsLast(xmlFormatter);
		} else {
			return toStringWithChildren(xmlFormatter);
		}
	}


	private String toStringAsLast(XmlFormatter xmlFormatter) {
		if (xmlFormatter.useEmptyTags()) {
			return toStringAsLastEmptyTag(xmlFormatter);
		} else {
			return toStringAsLastNormally();
		}
	}

	private String toStringAsLastEmptyTag(XmlFormatter xmlFormatter) {
		if (xmlFormatter.useSpaceInEmptyTags()) {
			return toStringAsLastEmptyTagWithSpace();
		} else {
			return toStringAsLastEmptyTagWithoutSpace();
		}
	}

	private String toStringAsLastNormally() {
		return createStartTag() + createEndTag();
	}

	private String toStringWithChildren(XmlFormatter xmlFormatter) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(createStartTag());
		for (XmlPrintable xmlPrintable : getPrintableChildrent(xmlFormatter)) {
			buffer.append(xmlPrintable.toString(xmlFormatter));
		}
		buffer.append(createEndTag());
		return buffer.toString();
	}


	private String createStartTag() {
		return createElementWithStartAndStop("<", ">");
	}

	private String createEndTag() {
		return createElementWithStartAndStop("</", ">");
	}

	private String toStringAsLastEmptyTagWithoutSpace() {
		return createElementWithStartAndStop("<", "/>");
	}

	private String toStringAsLastEmptyTagWithSpace() {
		return createElementWithStartAndStop("<", " />");
	}

	private String createElementWithStartAndStop(String beforeTag, String afterTag) {
		return beforeTag + getTagName() + afterTag;
	}

	private int calculateChildCount() {
		NodeList childNodes = element.getChildNodes();
		return childNodes.getLength();
	}


	private List<XmlPrintable> getPrintableChildrent(XmlFormatter xmlFormatter) {
		NodeList childNodes = element.getChildNodes();
		List<XmlPrintable> xmlPrintables = Lists.newArrayList();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof Element) {
				XmlElement element = new XmlElement((Element) item, level + 1);
				xmlPrintables.add(element);
			} else if (item instanceof Text) {
				xmlPrintables.add(new XmlText((Text) item));
			}
		}
		if (!containsOneText(xmlPrintables)) {
			insertFormattingSigns(xmlPrintables, xmlFormatter);
		}
		return xmlPrintables;
	}

	private void insertFormattingSigns(List<XmlPrintable> xmlPrintables, XmlFormatter xmlFormatter) {
		XmlPrintable[] beforeAndBetween = prepareBeforeAndBetween(xmlFormatter);
		XmlPrintable[] after = prepareAfter(xmlFormatter);

		ListUtil.insertBetween(xmlPrintables, beforeAndBetween);
		ListUtil.insertBefore(xmlPrintables, beforeAndBetween);
		ListUtil.insertAfter(xmlPrintables, after);
	}

	private XmlPrintable[] prepareBeforeAndBetween(XmlFormatter xmlFormatter) {
		List<XmlPrintable> before = Lists.newArrayList();
		if (xmlFormatter.keepLineBreaks()) {
			before.add(new XmlFormattingSign(xmlFormatter.getLineSeparator()));
		}
		if (xmlFormatter.useIndents()) {
			before.add(new XmlFormattingSign(xmlFormatter.getIndent(level + 1)));
		}
		return before.toArray(new XmlPrintable[before.size()]);
	}

	private XmlPrintable[] prepareAfter(XmlFormatter xmlFormatter) {
		List<XmlPrintable> before = Lists.newArrayList();
		if (xmlFormatter.keepLineBreaks()) {
			before.add(new XmlFormattingSign(xmlFormatter.getLineSeparator()));
		}
		if (xmlFormatter.useIndents()) {
			before.add(new XmlFormattingSign(xmlFormatter.getIndent(level)));
		}
		return before.toArray(new XmlPrintable[before.size()]);
	}


	private boolean containsOneText(List<XmlPrintable> xmlElements) {
		return xmlElements.size() == 1 && Iterables.getOnlyElement(xmlElements) instanceof XmlText;
	}


}
