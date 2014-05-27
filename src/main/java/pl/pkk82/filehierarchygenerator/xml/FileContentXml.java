package pl.pkk82.filehierarchygenerator.xml;

import java.nio.file.Path;

public class FileContentXml {

	private final Path path;

	public FileContentXml(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}
}
