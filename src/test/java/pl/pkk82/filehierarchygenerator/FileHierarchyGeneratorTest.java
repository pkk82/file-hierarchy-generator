package pl.pkk82.filehierarchygenerator;


import static org.assertj.core.api.Assertions.fail;
import static pl.pkk82.filehierarchygenerator.FileHierarchyAssertions.then;
import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.google.common.collect.ImmutableList;

import org.assertj.core.api.Condition;
import org.assertj.core.api.FileAssert;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import pl.pkk82.filehierarchyassert.FileHierarchyAssert;

public class FileHierarchyGeneratorTest {

	private FileHierarchy fileHierarchy;

	private FileHierarchyGenerator fileHierarchyGenerator;

	private Exception exception;

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
	public void shouldCreateDirectoriesWithTwoInvocations() {
		givenFileHierarchyGenerator("workspace")
				.directory("book")
				.directory("spring-in-action-2011");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(2).containsSubdir("spring-in-action-2011", "book");
	}

	@Test
	public void shouldCreateDirectoriesWithOneInvocation() {
		givenFileHierarchyGenerator("workspace")
				.directory("book/spring-in-action-2011")
				.up().up()
				.directory("book/spring-in-action-2007");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(3)
				.containsSubdir("spring-in-action-2011", "book")
				.containsSubdir("spring-in-action-2007", "book");
	}

	@Test
	public void shouldCreateDirectoriesAndUpWithOneInvocation() {
		givenFileHierarchyGenerator("workspace")
				.directoryAndUp("book/spring-in-action-2011")
				.directory("book/spring-in-action-2007");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(3)
				.containsSubdir("spring-in-action-2011", "book")
				.containsSubdir("spring-in-action-2007", "book");
	}

	@Test
	public void shouldCreateDirectoriesWithVarArgInvocation() {
		givenFileHierarchyGenerator("workspace")
				.directories("book", "spring-in-action-2011")
				.up().up()
				.directories("book", "spring-in-action-2007");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(3)
				.containsSubdir("spring-in-action-2011", "book")
				.containsSubdir("spring-in-action-2007", "book");
	}

	@Test
	public void shouldCreateDirectoriesAndUpWithVarArgInvocation() {
		givenFileHierarchyGenerator("workspace")
				.directoriesAndUp("book", "spring-in-action-2011")
				.directories("book", "spring-in-action-2007");
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(3)
				.containsSubdir("spring-in-action-2011", "book")
				.containsSubdir("spring-in-action-2007", "book");
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
	public void shouldCreateFilesWithOneInvocation() {
		givenFileHierarchyGenerator("workspace")
				.file("subdir1/fileInSubdir1")
				.file("subdir11/fileInSubdir11")
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
	public void shoudCreateFileWithStream() {
		givenFileHierarchyGenerator("workspace")
				.file("dir/file", new ByteArrayInputStream("line1".getBytes()));
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(1)
				.hasCountOfFiles(1)
				.containsFileWithContent("file", ImmutableList.of("line1"), "dir");
	}

	@Test
	public void shoudCreateFileUpWithStream() {
		givenFileHierarchyGenerator("workspace")
				.fileAndUp("dir1/file", new ByteArrayInputStream("dir1-file".getBytes()))
				.fileAndUp("dir2/file", new ByteArrayInputStream("dir2-file".getBytes()));
		whenGenerateFileHierarchy();
		thenFileHierarchy().hasCountOfSubdirs(2)
				.hasCountOfFiles(2)
				.containsFileWithContent("file", ImmutableList.of("dir1-file"), "dir1")
				.containsFileWithContent("file", ImmutableList.of("dir2-file"), "dir2");
	}

	@Test
	public void shoudValidateFileCreationLineAfterStream() {
		givenFileHierarchyGenerator("workspace")
				.file("dir/file", new ByteArrayInputStream("line1".getBytes()))
				.up()
				.file("dir/file");
		whenLineInGenerator("line2");
		thenException().isExactlyInstanceOf(IllegalStateException.class)
				.hasMessage("only one input method can be used for workspace\\dir\\file");
	}

	@Test
	public void shoudValidateFileCreationStreamAfterLine() {
		givenFileHierarchyGenerator("workspace")
				.file("dir/file").line("line1")
				.up();
		whenFileInGenerator("dir/file", new ByteArrayInputStream("line2".getBytes()));
		thenException().isExactlyInstanceOf(IllegalStateException.class)
				.hasMessage("only one input method can be used for workspace\\dir\\file");
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
		givenFileHierarchyGenerator("workspace");
		whenLineInGenerator("line1");
		thenException().isExactlyInstanceOf(IllegalInvocationException.class)
				.hasMessage("line method should not be invoked in current context (directory)");

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

	private void whenLineInGenerator(String line) {
		try {
			fileHierarchyGenerator.line(line);
		} catch (Exception e) {
			this.exception = e;
		}
	}

	private void whenFileInGenerator(String fileName, InputStream inputStream) {
		try {
			fileHierarchyGenerator.file(fileName, inputStream);
		} catch (Exception e) {
			this.exception = e;
		}
	}

	private FileHierarchyAssert thenFileHierarchy() {
		return then(fileHierarchy);
	}

	private FileAssert thenTempWorkingDirectory() {
		return then(fileHierarchy.getTempWorkingDirectoryAsFile());
	}

	private ThrowableAssert thenException() {
		return then(exception).isNotNull();
	}


}