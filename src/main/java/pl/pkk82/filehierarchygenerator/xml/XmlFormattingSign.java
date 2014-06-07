package pl.pkk82.filehierarchygenerator.xml;

public class XmlFormattingSign implements XmlPrintable {


	private final String text;

	public XmlFormattingSign(String text) {
		this.text = text;
	}


	@Override
	public String toString(XmlFormatter xmlFormatter) {
		return text;
	}

}
