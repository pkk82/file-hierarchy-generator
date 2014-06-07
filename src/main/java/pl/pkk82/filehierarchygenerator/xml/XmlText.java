package pl.pkk82.filehierarchygenerator.xml;

import org.w3c.dom.Text;

public class XmlText implements XmlPrintable {


	private final Text text;

	public XmlText(Text text) {
		this.text = text;
	}

	@Override
	public String toString(XmlFormatter xmlFormatter) {
		return text.getWholeText();
	}

}
