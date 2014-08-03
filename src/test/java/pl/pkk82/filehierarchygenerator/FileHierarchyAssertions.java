package pl.pkk82.filehierarchygenerator;

import org.assertj.core.api.BDDAssertions;

import pl.pkk82.filehierarchyassert.FileHierarchyAssert;

public class FileHierarchyAssertions extends BDDAssertions {

	public static FileHierarchyAssert then(FileHierarchy fileHierarchy) {
		return new FileHierarchyAssert(fileHierarchy);
	}
}
