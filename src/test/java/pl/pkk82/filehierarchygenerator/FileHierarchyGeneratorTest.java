package pl.pkk82.filehierarchygenerator;


import static org.assertj.core.api.Assertions.fail;
import static pl.pkk82.filehierarchygenerator.FileHierarchyAssertions.then;
import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.io.File;

import com.google.common.collect.ImmutableList;

import org.assertj.core.api.Condition;
import org.assertj.core.api.FileAssert;
import org.junit.Test;

import pl.pkk82.filehierarchyassert.FileHierarchyAssert;

public class FileHierarchyGeneratorTest {

	private FileHierarchy fileHierarchy;

	private FileHierarchyGenerator fileHierarchyGenerator;

	@Test
	public void shouldCreateRootDirectory() {
		givenFileHierarchyGenerator("workspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasRootDirWithName("workspace");
	}

	@Test
	public void shouldTempWorkingDirectoryNameStartsWithFhg() {
		givenFileHierarchyGenerator("workspace");
		whenGenerateFileHierarchy();
		thenTempWorkingDirectory().isDirectory().has(new Condition<File>() {
			@Override
			public boolean matches(File file) {
				return file.getName().matches("file-hierarchy-generator-\\d+");
			}
		});
	}

	@Test
	public void shouldTempWorkingDirectoryBeInTempDirectory() {
		givenFileHierarchyGenerator("workspace");
		whenGenerateFileHierarchy();
		thenTempWorkingDirectory().hasParent(new File(System.getProperty("java.io.tmpdir")));
	}

	@Test
	public void shouldCreateDirectory() {
		givenFileHierarchyGenerator("workspace")
				.directory("book");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(1).containsSubdir("book");
	}

	@Test
	public void shouldCreateDirectoryHierarchy() {
		// when
		givenFileHierarchyGenerator("workspace")
				.directory("book")
				.directory("spring-in-action-2011");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(2).containsSubdir("spring-in-action-2011", "book");
	}

	@Test
	public void shouldCreateTwoSubdirectories() {
		givenFileHierarchyGenerator("workspace")
				.directory("book")
				.up()
				.directory("prv");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(2)
				.containsSubdir("book")
				.containsSubdir("prv");
	}

	@Test
	public void shouldValidateDirectoryNavigation() {
		try {
			givenFileHierarchyGenerator("workspace");
			whenUpInGenerator();
			fail("exception should have been thrown");
		} catch (IllegalInvocationException e) {
			then(e).isExactlyInstanceOf(IllegalInvocationException.class)
					.hasMessage("up method should not be invoked in current context (root directory)");
		}
	}

	@Test
	public void shoudCreateEmptyFileInDirectory() {
		givenFileHierarchyGenerator("workspace").file("conf.iml");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(0).hasCountOfFiles(1).containsFile("conf.iml");
	}

	@Test
	public void shoudCreateEmptyFilesInHierarchy() {
		givenFileHierarchyGenerator("workspace")
				.directory("subdir1")
				.file("fileInSubdir1")
				.directory("subdir11")
				.file("fileInSubdir11")
				.up().up()
				.file("fileInWorkspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy()
				.hasCountOfSubdirs(2)
				.hasCountOfFiles(3)
				.containsFile("fileInWorkspace")
				.containsFile("fileInSubdir1", "subdir1")
				.containsFile("fileInSubdir11", "subdir1", "subdir11");
	}

	@Test
	public void shoudCreateEmptyFilesInSameDirectory() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.file("file2InWorkspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(0)
				.hasCountOfFiles(2)
				.containsFile("file1InWorkspace")
				.containsFile("file2InWorkspace");
	}

	@Test
	public void shoudCreateOneLineFile() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.line("contentOfFile1InWorkspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(0)
				.hasCountOfFiles(1)
				.containsFileWithContent("file1InWorkspace", ImmutableList.of("contentOfFile1InWorkspace"));
	}

	@Test
	public void shoudCreateTwoLinesFile() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.line("line1")
				.line("line2");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(0)
				.hasCountOfFiles(1)
				.containsFileWithContent("file1InWorkspace", ImmutableList.of("line1", "line2"));
	}

	@Test
	public void shouldCreatePropertyFile() {
		givenFileHierarchyGenerator("workspace")
				.file("workspace.properties")
				.property("key1", "value1")
				.property("key2", "value2");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(0)
				.hasCountOfFiles(1)
				.containsFileWithContent("workspace.properties", ImmutableList.of("key1=value1", "key2=value2"));
	}

	@Test
	public void shoudValidateLineInvocation() {
		try {
			givenFileHierarchyGenerator("workspace");
			whenLineInGenerator();
			fail("exception should have been thrown");
		} catch (IllegalInvocationException e) {
			then(e).isExactlyInstanceOf(IllegalInvocationException.class)
					.hasMessage("line method should not be invoked in current context (directory)");
		}
	}

	private FileHierarchyGenerator givenFileHierarchyGenerator(String workspace) {
		fileHierarchyGenerator = createRootDirectory(workspace);
		return fileHierarchyGenerator;
	}

	private FileHierarchy whenGenerateFileHierarchy() {
		fileHierarchy = fileHierarchyGenerator.generate();
		return fileHierarchy;
	}

	private void whenUpInGenerator() {
		fileHierarchyGenerator.up();
	}

	private void whenLineInGenerator() {
		fileHierarchyGenerator.line("line1");
	}

	private FileHierarchyAssert thenFileHierarchy() {
		return then(fileHierarchy);
	}

	private FileAssert thenTempWorkingDirectory() {
		return then(fileHierarchy.getTempWorkingDirectoryAsFile());
	}


}