package pl.pkk82.filehierarchygenerator;

import java.nio.file.OpenOption;
import java.nio.file.Path;

class FileToCreate {

	private Path path;

	private OpenOption writeOption;

	public FileToCreate(Path path, OpenOption writeOption) {
		this.path = path;
		this.writeOption = writeOption;
	}

	public Path getPath() {
		return path;
	}

	public OpenOption getWriteOption() {
		return writeOption;
	}
}
