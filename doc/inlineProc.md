## 4.3.2. InlineProcedure

The InlineProcedure template is designed to call a sequence of SQL commands, as if they were the body of a stored procedure.
A `CallableStatement` is used to execute the block of commands (not a `PreparedStatement`)
As in the case of the CallProcedure it is necessary to explicitly indicate any output parameters.
In this case it is possible to identify the output parameters from the context, but the context can be arbitrarily complex and developing an algorithm that identifies them is not trivial.

### Standard use

Template example (oracle database)

~~~yaml
  - methodName: addError
    perform: !InlineProcedure
      execSql: |
        delclare 
          id07 number;
        begin
          select p07q.nextval into id07 from dual;

          insert into p07 (
          id_elemento_cdm, tipo, id_pratica_app, id_funzione_app, ts_vers
          ) values (
          id07, 'D', null, :idFunzioneApp, current_timestamp
          );

          insert into p08 (
          id_elemento_page, id_elemento_cdm, elemento_page, elemento_frag
          ) values (
          p08q.nextval, id07, 1,
          '[ {  "codice" : "' || :code || 
          '",  "descrizione" : "' || :desc || 
          '",  "applicationId" : "' || :appId || 
          '",  "errorCode" : "' || :errCode || 
          '" } ]'
          );

        end;
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends AddErrorPS> void addError(
        final Connection c,
        final I i)
        throws SQLException ;
~~~

Example of client code:

~~~java
        DaoP01.addError(c, errorData);
~~~
