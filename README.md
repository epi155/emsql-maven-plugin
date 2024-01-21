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
    <version>0.5.0-SNAPSHOT</version>
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
className: DaoProdotto
methods:
  - methodName: searchByDescription
    perform: !SelectOptional
      inFields:
        dsPrd: VarChar
      outFields:
        idPrd: Int
        idCat: Int?
        idSco: Int?
        idUni: Int
      query: |
        select 
          id_prd,
          id_cat,
          id_sco,
          id_uni
        into
          :idPrd,
          :idCat,
          :idSco,
          :idUni
        from C03_PRODOTTO
        where ds_prd like :dsPrd
~~~

Generate code (metod body omitted)

~~~java
    public static <R extends SearchByDescriptionResponse> Optional<R> searchByDescription(
            Connection c,
            String dsPrd,
            Supplier<R> so)
            throws SQLException ;

~~~

Input and output interface are created as nexted classes

~~~java
    public interface SearchByDescriptionResponse {
        void setIdPrd(int s);
        void setIdCat(Integer s);
        void setIdSco(Integer s);
        void setIdUni(int s);
    }
~~~

Generated code requires dependencies

~~~xml
        <dependency>
            <groupId>io.github.epi155</groupId>
            <artifactId>esql-runtime</artifactId>
            <version>0.5.0-SNAPSHOT</version>
        </dependency>
~~~


Example of client code

~~~java
    Optional<XProd> oPrd = DaoProdotto.searchByDescription(con, "%SPA%", XProd::new);
~~~

where `XProd` is a client class that implements `CercaProdottoResponse` interface.


[3) Plugin parameters details](#3)<br/>
[4) Configuration YAML details (DQL)](#4)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1) SelectCount](#doc/SelectCount.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2) SelectList](#doc/SelectList.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3) SelectOptional](#SelectOptional.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4) SelectSingle](doc/SelectSingle.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.5) CursorForSelect](#doc/CursorForSelect.md)<br/>
[5) Configuration YAML details (DML)](#5)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.1) Delete](#51)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.2) DeleteBatch](#52)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.3) Insert](#53)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.4) InsertBatch](#54)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.5) Update](#55)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.6) UpdateBatch](#56)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.7) InsertReturnKeys](#57)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[5.8) InsertReturnInto](#58)<br/>
