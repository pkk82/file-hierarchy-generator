package pl.pkk82.filehierarchygenerator.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import pl.pkk82.filehierarchygenerator.util.TempWorkingDirectoryCreator;

public final class FileContentXmlGenerator {

	private final TempWorkingDirectoryCreator tempWorkingDirectoryCreator;
	private final XmlContent xmlContent;
	private final XmlWriter xmlWriter;
	private Path directory;
	private final String fileName;

	public static FileContentXmlGenerator createFile(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			throw new FileContentXmlGeneratorException("File name cannot be blank");
		}
		return new FileContentXmlGenerator(fileName);
	}

	public FileContentXmlGenerator withElement(String tagName) {
		xmlContent.addElement(tagName);
		return this;
	}


	public FileContentXmlGenerator up() {
		xmlContent.up();
		return this;
	}

	public FileContentXmlGenerator withDirectory(Path directory) {
		this.directory = directory;
		return this;
	}

	public FileContentXmlGenerator formatted() {
		xmlWriter.setFormatted();
		return this;
	}

	public FileContentXmlGenerator withDeclaration() {
		xmlWriter.setDeclaration();
		return this;
	}

	public FileContentXml generate() {
		Path path = directory.resolve(fileName);
		try {
			Files.createFile(path);
			StringWriter stringWriter = new StringWriter();
			xmlWriter.writeTo(stringWriter);
			Files.write(path, Arrays.asList(stringWriter.toString()), Charset.forName("utf8"));
		} catch (IOException e) {
			throw new FileContentXmlGeneratorException(e);
		}
		return new FileContentXml(path);
	}

	private FileContentXmlGenerator(String fileName) {
		this.fileName = fileName;
		this.tempWorkingDirectoryCreator = new TempWorkingDirectoryCreator();
		try {
			this.directory = tempWorkingDirectoryCreator.createTempWorkingDirectory();
		} catch (IOException e) {
			throw new FileContentXmlGeneratorException(e);
		}
		this.xmlContent = new XmlContent();
		this.xmlWriter = new XmlWriter(xmlContent);
	}
}
