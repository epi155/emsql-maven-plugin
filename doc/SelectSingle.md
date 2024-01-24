## <a id="44">4.4. SelectSingle</a>


The SelectSingle template is designed for the situation where the selection needs to return a single element, no elements, or multiple elements throw a SqlException

### 4,1,1. Standard use

Template example

~~~yaml
packageName: com.example.esql
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
      input:
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5      # (seconds) optional, default null (system default)
      query: |
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

### 4,1,2. Delegate output

~~~yaml
  - methodName: findUser
    perform: !SelectSingle
      output:
        delegate: yes
      query: |
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
    XUser user = new XUser();
    val rule = DaoU01.DelegateFindUserRS.builder()
                .surname(user::setSurname)
                .givenName(user::setGivenName)
                .birthDate(user::setBirthDate)
                // more fields
                .build();
    DaoU01.findUser(c, 1, rule);
    // user is set
~~~
