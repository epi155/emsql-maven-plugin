## 3. Plugin parameters details

Parameters

`generateDirectory` or property `maven.emsql.generated-directory`
: Indicates the base directory from which to generate packages, default value is `${project.build.directory}/generated-sources/emsql`,
ie **`target/generated-sources/emsql`**

`configDirectory` or property `maven.emsql.config-directory`
: Indicates the base directory that contains the configuration files, default value
is `${project.build.resources[0].directory}`, ie **`src/main/resources`**

**`modules`**
: List of configuration files (required).

`debugCode` or property `maven.emsql.debug-code`
: Indicates whether to generate debug code to show queries and input parameter values, the default value is **`true`**.

`java7` or property `maven.emsql.java7`
: Indicates whether to generate code compatible with java-7, otherwise use java-8 features, the default value is **`false`**.

`addCompileSourceRoot` or property `maven.emsql.add-compile-source-root`
: If set to **true** (default), adds target directory as a compile source root of this Maven project.

`addTestCompileSourceRoot` or property `maven.emsql.add-test-compile-source-root`
: If set to true, adds target directory as a test compile source root of this Maven project. Default value is **false**.

