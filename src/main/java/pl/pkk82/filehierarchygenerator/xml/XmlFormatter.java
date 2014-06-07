package pl.pkk82.filehierarchygenerator.xml;

import java.util.Arrays;

public class XmlFormatter {

	private boolean emptyTags = false;
	private boolean lineBreaks = false;
	private boolean spaceInEmptyTags = false;
	private boolean indents = false;
	private boolean declaration = false;

	String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	void setFormatted() {
		emptyTags = true;
		spaceInEmptyTags = true;
		lineBreaks = true;
		indents = true;
	}

	public boolean useEmptyTags() {
		return emptyTags;
	}

	public boolean useSpaceInEmptyTags() {
		return spaceInEmptyTags;
	}

	public boolean keepLineBreaks() {
		return lineBreaks;
	}

	public boolean useIndents() {
		return indents;
	}

	public void setDeclaration() {
		declaration = true;
	}


	public String getDeclaration() {
		StringBuilder buffer = new StringBuilder();
		if (declaration) {
			buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			buffer.append(getLineSeparator());
		}
		return buffer.toString();
	}

	public String getNewLine() {
		StringBuilder buffer = new StringBuilder();
		if (lineBreaks) {
			buffer.append(getLineSeparator());
		}
		return buffer.toString();
	}

	public String getIndent(int level) {
		StringBuilder buffer = new StringBuilder();
		if (indents) {
			char[] indent = new char[4 * level];
			Arrays.fill(indent, ' ');
			buffer.append(indent);
		}
		return buffer.toString();
	}

}
