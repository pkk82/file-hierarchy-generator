package pl.pkk82.filehierarchygenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	private final List<FileToCreate> filesToCreate;
	private final Map<Path, List<String>> fileLines;
	private int level;
	private final List<FileHierarchyGenerateOption> options;
	private OpenOption fileWriteOption = StandardOpenOption.APPEND;


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
		filesToCreate.add(new FileToCreate(filePath, fileWriteOption));
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

	public FileHierarchyGenerator override() {
		fileWriteOption = StandardOpenOption.WRITE;
		return this;
	}

	public FileHierarchyGenerator append() {
		fileWriteOption = StandardOpenOption.APPEND;
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


	private Path createDirectory(Path path) throws IOException {
		if (options.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_SUBDIR_ALREADY_EXISTS)
				&& directoryAlreadyExists(path)) {
			throw new FileHierarchyGeneratorException(String.format("Directory <%s> already exists", path));
		}
		return Files.createDirectories(path);
	}

	private void createFiles() throws IOException {
		for (FileToCreate fileToCreate : filesToCreate) {
			Path fileToCreatePath = fileToCreate.getPath();
			Path fullPathToResolve = workingDirectory.resolve(fileToCreatePath);
			createFile(fullPathToResolve);
			if (fileLines.containsKey(fileToCreatePath)) {
				List<String> lines = fileLines.get(fileToCreatePath);
				Files.write(fullPathToResolve, lines, Charset.forName("utf8"), fileToCreate.getWriteOption());
			}
		}
	}

	private void createFile(Path file) throws IOException {
		if (options.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_FILE_ALREADY_EXISTS)
				&& fileAlreadyExists(file)) {
			throw new FileHierarchyGeneratorException(String.format("File <%s> already exists", file));
		}
		if (!fileAlreadyExists(file)) {
			Files.createFile(file);
		}
	}

	private boolean directoryAlreadyExists(Path directory) {
		File directoryAsFile = directory.toFile();
		return directoryAsFile.exists() && directoryAsFile.isDirectory();
	}

	private boolean fileAlreadyExists(Path file) {
		File fileAsFile = file.toFile();
		return fileAsFile.exists() && fileAsFile.isFile();
	}


	private void validateLevel() {
		if (level - 1 < 0) {
			throw new IllegalInvocationException("up method should not be invoked in current context (root directory)");
		}
	}
}
