package pl.pkk82.filehierarchygenerator;


import static pl.pkk82.filehierarchygenerator.FileHierarchyAssertions.then;
import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.util.Arrays;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import pl.pkk82.filehierarchyassert.FileHierarchyAssert;

public class FileHierarchyGeneratorOnExistingHierarchyTest {

	private FileHierarchy newFileHierarchy;
	private FileHierarchyGenerator newFileHierarchyGenerator;
	private FileHierarchyGenerator existingFileHierarchyGenerator;
	private FileHierarchy existingFileHierarchy;
	private Exception exception;

	@Test
	public void shouldSucceedWhenRootDirectoryExists() {
		givenExistingFileHierarchy("workspace");
		givenNewFileHierarchy("workspace");
		whenGenerateNewFileHierarchy();
		thenNewFileHierarchyOverrideExisting();
		thenNewFileHierarchy().hasRootDirWithName("workspace");
	}

	@Test
	public void shouldFailWhenRootDirectoryAlreadyExists() {
		givenExistingFileHierarchy("workspace");
		givenNewFileHierarchy("workspace", FileHierarchyGenerateOption.EXCEPTION_WHEN_ROOT_ALREADY_EXISTS);
		whenGenerateNewFileHierarchy();
		thenExceptionIsThrown().hasMessage(String.format("Directory <%s> already exists",
				existingFileHierarchy.getRootDirectoryAsPath()));
	}

	@Test
	public void shouldAddAdditionalDirectory() {
		givenExistingFileHierarchy("workspace");
		givenNewFileHierarchy("workspace").directory("subdir");
		whenGenerateNewFileHierarchy();
		thenNewFileHierarchyOverrideExisting();
		thenNewFileHierarchy().hasRootDirWithName("workspace").hasCountOfSubdirs(1);
	}

	@Test
	public void shouldRetainExistingDirectory() {
		givenExistingFileHierarchy("workspace").directory("subdir2");
		givenNewFileHierarchy("workspace").directory("subdir1");
		whenGenerateNewFileHierarchy();
		thenNewFileHierarchyOverrideExisting();
		thenNewFileHierarchy().hasRootDirWithName("workspace").hasCountOfSubdirs(2);
	}

	@Test
	public void shouldFailWhenSubdirAlreadyExists() {
		givenExistingFileHierarchy("workspace").directory("subdir");
		givenNewFileHierarchy("workspace", FileHierarchyGenerateOption.EXCEPTION_WHEN_SUBDIR_ALREADY_EXISTS)
				.directory("subdir");
		whenGenerateNewFileHierarchy();
		thenExceptionIsThrown().hasMessage(String.format("Directory <%s> already exists",
				existingFileHierarchy.getRootDirectoryAsPath().resolve("subdir")));
	}


	@Test
	public void shouldRetainExistingFile() {
		givenExistingFileHierarchy("workspace").file("file").line("line1");
		givenNewFileHierarchy("workspace").file("file").line("line2");
		whenGenerateNewFileHierarchy();
		thenNewFileHierarchyOverrideExisting();
		thenNewFileHierarchy().hasRootDirWithName("workspace")
				.containsFileWithContent("file", Arrays.asList("line1", "line2"));
	}

	@Test
	public void shouldFailWhenFileAlreadyExists() {
		givenExistingFileHierarchy("workspace").file("file").line("line1");
		givenNewFileHierarchy("workspace", FileHierarchyGenerateOption.EXCEPTION_WHEN_FILE_ALREADY_EXISTS)
				.file("file").line("line2");
		whenGenerateNewFileHierarchy();
		thenExceptionIsThrown().hasMessage(String.format("File <%s> already exists",
				existingFileHierarchy.getRootDirectoryAsPath().resolve("file")));
	}

	private FileHierarchyGenerator givenExistingFileHierarchy(String rootDirectory) {
		existingFileHierarchyGenerator = createRootDirectory(rootDirectory);
		return existingFileHierarchyGenerator;
	}


	private FileHierarchyGenerator givenNewFileHierarchy(String workspace,
			FileHierarchyGenerateOption... fileHierarchyGenerateOptions) {
		existingFileHierarchy = existingFileHierarchyGenerator.generate();
		newFileHierarchyGenerator = createRootDirectory(existingFileHierarchy, workspace, fileHierarchyGenerateOptions);
		return newFileHierarchyGenerator;
	}



	private FileHierarchy whenGenerateNewFileHierarchy() {
		try {
			newFileHierarchy = newFileHierarchyGenerator.generate();
		} catch (Exception e) {
			this.exception = e;
		}
		return newFileHierarchy;
	}


	private FileHierarchyAssert thenNewFileHierarchy() {
		return then(newFileHierarchy);
	}


	private void thenNewFileHierarchyOverrideExisting() {
		then(newFileHierarchy.getRootDirectoryAsPath()).isEqualTo(existingFileHierarchy.getRootDirectoryAsPath());
	}

	private ThrowableAssert thenExceptionIsThrown() {
		return then(exception).isNotNull().isInstanceOf(FileHierarchyGeneratorException.class);
	}


}