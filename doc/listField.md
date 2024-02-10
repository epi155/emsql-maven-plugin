### List parameters

Sometimes a search field can take only a list of values but the number of such values is not known a priori.
It would be convenient to be able to write something like this:

~~~sql
select
  common_name, organizational_unit, organization, locality, state, country
from certificate
where locality in ( :localities )
~~~

where localities is the list of locality we are looking for.
But JDBC drivers do not provide list-type fields,
it is necessary to transform the list field into n single fields at runtime before creating the statement.

This operation is performed by the generated code, but the list field must be defined appropriately.

~~~yaml
packageName: com.example.emsql
className: DaoCertificate
declare:
  commonName: varchar
  organizationalUnit: varchar
  organization: varchar
  locality: varchar
  state: varchar
  country: char
  localities: ( locality )
methods:
  - methodName: certificateList
    perform: !SelectList
      execSql: |
        select
          common_name, organizational_unit, organization, locality, state, country
        into
          :commonName, :organizationalUnit, :organization, :locality, :state, :country
        from certificate
        where locality in ( :localities )
~~~

And that's it.
Generated DAO method signature (body omitted):

~~~java
    public static <O extends CertificateListRS> List<O> certificateList(
            final Connection c,
            final List<String> localities,
            final Supplier<O> so)
            throws SQLException ;
~~~

And you can do even more

~~~yaml
declare:
  ...
  organization: varchar
  locality: varchar
  ...
  locOrgs: ( locality, organization )
methods:
  - methodName: certificateListStrict
    perform: !SelectList
      execSql: |
        select
          common_name, organizational_unit, organization, locality, state, country
        into
          :commonName, :organizationalUnit, :organization, :locality, :state, :country
        from certificate
        where (locality, organization) in ( :locOrgs )
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <O extends CertificateListStrictRS,L1 extends LocOrgsPS> List<O> certificateListStrict(
            final Connection c,
            final List<L1> locOrgs,
            final Supplier<O> so)
            throws SQLException ;
~~~

Where `LocOrgsPS` is the DTO interface with getters

~~~java
    public interface LocOrgsPS {
        String getLocality();
        String getOrganization();
    }
~~~
