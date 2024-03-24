## 4.3.7. InsertReturnKeys

When the primary key column of a table is defined as IDENTITY, how is it possible to recover the value generated in the insert together with the insert command?

When a PreparedStatement is created it is possible to indicate the RETURN_GENERATED_KEYS flag, with this option it is possible, after executing the insert, to recover the value of the generated key.

The InsertReturnKeys template is designed to insert a new item into the table and retrieve the value of the generated key.
It is similar to the Insert template, but you need to define the retrieved key fields in the output element

### Standard use

Template example

~~~yaml
  - methodName: saveUser
    perform: !InsertReturnKeys
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      output:
        fields:
          - idUser
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
    public static <I extends SaveUserPS> Optional<Long> saveUser(
            final Connection c,
            final I i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        Optional<Long> oIdUser = DaoU01.saveUser(c, user);
~~~

### Delegate input

see [Insert](insert.md#delegate)


[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](deleteBatch.md) [![Next](go-next.png)](callProc.md)