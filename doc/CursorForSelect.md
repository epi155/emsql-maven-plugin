## 4.2.4. CursorForSelect

The CursorForSelect template is designed for the situation where you need to manage a list of objects one at a time

### Standard use (Imperative)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
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

### Delegate output (Imperative)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      output:
        delegate: yes
      mode: IP
      execSql: |
        select
          ID_USER,
          SURNAME,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :idUser
          :surname,
          :givenName,
          :birthDate,
          :birthPlace,
          :birthCountry,
          :citizenship
        from u01_user
        where BIRTH_STATE = :birthState
        and   BIRTH_DATE between :dtMin and :dtMax
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DO extends DelegateFindUsersRS> SqlDelegateCursor openFindUsers(
            Connection c,
            String birthState,
            LocalDate dtMin,
            LocalDate dtMax,
            DO o)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateFindUsersRS rule = DaoU01.DelegateFindUsersRS.builder()
                .surname(user::setSurname)
                .givenName(user::setGivenName)
                // ...
                .build();
        try (val crs = DaoU01.openFindUsers(c, state, dtMin, dtMax, rule)) {
            while (crs.hasNext()) {
                crs.fetchNext();
                // user is set
            }
        }
~~~


### Delegate output (Functional)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      output:
        delegate: yes
      mode: FP
      execSql: |
        select
          ID_USER,
          SURNAME,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :idUser
          :surname,
          :givenName,
          :birthDate,
          :birthPlace,
          :birthCountry,
          :citizenship
        from u01_user
        where BIRTH_STATE = :birthState
        and   BIRTH_DATE between :dtMin and :dtMax
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DO extends DelegateFindUsersRS> void loopFindUsers(
            Connection c,
            String birthState,
            LocalDate dtMin,
            LocalDate dtMax,
            DO o,
            Runnable co)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateFindUsersRS rule = DaoU01.DelegateFindUsersRS.builder()
                .surname(user::setSurname)
                .givenName(user::setGivenName)
                // ...
                .build();
        DaoU01.loopFindUsers(c, state, dtMin, dtMax, rule, () -> {
            // user is set
        });
~~~



### Delegate input (Imperative)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      input:
        delegate: yes
      mode: IP
      execSql: |
        select
          ID_USER,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :idUser
          :givenName,
          :birthDate,
          :birthPlace,
          :birthCountry,
          :citizenship
        from u01_user
        where BIRTH_STATE = :birthState
        and   BIRTH_DATE between :dtMin and :dtMax
        and   SURNAME like :surname
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DI extends DelegateFindUsersPS,O extends FindUsersRS> SqlCursor<O> openFindUsers(
            Connection c,
            DI i,
            Supplier<O> so)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateFindUsersPS rule = DaoU01.DelegateFindUsersPS.builder()
                .birthState(fo::getBirthState)
                .dtMin(fo::getDtMin)
                .dtMax(fo::getDtMax)
                .surname(fo::getSurname)
                .build();
        try(val crs = DaoU01.openFindUsers(c, rule, XUser::new)) {
            while (crs.hasNext()) {
                XUser user = crs.fetchNext();
                // ...
            }
        }
~~~

### Delegate input (Functional)

Template example

~~~yaml
  - methodName: findUsers
    perform: !CursorForSelect
      input:
        delegate: yes
      mode: FP
      execSql: |
        select
          ID_USER,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :idUser
          :givenName,
          :birthDate,
          :birthPlace,
          :birthCountry,
          :citizenship
        from u01_user
        where BIRTH_STATE = :birthState
        and   BIRTH_DATE between :dtMin and :dtMax
        and   SURNAME like :surname
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DI extends DelegateFindUsersPS,O extends FindUsersRS> void loopFindUsers(
            Connection c,
            DI i,
            Supplier<O> so,
            Consumer<O> co)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateFindUsersPS rule = DaoU01.DelegateFindUsersPS.builder()
                .birthState(fo::getBirthState)
                .dtMin(fo::getDtMin)
                .dtMax(fo::getDtMax)
                .foSurname(fo::getSurname)
                .build();
        DaoU01.loopFindUsers(c, rule, XUser::new, user -> {
            // ...
        });
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectList.md) [![Next](go-next.png)](SelectListDyn.md)