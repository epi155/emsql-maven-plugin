## 4.2.1. Insert

The Insert template is designed to insert a new item into the table

### Standard use

Template example

~~~yaml
  - methodName: insertUser
    perform: !Insert
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
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

### Delegate input

~~~yaml
  - methodName: insertUser
    perform: !Insert
      input:
        delegate: yes
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
    public static <DI extends DelegateInsertUserPS> int insertUser(
            final Connection c,
            final DI i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateInsertUserPS rule = DaoU01.DelegateInsertUserPS.builder()
                .surname(u01::getSurname)
                .givenName(u01::getGivenName)
                // ...
                .build();
        int nmInsert = DaoU01.insertUser(c,  rule);
~~~
