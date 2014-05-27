package pl.pkk82.filehierarchygenerator.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.AbstractAssert;

public class FileContentXmlAssert extends AbstractAssert<FileContentXmlAssert, FileContentXml> {
	public FileContentXmlAssert(FileContentXml actual) {
		super(actual, FileContentXmlAssert.class);
	}

	public FileContentXmlAssert containsLines(String... expectedLines) {
		isNotNull();
		List<String> lines;
		try {
			lines = FileUtils.readLines(actual.getPath().toFile());
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		assertThat(lines).containsExactly(expectedLines);
		return this;
	}

	public FileContentXmlAssert hasName(String name) {
		isNotNull();
		assertThat(actual.getPath().getFileName().toString()).isEqualTo(name);
		return this;
	}

	public FileContentXmlAssert hasGrandParent(File file) {
		isNotNull();
		assertThat(getParent().toFile()).hasParent(file);
		return this;
	}

	private Path getParent() {
		return actual.getPath().getParent();
	}

	public FileContentXmlAssert parentNameMatches(String parent) {
		isNotNull();
		assertThat(getParent().getFileName().toString()).matches(parent);
		return this;
	}
}
