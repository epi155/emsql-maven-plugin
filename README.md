# emsql-maven-plugin
Generate DAO classes with JDBC code and DTO interfaces from YAML configuration files

## <a id="1">1. Introduction</a>
This plugin generate java class from one or more configuration
file in yaml format

## <a id="2">2. Preview</a>

Configuration plugin example:

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
            <configuration>
                <modules>
                    <module>dao.yaml</module>
                </modules>
            </configuration>
        </execution>
    </executions>
</plugin>
~~~

Configuration file example:

~~~yaml
packageName: com.example.dao
className: DaoUser
declare:
  idUser: int
  surname: VarChar
  givenName: VarChar
  birthDate: LocalDate
  birthPlace: VarChar
  birthState: VarChar?
  birthCountry: VarChar
  citizenship: VarChar
methods:
  - methodName: findUser
    perform: !SelectOptional
      execSql: |
        SELECT
          SURNAME,
          GIVEN_NAME, 
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        INTO
          :surname,
          :givenName, 
          :birthDate,
          :birthPlace,
          :birthState,
          :birthCountry,
          :citizenship
        FROM U01_USER
        WHERE ID_USER = :idUser
~~~

Generate code (metod body omitted)

~~~java
    public static <O extends FindUserRS> Optional<O> findUser(
        Connection c,
        int idUser,
        Supplier<O> so)
        throws SQLException;

~~~

Input and output interface are created as nested classes

~~~java
public interface FindUserRS {
    void setSurname(String s);
    void setGivenName(String s);
    void setBirthDate(LocalDate s);
    void setBirthPlace(String s);
    void setBirthState(String s);
    void setBirthCountry(String s);
    void setCitizenship(String s);
}
~~~

Generated code requires dependencies

~~~xml
        <dependency>
            <groupId>io.github.epi155</groupId>
            <artifactId>emsql-runtime</artifactId>
            <version>x.y.z</version>
        </dependency>
~~~

If java 9 jigsaw modules are used it is necessary to add in `module-info.java`

~~~java
module myModule {
    ...
    requires emsql.runtime;
}~~~

Example of client code

~~~java
    Optional<XUser> oUser = DaoU01.findUser(c, 1, XUser::new);
~~~

where `XUser` is a client class that implements `FindUserRS` interface.


[3) Plugin parameters details](doc/plugin.md)<br/>
[4) Configuration YAML details](doc/ConfigYaml.md)<br/>

[![Next](doc/go-next.png)](doc/plugin.md)