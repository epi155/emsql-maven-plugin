## 4.1.2. SelectOptional


The SelectOptional template is designed for the situation where the selection must return one or no elements, the presence of multiple elements throws a SqlException

### Standard use

Template example

~~~yaml
  - methodName: findUserAny
    perform: !SelectOptional
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        select
          SURNAME,
          GIVEN_NAME, 
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :surname,
          :givenName, 
          :birthDate,
          :birthPlace,
          :birthState,
          :birthCountry,
          :citizenship
        from u01_user
        where id_user = :idUser
~~~

Generated DAO method signature (body omitted):

~~~yaml
    public static <O extends FindUserAnyRS> Optional<O> findUserAny(
            Connection c,
            long idUser,
            Supplier<O> so)
            throws SQLException ;
~~~

Example of client code:

~~~java
        Optional<XUser> oUser = DaoU01.findUserAny(c, 1, XUser::new);
~~~

where Xuser classe implements FindUserAnyRS interface.

### Delegate output

~~~yaml
  - methodName: findUserAny
    perform: !SelectOptional
      output:
        delegate: yes
      execSql: |
        select
          SURNAME,
          GIVEN_NAME, 
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        into
          :surname,
          :givenName, 
          :birthDate,
          :birthPlace,
          :birthState,
          :birthCountry,
          :citizenship
        from u01_user
        where id_user = :idUser
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DO extends DelegateFindUserAnyRS> boolean findUserAny(
            Connection c,
            long idUser,
            DO o)
            throws SQLException ;
~~~

In this case the result is a boolean, true indicates that the data has been found and the delegated variable has been set, false indicates that nothing has been found and the delegated variable has remained at its previous value

Example of client code:

~~~java
        NUser user = new NUser();
        BUser born = new BUser();
        val rule = DaoU01.DelegateFindUserAnyRS.builder()
                .surname(user::setSurname)
                .givenName(user::setGivenName)
                .birthDate(born::setBirthDate)
                .birthPlace(born::setBirthPlace)
                // more fields
                .build();
        if (DaoU01.findUserAny(c, 1, rule)) {
            // user and born are set
        }
~~~

### Delegate input

TODO