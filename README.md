# esql-maven-plugin
Generate DAO classes with JDBC code and DTO interfaces from YAML configuration files

## <a id="1">1. Introduction</a>
This plugin generate java class from one or more configuration
file in yaml format

## <a id="2">2. Preview</a>

Configuration plugin example:

~~~xml
<plugin>
    <groupId>io.github.epi155</groupId>
    <artifactId>esql-maven-plugin</artifactId>
    <version>0.1-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <modules>
                    <module>DaoC03Prodotto.yaml</module>
                </modules>
            </configuration>
        </execution>
    </executions>
</plugin>
~~~

Configuration file example:

~~~yaml
packageName: com.example.esql
className: DaoUser
methods:
  - methodName: findUser
    perform: !SelectOptional
      input:
        fields:
          idUser: int
      output:
        fields:
          surname: VarChar
          givenName: VarChar
          birthDate: LocalDate
          birthPlace: VarChar
          birthState: VarChar?
          birthCountry: VarChar
          citizenship: VarChar
      query: |
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

Input and output interface are created as nexted classes

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
            <artifactId>esql-runtime</artifactId>
            <version>0.1-SNAPSHOT</version>
        </dependency>
~~~


Example of client code

~~~java
    Optional<XUser> oUser = DaoU01.findUser(c, 1, XUser::new);
~~~

where `XUser` is a client class that implements `FindUserRS` interface.


[3) Plugin parameters details](#3)<br/>
[4) Configuration YAML details (DQL)](doc/ConfigYaml.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1) SelectCount](doc/SelectCount.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2) SelectList](doc/SelectList.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3) SelectOptional](doc/SelectOptional.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4) SelectSingle](doc/SelectSingle.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.5) CursorForSelect](doc/CursorForSelect.md)<br/>
[5) Configuration YAML details (DML)](#5)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.1) Delete](#51)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.2) DeleteBatch](#52)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.3) Insert](#53)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.4) InsertBatch](#54)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.5) Update](#55)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.6) UpdateBatch](#56)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.7) InsertReturnKeys](#57)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.8) InsertReturnInto](#58)<br/>
