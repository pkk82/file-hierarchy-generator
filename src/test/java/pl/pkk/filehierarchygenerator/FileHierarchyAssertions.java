package pl.pkk.filehierarchygenerator;

import org.assertj.core.api.BDDAssertions;

public class FileHierarchyAssertions extends BDDAssertions {

	public static FileHierarchyAssertion then(FileHierarchy fileHierarchy) {
		return new FileHierarchyAssertion(fileHierarchy);
	}
}
