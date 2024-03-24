## 4.3.2. Update

The Update template is designed to modify one or more elements from the table.

### Standard use

Template example

~~~yaml
  - methodName: updateUser
    perform: !Update
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        update u01_user
        set SURNAME     = :surname
          , GIVEN_NAME  = :givenName
          , BIRTH_DATE  = :birthDate
          , BIRTH_PLACE = :birthPlace
        where ID_USER = :idUser
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends UpdateUserPS> int updateUser(
            final Connection c,
            final I i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        int nmUpdate = DaoU01.updateUser(c,  user);
~~~

### Delegate input:

see [Insert](insert.md#delegate-input)

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](insert.md) [![Next](go-next.png)](delete.md)