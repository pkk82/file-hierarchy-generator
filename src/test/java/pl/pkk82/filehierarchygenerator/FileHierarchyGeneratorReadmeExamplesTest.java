package pl.pkk82.filehierarchygenerator;

import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import org.junit.Test;
import pl.pkk82.filehierarchyassert.FileHierarchyAssert;
import pl.pkk82.filehierarchygenerator.xml.FileContentXml;
import pl.pkk82.filehierarchygenerator.xml.FileContentXmlAssert;
import pl.pkk82.filehierarchygenerator.xml.FileContentXmlGenerator;

public class FileHierarchyGeneratorReadmeExamplesTest {

	private FileHierarchyGenerator fileHierarchyGenerator;
	private FileHierarchy fileHierarchy;
	private FileContentXmlGenerator fileContentXmlGenerator;
	private FileContentXml fileContentXml;

	@Test
	public void shouldGenerateFileHierarchy() {
		givenFileHierarchyGenerator("workspace")
				.directory("subdir1")
				.file("fileInSubdir1")
				.directory("subdir11")
				.file("fileInSubdir11")
				.up().up()
				.file("fileInWorkspace")
				.line("content of fileInWorkspace")
				.generate();
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(2)
				.hasCountOfFiles(3)
				.containsFile("fileInSubdir1", "subdir1")
				.containsFile("fileInSubdir11", "subdir1", "subdir11")
				.containsFile("fileInWorkspace");
	}

	@Test
	public void shouldGenerateXmlFile() {
		givenXmlFile()
				.withElement("root")
				.withElement("subroot1")
				.withText("value")
				.up()
				.withElement("subroot2")
				.withElement("subroot21")
				.withText("text21")
				.up()
				.withElement("subroot22")
				.withText("text22")
				.formatted();
		whenGenerateXmlFile();
		thenFileContentXml().containsLines(
				"<root>",
				"    <subroot1>value</subroot1>",
				"    <subroot2>",
				"        <subroot21>text21</subroot21>",
				"        <subroot22>text22</subroot22>",
				"    </subroot2>",
				"</root>");

	}

	private void whenGenerateXmlFile() {

		fileContentXml = fileContentXmlGenerator.generate();
	}


	private FileContentXmlAssert thenFileContentXml() {
		return new FileContentXmlAssert(fileContentXml);
	}

	private FileContentXmlGenerator givenXmlFile() {
		fileContentXmlGenerator = FileContentXmlGenerator.createFile("root.xml");
		return fileContentXmlGenerator;
	}

	private FileHierarchyGenerator givenFileHierarchyGenerator(String workspace) {
		fileHierarchyGenerator = createRootDirectory(workspace);
		return fileHierarchyGenerator;
	}

	private FileHierarchy whenGenerateFileHierarchy() {
		fileHierarchy = fileHierarchyGenerator.generate();
		return fileHierarchy;
	}

	private FileHierarchyAssert thenFileHierarchy() {
		return new FileHierarchyAssert(fileHierarchy);
	}
}
