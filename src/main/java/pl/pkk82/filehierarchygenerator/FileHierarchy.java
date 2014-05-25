package pl.pkk82.filehierarchygenerator;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

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

	public Path getFirstDirectoryLeafAsPath() {
		File rootDirectoryFile = rootDirectory.toFile();
		return getFirstDirectoryFrom(rootDirectoryFile).toPath();
	}

	public File getFirstDirectoryLeafAsFile() {
		File rootDirectoryFile = rootDirectory.toFile();
		return getFirstDirectoryFrom(rootDirectoryFile);
	}

	public Path getLastDirectoryLeafAsPath() {
		File rootDirectoryFile = rootDirectory.toFile();
		return getLastDirectoryFrom(rootDirectoryFile).toPath();
	}

	public File getLastDirectoryLeafAsFile() {
		File rootDirectoryFile = rootDirectory.toFile();
		return getLastDirectoryFrom(rootDirectoryFile);
	}

	private File getFirstDirectoryFrom(File directory) {
		List<File> subdirectories = getDirectoriesSortedByName(directory, Ordering.natural());
		if (subdirectories.isEmpty()) {
			return directory;
		} else {
			return getFirstDirectoryFrom(subdirectories.get(0));
		}
	}

	private File getLastDirectoryFrom(File directory) {
		List<File> subdirectories = getDirectoriesSortedByName(directory, Ordering.natural().reverse());
		if (subdirectories.isEmpty()) {
			return directory;
		} else {
			return getLastDirectoryFrom(subdirectories.get(0));
		}
	}

	private List<File> getDirectoriesSortedByName(File directory, Ordering<Comparable> ordering) {
		List<File> files = Lists.newArrayList(directory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE));
		return ordering.sortedCopy(files);
	}
}
