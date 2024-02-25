## 3. Plugin parameters details

Plugin with all parameters with default values:

~~~xml
<plugin>
    <groupId>io.github.epi155</groupId>
    <artifactId>emsql-maven-plugin</artifactId>
    <version>x.y.z</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <generateDirectory>target/generated-sources/emsql</generateDirectory>
                <configDirectory>src/main/resources</configDirectory>
                <modules>
                    <module>...</module>
                </modules>
                <debugCode>true</debugCode>
                <java7>false</java7>
                <addCompileSourceRoot>true</addCompileSourceRoot>
                <addTestCompileSourceRoot>false</addTestCompileSourceRoot>
                <provider>POJO</provider>
            </configuration>
        </execution>
    </executions>
</plugin>
~~~

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

`provider` or property `maven.emsql.provider`
: Indicates the java code generation module.
The default value (**POJO**) generates utility classes with static methods that require the connection as a parameter.
The alternative value (SPRING) generates spring-bean classes with instance methods that do not require connection as a parameter.
The connection is retrieved from the spring context. Transactions are also managed via spring.
Of course in this case some spring libraries are required for compilation.
If using springboot `spring-boot-starter-data-jdbc` is required, if simply using springframework `spring-jdbc` (and `spring-context`) is required.
In the following documentation only code examples generated with the POJO module will be shown.
The corresponding SPRING code is obtained by replacing the static class with its instance and removing the connection parameter.

POJO client code as an example

~~~java
	XUser user = DaoU01.findUser(c, 1, XUser::new);
~~~

in the SPRING version it becomes

~~~java
    @Autowired DaoU01 daoU01;
    ...
	XUser user = daoU01.findUser(1, XUser::new);
~~~
