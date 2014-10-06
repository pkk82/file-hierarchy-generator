package pl.pkk82.filehierarchygenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import pl.pkk82.filehierarchygenerator.util.TempWorkingDirectoryCreator;

public class FileHierarchyGenerator {

	private Path workingDirectory;
	private Path rootDirectory;
	private Path currentDirectory;
	private Path currentFile;
	private final List<Path> directoriesToCreate;
	private final List<Path> filesToCreate;
	private final Map<Path, List<String>> fileLines;
	private int level;
	private final List<FileHierarchyGenerateOption> options;


	public static FileHierarchyGenerator createRootDirectory(String rootDirectoryName,
			FileHierarchyGenerateOption... options) {
		return createRootDirectory((Path) null, rootDirectoryName, options);
	}

	public static FileHierarchyGenerator createRootDirectory(File workingDirectory, String rootDirectoryName,
			FileHierarchyGenerateOption... options) {
		return createRootDirectory(workingDirectory == null ? null : workingDirectory.toPath(), rootDirectoryName,
				options);
	}

	public static FileHierarchyGenerator createRootDirectory(FileHierarchy workingDirectory, String rootDirectoryName,
			FileHierarchyGenerateOption... options) {
		return createRootDirectory(workingDirectory == null ? null :
				workingDirectory.getRootDirectoryAsPath().getParent(), rootDirectoryName, options);
	}

	public static FileHierarchyGenerator createRootDirectory(Path workingDirectory, String rootDirectoryName,
			FileHierarchyGenerateOption... options) {
		return new FileHierarchyGenerator(workingDirectory, rootDirectoryName, options);
	}

	public FileHierarchy generate() {
		try {
			createWorkingDirectory();
			createRootDirectory();
			createDirectories();
			createFiles();
			return new FileHierarchy(rootDirectory);
		} catch (IOException e) {
			throw new FileHierarchyGeneratorException(e);
		}

	}

	private void createWorkingDirectory() throws IOException {
		if (workingDirectory == null) {
			workingDirectory = new TempWorkingDirectoryCreator().createTempWorkingDirectory();
		}
	}

	public FileHierarchyGenerator directory(String directoryName) {
		Path newCurrentDirectory = currentDirectory.resolve(directoryName);
		directoriesToCreate.add(newCurrentDirectory);
		currentDirectory = newCurrentDirectory;
		level++;
		return this;
	}

	public FileHierarchyGenerator up() {
		validateLevel();
		level--;
		currentDirectory = currentDirectory.getParent();
		return this;
	}

	public FileHierarchyGenerator file(String fileName) {
		Path filePath = currentDirectory.resolve(fileName);
		currentFile = filePath;
		filesToCreate.add(filePath);
		return this;
	}

	public FileHierarchyGenerator line(String line) {
		if (currentFile == null) {
			throw new IllegalInvocationException("line method should not be invoked in current context (directory)");
		}
		if (!fileLines.containsKey(currentFile)) {
			fileLines.put(currentFile, new ArrayList<String>());
		}
		List<String> lines = fileLines.get(currentFile);
		lines.add(line);
		return this;
	}

	private FileHierarchyGenerator(Path workingDirectory, String rootDirectoryName,
			FileHierarchyGenerateOption... options) {
		directoriesToCreate = new ArrayList<>();
		filesToCreate = new ArrayList<>();
		fileLines = new HashMap<>();
		level = 0;
		rootDirectory = Paths.get(rootDirectoryName);
		currentDirectory = rootDirectory;
		this.workingDirectory = workingDirectory;
		this.options = Lists.newArrayList(options);
	}

	private void createDirectories() throws IOException {
		for (Path directoryToCreate : directoriesToCreate) {
			Path fullPathToResolve = workingDirectory.resolve(directoryToCreate);
			createDirectory(fullPathToResolve);
		}
	}

	private void createRootDirectory() throws IOException {
		Path rootDirectory = workingDirectory.resolve(this.rootDirectory);
		if (options.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_ROOT_ALREADY_EXISTS)
				&& directoryAlreadyExists(rootDirectory)) {
			throw new FileHierarchyGeneratorException(String.format("Directory <%s> already exists", rootDirectory));
		}
		this.rootDirectory = Files.createDirectories(rootDirectory);
	}

	private boolean directoryAlreadyExists(Path path) {
		File rootDirAsFile = path.toFile();
		return rootDirAsFile.exists() && rootDirAsFile.isDirectory();
	}


	private Path createDirectory(Path fullPath) throws IOException {
		if (options.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_SUBDIR_ALREADY_EXISTS)
				&& directoryAlreadyExists(fullPath)) {
			throw new FileHierarchyGeneratorException(String.format("Directory <%s> already exists", fullPath));
		}
		return Files.createDirectories(fullPath);
	}

	private void createFiles() throws IOException {
		for (Path fileToCreate : filesToCreate) {
			Path fullPathToResolve = workingDirectory.resolve(fileToCreate);
			Files.createFile(fullPathToResolve);
			if (fileLines.containsKey(fileToCreate)) {
				List<String> lines = fileLines.get(fileToCreate);
				Files.write(fullPathToResolve, lines, Charset.forName("utf8"));
			}
		}
	}

	private void validateLevel() {
		if (level - 1 < 0) {
			throw new IllegalInvocationException("up method should not be invoked in current context (root directory)");
		}
	}


}
