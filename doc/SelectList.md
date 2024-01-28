## 4.1.3. SelectList


The SelectList template is designed for the situation where the selection must return a list of objects, or even none. it is advisable that the list is not excessively large

### Standard use

Template example

~~~yaml
  - methodName: findUsers
    perform: !SelectList
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

~~~yaml
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

For this type of template it makes no sense to define output delegation rules.

### Delegate input

~~~yaml
  - methodName: findUsers
    perform: !SelectList
      fetchSize: 2048
      input:
        delegate: yes
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
        and   SURNAME like :foSurname
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DI extends DelegateFindUsersPS,O extends FindUsersRS> List<O> findUsers(
            Connection c,
            DI i,
            Supplier<O> so)
            throws SQLException ;
~~~

Example of client code:

~~~java
        DaoU01.DelegateFindUsersPS rule = DaoU01.DelegateFindUsersPS.builder()
                .foSurname(fo::getFoSurname)
                .dtMin(fo::getFoDtMin)
                .dtMax(fo::getFoDtMax)
                .birthState(user::getBirthState)
                .build();
        List<XUser> users = DaoU01.findUsers(c, rule, XUser::new);
~~~
