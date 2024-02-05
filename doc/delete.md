## 4.2.3. Delete

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
        delete from u01_user (
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

~~~yaml
  - methodName: deleteUser
    perform: !Delete
      input:
        delegate: Yes
      execSql: |
        delete from u01_user (
        where SURNAME     = :surname
          and GIVEN_NAME  = :givenName
          and BIRTH_DATE  = :birthDate
          and BIRTH_PLACE = :birthPlace
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DI extends DelegateDeleteUserPS> int deleteUser(
            final Connection c,
            final DI i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateDeleteUserPS rule = DaoU01.DelegateDeleteUserPS.builder()
                .surname(user::getSurname)
                .givenName(user::getGivenName)
                .birthDate(user::getBirthDate)
                .birthPlace(user::getBirthPlace)
                .build();
        int nmDelete = DaoU01.deleteUser(c,  rule);
~~~
