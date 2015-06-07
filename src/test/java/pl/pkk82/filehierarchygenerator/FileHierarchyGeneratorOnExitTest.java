package pl.pkk82.filehierarchygenerator;

import static pl.pkk82.filehierarchygenerator.FileHierarchyGenerator.createRootDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.util.JavaEnvUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pkk82.filehierarchyassert.FileHierarchyAssert;

public class FileHierarchyGeneratorOnExitTest {

	private static final Logger LOG = LoggerFactory.getLogger(FileHierarchyGeneratorOnExitTest.class);

	private FileHierarchyGenerator fileHierarchyGenerator;
	private FileHierarchy fileHierarchy;

	@Test
	public void shouldDeleteAll() throws IOException, InterruptedException {
		givenTempDirectory()
				.directory("workspace")
				.file("file-1")
				.directoryAndUp("subdir-empty-1")
				.directory("subdir-1")
				.file("file-1")
				.directory("subdir-empty-1");
		whenGenerateFileHierarchy(FileHierarchyExitOption.DELETE_ALL);
		thenFileHierarchy().doesNotExist();
	}

	@Test
	public void shouldDeleteByDefault() throws IOException, InterruptedException {
		givenTempDirectory()
				.directory("workspace")
				.file("file")
				.directoryAndUp("subdir-empty")
				.directory("subdir")
				.file("file-1");
		whenGenerateFileHierarchy();
		thenFileHierarchy()
				.hasRootDirWithName("rootDir")
				.hasCountOfSubdirs(2)
				.hasCountOfFiles(1)
				.containsSubdir("workspace")
				.containsSubdir("subdir", "workspace")
				.containsFile("file-1", "workspace", "subdir");
	}

	@Test
	public void shouldDelete() throws IOException, InterruptedException {
		givenTempDirectory()
				.directory("workspace")
				.file("file")
				.directoryAndUp("subdir-empty")
				.directory("subdir")
				.file("file-1");
		whenGenerateFileHierarchy(FileHierarchyExitOption.DELETE);
		thenFileHierarchy()
				.hasRootDirWithName("rootDir")
				.hasCountOfSubdirs(2)
				.hasCountOfFiles(1)
				.containsSubdir("workspace")
				.containsSubdir("subdir", "workspace")
				.containsFile("file-1", "workspace", "subdir");
	}

	@Test
	public void shouldDeleteNewlyCreated() throws IOException, InterruptedException {
		givenTempDirectory()
				.directory("workspace")
				.file("file")
				.directoryAndUp("subdir-empty")
				.directory("subdir")
				.file("file-1");
		whenGenerateFileHierarchy(FileHierarchyExitOption.DELETE_NEW);
		thenFileHierarchy()
				.hasRootDirWithName("rootDir")
				.hasCountOfSubdirs(3)
				.hasCountOfFiles(2)
				.containsSubdir("subdir-empty", "workspace")
				.containsSubdir("workspace")
				.containsSubdir("subdir", "workspace")
				.containsFile("file", "workspace")
				.containsFile("file-1", "workspace", "subdir");
	}

	@Test
	public void shouldKeepAll() throws IOException, InterruptedException {
		givenTempDirectory()
				.directory("workspace")
				.file("file")
				.directoryAndUp("subdir-empty")
				.directory("subdir")
				.file("file-1");
		whenGenerateFileHierarchy(FileHierarchyExitOption.NONE);
		thenFileHierarchy()
				.hasRootDirWithName("rootDir")
				.hasCountOfSubdirs(4)
				.hasCountOfFiles(3)
				.containsSubdir("workspace")
				.containsSubdir("subdir-empty", "workspace")
				.containsSubdir("subdir", "workspace")
				.containsSubdir("subdir-empty", "workspace", "subdir")
				.containsFile("file", "workspace")
				.containsFile("file", "workspace", "subdir")
				.containsFile("file-1", "workspace", "subdir");
	}


	private FileHierarchyGenerator givenTempDirectory() {
		fileHierarchyGenerator = createRootDirectory("rootDir", FileHierarchyExitOption.DELETE_ALL);
		return fileHierarchyGenerator;
	}


	private void whenGenerateFileHierarchy() throws IOException, InterruptedException {
		fileHierarchy = fileHierarchyGenerator.generate();
		startSecondJVM(Launcher.class, fileHierarchy.getRootDirectoryAsPath().toString());
	}

	private void whenGenerateFileHierarchy(FileHierarchyExitOption fileHierarchyExitOption)
			throws IOException, InterruptedException {
		fileHierarchy = fileHierarchyGenerator.generate();
		startSecondJVM(Launcher.class, fileHierarchy.getRootDirectoryAsPath().toString(),
				fileHierarchyExitOption.toString());
	}

	private FileHierarchyAssert thenFileHierarchy() {
		return new FileHierarchyAssert(fileHierarchy.getRootDirectoryAsPath());
	}


	public void startSecondJVM(Class<?> clazz, String... args) throws IOException, InterruptedException {
		List<String> commands = prepareCommands(clazz, args);
		ProcessBuilder processBuilder = prepareProcess(commands);
		Process process = processBuilder.start();
		process.waitFor();
		displayProcessOutput(process);
	}

	private List<String> prepareCommands(Class<?> clazz, String[] args) {
		String classpath = System.getProperty("java.class.path");
		String path = JavaEnvUtils.getJdkExecutable("java");

		List<String> commands = new ArrayList<>();
		commands.add(path);
		commands.add("-cp");
		commands.add(classpath);
		commands.add(FileHierarchyGeneratorOnExitTest.class.getCanonicalName() + "$" + clazz.getSimpleName());
		commands.addAll(Arrays.asList(args));
		return commands;
	}

	private ProcessBuilder prepareProcess(List<String> commands) {
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput();
		return processBuilder;
	}

	private void displayProcessOutput(Process process) throws IOException {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			LOG.info("outputLine: {}", line);
		}
	}


	public static class Launcher {
		public Launcher(String path, String option) {
			FileHierarchyGenerator fileHierarchyGenerator;
			if (option == null) {
				fileHierarchyGenerator = createRootDirectory(Paths.get(path), "workspace");
			} else {
				fileHierarchyGenerator = createRootDirectory(Paths.get(path), "workspace", FileHierarchyExitOption.valueOf(option));
			}
			fileHierarchyGenerator
					.file("file")
					.directoryAndUp("subdir-empty")
					.directory("subdir")
					.file("file")
					.directory("subdir-empty");
			fileHierarchyGenerator.generate();
		}


		public static void main(String[] args) {
			new Launcher(args[0], args.length > 1 ? args[1] : null);
		}
	}

}
