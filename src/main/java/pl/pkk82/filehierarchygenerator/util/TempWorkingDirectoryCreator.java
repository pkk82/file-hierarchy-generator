package pl.pkk82.filehierarchygenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempWorkingDirectoryCreator {

	public static Path createTempWorkingDirectory() throws IOException {
		return Files.createTempDirectory("file-hierarchy-generator-");
	}
}