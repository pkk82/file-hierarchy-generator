package pl.pkk82.filehierarchygenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.nio.file.Path;

import org.assertj.core.api.FileAssert;
import org.junit.Test;

public class FileHierarchyTest {

	private FileHierarchy fileHierarchy;
	private Path firstDirectoryLeafAsPath;
	private Path lastDirectoryLeafAsPath;

	@Test
	public void shouldGetRootDirAsFirstDirectoryLeafAsPath() {
		givenFileHierarchy(createRootDirectory("workspace"));
		whenFirstDirectoryLeafAsPath();
		thenFirstDirectoryLeafAsPath().hasName("workspace");
	}

	@Test
	public void shouldGetDirAsFirstDirectoryLeaf() {
		givenFileHierarchy(createRootDirectory("workspace")
				.directory("subdir1")
				.file("subdirFile"));
		whenFirstDirectoryLeafAsPath();
		thenFirstDirectoryLeafAsPath().hasName("subdir1");
	}

	@Test
	public void shouldGetDirFromFirstLevelAsFirstDirectoryLeafAsPath() {
		givenFileHierarchy(createRootDirectory("workspace")
				.directory("subdir1").up()
				.directory("subdir2")
				.directory("subdir21"));
		whenFirstDirectoryLeafAsPath();
		thenFirstDirectoryLeafAsPath().hasName("subdir1");
	}

	@Test
	public void shouldGetRootDirAsLastDirectoryLeafAsPath() {
		givenFileHierarchy(createRootDirectory("workspace"));
		whenLastDirectoryLeafAsPath();
		thenLastDirectoryLeafAsPath().hasName("workspace");
	}

	@Test
	public void shouldGetDirAsLastDirectoryLeaf() {
		givenFileHierarchy(createRootDirectory("workspace")
				.directory("subdir1")
				.file("subdirFile"));
		whenLastDirectoryLeafAsPath();
		thenLastDirectoryLeafAsPath().hasName("subdir1");
	}

	@Test
	public void shouldGetDirFromSecondLevelAsLastDirectoryLeafAsPath() {
		givenFileHierarchy(createRootDirectory("workspace")
				.directory("subdir1").up()
				.directory("subdir2")
				.directory("subdir21"));
		whenLastDirectoryLeafAsPath();
		thenLastDirectoryLeafAsPath().hasName("subdir21");
	}

	private void givenFileHierarchy(FileHierarchyGenerator fileHierarchyGenerator) {
		fileHierarchy = fileHierarchyGenerator.generate();

	}

	private void whenFirstDirectoryLeafAsPath() {
		firstDirectoryLeafAsPath = fileHierarchy.getFirstDirectoryLeafAsPath();
	}

	private void whenLastDirectoryLeafAsPath() {
		lastDirectoryLeafAsPath = fileHierarchy.getLastDirectoryLeafAsPath();
	}

	private FileAssert thenFirstDirectoryLeafAsPath() {
		return assertThat(firstDirectoryLeafAsPath.toFile());
	}

	private FileAssert thenLastDirectoryLeafAsPath() {
		return assertThat(lastDirectoryLeafAsPath.toFile());
	}
}