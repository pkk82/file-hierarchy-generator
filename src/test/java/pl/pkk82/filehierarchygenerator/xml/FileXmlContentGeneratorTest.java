package pl.pkk82.filehierarchygenerator.xml;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;

import org.junit.Test;

import pl.pkk82.filehierarchygenerator.IllegalInvocationException;

public class FileXmlContentGeneratorTest {

	private FileContentXmlGenerator fileContentXmlGenerator;
	private FileContentXml fileContentXml;

	@Test
	public void shouldNotGenerateWhenNoFileName() {
		try {
			givenXmlFile("");
			unreachableCode();
		} catch (FileContentXmlGeneratorException e) {
			then(e).isExactlyInstanceOf(FileContentXmlGeneratorException.class)
					.hasMessage("File name cannot be blank");
		}
	}

	@Test
	public void shouldGenerateFileInDirectoryWithName() {
		givenXmlFile("empty.xml");
		whenGenerateXmlFile();
		thenFileContentXml().hasName("empty.xml").parentNameMatches("file-hierarchy-generator-\\d+");
	}

	@Test
	public void shouldGenerateFileInTempDir() {
		givenXmlFile("empty.xml");
		whenGenerateXmlFile();
		thenFileContentXml().hasGrandParent(new File(System.getProperty("java.io.tmpdir")));
	}


	@Test
	public void shouldGenerateOneTagXml() {
		givenXmlFile("root.xml").withElement("root");
		whenGenerateXmlFile();
		thenFileContentXml().containsLines("<root></root>");
	}

	@Test
	public void shouldGenerateOneTagFormattedXml() {
		givenXmlFile("root.xml").withElement("root")
				.formatted();
		whenGenerateXmlFile();
		thenFileContentXml().containsLines("<root />");
	}

	@Test
	public void shouldGenerateOneTagXmlWithDeclaration() {
		givenXmlFile("root.xml").withElement("root")
				.withDeclaration();
		whenGenerateXmlFile();
		thenFileContentXml().containsLines("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "<root></root>");
	}

	@Test
	public void shouldGenerateLeafInRoot() {
		givenXmlFile("root.xml").withElement("root").withElement("leaf");
		whenGenerateXmlFile();
		thenFileContentXml().containsLines("<root><leaf></leaf></root>");
	}

	@Test
	public void shouldGenerateLeavesInRoot() {
		givenXmlFile("root.xml").withElement("root").withElement("leaf1").up().withElement("leaf2");
		whenGenerateXmlFile();
		thenFileContentXml().containsLines("<root><leaf1></leaf1><leaf2></leaf2></root>");
	}

	@Test
	public void shouldNotAllowToUpOutsideRoot() {
		givenXmlFile("root.xml").withElement("root");
		try {
			whenUp();
			unreachableCode();
		} catch (IllegalInvocationException e) {
			then(e).isExactlyInstanceOf(IllegalInvocationException.class)
					.hasMessage("up method should not be invoked in current context (root element)");
		}
	}

	private void unreachableCode() {
		fail("Exception should be thrown");
	}

	private FileContentXmlGenerator givenXmlFile(String fileName) {
		fileContentXmlGenerator = FileContentXmlGenerator.createFile(fileName);
		return fileContentXmlGenerator;
	}

	private void whenGenerateXmlFile() {
		fileContentXml = fileContentXmlGenerator.generate();
	}

	private void whenUp() {
		fileContentXmlGenerator.up();
	}

	private FileContentXmlAssert thenFileContentXml() {
		return new FileContentXmlAssert(fileContentXml);
	}
}
