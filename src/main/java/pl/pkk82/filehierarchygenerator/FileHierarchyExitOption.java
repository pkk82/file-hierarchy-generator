package pl.pkk82.filehierarchygenerator;

public enum FileHierarchyExitOption {

	/**
	 * Deletes whole working directory.
	 */
	DELETE_ALL,

	/**
	 * Deletes all files/dirs managed by hierarchy despite some dirs containing files/dirs not managed by hierarchy.
	 */
	DELETE,

	/**
	 * Deletes only newly created files/dirs.
	 */
	DELETE_NEW,

	NONE
}
