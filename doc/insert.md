## 4.3.1. Insert

The Insert template is designed to insert a new item into the table

### Standard use

Template example

~~~yaml
  - methodName: insertUser
    perform: !Insert
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        insert into u01_user (
          SURNAME,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        ) values (
          :surname,
          :givenName,
          :birthDate,
          :birthPlace,
          :birthState,
          :birthCountry,
          :citizenship
        )
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends InsertUserPS> int insertUser(
            final Connection c,
            final I i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        int nmInsert = DaoU01.insertUser(c,  user);
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](CursorForSelectDyn.md) [![Next](go-next.png)](update.md)