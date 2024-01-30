## 4.1.4. CursorForSelect

The CursorForSelect template is designed for the situation where you need to manage a list of objects one at a time

### Standard use

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
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
    public static <O extends FindUsersRS> ESqlCursor<O> openFindUsers(
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
        try(ESqlCursor<XUser> crs = DaoU01.openFindUsers(c, "Washington", LocalDate.of(1980, 1, 1), LocalDate.of(1985, 12, 31), XUser::new)) {
            while (crs.hasNext()) {
                XUser user = crs.fetchNext();
                // consume user ...
            }
        }   // close cursor (rs, ps)
~~~
