## 4.2.2. SelectOptional


The SelectOptional template is designed for the situation where the selection must return one or no elements, the presence of multiple elements throws a SqlException

### Standard use

Template example

~~~yaml
  - methodName: findUserAny
    perform: !SelectOptional
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

~~~java
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

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectSingle.md) [![Next](go-next.png)](SelectList.md)