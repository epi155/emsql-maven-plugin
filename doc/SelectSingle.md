## 4.2.1. SelectSingle


The SelectSingle template is designed for the situation where the selection needs to return a single element, no elements, or multiple elements throw a SqlException

### Standard use

Template example

~~~yaml
packageName: com.example.emsql
className: DaoU01
declare:
  idUser: BigInt
  surname: VarChar
  givenName: VarChar
  birthDate: LocalDate
  birthPlace: VarChar
  birthState: VarChar?
  birthCountry: VarChar
  citizenship: VarChar
methods:
  - methodName: findUser
    perform: !SelectSingle
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
    public static <O extends FindUserRS> O findUser(
            Connection c,
            long idUser,
            Supplier<O> so)
            throws SQLException ;

~~~

Example of client code:

~~~java
	XUser user = DaoU01.findUser(c, 1, XUser::new);
~~~

where `Xuser` classe implements `FindUserRS` interface.

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](typeUnicode.md) [![Next](go-next.png)](SelectOptional.md)
