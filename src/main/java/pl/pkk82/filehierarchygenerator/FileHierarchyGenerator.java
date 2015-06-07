package pl.pkk82.filehierarchygenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.io.FileUtils;
import pl.pkk82.filehierarchygenerator.util.TempWorkingDirectoryCreator;

public class FileHierarchyGenerator {

	private final List<Path> directoriesToCreate;
	private final Map<Path, FileToCreate> filesToCreate;
	private final List<FileHierarchyGenerateOption> generateOptions;
	private final FileHierarchyExitOption exitOption;
	private Path workingDirectory;
	private Path rootDirectory;
	private Path currentDirectory;
	private FileToCreate currentFile;
	private int previousLevel;
	private int level;
	private OpenOption fileWriteOption = StandardOpenOption.APPEND;


	private FileHierarchyGenerator(Path workingDirectory, String rootDirectoryName,
								   FileHierarchyExitOption exitOption,
								   FileHierarchyGenerateOption... generateOptions) {
		directoriesToCreate = new ArrayList<>();
		filesToCreate = new HashMap<>();
		previousLevel = 0;
		level = 0;
		rootDirectory = Paths.get(rootDirectoryName);
		currentDirectory = rootDirectory;
		this.workingDirectory = workingDirectory;
		this.generateOptions = Lists.newArrayList(generateOptions);
		this.exitOption = exitOption;
	}

	public static FileHierarchyGenerator createRootDirectory(String rootDirectoryName,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory((Path) null, rootDirectoryName, generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(File workingDirectory, String rootDirectoryName,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory(workingDirectory == null ? null : workingDirectory.toPath(), rootDirectoryName,
				generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(FileHierarchy workingDirectory, String rootDirectoryName,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory(workingDirectory == null ? null :
				workingDirectory.getRootDirectoryAsPath().getParent(), rootDirectoryName, generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(Path workingDirectory, String rootDirectoryName,
															 FileHierarchyGenerateOption... generateOptions) {
		return new FileHierarchyGenerator(workingDirectory, rootDirectoryName, FileHierarchyExitOption.DELETE,
				generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(String rootDirectoryName,
															 FileHierarchyExitOption exitOption,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory((Path) null, rootDirectoryName, exitOption, generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(File workingDirectory, String rootDirectoryName,
															 FileHierarchyExitOption exitOption,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory(workingDirectory == null ? null : workingDirectory.toPath(), rootDirectoryName,
				exitOption, generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(FileHierarchy workingDirectory, String rootDirectoryName,
															 FileHierarchyExitOption exitOption,
															 FileHierarchyGenerateOption... generateOptions) {
		return createRootDirectory(workingDirectory == null ? null :
				workingDirectory.getRootDirectoryAsPath().getParent(), rootDirectoryName, exitOption, generateOptions);
	}

	public static FileHierarchyGenerator createRootDirectory(Path workingDirectory, String rootDirectoryName,
															 FileHierarchyExitOption exitOption,
															 FileHierarchyGenerateOption... generateOptions) {
		return new FileHierarchyGenerator(workingDirectory, rootDirectoryName, exitOption, generateOptions);
	}

	public FileHierarchy generate() {
		try {
			createWorkingDirectory();
			addHook();
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
			workingDirectory = TempWorkingDirectoryCreator.createTempWorkingDirectory();
			setExitBehaviourForNew(workingDirectory);
		} else if (directoryAlreadyExists(workingDirectory)) {
			setExitBehaviourForExisting(workingDirectory);
		}
		// TODO what about not existing but specified
	}

	public FileHierarchyGenerator directory(String directoryName) {
		Path directoryPath = Paths.get(directoryName);
		return directory(directoryPath);
	}

	public FileHierarchyGenerator directoryAndUp(String directoryName) {
		Path directoryPath = Paths.get(directoryName);
		directory(directoryPath);
		up(level - previousLevel);
		return this;
	}

	public FileHierarchyGenerator directories(String directoryName, String... directoryNames) {
		Path directoryPath = Paths.get(directoryName, directoryNames);
		return directory(directoryPath);
	}

	public FileHierarchyGenerator directoriesAndUp(String directoryName, String... directoryNames) {
		Path directoryPath = Paths.get(directoryName, directoryNames);
		directory(directoryPath);
		up(level - previousLevel);
		return this;
	}

	public FileHierarchyGenerator up() {
		validateLevel();
		previousLevel = level;
		level--;
		currentDirectory = currentDirectory.getParent();
		return this;
	}

	public FileHierarchyGenerator file(String fileName) {
		Path fileNameAsPath = Paths.get(fileName);
		Path parent = fileNameAsPath.getParent();
		if (parent != null) {
			directory(parent);
		}
		Path filePath = currentDirectory.resolve(fileNameAsPath.getFileName());
		if (!filesToCreate.containsKey(filePath)) {
			filesToCreate.put(filePath, new FileToCreate(filePath, fileWriteOption));
		}
		currentFile = filesToCreate.get(filePath);
		return this;
	}

	public FileHierarchyGenerator file(String fileName, InputStream inputStream) {
		file(fileName);
		currentFile.setInputStream(inputStream);
		return this;
	}

	public FileHierarchyGenerator fileAndUp(String fileName, InputStream inputStream) {
		file(fileName);
		currentFile.setInputStream(inputStream);
		up(level - previousLevel);
		return this;
	}

	public FileHierarchyGenerator line(String line) {
		if (currentFile == null) {
			throw new IllegalInvocationException("line method should not be invoked in current context (directory)");
		}
		currentFile.addLine(line);
		return this;
	}

	public FileHierarchyGenerator property(String key, String value) {
		return line(createProperty(key, value));
	}

	public FileHierarchyGenerator override() {
		fileWriteOption = StandardOpenOption.TRUNCATE_EXISTING;
		return this;
	}

	public FileHierarchyGenerator append() {
		fileWriteOption = StandardOpenOption.APPEND;
		return this;
	}

	private void createDirectories() throws IOException {
		for (Path directoryToCreate : directoriesToCreate) {
			Path fullPathToResolve = workingDirectory.resolve(directoryToCreate);
			createDirectory(fullPathToResolve);
		}
	}

	private void createRootDirectory() throws IOException {
		Path rootDirectory = workingDirectory.resolve(this.rootDirectory);
		if (generateOptions.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_ROOT_ALREADY_EXISTS)
				&& directoryAlreadyExists(rootDirectory)) {
			throw new FileHierarchyGeneratorException(String.format("Directory <%s> already exists", rootDirectory));
		}

		if (directoryAlreadyExists(rootDirectory)) {
			this.rootDirectory = rootDirectory;
			setExitBehaviourForExisting(rootDirectory);
		} else {
			this.rootDirectory = Files.createDirectories(rootDirectory);
			setExitBehaviourForNew(rootDirectory);
		}

	}

	private Path createDirectory(Path path) throws IOException {
		if (generateOptions.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_SUBDIR_ALREADY_EXISTS)
				&& directoryAlreadyExists(path)) {
			throw new FileHierarchyGeneratorException(String.format("Directory <%s> already exists", path));
		}
		Path result;
		if (directoryAlreadyExists(path)) {
			result = path;
			setExitBehaviourForExisting(path);
		} else {
			result = Files.createDirectories(path);
			setExitBehaviourForNew(path);
		}
		return result;
	}

	private FileHierarchyGenerator directory(Path directoryPath) {
		Path newCurrentDirectory = currentDirectory.resolve(directoryPath);
		directoriesToCreate.add(newCurrentDirectory);
		currentDirectory = newCurrentDirectory;
		previousLevel = level;
		level += directoryPath.getNameCount();
		return this;
	}

	private void createFiles() throws IOException {
		for (FileToCreate fileToCreate : filesToCreate.values()) {
			Path fileToCreatePath = fileToCreate.getPath();
			Path fullPathToResolve = workingDirectory.resolve(fileToCreatePath);
			createFile(fullPathToResolve);
			fileToCreate.write(fullPathToResolve);
		}
	}

	private void createFile(Path file) throws IOException {
		if (generateOptions.contains(FileHierarchyGenerateOption.EXCEPTION_WHEN_FILE_ALREADY_EXISTS)
				&& fileAlreadyExists(file)) {
			throw new FileHierarchyGeneratorException(String.format("File <%s> already exists", file));
		}
		if (fileAlreadyExists(file)) {
			setExitBehaviourForExisting(file);
		} else {
			Files.createFile(file);
			setExitBehaviourForNew(file);
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

	private void up(int nTimes) {
		for (int i = 0; i < nTimes; i++) {
			up();
		}
	}

	private void validateLevel() {
		if (level - 1 < 0) {
			throw new IllegalInvocationException("up method should not be invoked in current context (root " +
					"directory)");
		}
	}

	private String createProperty(String key, String value) {
		return String.format("%s=%s", key, value);
	}

	private void setExitBehaviourForExisting(Path path) {
		if (exitOption == FileHierarchyExitOption.DELETE) {
			path.toFile().deleteOnExit();
		}
	}

	private void setExitBehaviourForNew(Path path) {
		if (exitOption == FileHierarchyExitOption.DELETE_NEW || exitOption == FileHierarchyExitOption.DELETE) {
			path.toFile().deleteOnExit();
		}
	}


	private void addHook() {
		if (exitOption == FileHierarchyExitOption.DELETE_ALL) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						FileUtils.forceDelete(workingDirectory.toFile());
					} catch (IOException e) {
						// empty catch
					}
				}
			});
		}

	}


}
