# file-hierarchy-generator

A small library to create directory hierarchy with files in tests.

## Create files in directories

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

Result:

```
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/subdir1/fileInSubdir1
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/subdir1/subdir11/fileInSubdir11
${java.io.tmpdir}/file-hierarchy-generator-${timestamp}/workspace/fileInWorkspace
```

## Create xml files
```java
FileContentXmlGenerator
    .createFile("root.xml")
    .withElement("root")
	.withElement("subroot1")
	.withText("value")
	.up()
	.withElement("subroot2")
	.withElement("subroot21")
	.withText("text21")
	.up()
	.withElement("subroot22")
	.withText("text22")
	.formatted();
```

Result:

```xml
<root>
    <subroot1>value</subroot1>
	<subroot2>
	    <subroot21>text21</subroot21>
	    <subroot22>text22</subroot22>
    </subroot2>
</root>
```
