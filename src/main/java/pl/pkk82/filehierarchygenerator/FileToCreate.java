package pl.pkk82.filehierarchygenerator;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

class FileToCreate {

	private Path path;

	private final List<String> lines;

	private InputStream inputStream;

	private OpenOption writeOption;

	public FileToCreate(Path path, OpenOption writeOption) {
		this.path = path;
		this.writeOption = writeOption;
		lines = new ArrayList<>();
	}

	public Path getPath() {
		return path;
	}

	public OpenOption getWriteOption() {
		return writeOption;
	}

	public void setInputStream(InputStream inputStream) {
		checkState(lines.isEmpty(), "only one input method can be used for %s", path);
		this.inputStream = inputStream;
	}

	public void addLine(String line) {
		checkState(inputStream == null, "only one input method can be used for %s", path);
		lines.add(line);
	}

	public void write(Path fullPathToResolve) throws IOException {
		if (inputStream != null) {
			Files.copy(inputStream, fullPathToResolve, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.write(fullPathToResolve, lines, Charset.forName("utf8"), getWriteOption());
		}
	}
}
