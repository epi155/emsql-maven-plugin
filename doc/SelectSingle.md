## 4.1.1. SelectSingle


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

### Delegate output

~~~yaml
  - methodName: findUser
    perform: !SelectSingle
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
    public static <DO extends DelegateFindUserRS> void findUser(
            Connection c,
            long idUser,
            DO o)
            throws SQLException ;
~~~

The method returns nothing, the output field is set using the rules provided by the delegation

Example of client code:

~~~java
        NUser user = new NUser();
        BUser born = new BUser();
        val rule = DaoU01.DelegateFindUserRS.builder()
                .surname(user::setSurname)
                .givenName(user::setGivenName)
                .birthDate(born::setBirthDate)
                .birthPlace(born::setBirthPlace)
                // more fields
                .build();
        DaoU01.findUser(c, 1, rule);
        // user and born are set
~~~

The use of output delegation is more laborious than a normal DTO interface, the advantage is that it is possible to set fields on different objects


### Delegate input

~~~yaml
  - methodName: findUserId
    perform: !SelectSingle
      input:
        delegate: yes
      execSql: |
        select
          ID_USER
        into
          :idUser
        from u01_user
        where SURNAME     = :surname
          and GIVEN_NAME  = :givenName
          and BIRTH_DATE  = :birthDate
          and BIRTH_PLACE = :birthPlace 
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <DI extends DelegateFindUserIdPS> long findUserId(
            Connection c,
            DI i)
            throws SQLException ;
~~~

Example of client code:

~~~java
        val rule = DaoU01.DelegateFindUserIdPS.builder()
                .surname(user::getSurname)
                .givenName(user::getGivenName)
                .birthDate(born::getBirthDate)
                .birthPlace(born::getBirthPlace)
                .build();
        long userId = DaoU01.findUserId(c, rule);
~~~

The use of input delegation is more laborious than a normal DTO interface, the advantage is that it is possible to obtain the value of the fields from different objects

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-next.png)](SelectOptional.md)