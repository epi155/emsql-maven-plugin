## 4.2.4. CursorForSelect

The CursorForSelect template is designed for the situation where you need to manage a list of objects one at a time

### Standard use (Imperative)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      timeout: 5          # (seconds) optional, default null (system default)
      fetchSize: 2048     # optional, default null (system default)
      mode: IP            # optional, default IP (Imperative), else FP (Functional)
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
    public static <O extends FindUsersRS> SqlCursor<O> openFindUsers(
            Connection c,
            String birthState,
            LocalDate dtMin,
            LocalDate dtMax,
            Supplier<O> so)
            throws SQLException ;
~~~

The returned object allows you to loop through the list similar to an iterator.
Example of client code:

~~~java
        try(SqlCursor<XUser> crs = DaoU01.openFindUsers(c, "Washington", LocalDate.of(1980, 1, 1), LocalDate.of(1985, 12, 31), XUser::new)) {
            while (crs.hasNext()) {
                XUser user = crs.fetchNext();
                // consume user ...
            }
        }   // close cursor (rs, ps)
~~~

### Standard use (Functional)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      mode: FP
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
    public static <O extends FindUsersRS> void loopFindUsers(
            Connection c,
            String birthState,
            LocalDate dtMin,
            LocalDate dtMax,
            Supplier<O> so,
            Consumer<O> co)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.loopFindUsers(c, "Washington", LocalDate.of(1980, 1, 1), LocalDate.of(1985, 12, 31), XUser::new, user -> {
                    // consume user
                }
        ) ;
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectList.md) [![Next](go-next.png)](SelectListDyn.md)