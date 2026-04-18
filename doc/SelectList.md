## 4.2.3. SelectList


The SelectList template is designed for the situation where the selection must return a list of objects, or even none. it is advisable that the list is not excessively large

### Standard use

Template example

~~~yaml
  - methodName: findUsers
    perform: !SelectList
      timeout: 5          # (seconds) optional, default null (system default)
      fetchSize: 2048     # optional, default null (system default)
      execSql: |
        select
          ID_USER,
          SURNAME,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :idUser
          :surname,
          :givenName,
          :birthDate,
          :birthState,
          :birthCountry,
          :citizenship
        from u01_user
        where BIRTH_PLACE = :birthPlace
        and   BIRTH_DATE between :dtMin and :dtMax
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <O extends FindUsersRS> List<O> findUsers(
            Connection c,
            String birthPlace,
            LocalDate dtMin,
            LocalDate dtMax,
            Supplier<O> so)
            throws SQLException ;
~~~

Example of client code:

~~~java
        List<XUser> users = DaoU01.findUsers(c, "Washington", LocalDate.of(1980, 1, 1), LocalDate.of(1985, 12, 31), XUser::new);
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectOptional.md) [![Next](go-next.png)](CursorForSelect.md)