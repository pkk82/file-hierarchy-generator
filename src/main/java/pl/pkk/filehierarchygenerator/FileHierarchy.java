package pl.pkk.filehierarchygenerator;

import java.io.File;
import java.nio.file.Path;

public class FileHierarchy {
	private final Path rootDirectory;

	public FileHierarchy(Path rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public Path getRootDirectoryAsPath() {
		return rootDirectory;
	}

	public File getRootDirectoryAsFile() {
		return rootDirectory.toFile();
	}

	public Path getTempWorkingDirectoryAsPath() {
		return rootDirectory.getParent();
	}

	public File getTempWorkingDirectoryAsFile() {
		return getTempWorkingDirectoryAsPath().toFile();
	}
}
