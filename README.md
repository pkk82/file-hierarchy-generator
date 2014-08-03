file-hierarchy-generator
========================

A small library to create directory hierarchy with files in tests.

```java
FileHierarchyGenerator
    .createRootDirectory("workspace")
    .directory("subdir1")
    .file("fileInSubdir1")
    .directory("subdir11")
    .file("fileInSubdir11")
    .up().up()
    .file("fileInWorkspace")
    .line("content of fileInWorkspace")
    .generate();
```
creates files
```
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/subdir1/fileInSubdir1
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/subdir1/subdir11/fileInSubdir11
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/fileInWorkspace
```
