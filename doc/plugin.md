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
If you are using Springboot you can add the `spring-boot-data-jdbc` dependency; if you are using SpringFramework the required dependencies are `spring-jdbc` and `spring-context`.

> In the following documentation only code examples generated with the POJO module will be shown.
> The corresponding SPRING code is obtained by replacing the static class with its instance and removing the connection parameter.

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

If your application uses more than one data source, you must associate a qualifier with each data source and specify the qualifier in the configuration file to indicate which data source to query.

~~~yaml
...
qualifier: ds1              # @Qualifier("ds1") DataSource dataSource
...
~~~

`parserProvider` or property `maven.emsql.parser-provider`
: Specifies the name of the parser provider to use for query validation. The parser provider must be provided as a dependency in an additional library
The plugin does not perform any specific grammar checks on queries, and can be used as such without any problems.
If you want more control over your queries, you can provide an implementation of the query validation interface exposed by the plugin.

~~~java
package io.github.epi155.emsql.spi;

public interface ParserProvider {
    String name();
    SqlParser create();
}
~~~

~~~java
public interface SqlParser {
    void  validate(String query, Class<? extends SqlAction> action);
}
~~~

If a library that implements the interface as a service provider is added to the plugin, the plugin will use the library to validate queries.

~~~xml
<plugin>
    <groupId>io.github.epi155</groupId>
    <artifactId>emsql-maven-plugin</artifactId>
    <version>x.y.z</version>
    <executions>
        <execution>
            ...
            <configuration>
                ...
                <parserProvider>...</parserProvider>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>custom-parser</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
~~~

if the parserProvider name is not explicitly indicated, the first one retrieved from the `ServiceLoader` is used. In the absence of a service provider, no query validation will be performed.

For validation, we can use generic validation libraries like [jooq](https://www.jooq.org/doc/latest/manual/sql-building/sql-parser/) or [JSqlParser](https://jsqlparser.github.io/JSqlParser/).

~~~java
public class JooqProvider implements ParserProvider {
    @Override
    public String name() { return "JOOQ"; }

    private static class Helper {
        private static final SqlParser INSTANCE = new SqlParser() {
            private final org.jooq.Parser jooqParser = new DefaultDSLContext(SQLDialect.DEFAULT).parser();
            @Override
            public void validate(String query, Class<? extends SqlAction> action) { jooqParser.parse(query); }
        };
    }

    @Override
    public SqlParser create() {
        return Helper.INSTANCE;
    }
}
~~~

Validators can give false positives (complex queries that are seen as incorrect) or false negatives (queries with small errors that are not detected by the validator)

[![Up](go-up.png)](../README.md) [![Next](go-next.png)](ConfigYaml.md)