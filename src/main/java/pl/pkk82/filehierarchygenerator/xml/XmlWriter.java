package pl.pkk82.filehierarchygenerator.xml;

import java.io.StringWriter;

public class XmlWriter {

	private XmlFormatter xmlFormatter;
	private StringWriter stringWriter = new StringWriter();


	public XmlWriter(XmlFormatter xmlFormatter) {
		this.xmlFormatter = xmlFormatter;
	}

	public void write(XmlContent xmlContent) {
		XmlDocument xmlDocument = XmlDocument.create(xmlContent);
		stringWriter.write(xmlDocument.toString(xmlFormatter));
	}

	@Override
	public String toString() {
		return stringWriter.toString();
	}
}
