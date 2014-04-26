package pl.pkk82.filehierarchygenerator;


import static org.assertj.core.api.Assertions.fail;
import static pl.pkk82.filehierarchygenerator.FileHierarchyAssertions.then;
import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.io.File;

import com.google.common.collect.ImmutableList;

import org.assertj.core.api.Condition;
import org.assertj.core.api.FileAssert;
import org.junit.Test;

public class FileHierarchyGeneratorTest {

	private FileHierarchy fileHierarchy;

	private FileHierarchyGenerator fileHierarchyGenerator;

	@Test
	public void shouldCreateRootDirectory() {
		givenFileHierarchyGenerator("workspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasRootDirectoryWithName("workspace");
	}

	@Test
	public void shouldTempWorkingDirectoryNameStartsWithFhg() {
		givenFileHierarchyGenerator("workspace");
		whenGenerateFileHierarchy();
		thenTempWorkingDirectory().isDirectory().has(new Condition<File>() {
			@Override
			public boolean matches(File file) {
				return file.getName().matches("fhg\\d+");
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
		thenFileHierarchy().hasCountOfSubdirectories(1).containsDirectoriesInPath("book");
	}

	@Test
	public void shouldCreateDirectoryHierarchy() {
		// when
		givenFileHierarchyGenerator("workspace")
				.directory("book")
				.directory("spring-in-action-2011");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(2).containsDirectoriesInPath("book", "spring-in-action-2011");
	}

	@Test
	public void shouldCreateTwoSubdirectories() {
		givenFileHierarchyGenerator("workspace")
				.directory("book")
				.up()
				.directory("prv");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(2)
				.containsDirectoriesInPath("book")
				.containsDirectoriesInPath("prv");
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
		givenFileHierarchyGenerator("workspace").file("conf.iml").generate();
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(0).hasCountOfFiles(1).containsFileInPath("conf.iml");
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
				.hasCountOfSubdirectories(2)
				.hasCountOfFiles(3)
				.containsFileInPath("fileInWorkspace")
				.containsFileInPath("fileInSubdir1", "subdir1")
				.containsFileInPath("fileInSubdir11", "subdir1", "subdir11");
	}

	@Test
	public void shoudCreateEmptyFilesInSameDirectory() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.file("file2InWorkspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(0)
				.hasCountOfFiles(2)
				.containsFileInPath("file1InWorkspace")
				.containsFileInPath("file2InWorkspace");
	}

	@Test
	public void shoudCreateOneLineFile() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.line("contentOfFile1InWorkspace");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(0)
				.hasCountOfFiles(1)
				.containsFileInPathWithContent("file1InWorkspace", ImmutableList.of("contentOfFile1InWorkspace"));
	}

	@Test
	public void shoudCreateTwoLinesFile() {
		givenFileHierarchyGenerator("workspace")
				.file("file1InWorkspace")
				.line("line1")
				.line("line2");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirectories(0)
				.hasCountOfFiles(1)
				.containsFileInPathWithContent("file1InWorkspace", ImmutableList.of("line1", "line2"));
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

	private FileHierarchyAssertion thenFileHierarchy() {
		return then(fileHierarchy);
	}

	private FileAssert thenTempWorkingDirectory() {
		return then(fileHierarchy.getTempWorkingDirectoryAsFile());
	}


}