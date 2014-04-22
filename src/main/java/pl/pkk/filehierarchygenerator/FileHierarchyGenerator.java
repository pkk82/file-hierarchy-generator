package pl.pkk.filehierarchygenerator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHierarchyGenerator {

	private Path currentDirectory;
	private Path currentFile;
	private final List<Path> directoriesToCreate;
	private final List<Path> filesToCreate;
	private final Map<Path, List<String>> fileLines;
	private int level;

	public static FileHierarchyGenerator createRootDirectory(String rootDirectoryName) {
		return new FileHierarchyGenerator(rootDirectoryName);

	}

	public FileHierarchy generate() {
		try {
			Path tempWorkingDirectory = createTempWorkingDirectory();
			Path rootDirectory = createDirectories(tempWorkingDirectory);
			createFiles(tempWorkingDirectory);
			return new FileHierarchy(rootDirectory);
		} catch (IOException e) {
			throw new FileHierarchyGeneratorException(e);
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

	private FileHierarchyGenerator(String rootDirectoryName) {
		directoriesToCreate = new ArrayList<>();
		filesToCreate = new ArrayList<>();
		fileLines = new HashMap<>();
		level = 0;
		currentDirectory = Paths.get(rootDirectoryName);
		directoriesToCreate.add(currentDirectory);
	}

	private Path createDirectories(Path tempWorkingDirectory) throws IOException {
		Path rootDirectory = null;
		for (Path directoryToCreate : directoriesToCreate) {
			Path fullPathToResolve = tempWorkingDirectory.resolve(directoryToCreate);
			Path createdDirectory = Files.createDirectories(fullPathToResolve);
			if (rootDirectory == null) {
				rootDirectory = createdDirectory;
			}
		}
		return rootDirectory;
	}

	private void createFiles(Path tempWorkingDirectory) throws IOException {
		for (Path fileToCreate : filesToCreate) {
			Path fullPathToResolve = tempWorkingDirectory.resolve(fileToCreate);
			Files.createFile(fullPathToResolve);
			if (fileLines.containsKey(fileToCreate)) {
				List<String> lines = fileLines.get(fileToCreate);
				Files.write(fullPathToResolve, lines, Charset.forName("utf8"));
			}
		}
	}

	private Path createTempWorkingDirectory() throws IOException {
		return Files.createTempDirectory("fhg");
	}

	private void validateLevel() {
		if (level - 1 < 0) {
			throw new IllegalInvocationException("up method should not be invoked in current context (root directory)");
		}
	}


}
