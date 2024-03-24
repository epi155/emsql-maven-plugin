## 4.3.3. Delete

The Delete template is designed to remove one or more elements from the table.

### Standard use

Template example

~~~yaml
  - methodName: deleteUser
    perform: !Delete
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        delete from u01_user
        where SURNAME     = :surname
          and GIVEN_NAME  = :givenName
          and BIRTH_DATE  = :birthDate
          and BIRTH_PLACE = :birthPlace
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends DeleteUserPS> int deleteUser(
            final Connection c,
            final I i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        int nmDelete = DaoU01.deleteUser(c, user);
~~~

### Delegate input

see [Insert](insert.md#delegate)

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](update.md) [![Next](go-next.png)](insertBatch.md)